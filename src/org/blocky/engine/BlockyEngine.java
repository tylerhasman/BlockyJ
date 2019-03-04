package org.blocky.engine;

import org.blocky.engine.blocks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BlockyEngine extends BlockFunction {

    private Stack stack;

    private Scanner input;

    public BlockyEngine(){
        super(new Scope());
        stack = new Stack();

        input = new Scanner(System.in);

        declareFunction(new BlockNativeFunction(getScope(), "print", new String[] {"string"}) {
            @Override
            public Object executeFunction(Object... params) {
                System.out.println(params[0]);
                return null;
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "readline", new String[] {}) {
            @Override
            public Object executeFunction(Object... params) {
                return input.nextLine();
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "readnumber", new String[] {}) {
            @Override
            public Object executeFunction(Object... params) {
                return input.nextInt();
            }
        });

        declareFunction(new BlockNativeFunction(getScope(), "random", new String[] {"range"}) {
            @Override
            public Object executeFunction(Object... params) {
                return new Random().nextInt((Integer) params[0]);
            }
        });

    }

    public void declareFunction(BlockNativeFunction nativeFunction){
        getScope().setValue(nativeFunction.getName(), nativeFunction);
    }

    public void run() throws Exception {
        execute(stack);
    }

    public void printOutCompiledCode(){
        printBlock(blocks, 0);
    }

}
