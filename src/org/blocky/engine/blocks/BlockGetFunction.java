package org.blocky.engine.blocks;

import org.blocky.engine.NonWritableScope;
import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public class BlockGetFunction extends ScopeBlock {

    public BlockGetFunction(Scope scope) {
        super(scope);
    }

    @Override
    public void execute(Stack stack) {

        String varName = stack.pop();

        Scope scope = getScope();

        while(!(scope instanceof NonWritableScope)){
            scope = scope.parent();
        }

        stack.push(scope.getValue(varName));

    }
}
