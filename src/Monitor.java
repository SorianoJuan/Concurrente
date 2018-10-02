import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import Transition

public class Monitor{
    private PetriNet PNet;

    private Condition[] ConditionQueue;
    private Condition CortesyQueue;

    private Lock lock;

    public Monitor(PetriNet PNet){
        this.PNet = PNet;

        this.lock = new ReentrantLock();

        this.ConditionQueue = new Condition[this.PNet.getAmountTransitions()];
        for(int i = 0; i < this.ConditionQueue.size(); i++) this.ConditionQueue[i] = lock.newCondition();

        this.CortesyQueue = lock.newCondition();
    }
    public void exec(Transicion t){
        this.lock.lock();
        //TODO: implementar politica
        Transition next_t = this.PNet.getNextTransition();
        if(t.getId() == next_t.getId){
            this.PNet.trigger(t);
        }else{
            this.ConditionQueue[next_t.getId()].signal();
            this.ConditionQueue[t.getId()].await();
        }
        // while(trans == 0){
            
        // }
        // if(trans != 0){
        //     this.pnet.disparar(trans);
        // }else{
        //     this.encolar();
        // }
        
    }

}
