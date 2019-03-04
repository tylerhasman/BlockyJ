package org.blocky.engine.blocks;

import org.blocky.engine.Stack;

public class BlockRunDefinedFunction extends Block {
    @Override
    public void execute(Stack stack) throws Exception {

        BlockDefinedFunction blockFunction = stack.pop();

        if(blockFunction instanceof BlockNativeFunction){
            Object[] objects = new Object[blockFunction.getHeader().length];
            for(int i = 0; i < blockFunction.getHeader().length;i++){

                Object obj = stack.pop();

                objects[i] = obj;

            }

            Object returnValue = ((BlockNativeFunction) blockFunction).executeFunction(objects);

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
