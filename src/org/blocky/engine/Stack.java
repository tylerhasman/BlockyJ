package org.blocky.engine;

import org.blocky.engine.blocks.Block;

import java.util.ArrayList;
import java.util.List;

public class Stack {

    private List<Object> stack;

    public Stack(){
        stack = new ArrayList<>();
    }

    public <T> T peek(){
        if(stack.isEmpty()){
            throw new IllegalStateException("Cannot peek stack when it is empty!");
        }

        return (T) stack.get(stack.size() - 1);
    }

    public void push(Object object){
        if(object == null)
            throw new IllegalArgumentException("Cannot push null to stack");
        stack.add(object);
    }

    public <T> T pop(){
        if(stack.isEmpty()){
            throw new IllegalStateException("Cannot pop stack when it is empty!");
        }
        T t = (T) stack.remove(stack.size()-1);

        return t;
    }

}
