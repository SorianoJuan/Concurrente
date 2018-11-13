package main;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;

public class Monitor{
    private PetriNet PNet;
    private int count;
    private int verbose;
    private Runnable op;
    private Condition[] ConditionQueue;
    // private Condition CourtesyQueue;

    private Lock lock;

    public Monitor(PetriNet PNet){
        this.PNet = PNet;
        this.count = 0;
        this.op = () -> {};
        this.lock = new ReentrantLock();
        this.verbose = 0;

        this.ConditionQueue = new Condition[this.PNet.getAmountTransitions()];
        for(int i = 0; i < this.PNet.getAmountTransitions(); i++) this.ConditionQueue[i] = lock.newCondition();

        // this.CourtesyQueue = lock.newCondition();
    }

    public void stopAfterTransitionsFired(int number){
        this.count = number;
        this.op = () -> this.count--;
    }

    public void setVerboseLevel(int lvl){
        this.verbose = lvl;
    }

    public boolean checkNet(ArrayList<PInvariant> invList){
        this.lock.lock();
        for(PInvariant inv: invList){
            if(!this.PNet.checkPInvariant(inv)){
                this.lock.unlock();
                throw new VerifyError("No se cumple con un invariante");
            }
        }
        this.lock.unlock();
        return this.count >= 0;
    }

    public boolean exec(Transition t){
        // Monitor simple, usa "signal and continue"
        // no tiene cola de cortesia, los threads que
        // reciben signal tienen que pelear por el lock
        // con los threads de la cola de entrada
        this.lock.lock();
        // TODO: [!] Pensar bien este if, posible problema de concurrencia
        if(this.count < 0){
            for(Condition cond:ConditionQueue){
                cond.signalAll();
            }
            this.lock.unlock();
            return false;
        }
        try{
            Transition next_t;

            while(!this.PNet.isSensibilized(t)){
                next_t = this.PNet.getNextTransition();
                this.ConditionQueue[next_t.getId()].signal();
                try{
                    this.ConditionQueue[t.getId()].await();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            this.PNet.trigger(t);
            if(this.verbose > 0){
                System.out.println("Transicion disparada: "+t.getName());
            }
            this.op.run();
            next_t = this.PNet.getNextTransition();
            this.ConditionQueue[next_t.getId()].signal();
        }finally{
            this.lock.unlock();
        }
        return true;
    }
}
