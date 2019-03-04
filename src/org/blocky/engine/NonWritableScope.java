package org.blocky.engine;

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
}
