package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockRunFunction extends Block{
    @Override
    public void execute(Stack stack) throws Exception {
        BlockFunction blockFunction = stack.pop();

        blockFunction.execute(stack);
    }
}
