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

        Object one1 = stack.pop();
        Object two1 = stack.pop();

        System.out.println("MATH "+one1+" "+two1);

        int one = (int) one1;
        int two = (int) two1;

        if(type == TYPE_ADD){
            stack.push(one + two);
        }else if(type == TYPE_SUB){
            stack.push(two - one);
        }else if(type == TYPE_MULT){
            stack.push(one  * two);
        }else if(type == TYPE_DIV){
            stack.push(two / one);
        }else{
            throw new IllegalArgumentException("Unknown arithmetic type "+type);
        }

    }

}
