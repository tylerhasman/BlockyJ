package org.blocky.engine.blocks;

import org.blocky.engine.Scope;

/**
 * Sub routines do not need to be run with BlockRunFunction as they are essentially unwrapped before runtime.
 * This helps to eliminate unnecessary blocks.
 */
public class BlockSubroutine extends BlockFunction {
    public BlockSubroutine(Scope scope) {
        super(scope);
    }
}
