package concurrency;

import java.util.concurrent.TimeUnit;

public class Task implements Runnable{
    private Monitor mon;
    private Transition t;

    public Task(Transition t, Monitor mon){
        this.t = t;
        this.mon = mon;
    }

    @Override
    public void run(){
        for(int i=0; i<20; i++){
            try{
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            System.out.println("Tratando de disparar transicion: "+this.t.getId());
            this.mon.exec(this.t);
        }
    }
}
