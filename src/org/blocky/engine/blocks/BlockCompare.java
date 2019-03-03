package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockCompare extends Block {

    public static final int TYPE_EQUALS = 0, TYPE_GT = 1, TYPE_GTE = 2, TYPE_LT = 3, TYPE_LTE = 4, TYPE_NOT_EQUALS = 5;

    private final int type;

    public BlockCompare(int type) {
        this.type = type;
    }

    @Override
    public void execute(Stack stack) {

        int j = stack.pop();
        int i = stack.pop();

        if(type == TYPE_EQUALS){
            stack.push(i == j);
        }else if(type == TYPE_GT){
            stack.push(i > j);
        }else if(type == TYPE_GTE){
            stack.push(i >= j);
        }else if(type == TYPE_LT){
            stack.push(i < j);
        }else if(type == TYPE_LTE){
            stack.push(i <= j);
        }else if(type == TYPE_NOT_EQUALS){
            stack.push(i != j);
        }else{
            throw new IllegalArgumentException("Unknown compare type "+type);
        }


    }
}
