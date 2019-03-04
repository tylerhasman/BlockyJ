package org.blocky.engine;

import org.blocky.engine.blocks.*;

import java.util.ArrayList;
import java.util.List;

public class BlockyEngine extends BlockFunction {

    private Stack stack;

    public BlockyEngine(){
        super(new Scope());
        stack = new Stack();
    }

    public void run() throws Exception {
        execute(stack);
    }

    public void printOutCompiledCode(){
        printBlock(blocks, 0);
    }

}
