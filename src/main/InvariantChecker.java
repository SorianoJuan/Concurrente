package main;

import java.util.ArrayList;
import java.util.Collections;

public class InvariantChecker implements Runnable{
    private ArrayList<PInvariant> invList;
    private int time;
    private Monitor mon;
    private boolean keepGoing;

    public InvariantChecker(Monitor mon, int timems, PInvariant...invs){
        this.invList = new ArrayList<>();
        this.time = timems;
        this.mon = mon;
        this.keepGoing = true;
        Collections.addAll(invList, invs);
    }

    @Override
    public void run(){
        while(this.keepGoing){
            try{
                Thread.sleep(time);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            this.keepGoing = mon.checkNet(this.invList);
        }
    }
}
