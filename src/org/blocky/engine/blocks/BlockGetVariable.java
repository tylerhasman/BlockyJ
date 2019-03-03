package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockGetVariable extends ScopeBlock {

    public BlockGetVariable(Scope scope) {
        super(scope);
    }

    @Override
    public void execute(Stack stack) {

        String varName = stack.pop();

        //System.out.println("Get var '"+varName+"' "+getScope().toString());

        stack.push(getScope().getValue(varName));

    }
}
