package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockWhile extends BlockFunction {

    private BlockCondition blockCondition;

    public BlockWhile(BlockCondition blockCondition, Scope scope) {
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
