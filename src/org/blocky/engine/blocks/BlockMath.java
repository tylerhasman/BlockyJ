package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockMath extends Block {

    public static final int TYPE_ADD = 0, TYPE_SUB = 1, TYPE_MULT = 2, TYPE_DIV = 3;

    private final int type;

    public BlockMath(int type) {
        this.type = type;
    }

    @Override
    public void execute(Stack stack) {

        int one = stack.pop();
        int two = stack.pop();

        if(type == TYPE_ADD){
            stack.push(one + two);
        }else if(type == TYPE_SUB){
            stack.push(one - two);
        }else if(type == TYPE_MULT){
            stack.push(one  * two);
        }else if(type == TYPE_DIV){
            stack.push(one / two);
        }else{
            throw new IllegalArgumentException("Unknown arithmetic type "+type);
        }

    }

}
