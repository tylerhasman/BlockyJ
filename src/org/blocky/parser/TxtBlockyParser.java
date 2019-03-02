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

    private Block parseLiteral(BlockyEngine blockyEngine, StringTokenizer stringTokenizer) throws CompilerException {

        String token = stringTokenizer.nextToken("\n");

        while (token.startsWith(" "))
            token = token.substring(1);

        if(token.charAt(0) == '"'){

            String string = "";

            for(int i = 1; i < token.length();i++){
                char c = token.charAt(i);

                if(c == '"')
                    break;

                string += c;

            }

            return new BlockPushNative(string);
        }else{
            BlockFunction blockFunction = new BlockFunction(new Scope());

            blockFunction.addBlock(new BlockPushNative(token));
            blockFunction.addBlock(new BlockGetVariable(blockyEngine.getScope()));

            return blockFunction;
        }


    }

    @Override
    public BlockyEngine parse() throws CompilerException {

        StringTokenizer stringTokenizer = new StringTokenizer(text, " ");

        BlockyEngine blockyEngine = new BlockyEngine();

        while(stringTokenizer.hasMoreTokens()){

            String cmd = stringTokenizer.nextToken(" ");

            while(cmd.startsWith("\n"))
                cmd = cmd.substring(1);

            if(cmd.equals("print")) {

                BlockPrintOut blockPrintOut = new BlockPrintOut();

                blockyEngine.addBlock(parseLiteral(blockyEngine, stringTokenizer));
                blockyEngine.addBlock(new BlockPushNative(1));
                blockyEngine.addBlock(blockPrintOut);
            }else if(cmd.equals("set")){

                String variableName = stringTokenizer.nextToken(" ");

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
        File file = new File("scripts/hello_world.blocky");

        TxtBlockyParser txtBlockyParser = new TxtBlockyParser(file);

        BlockyEngine engine = txtBlockyParser.parse();

        engine.run();
    }

}
