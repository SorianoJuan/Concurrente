package concurrency;

public class Main{
    public static void main(String[] args){
        PetriNet pnet = new PetriNet();
        Monitor mon = new Monitor(pnet);
        Task inc = new Task(pnet.getTransitionList().get(0), mon);
        Task dec = new Task(pnet.getTransitionList().get(1), mon);
        Thread th_inc = new Thread(inc, "IncrementThread");
        Thread th_dec = new Thread(dec, "DecrementThread");

        th_inc.start();
        th_dec.start();

        try{
            th_inc.join();
            th_dec.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
