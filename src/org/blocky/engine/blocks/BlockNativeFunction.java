package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

public abstract class BlockNativeFunction extends BlockDefinedFunction {

    public BlockNativeFunction(Scope scope, String name, String[] header) {
        super(scope, name, header);
    }

    public abstract Object executeFunction(Object... params);

}
