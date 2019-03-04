package org.blocky.engine;

import java.util.Set;

public class NonWritableScope extends Scope{

    private boolean locked;

    public void lock(){
        locked = true;
    }

    @Override
    public boolean setValue(String key, Object value) {
        if(locked)
            return false;
        return super.setValue(key, value);
    }


    public Set<String> keys(){
        return values.keySet();
    }
}
