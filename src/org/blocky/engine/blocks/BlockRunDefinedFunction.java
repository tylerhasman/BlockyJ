package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

import java.util.Arrays;

public class BlockRunDefinedFunction extends Block {

    public static final int MAX_ARGS = 16;

    @Override
    public void execute(Stack stack) throws Exception {

        BlockDefinedFunction blockFunction = stack.pop();

        int args = stack.pop();

        if(blockFunction instanceof BlockNativeFunction){

            Object[] objects = new Object[args];

            for(int i = 0; i < args;i++){
                Object obj = stack.pop();

                objects[i] = obj;
            }

            Object returnValue = ((BlockNativeFunction) blockFunction).executeFunction(Arrays.copyOfRange(objects, 0, args));

            if(returnValue != null)
                stack.push(returnValue);

        }else{
            for(int i = 0; i < blockFunction.getHeader().length;i++){

                Object obj = stack.pop();

                blockFunction.getScope().setValue(blockFunction.getHeader()[i], obj);

            }

            blockFunction.execute(stack);
        }

    }
}
