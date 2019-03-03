package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockIf extends BlockFunction {

    private boolean hasElse;

    public BlockIf(Scope scope) {
        super(scope);
        hasElse = false;
    }

    public void introduceElse() {
        hasElse = true;
    }

    public void execute(Stack stack) throws Exception {

        boolean value = stack.pop();

        if(value){
            super.execute(stack);
        }

        if(hasElse)
            stack.push(!value);

    }

}
