package org.blocky.engine;

import org.blocky.engine.blocks.*;

import java.util.ArrayList;
import java.util.List;

public class BlockyEngine extends BlockFunction {

    private Stack stack;

    public BlockyEngine(){
        super(new Scope());
        stack = new Stack();

        declareFunction(new BlockNativeFunction(getScope(), "print", new String[] {"string"}) {
            @Override
            public Object executeFunction(Object... params) {
                System.out.println(params[0]);
                return null;
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
