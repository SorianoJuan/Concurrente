package main;

public class Main{
    public static void main(String[] args){
        PetriNet pnet = new PetriNet
            (
             "./includes/prod_cons/incidense.csv",
             "./includes/prod_cons/marking.csv",
             "",
             ""
             );
        Monitor mon = new Monitor(pnet);
        mon.stopAfterTransitionsFired(50);
        mon.setVerboseLevel(10);

        Task prod = new Task(new int[]{0, 2}, pnet, mon);
        Task cons = new Task(new int[]{1, 3}, pnet, mon);
        prod.setVerboseLevel(10);
        cons.setVerboseLevel(10);

        ThreadGroup tg = new ThreadGroup("Task Threads");
        Thread th_prod = new Thread(tg, prod, "ProdThread");
        Thread th_cons = new Thread(tg, cons, "ConsThread");

        th_prod.start();
        th_cons.start();

        try{
            th_prod.join();
            th_cons.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        if(
           pnet.checkPInvariant(new PInvariant(new int[]{0,2}, 1)) &&
           pnet.checkPInvariant(new PInvariant(new int[]{1,3}, 1)) &&
           pnet.checkPInvariant(new PInvariant(new int[]{4,5}, 4))
           ){
            System.out.println("Todo piola amiguero");
        }

        mon.stopAfterTransitionsFired(50); // disparamos 50 veces mas
        // tg = new ThreadGroup("Task Threads");
        th_prod = new Thread(tg, prod, "ProdThread");
        th_cons = new Thread(tg, cons, "ConsThread");

        th_prod.start();
        th_cons.start();

        try{
            System.out.println("Esperando Threads");
            th_prod.join();
            th_cons.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        if(
           pnet.checkPInvariant(new PInvariant(new int[]{0,2}, 1)) &&
           pnet.checkPInvariant(new PInvariant(new int[]{1,3}, 1)) &&
           pnet.checkPInvariant(new PInvariant(new int[]{4,5}, 4))
           ){
            System.out.println("Todo piola amiguero");
        }
    }
}
