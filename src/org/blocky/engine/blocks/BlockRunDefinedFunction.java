package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockRunDefinedFunction extends Block{
    @Override
    public void execute(Stack stack) throws Exception {
        BlockDefinedFunction blockFunction = stack.pop();

        for(int i = 0; i < blockFunction.getHeader().length;i++){

            Object obj = stack.pop();

            blockFunction.getScope().setValue(blockFunction.getHeader()[i], obj);

        }

        blockFunction.execute(stack);
    }
}
