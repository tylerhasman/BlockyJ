package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockIf extends BlockFunction {

    public BlockIf(Scope scope) {
        super(scope);
    }

    public void execute(Stack stack) throws Exception {

        boolean value = stack.pop();

        if(value){
            super.execute(stack);
        }

    }

}
