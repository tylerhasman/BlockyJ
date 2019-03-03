package org.blocky.engine.blocks;

import org.blocky.engine.Scope;

public class BlockDefinedFunction extends BlockFunction{

    private final String name;
    private final String[] header;

    public BlockDefinedFunction(Scope scope, String name, String[] header) {
        super(scope);
        this.name = name;
        this.header = header;
    }

    public String[] getHeader() {
        return header;
    }

    public String getName() {
        return name;
    }
}
