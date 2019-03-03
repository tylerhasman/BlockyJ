package org.blocky.parser;

import org.blocky.engine.BlockyEngine;
import org.blocky.engine.Scope;
import org.blocky.engine.blocks.*;
import org.blocky.exception.CompilerException;

import java.io.*;
import java.util.StringTokenizer;

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

        while(tokenizer.hasMoreTokens()){

            String c = tokenizer.nextToken(1);

            if(c.isEmpty() || c.equals(" "))
                continue;

            if(c.equals("(")){
                BlockFunction bracket = new BlockFunction(top.getScope());

                parseMath(tokenizer, bracket);

                top.addBlock(bracket);
            }else if(c.equals(")")){
                break;
            }else if(c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/")){

                BlockFunction afterPlus = new BlockFunction(top.getScope());
                parseMath(tokenizer, afterPlus);
                top.addBlock(afterPlus);

                if(c.equals("+"))
                    top.addBlock(new BlockMath(BlockMath.TYPE_ADD));
                else if(c.equals("-"))
                    top.addBlock(new BlockMath(BlockMath.TYPE_SUB));
                else if(c.equals("*"))
                    top.addBlock(new BlockMath(BlockMath.TYPE_MULT));
                else if(c.equals("/"))
                    top.addBlock(new BlockMath(BlockMath.TYPE_DIV));

            }else if(Character.isDigit(c.charAt(0))){
                String numStr = c + tokenizer.nextNumber();
                int number = Integer.parseInt(numStr);

                top.addBlock(new BlockPushNative(number));
            }else if(Character.isAlphabetic(c.charAt(0))){
                String varName = c + tokenizer.nextToken(' ');

                top.addBlock(new BlockPushNative(varName));
                top.addBlock(new BlockGetVariable(top.getScope()));
            }

        }

    }

    private Block parseLiteral(BlockyEngine blockyEngine, Tokenizer stringTokenizer) throws CompilerException {

        String token = stringTokenizer.nextToken(1);

        if(token.charAt(0) == '"') {
            String string = stringTokenizer.nextToken('"');

            return new BlockPushNative(string);
        }else{
            BlockFunction math = new BlockFunction(blockyEngine.getScope());

            String equation = token + stringTokenizer.nextToken(';');

            parseMath(new Tokenizer(equation), math);

            return math;
        }


    }

    @Override
    public BlockyEngine parse() throws CompilerException {

        Tokenizer stringTokenizer = new Tokenizer(text);

        BlockyEngine blockyEngine = new BlockyEngine();

        while(stringTokenizer.hasMoreTokens()){

            String cmd = stringTokenizer.peek(1);

            if(cmd.isEmpty() || cmd.equals("\n") || cmd.equals(";")) {
                stringTokenizer.skip(1);
                continue;
            }

            cmd = stringTokenizer.nextToken(' ');

            if(cmd.equals("print")) {

                BlockPrintOut blockPrintOut = new BlockPrintOut();

                blockyEngine.addBlock(parseLiteral(blockyEngine, stringTokenizer));
                blockyEngine.addBlock(new BlockPushNative(1));
                blockyEngine.addBlock(blockPrintOut);

            }else if(Character.isAlphabetic(cmd.charAt(0))){//Variable declaration

                String variableName = cmd;

                Block block = parseLiteral(blockyEngine, stringTokenizer);

                blockyEngine.addBlock(block);
                blockyEngine.addBlock(new BlockPushNative(variableName));
                blockyEngine.addBlock(new BlockSetVariable(blockyEngine.getScope()));

            }else{
                throw new CompilerException("Unexpected token '"+cmd+"'");
            }

        }

        return blockyEngine;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("scripts/math.blocky");

        TxtBlockyParser txtBlockyParser = new TxtBlockyParser(file);

        BlockyEngine engine = txtBlockyParser.parse();

        engine.run();
    }

}
