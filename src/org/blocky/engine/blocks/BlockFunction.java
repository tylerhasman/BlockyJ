package org.blocky.engine.blocks;

import org.blocky.engine.Scope;
import org.blocky.engine.Stack;

import java.util.ArrayList;
import java.util.List;

public class BlockFunction extends ScopeBlock{

    private List<Block> blocks;

    public BlockFunction(Scope scope) {
        super(new Scope(scope));//Every function has its own scope
        blocks = new ArrayList<>();
    }

    public void addBlock(Block block){
        blocks.add(block);
    }

    @Override
    public void execute(Stack stack) throws Exception {

        for(Block block : blocks){
            block.execute(stack);
        }

    }

}
