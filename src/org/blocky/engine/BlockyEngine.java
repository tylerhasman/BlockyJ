package org.blocky.engine;

import org.blocky.engine.blocks.*;
import org.blocky.parser.TxtBlockyParser;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Arrays;
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

        declareFunction(new BlockNativeFunction(getScope(), "call", new String[] {"object", "methodName", "args"}) {
            @Override
            public Object executeFunction(Object... params) throws Exception {

                Object obj = params[0];
                String methodName = params[1].toString();

                for(Method method : obj.getClass().getMethods()){
                    if(method.getName().equals(methodName)){
                        if(method.getParameterCount() == params.length - 2){
                            try{
                                return method.invoke(obj, Arrays.copyOfRange(params, 2, params.length));
                            }catch(Exception e){
                            }
                        }
                    }
                }

                throw new IllegalArgumentException("No such method exists for "+obj+" "+methodName+" "+Arrays.toString(params));
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "native", new String[] {"className", "args"}) {
            @Override
            public Object executeFunction(Object... params) throws Exception {

                String className = params[0].toString();

                Class<?> clazz = Class.forName(className);

                Class<?>[] types = new Class[params.length-1];

                for(int i = 1; i < params.length;i++){
                    Class<?> paramClazz = params[i].getClass();

                    if(paramClazz.equals(Integer.class))
                        paramClazz = int.class;
                    if(paramClazz.equals(Boolean.class))
                        paramClazz = boolean.class;

                    types[i - 1] = paramClazz;
                }

                return clazz.getConstructor(types).newInstance(Arrays.copyOfRange(params, 1, params.length));
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
