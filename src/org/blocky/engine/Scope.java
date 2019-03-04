package org.blocky.engine;

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private Scope parent;

    private Map<String, Object> values;

    public Scope(){
        this(null);
    }

    public Scope(Scope parent){
        this.parent = parent;
        values = new HashMap<>();
    }

    public Scope parent(){
        return parent;
    }

    @Override
    public String toString() {
        if(parent != null){
            return parent.toString() + values.toString();
        }
        return values.toString();
    }

    public boolean hasValue(String key){
        if(parent != null){
            return parent.hasValue(key) || values.containsKey(key);
        }

        return values.containsKey(key);
    }

    public boolean setValue(String key, Object value){
        if(values.containsKey(key)){
            values.put(key, value);
            return true;
        }else if(parent != null){
            if(!parent.setValue(key, value)){
                values.put(key, value);
                return true;
            }
        }else{
            values.put(key, value);
            return true;
        }
        return false;
    }

    public Object getValue(String key){
        if(!values.containsKey(key) && parent != null){
            return parent.getValue(key);
        }
        return values.get(key);
    }

}
