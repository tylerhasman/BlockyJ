package org.blocky.exception;

public class CompilerException extends Exception {

    public CompilerException(String msg){
        super(msg);
    }

    public CompilerException(Exception e){
        super(e);
    }

}
