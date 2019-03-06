package org.blocky.parser;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;
import org.blocky.engine.blocks.*;
import org.blocky.exception.CompilerException;
import org.blocky.util.BTreePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExpressionTree {

    private static final Pattern SIDE_BY_SIDE_STRING_FIXER = Pattern.compile("\\\"[ ]*\\+[ ]*\\\"");

    private ExpressionTreeNode root;

    public ExpressionTree(String expression) throws CompilerException {
        this(new Tokenizer(fixStringLiterals(expression)));
    }

    private ExpressionTree(Tokenizer tokenizer) throws CompilerException {
        root = parse(tokenizer, 0);
    }

    private ExpressionTreeNode parse(Tokenizer tokenizer, int precedent) throws CompilerException {
        Stack stack = new Stack();

        while(tokenizer.hasMoreTokens()){

            char token = tokenizer.nextTokenSkipWhitespace().charAt(0);

            if(Character.isDigit(token)){
                String number = token + tokenizer.nextNumber();
                try{
                    Integer.parseInt(number);
                    stack.push(new ExpressionTreeNode(number, "i", null, null));
                }catch(NumberFormatException e){
                    throw new CompilerException("Could not parse number '"+number+"'");
                }
            }else if(Character.isAlphabetic(token)) {

                String variable = token + tokenizer.nextWord();

                String peek = tokenizer.peekIfPossible(1);

                if (peek.equals("(")) {
                    tokenizer.skip(1);
                    String inBrackets = tokenizer.parseBrackets();

                    stack.push(new ExpressionTreeNode(variable + "_" + inBrackets, "f", null, null));

                } else {
                    stack.push(new ExpressionTreeNode(variable, "v", null, null));
                }

            }else if(token == '"'){

                String quotes = tokenizer.nextToken('"');

                stack.push(new ExpressionTreeNode(quotes, "s", null, null));

            }else if(token == '(') {

                String inBrackets = tokenizer.parseBrackets();

                stack.push(new ExpressionTreeNode(inBrackets, "b", null, null));

                //ExpressionTree expressionTree = new ExpressionTree(new Tokenizer(inBrackets));
                //
                //                stack.push(expressionTree.root);
                //Later on we have to parse it
            }else if(isOperator(String.valueOf(token))){

                String other = "";

                if(token == '>' || token == '<' || token == '!' || token == '='){
                    if(tokenizer.peekSkipWhitespace().equals("=")){
                        other = token + tokenizer.nextTokenSkipWhitespace();
                    }
                }

                if(other.isEmpty())
                    other = String.valueOf(token);

                ExpressionTreeNode left = stack.pop();

                if(precedent(other) < precedent){
                    tokenizer.rewind(other.length());
                    return left;
                }

                ExpressionTreeNode right = parse(tokenizer, precedent(other));

                if(left.evaluatesTo().equals("s") || right.evaluatesTo().equals("s"))
                    other = "+s";

                stack.push(new ExpressionTreeNode(other, "o", left, right));

            }else{
                throw new CompilerException("Unknown operator "+token);
            }

        }

        return stack.pop();
    }

    public BlockFunction eval(Scope scope) throws CompilerException {
        return eval(root, scope);
    }

    private BlockFunction eval(ExpressionTreeNode node, Scope scope) throws CompilerException {

        BlockFunction function = new BlockSubroutine(scope);

        if(node.dataType.equals("o")) {

            String operator = node.value;

            ExpressionTreeNode left = node.left;
            ExpressionTreeNode right = node.right;

            BlockFunction rightFunction = eval(left, scope);
            BlockFunction leftFunction = eval(right, scope);

            function.addBlock(rightFunction);
            function.addBlock(leftFunction);

            if (operator.equals("+")) {
                function.addBlock(new BlockMath(BlockMath.TYPE_ADD));
            } else if (operator.equals("-")) {
                function.addBlock(new BlockMath(BlockMath.TYPE_SUB));
            } else if (operator.equals("*")) {
                function.addBlock(new BlockMath(BlockMath.TYPE_MULT));
            } else if (operator.equals("/")) {
                function.addBlock(new BlockMath(BlockMath.TYPE_DIV));
            } else if (operator.equals(">")) {
                function.addBlock(new BlockCompare(BlockCompare.TYPE_GT));
            } else if (operator.equals("<")) {
                function.addBlock(new BlockCompare(BlockCompare.TYPE_LT));
            } else if (operator.equals(">=")) {
                function.addBlock(new BlockCompare(BlockCompare.TYPE_GTE));
            } else if (operator.equals("<=")) {
                function.addBlock(new BlockCompare(BlockCompare.TYPE_LTE));
            } else if (operator.equals("==")) {
                function.addBlock(new BlockCompare(BlockCompare.TYPE_EQUALS));
            } else if (operator.equals("!=")) {
                function.addBlock(new BlockCompare(BlockCompare.TYPE_NOT_EQUALS));
            }else if(operator.equals("+s")){
                function.addBlock(new BlockStringConcat());
            }

        }else if(node.dataType.equals("s")){
            function.addBlock(new BlockPushNative(node.value));
        }else if(node.dataType.equals("i")){
            function.addBlock(new BlockPushNative(Integer.parseInt(node.value)));
        }else if(node.dataType.equals("v")){
            function.addBlock(new BlockPushNative(node.value));
            function.addBlock(new BlockGetVariable(scope));
        }else if(node.dataType.equals("b")){
            ExpressionTree tree = new ExpressionTree(new Tokenizer(node.value));

            function.addBlock(tree.eval(scope));
        }else if(node.dataType.equals("f")){
            String[] funcParts = node.value.split("_", 2);

            String name = funcParts[0];
            List<String> header = new ArrayList<>();

            Tokenizer headerTokenizer = new Tokenizer(funcParts[1]);

            int brackets = 0;

            String tracker = "";

            while(headerTokenizer.hasMoreTokens()){
                String token = headerTokenizer.nextToken(1);

                if(token.equals("("))
                    brackets++;
                else if(token.equals(")"))
                    brackets--;

                tracker += token;

                if(brackets == 0 && token.equals(",")){
                    header.add(tracker.substring(0, tracker.length()-1));
                    tracker = "";
                }

            }

            if(!tracker.isEmpty())
                header.add(tracker);

            for(int i = header.size() - 1;i >= 0;i--){
                ExpressionTree expressionTree = new ExpressionTree(new Tokenizer(header.get(i)));

                function.addBlock(expressionTree.eval(scope));
            }

            function.addBlock(new BlockPushNative(name));
            function.addBlock(new BlockGetFunction(scope));
            function.addBlock(new BlockRunDefinedFunction());
        }

        return function;
    }

    public static int precedent(String operator){
        if(operator.equals("+s"))
            return 0;
        if(operator.equals("+") || operator.equals("-"))
            return 1;
        if(operator.equals("*") || operator.equals("/"))
            return 2;
        if(operator.equals(">")
                || operator.equals("<")
                || operator.equals(">=")
                || operator.equals("<=")
                || operator.equals("==")
                || operator.equals("!="))
            return 0;

        throw new RuntimeException("Unknown operator "+operator);
    }

    public static String fixStringLiterals(String expression){
        StringBuffer buffer = new StringBuffer();

        Tokenizer tokenizer = new Tokenizer(SIDE_BY_SIDE_STRING_FIXER.matcher(expression).replaceAll(""));

        boolean inside = expression.startsWith("\"");
        boolean justLeftQuote = false;
        int function = 0;

        if(!inside){
            buffer.append('(');
        }

        while(tokenizer.hasMoreTokens()){

            if(inside){
                String until = tokenizer.nextToken('"');

                buffer.append(until);
                buffer.append('"');
                inside = false;
                justLeftQuote = true;
            }else{

                String token = tokenizer.nextToken(1);

                if(function <= 0 && token.equals(" "))
                    continue;

                if(Character.isAlphabetic(token.charAt(0))) {
                    String word = token + tokenizer.nextWord();

                    buffer.append(word);

                    if (tokenizer.peekIfPossible(1).equals("(")) {
                        function++;
                    }
                }else if(function > 0){
                    buffer.append(token);
                    if(token.equals(")")){
                        function--;
                    }
                }else if(token.equals("\"")){
                    buffer.insert(buffer.length()-1, ')');
                    buffer.append('"');
                    inside = true;
                }else if(token.equals("+") && justLeftQuote){
                    buffer.append('+');
                    buffer.append('(');
                }else{
                    buffer.append(token);
                }

                justLeftQuote = false;

            }

        }

        if(!inside && !justLeftQuote){
            buffer.append(')');
        }

        return buffer.toString();
    }

    public static boolean isOperator(String operator){
        return operator.equals("+")
                || operator.equals("-")
                || operator.equals("*")
                || operator.equals("/")
                || operator.equals(">")
                || operator.equals("<")
                || operator.equals(">=")
                || operator.equals("<=")
                || operator.equals("==")
                || operator.equals("!=")
                || operator.equals("=")
                || operator.equals("!");
    }

    public static void main(String[] args) throws Exception {
        String equation = "6 * 3 + 5 + \"TOP\" + 5 + \"Woot\" + 5 * 100 + \"KLED\"";

        ExpressionTree node = new ExpressionTree(equation);

        BTreePrinter.printNode(node.root);

        BlockFunction function = node.eval(new Scope());
        function.printBlock(function.blocks, 0);

        Stack stack = new Stack();

        Thread.sleep(5);//Give console time to catch up

        function.execute(stack);

        System.out.println(stack.pop().toString());
    }

    static final class ExpressionTreeNode extends BTreePrinter.Node {

        private ExpressionTreeNode left;
        private ExpressionTreeNode right;

        private String value;
        private String dataType;

        public ExpressionTreeNode(String value, String dataType, ExpressionTreeNode left, ExpressionTreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.dataType = dataType;
        }

        @Override
        public ExpressionTreeNode getLeft() {
            return left;
        }

        @Override
        public ExpressionTreeNode getRight() {
            return right;
        }

        @Override
        public Object getData() {
            return value;
        }

        public String evaluatesTo(){
            if(dataType.equals("o")){
                if(left.evaluatesTo().equals("s") || right.evaluatesTo().equals("s"))
                    return "s";
            }
            return dataType;
        }

        public boolean isLeaf(){
            return dataType.equals("o");//Operator
        }

    }

}
