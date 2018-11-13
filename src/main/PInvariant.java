package main;

public class PInvariant{
    private int[] placeArray;
    private int value;

    public PInvariant(int[] placeArray, int value){
        this.placeArray = placeArray;
        this.value = value;
    }

    public int[] getPlaceArray(){
        return this.placeArray;
    }

    public int getValue(){
        return this.value;
    }
}
