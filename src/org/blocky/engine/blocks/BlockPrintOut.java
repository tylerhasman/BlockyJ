package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockPrintOut extends Block {

    @Override
    public void execute(Stack stack) {

        String combined = "";

        int numArgs = stack.pop();

        for(int i = 0; i < numArgs;i++){
            Object obj =  stack.pop().toString();

            combined += obj.toString();
        }

        System.out.println(combined);

    }

}
