package org.blocky.engine;

import org.blocky.engine.blocks.*;
import org.blocky.parser.TxtBlockyParser;

import java.io.*;
import java.util.Random;

public class BlockyEngine extends BlockFunction {

    private Stack stack;

    private OutputStream stdOut;
    private InputStream stdIn;

    private NonWritableScope definedFunctions;

    public BlockyEngine(){
        super(new NonWritableScope());
        stack = new Stack();

        stdOut = System.out;
        stdIn = System.in;

        definedFunctions = (NonWritableScope) getScope().parent();

        declareFunction(new BlockNativeFunction(getScope(), "print", new String[] {"string"}) {
            @Override
            public Object executeFunction(Object... params) {

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(BlockyEngine.this.stdOut));

                try {
                    writer.write(params[0].toString());
                    writer.write("\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "readnumber", new String[] {}) {
            @Override
            public Object executeFunction(Object... params) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(BlockyEngine.this.stdIn));
                return Integer.parseInt(reader.readLine());
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "random", new String[] {"range"}) {
            @Override
            public Object executeFunction(Object... params) {
                return new Random().nextInt((Integer) params[0]);
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "file", new String[] {"name"}) {
            @Override
            public Object executeFunction(Object... params) {
                return new File(params[0].toString());
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "readfile", new String[] {"file"}) {
            @Override
            public Object executeFunction(Object... params) throws IOException {
                File file = (File) params[0];

                String content = "";

                try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                    String line;

                    while((line = reader.readLine()) != null){
                        content += line + "\n";
                    }
                }

                return content;
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "eval", new String[] {"text"}) {
            @Override
            public Object executeFunction(Object... params) throws Exception {
                BlockyEngine blockyEngine = new TxtBlockyParser(params[0].toString()).parse();

                blockyEngine.run();

                return null;
            }
        });

    }

    public void setStdOut(OutputStream stdOut) {
        this.stdOut = stdOut;
    }

    public void setStdIn(InputStream stdIn) {
        this.stdIn = stdIn;
    }

    public void declareFunction(BlockDefinedFunction blockDefinedFunction){
        definedFunctions.setValue(blockDefinedFunction.getName(), blockDefinedFunction);
    }

    public void run() throws Exception {
        definedFunctions.lock();
        try{
            execute(stack);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Crash! "+stack.toString());
            System.err.println("Crash! "+getScope().toString());
        }

    }

    public void printOutCompiledCode(){
        printBlock(blocks, 0);
        for(String key : definedFunctions.keys()){
            System.out.println(key+"{");
            printBlock(((BlockDefinedFunction)definedFunctions.getValue(key)).blocks, 1);
            System.out.println("}");
        }
    }

}
