package main;

import java.util.ArrayList;
import java.util.Collections;

public class InvariantChecker implements Runnable{
    private ArrayList<PInvariant> invList;
    private int time;
    private Monitor mon;
    private int verbose;

    public InvariantChecker(Monitor mon, int timems, PInvariant...invs){
        this.invList = new ArrayList<>();
        this.time = timems;
        this.mon = mon;
        Collections.addAll(invList, invs);
    }

    public void setVerboseLevel(int lvl){
        this.verbose = lvl;
    }

    @Override
    public void run(){
        boolean keepGoing = true;
        while(keepGoing){
            try{
                Thread.sleep(time);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            try{
                keepGoing = mon.checkNet(this.invList);
            } catch(VerifyError e){
                System.out.println
                    (
                     "Msg From: "+Thread.currentThread().getName()+
                     "\nERROR: Algun invariante no cumple la condicion"
                     );
                this.mon.stopAfterTransitionsFired(0);
                break;

            };
            if(this.verbose > 1)
                System.out.println
                    (
                     "Msg From: "+Thread.currentThread().getName()+
                     "\nInvariantes correctos"
                     );
        }
        if(this.verbose > 0)
            System.out.println
                (
                 "Msg From: "+Thread.currentThread().getName()+
                 "\nThread terminado"
                 );
    }
}
