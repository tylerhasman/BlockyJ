package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockIfElse extends BlockFunction {

    private BlockFunction elseFunction;

    public BlockIfElse(Scope scope, BlockFunction elseFunction) {
        super(scope);
        this.elseFunction = elseFunction;
    }

    public void execute(Stack stack) throws Exception {

        boolean value = stack.pop();

        if(value){
            super.execute(stack);
        }else{
            elseFunction.execute(stack);
        }

    }

}
