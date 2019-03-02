package org.blocky.engine.blocks;

import org.blocky.engine.Scope;

public abstract class ScopeBlock extends Block {

    private final Scope scope;

    public ScopeBlock(Scope scope){
        this.scope = scope;
    }

    public Scope getScope(){
        return scope;
    }

}
