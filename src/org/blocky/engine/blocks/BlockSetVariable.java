package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockSetVariable extends ScopeBlock{

    public BlockSetVariable(Scope scope) {
        super(scope);
    }

    @Override
    public void execute(Stack stack) {

        String variableName = stack.pop();

        Object val = stack.pop();

        if(val instanceof Block){
            throw new IllegalStateException("Cannot set a variable to a block. "+val.getClass().getName());
        }

        getScope().setValue(variableName, val);

    }

}
