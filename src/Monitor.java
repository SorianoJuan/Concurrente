package concurrency;

import java.util.ArrayList;
// import java.util.Collections;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor{
    private PetriNet PNet;

    private Condition[] ConditionQueue;
    private Condition CourtesyQueue;

    private Lock lock;

    public Monitor(PetriNet PNet){
        this.PNet = PNet;

        this.lock = new ReentrantLock();

        this.ConditionQueue = new Condition[this.PNet.getAmountTransitions()];
        for(int i = 0; i < this.PNet.getAmountTransitions(); i++) this.ConditionQueue[i] = lock.newCondition();

        this.CourtesyQueue = lock.newCondition();
    }

    public void exec(Transition t){
        // Monitor simple, usa "signal and continue"
        // no tiene cola de cortesia, los threads que
        // reciben signal tienen que pelear por el lock
        // con los threads de la cola de entrada
        this.lock.lock();
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
            next_t = this.PNet.getNextTransition();
            this.ConditionQueue[next_t.getId()].signal();
        }finally{
            this.lock.unlock();
        }
    }
}
