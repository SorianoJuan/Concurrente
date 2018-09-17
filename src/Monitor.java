import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor{
    private PetriNet pnet;

    private Condition[] ConditionQueue;
    private Integer[] ConditionQueueN;

    private Condition CortesyQueue;
    private int CortesyQueueN;

    //private Condition InputQueue; No haria falta?
    //private int InputQueueN;


    private Lock lock;
    //private politica

    public Monitor(PetriNet pnet){
        this.pnet = pnet;

        this.lock = new ReentrantLock();

        this.ConditionQueue = new Condition[this.pnet.getAmountTransitions()];
        for(int i = 0; i < this.ConditionQueue.size(); i++) this.ConditionQueue[i] = lock.newCondition();
        this.ConditionQueueN = new Integer[this.pnet.getAmountTransitions()]; //TODO: rellenar con 0

        this.CortesyQueue = lock.newCondition();
        this.CortesyQueueN = 0;

        //this.InputQueue = lock.newCondition();
        //this.InputQueueN = 0;
    }
    public void exec(ArrayList<Integer> tlist){
        synchronized(pnet){
            //TODO: convertir vector
            trans = this.calculate(tlist); //calculate es metodo de RdP o monitor?
            if(trans != 0){
                pnet.disparar(trans);
            }else{
                this.encolar();
            }

        }
    }

}
