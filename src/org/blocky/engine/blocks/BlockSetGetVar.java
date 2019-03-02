package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockSetGetVar extends ScopeBlock {
    public BlockSetGetVar(Scope scope) {
        super(scope);
    }

    @Override
    public void execute(Stack stack) throws Exception {
        String variableName = stack.pop();

        Object val = stack.pop();

        if(val instanceof Block){
            throw new IllegalStateException("Cannot set a variable to a block. "+val.getClass().getName());
        }

        getScope().setValue(variableName, val);
        stack.push(val);
    }
}
