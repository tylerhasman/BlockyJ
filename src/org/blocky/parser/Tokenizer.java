package org.blocky.parser;

public class Tokenizer {

    private int index;

    private String string;

    public Tokenizer(String str){
        string = str.replace("\n", "");
        index = 0;
    }

    public String getString() {
        return string;
    }

    public boolean hasMoreTokens(){
        return index < string.length();
    }

    public void skip(int width){
        if(index + width > string.length())
            throw new IndexOutOfBoundsException(index+" + "+width+" > "+string.length());

        index += width;
    }

    public String nextToken(int width){
        String sub = peek(width);

        index += width;

        return sub;
    }

    public String nextTokenSkipWhitespace(){
        String sub = peek(1);
        while(sub.equals(" ") || sub.equals("\t")){
            index++;
            sub = peek(1);
        }
        index++;

        return sub;
    }

    public String nextNumber(){
        String num = "";

        for(int i = index;i < string.length();i++){
            if(!Character.isDigit(string.charAt(i)))
                break;
            num += string.charAt(i);
            index++;
        }

        return num;
    }

    public String peek(int width){
        if(index + width > string.length())
            throw new IndexOutOfBoundsException(index+" + "+width+" > "+string.length());

        String sub = string.substring(index, index + width);

        return sub;
    }

    public String peekIfPossible(int width){
        if(index + width > string.length())
            return "";

        String sub = string.substring(index, index + width);

        return sub;
    }

    public String nextWord(){
        String num = "";

        for(int i = index;i < string.length();i++){
            if(!Character.isAlphabetic(string.charAt(i)))
                break;
            num += string.charAt(i);
            index++;
        }

        return num;
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
