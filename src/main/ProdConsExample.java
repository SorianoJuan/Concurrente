package main;

public class ProdConsExample{
    public static void main(String[] args){
        PetriNet pnet = new PetriNet
            (
             "./includes/prod_cons/incidense.csv",
             "./includes/prod_cons/marking.csv",
             "",
             "",
             ""
             );
        Monitor mon = new Monitor(pnet);
        mon.stopAfterTransitionsFired(1005);
        mon.setVerboseLevel(10);

        Task prod = new Task(new int[]{0, 2}, pnet, mon);
        Task cons = new Task(new int[]{1, 3}, pnet, mon);
        prod.setVerboseLevel(10);
        cons.setVerboseLevel(10);

        InvariantChecker check = new InvariantChecker
            (
             mon,
             2,
             new PInvariant(new int[]{0,2}, 1),
             new PInvariant(new int[]{1,3}, 1),
             new PInvariant(new int[]{4,5}, 4)
             );
        check.setVerboseLevel(10);

        ThreadGroup tg = new ThreadGroup("Task Threads");
        Thread th_prod = new Thread(tg, prod, "ProdThread");
        Thread th_cons = new Thread(tg, cons, "ConsThread");
        Thread th_check = new Thread(check, "CheckerThread");

        th_prod.start();
        th_cons.start();
        th_check.start();

        try{
            th_prod.join();
            th_cons.join();
            th_check.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
