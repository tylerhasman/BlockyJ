package org.blocky.parser;

import org.blocky.engine.BlockyEngine;
import org.blocky.engine.Scope;
import org.blocky.engine.Stack;
import org.blocky.engine.blocks.*;
import org.blocky.exception.CompilerException;

import java.io.*;

public class TxtBlockyParser implements BlockyParser {

    private String text;

    public TxtBlockyParser(String txt){
        text = txt;
    }

    public TxtBlockyParser(File file) throws IOException {
        text = readFile(file);
    }

    private static String readFile(File file) throws IOException{
        String text = "";
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))){

                String line;

                while((line = reader.readLine()) != null){

                    if(line.startsWith("#include")){
                        String what = line.substring(line.indexOf(' ')+1);

                        text += readFile(new File(what));
                        System.out.println("Included "+what);
                    }else{
                        text += line + "\n";
                    }

                }

            }
        }
        return text;
    }

    private Block parseExpression(Scope scope, Tokenizer stringTokenizer, char endCharacter) throws CompilerException {

        String token = stringTokenizer.nextTokenSkipWhitespace();

        BlockFunction expression = new BlockSubroutine(scope);

        String equation = token + stringTokenizer.nextToken(endCharacter);

        ExpressionTree expressionTree = new ExpressionTree(equation);

        expression.addBlock(expressionTree.eval(expression.getScope()));

        return expression;


    }

    @Override
    public BlockyEngine parse() throws CompilerException {

        Tokenizer stringTokenizer = new Tokenizer(text);

        BlockyEngine blockyEngine = new BlockyEngine();

        int line = 1;

        Stack functionStack = new Stack();
        functionStack.push(blockyEngine);

        try{
            while(stringTokenizer.hasMoreTokens()){

                BlockFunction currentFunction = functionStack.peek();

                String cmd = stringTokenizer.peek(1);

                if(cmd.isEmpty() || cmd.equals("\n") || cmd.equals(";") || cmd.equals(" ")) {
                    stringTokenizer.skip(1);
                    continue;
                }

                line++;

                cmd = stringTokenizer.nextWord();

                if(cmd.isEmpty()) {
                    stringTokenizer.skip(1);
                    continue;
                }

                if(cmd.equals("if")) {

                    Block condition = parseExpression(currentFunction.getScope(), stringTokenizer, ':');

                    currentFunction.addBlock(condition);

                    BlockIf blockIf = new BlockIf(currentFunction.getScope());

                    functionStack.push(blockIf);

                }else if(cmd.equals("else")) {

                    BlockIf currentIf = functionStack.pop();
                    currentIf.introduceElse();

                    currentFunction = functionStack.peek();

                    currentFunction.addBlock(currentIf);

                    BlockIf blockIf = new BlockIf(currentFunction.getScope());

                    functionStack.push(blockIf);
                }else if(cmd.equals("return")){

                    if(currentFunction instanceof BlockFunction){
                        currentFunction.addBlock(parseExpression(currentFunction.getScope(), stringTokenizer, ';'));
                    }else{
                        throw new CompilerException("Cannot return value in "+currentFunction.getClass().getSimpleName()+" context.");
                    }

                }else if(cmd.equals("end")) {

                    BlockFunction function = functionStack.pop();

                    if (function instanceof BlockyEngine) {
                        throw new CompilerException("Cannot pop context outside of this scope");
                    }

                    if (function instanceof BlockDefinedFunction) {
                        blockyEngine.declareFunction((BlockDefinedFunction) function);
                    } else {
                        BlockFunction parent = functionStack.peek();

                        parent.addBlock(function);
                    }
                }else if(cmd.equals("while")){
                    Block condition = parseExpression(currentFunction.getScope(), stringTokenizer, ':');

                    BlockWhile blockWhile = new BlockWhile(condition, currentFunction.getScope());

                    functionStack.push(blockWhile);
                }else if(cmd.equals("function")){

                    if(currentFunction instanceof BlockyEngine){
                        stringTokenizer.skip(1);
                        String funcName = stringTokenizer.nextWord();

                        stringTokenizer.skip(1);

                        String[] header = stringTokenizer.nextToken(')').replace(" ", "").split(",");

                        if(header.length == 1 && header[0].isEmpty())
                            header = new String[0];

                        BlockDefinedFunction function = new BlockDefinedFunction(currentFunction.getScope(), funcName, header);

                        functionStack.push(function);
                    }else{
                        throw new CompilerException("Cannot define function inside of "+currentFunction.getClass().getSimpleName());
                    }

                }else if(Character.isAlphabetic(cmd.charAt(0))){//Variable declaration

                    String variableName = cmd;

                    String operation = stringTokenizer.nextTokenSkipWhitespace();

                    if(operation.equals("=")) {
                        Block block = parseExpression(currentFunction.getScope(), stringTokenizer, ';');

                        currentFunction.addBlock(block);
                        currentFunction.addBlock(new BlockPushNative(variableName));
                        currentFunction.addBlock(new BlockSetVariable(currentFunction.getScope()));
                    }else if(operation.equals("(")){
                        String theRest = stringTokenizer.nextToken(';');
                        //Function!
                        currentFunction.addBlock(parseExpression(currentFunction.getScope(), new Tokenizer(variableName+"("+theRest), ';'));
                    }else{
                        throw new CompilerException("Unknown assignement operation "+operation);
                    }

                }else{
                    throw new CompilerException("Unexpected token '"+cmd+"'");
                }

            }
        }catch(Exception e){
            System.err.println("Error on line "+line);
            throw new CompilerException(e);
        }


        return blockyEngine;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("scripts/include_test.blocky");


        TxtBlockyParser txtBlockyParser = new TxtBlockyParser(file);

        long time = System.currentTimeMillis();

        BlockyEngine engine = txtBlockyParser.parse();

        System.out.println("Compiled in "+(System.currentTimeMillis() - time)+"ms");

        engine.printOutCompiledCode();

        time = System.currentTimeMillis();

        System.out.println("---- PROGRAM OUTPUT ----");
        Thread.sleep(5);//Allow the compiled code to print out properly
        engine.run();

        System.out.println("---- PROGRAM OUTPUT ----");

        System.out.println("Ran in "+(System.currentTimeMillis() - time)+"ms");

    }

}
