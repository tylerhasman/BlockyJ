package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

import java.io.IOException;

public abstract class BlockNativeFunction extends BlockDefinedFunction {

    public BlockNativeFunction(Scope scope, String name, String[] header) {
        super(scope, name, header);
    }

    public abstract Object executeFunction(Object... params) throws Exception;

}
