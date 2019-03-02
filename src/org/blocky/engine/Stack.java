package org.blocky.engine;

import org.blocky.engine.blocks.Block;

import java.util.ArrayList;
import java.util.List;

public class Stack {

    private List<Object> stack;

    public Stack(){
        stack = new ArrayList<>();
    }

    public void push(Object object){
        stack.add(object);
    }

    public <T> T pop(){
        if(stack.isEmpty()){
            throw new IllegalStateException("Cannot pop stack when it is empty!");
        }
        return (T) stack.remove(stack.size()-1);
    }

}
