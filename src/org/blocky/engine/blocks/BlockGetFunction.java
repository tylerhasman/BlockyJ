package org.blocky.engine.blocks;

import org.blocky.engine.NonWritableScope;
import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

import java.awt.*;

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

        Object function = scope.getValue(varName);

        if(function == null)
            throw new IllegalArgumentException("Unknown function "+varName);

        stack.push(scope.getValue(varName));

    }
}
