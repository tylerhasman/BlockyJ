package org.blocky.parser;

public class Tokenizer {

    private int index;

    private String string;

    public Tokenizer(String str){
        string = str.replace("\n", "");
        index = 0;
    }

    public boolean hasMoreTokens(){
        return index < string.length();
    }

    public String nextToken(int width){
        if(index + width >= string.length())
            throw new IndexOutOfBoundsException(index+" + "+width+" > "+string.length());

        String sub = string.substring(index, index + width);

        index += width;

        return sub;
    }

    public String nextToken(char until){
        int i = string.indexOf(until, index);

        String sub;

        if(i == -1){
            sub = string.substring(index, string.length());
            index = string.length();
        }else{
            sub = nextToken(i - index);
            index++;
        }

        return sub;
    }

}
