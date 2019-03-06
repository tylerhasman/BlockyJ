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

    public void rewind(int width){
        if(index - width < 0)
            throw new IndexOutOfBoundsException(index+" - "+width+" < 0");
        index -= width;
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

    //This method assumes we are already in brackets!
    public String parseBrackets(){
        int brackets = 1;

        String insideBrackets = "";

        while(brackets > 0){

            char nextToken = nextTokenSkipWhitespace().charAt(0);

            if(nextToken == '(')
                brackets++;
            else if(nextToken == ')')
                brackets--;

            insideBrackets += nextToken;
        }

        return insideBrackets.substring(0, insideBrackets.length()-1);//remove final brackets
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

    public String peekSkipWhitespace(){
        int index = 0;
        String sub = peek(1);
        while(sub.equals(" ") || sub.equals("\t")){
            index++;
            sub = peek(1 + index);
        }

        return sub.substring(sub.length()-1);
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
            sub = string.substring(index);
            index = string.length();
        }else{
            sub = nextToken(i - index);
            index++;
        }

        return sub;
    }

}
