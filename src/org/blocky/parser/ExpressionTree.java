package org.blocky.parser;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;
import org.blocky.engine.blocks.*;
import org.blocky.util.BTreePrinter;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionTree {

    private ExpressionTreeNode root;

    public ExpressionTree(Tokenizer tokenizer){
        root = parse(tokenizer);
        fixOrder(root);
    }

    private int precedent(String op){
        if(op.equals("*") || op.equals("/"))
            return 2;
        if(op.equals("+") || op.equals("-"))
            return 1;
        if(op.equals(">") || op.equals("<"))
            return 0;
        throw new IllegalArgumentException("Unknown op "+op);
    }

    private void fixOrder(ExpressionTreeNode root){
        if(!root.isLeaf()) {
            ExpressionTreeNode right = root.right;

            int precedent = precedent(root.value);
            //The right side of the tree always branches out while the left is leafs
            if(!right.isLeaf()){
                fixOrder(right);
                if(precedent > precedent(right.value)){
                    swap(root, right);
                }
            }

        }
    }

    private void swap(ExpressionTreeNode parent, ExpressionTreeNode child){

        String precedentOp = parent.value;

        ExpressionTreeNode oldRight = child.right;

        parent.value = child.value;
        child.value = precedentOp;

        child.right = parent.left;
        parent.left = oldRight;

        parent.updateSupers();
        child.updateSupers();

    }

    private ExpressionTreeNode parse(Tokenizer tokenizer){

        Stack stack = new Stack();

        while(tokenizer.hasMoreTokens()){

            String token = tokenizer.nextTokenSkipWhitespace();

            if(!isOperator(token.charAt(0))) {
                if(Character.isAlphabetic(token.charAt(0))) {

                    String name = token + tokenizer.nextWord();

                    if(tokenizer.peekIfPossible(1).equals("(")){//Function

                        tokenizer.skip(1);

                        int openBrackets = 1;

                        String inBrackets = "";

                        while(openBrackets > 0){

                            String withinToken = tokenizer.nextToken(1);

                            if(withinToken.equals("(")){
                                openBrackets++;
                            }else if(withinToken.equals(")")){
                                openBrackets--;
                            }

                            inBrackets += withinToken;
                        }

                        inBrackets = inBrackets.substring(0, inBrackets.length()-1);

                        ExpressionTreeNode expression = new ExpressionTreeNode("f"+name+"_"+inBrackets, null, null);

                        stack.push(expression);

                    }else{
                        ExpressionTreeNode expression = new ExpressionTreeNode("v"+name, null, null);

                        stack.push(expression);
                    }


                }else if(Character.isDigit(token.charAt(0))) {
                    ExpressionTreeNode node = new ExpressionTreeNode("i"+token + tokenizer.nextNumber(), null, null);

                    stack.push(node);
                }else if(token.charAt(0) == '"'){
                    String string = tokenizer.nextToken('"');
                    ExpressionTreeNode node = new ExpressionTreeNode("s"+string, null, null);

                    stack.push(node);
                }else{
                    throw new IllegalArgumentException("Cannot parse token "+token);
                }
            }else{

                ExpressionTreeNode left = stack.pop();

                ExpressionTreeNode expression = new ExpressionTreeNode(token, left, parse(tokenizer));

                stack.push(expression);

            }

        }

        return stack.pop();
    }

    public Block eval(Scope scope){
        return eval(root, scope);
    }

    private static BlockFunction eval(ExpressionTreeNode node, Scope scope){
        BlockFunction block = new BlockFunction(scope);

        if(!isOperator(node.value.charAt(0))){

            String value = node.value;

            char op = value.charAt(0);

            value = value.substring(1);

            if(op == 'v'){
                block.addBlock(new BlockPushNative(value));
                block.addBlock(new BlockGetVariable(scope));
            }else if(op == 'i'){
                block.addBlock(new BlockPushNative(Integer.parseInt(value)));
            }else if(op == 's') {
                block.addBlock(new BlockPushNative(value));
            }else if(op == 'f'){

                String[] funcParts = value.split("_", 2);

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

                header.add(tracker);

                for(int i = header.size() - 1;i >= 0;i--){
                    ExpressionTree expressionTree = new ExpressionTree(new Tokenizer(header.get(i)));

                    block.addBlock(expressionTree.eval(scope));
                }

                block.addBlock(new BlockPushNative(name));
                block.addBlock(new BlockGetVariable(scope));
                block.addBlock(new BlockRunDefinedFunction());

            }

        }else{
            String op = node.value;
            BlockFunction left;
            BlockFunction right;

            left = eval(node.left, scope);
            right = eval(node.right, scope);

            BlockFunction val = new BlockFunction(null);

            val.addBlock(new BlockPushNative(left));
            val.addBlock(new BlockRunFunction());
            val.addBlock(new BlockPushNative(right));
            val.addBlock(new BlockRunFunction());

            if(op.equals("+")){
                if(node.left.value.startsWith("s") || node.right.value.startsWith("s")){
                    val.addBlock(new BlockStringConcat());
                }else{
                    val.addBlock(new BlockMath(BlockMath.TYPE_ADD));
                }
            }else if(op.equals("-")){
                val.addBlock(new BlockMath(BlockMath.TYPE_SUB));
            }else if(op.equals("*")){
                val.addBlock(new BlockMath(BlockMath.TYPE_MULT));
            }else if(op.equals("/")) {
                val.addBlock(new BlockMath(BlockMath.TYPE_DIV));
            }else if(op.equals(">")) {
                val.addBlock(new BlockCompare(BlockCompare.TYPE_GT));
            }else if(op.equals("<")){
                val.addBlock(new BlockCompare(BlockCompare.TYPE_LT));
            }else{
                throw new IllegalStateException("Unknown op "+op);
            }

            /*if(op.equals("*") || op.equals("/")){
                if(node.right.left != null){
                    node.right.left.value = String.valueOf(val);
                    return eval(node.right, scope);
                }
            }*/

            return val;
        }


        return block;
    }

    private static int eval(ExpressionTreeNode node){

        if(!isOperator(node.value.charAt(0))){
            return Integer.valueOf(node.value);
        }


        String op = node.value;
        int left;
        int right;

        /*if(op.equals("*") || op.equals("/")){
            left = eval(node.left);
            if(node.right.left == null){
                right = eval(node.right);
            }else{
                right = eval(node.right.left);
            }
        }else{
            left = eval(node.left);
            right = eval(node.right);
        }*/
        left = eval(node.left);
        right = eval(node.right);

        int val;

        if(op.equals("+")){
            val = left + right;
        }else if(op.equals("-")){
            val = left - right;
        }else if(op.equals("*")){
            val = left * right;
        }else if(op.equals("/")){
            val = left / right;
        }else{
            throw new IllegalStateException("Unknown op "+op);
        }

        /*if(op.equals("*") || op.equals("/")){
            node.right.left.value = String.valueOf(val);
            return eval(node.right);
        }*/
        System.out.println(left+" "+op+" "+right+" = "+val);

        return val;
    }

    private static boolean isOperator(char c){
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '<' || c == '>';
    }

    public static void main(String[] args){
        String equation = "32 * 10 + add(50, sub(20, 20))";

        ExpressionTree node = new ExpressionTree(new Tokenizer(equation));

        BTreePrinter.printNode(node.root);

        //System.out.println(eval(node.root));
    }

    static final class ExpressionTreeNode extends BTreePrinter.Node<String> {

        private ExpressionTreeNode left;
        private ExpressionTreeNode right;

        private String value;

        public ExpressionTreeNode(String value, ExpressionTreeNode left, ExpressionTreeNode right) {
            super(value);
            super.left = left;
            super.right = right;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public void updateSupers(){
            super.left = left;
            super.right = right;
            super.data = value;
        }

        public boolean isLeaf(){
            return left == null && right == null;
        }

    }

}
