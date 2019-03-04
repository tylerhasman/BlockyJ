package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

import java.util.Collections;

public class BlockWhile extends BlockFunction {

    private Block blockCondition;

    public BlockWhile(Block blockCondition, Scope scope) {
        super(scope);
        this.blockCondition = blockCondition;
    }

    @Override
    public void execute(Stack stack) throws Exception {
        blockCondition.execute(stack);

        boolean val = stack.pop();

        while(val){

            super.execute(stack);

            blockCondition.execute(stack);
            val = stack.pop();
        }
    }

}
