package org.blocky.parser;

import org.blocky.engine.BlockyEngine;
import org.blocky.engine.Scope;
import org.blocky.engine.Stack;
import org.blocky.engine.blocks.*;
import org.blocky.exception.CompilerException;

import java.io.*;
import java.util.Arrays;

public class TxtBlockyParser implements BlockyParser {

    private String text;

    public TxtBlockyParser(String txt){
        text = txt;
    }

    public TxtBlockyParser(File file) throws IOException {
        text = "";
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))){

                String line;

                while((line = reader.readLine()) != null){
                    text += line + "\n";
                }

            }
        }
    }

    private void parseMath(Tokenizer tokenizer, BlockFunction top){

        ExpressionTree expressionTree = new ExpressionTree(tokenizer);

        top.addBlock(expressionTree.eval(top.getScope()));

    }

    private Block parseLiteral(Scope scope, Tokenizer stringTokenizer) throws CompilerException {

        String token = stringTokenizer.nextTokenSkipWhitespace();

        BlockFunction math = new BlockFunction(scope);

        String equation = token + stringTokenizer.nextToken(';');

        parseMath(new Tokenizer(equation), math);

        return math;


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
                    if(cmd.equals("\n"))
                        line++;
                    continue;
                }

                cmd = stringTokenizer.nextWord();

                if(cmd.equals("print")) {

                    BlockPrintOut blockPrintOut = new BlockPrintOut();

                    currentFunction.addBlock(parseLiteral(currentFunction.getScope(), stringTokenizer));
                    currentFunction.addBlock(new BlockPushNative(1));
                    currentFunction.addBlock(blockPrintOut);
                }else if(cmd.equals("if")) {

                    Block condition = parseLiteral(currentFunction.getScope(), stringTokenizer);

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

                    currentFunction.addBlock(parseLiteral(currentFunction.getScope(), stringTokenizer));

                }else if(cmd.equals("end")) {

                    BlockFunction function = functionStack.pop();

                    if (function instanceof BlockyEngine) {
                        throw new CompilerException("Cannot pop context outside of this scope");
                    }

                    if(function instanceof BlockDefinedFunction){
                        BlockFunction parent = functionStack.peek();

                        parent.getScope().setValue(((BlockDefinedFunction) function).getName(), function);
                    }else{
                        BlockFunction parent = functionStack.peek();

                        parent.addBlock(function);
                    }

                }else if(cmd.equals("function")){

                    stringTokenizer.skip(1);
                    String funcName = stringTokenizer.nextWord();

                    stringTokenizer.skip(1);

                    String[] header = stringTokenizer.nextToken(')').replace(" ", "").split(",");

                    BlockDefinedFunction function = new BlockDefinedFunction(currentFunction.getScope(), funcName, header);

                    functionStack.push(function);

                }else if(Character.isAlphabetic(cmd.charAt(0))){//Variable declaration

                    String variableName = cmd;

                    String operation = stringTokenizer.nextTokenSkipWhitespace();

                    if(operation.equals("=")){
                        Block block = parseLiteral(currentFunction.getScope(), stringTokenizer);

                        currentFunction.addBlock(block);
                        currentFunction.addBlock(new BlockPushNative(variableName));
                        currentFunction.addBlock(new BlockSetVariable(currentFunction.getScope()));
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
        File file = new File("scripts/function.blocky");


        TxtBlockyParser txtBlockyParser = new TxtBlockyParser(file);

        long time = System.currentTimeMillis();

        BlockyEngine engine = txtBlockyParser.parse();

        System.out.println("Compiled in "+(System.currentTimeMillis() - time)+"ms");

        engine.printOutCompiledCode();

        time = System.currentTimeMillis();

        engine.run();

        System.out.println("Ran in "+(System.currentTimeMillis() - time)+"ms");

    }

}