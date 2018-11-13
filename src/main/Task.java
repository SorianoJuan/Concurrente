package main;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Task implements Runnable{
    private Monitor mon;
    private Transition[] tarray;
    private boolean keepGoing;

    public Task(int[] indexs, PetriNet pnet, Monitor mon){
        this.tarray = Arrays.stream(indexs).mapToObj(pnet.getTransitionList()::get).toArray(Transition[]::new);
        this.mon = mon;
        this.keepGoing = true;
    }

    public Transition[] getTransitionArray(){
        return this.tarray;
    }

    @Override
    public void run(){
        while(this.keepGoing){
            for(Transition t: this.tarray){
                this.keepGoing = this.mon.exec(t);
            }
        }
    }
}
