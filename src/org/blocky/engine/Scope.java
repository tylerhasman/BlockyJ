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

    public void setValue(String key, Object value){
        values.put(key, value);
    }

    public Object getValue(String key){
        if(!values.containsKey(key) && parent != null){
            return parent.getValue(key);
        }
        return values.get(key);
    }

}
