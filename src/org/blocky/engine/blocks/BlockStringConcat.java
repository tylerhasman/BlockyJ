package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockStringConcat extends Block {

    @Override
    public void execute(Stack stack) throws Exception {
        Object one = stack.pop();
        Object two = stack.pop();

        stack.push(two.toString() + one.toString());
    }

}
