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

    private void printBlock(List<Block> blocks, int tabs){
        String t = "";
        for(int i = 0; i < tabs;i++){
            t += "\t";
        }
        for(Block block : blocks){
            if(block instanceof BlockPushNative){
                System.out.println(t + block.getClass().getSimpleName().replace("Block", "")+" ("+((BlockPushNative) block).getObj().toString()+")");
            }else if(block instanceof BlockFunction){
                printBlock(((BlockFunction) block).blocks, ++tabs);
            }else{
                System.out.println(t + block.getClass().getSimpleName().replace("Block", ""));
            }
        }
    }

    @Override
    public void execute(Stack stack) throws Exception {

        printBlock(blocks, 0);

        /*for(Block block : blocks){
            block.execute(stack);
        }*/

    }

}
