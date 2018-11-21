package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Task implements Runnable{
    private Monitor mon;
    private Transition[] tarray;
    private int verbose;
    private String name;

    public Task(int[] indexs, PetriNet pnet, Monitor mon){
        this.tarray = Arrays.stream(indexs)
            .mapToObj(pnet.getTransitionList()::get).toArray(Transition[]::new);
        this.mon = mon;
        this.name = null;
    }

    public Task(String[] names, PetriNet pnet, Monitor mon, String TaskName){
        ArrayList<Transition> completeTlist = pnet.getTransitionList();
        ArrayList<Transition> auxTlist = new ArrayList<>();
        for(String name: names){
            auxTlist.add
                (completeTlist.stream() .filter(i -> name.equals(i.getName()))
                 .findFirst().orElse(null)
                 );
        }
        this.tarray = auxTlist.toArray(new Transition[0]);
        this.mon = mon;
        this.name = TaskName;
    }

    public String getName(){
        return this.name;
    }

    public Transition[] getTransitionArray(){
        return this.tarray;
    }
    public void setVerboseLevel(int lvl){this.verbose = lvl;}

    @Override
    public void run(){
        boolean keepGoing = true;
        while(keepGoing){
            for(Transition t: this.tarray){
                keepGoing = this.mon.exec(t);
                if(!keepGoing) break;
            }
        }
        if(verbose > 0)
            System.out.println
                (
                 "Msg From:"+ Thread.currentThread().getName()+
                 "\nThread terminado"
                 );
    }
}
