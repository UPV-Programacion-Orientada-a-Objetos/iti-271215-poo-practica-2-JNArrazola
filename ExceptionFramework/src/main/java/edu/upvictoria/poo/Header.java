package edu.upvictoria.poo;

/**
 * Class to represent the position and the name of the header, this with
 * the objective to store the name of the table and the index of it at the same
 * time to make the process of replacing easier
  */
public class Header {
    private final String name;
    private final int index;

    public Header(String name, int index){
        this.name = name;
        this.index = index;
    }

    public String getName(){
        return name;
    }

    public int getIndex(){
        return index;
    }
}
