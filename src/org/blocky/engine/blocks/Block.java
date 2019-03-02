package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public abstract class Block{

    public abstract void execute(Stack stack) throws Exception;

}
