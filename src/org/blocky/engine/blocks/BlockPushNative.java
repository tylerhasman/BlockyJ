package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockPushNative extends Block {

    private final Object obj;

    public BlockPushNative(Object obj){
        this.obj = obj;
    }

    @Override
    public void execute(Stack stack) throws Exception {
        stack.push(obj);
    }
}
