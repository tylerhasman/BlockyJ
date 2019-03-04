package org.blocky.engine.blocks;

import org.blocky.engine.BlockyEngine;
import org.blocky.engine.Scope;
import org.blocky.engine.Stack;
import org.blocky.exception.CompilerException;

import java.util.ArrayList;
import java.util.List;

public class BlockFunction extends ScopeBlock {

    public List<Block> blocks;
    private int cursor;

    public BlockFunction(Scope scope) {
        super(new Scope(scope));//Every function has its own scope
        blocks = new ArrayList<>();
        cursor = 0;
    }

    public void addBlock(Block block){
        blocks.add(block);
    }

    public void printBlock(List<Block> blocks, int tabs){
        String t = "";
        for(int i = 0; i < tabs;i++){
            t += "\t";
        }
        for(Block block : blocks){
            if(block instanceof BlockPushNative){
                if(((BlockPushNative) block).getObj() instanceof BlockFunction){
                    System.out.println(t + block.getClass().getSimpleName().replace("Block", "")+" ("+((BlockPushNative) block).getObj().toString()+")");
                    printBlock(((BlockFunction)((BlockPushNative) block).getObj()).blocks, tabs + 1);
                }else{
                    System.out.println(t + block.getClass().getSimpleName().replace("Block", "")+" ("+((BlockPushNative) block).getObj().toString()+")");
                }
            }else if(block instanceof BlockFunction){
                System.out.println(t + block.getClass().getSimpleName().replace("Block", ""));
                printBlock(((BlockFunction) block).blocks, tabs + 1);
            }else{
                System.out.println(t + block.getClass().getSimpleName().replace("Block", ""));
            }
        }
    }

    @Override
    public void execute(Stack stack) throws Exception {

        cursor = 0;

        while(cursor < blocks.size()){
            Block block = blocks.get(cursor);
            block.execute(stack);
            cursor++;
        }


    }

}
