package main;

import java.util.ArrayList;

public class Main{
    public static void main(String[] args){
        PetriNet pnet = new PetriNet
            (
             "./includes/2_estaciones_sintiempo/2_estaciones_sintiempo_incidence.csv",
             "./includes/2_estaciones_sintiempo/2_estaciones_sintiempo_marking.csv",
             "./includes/2_estaciones_sintiempo/2_estaciones_sintiempo_inhibitor.csv",
             ""
             );
        Monitor mon = new Monitor(pnet);
        mon.stopAfterTransitionsFired(10000);
        mon.setVerboseLevel(10);

        InvariantChecker check = new InvariantChecker
            (
             mon,
             10,
             new PInvariant(new int[]{4,16, 17}, 1),
             new PInvariant(new int[]{1,5,8,9,15,22,23,24,25}, 50)
             );
        check.setVerboseLevel(10);

        // Task tren = new Task(new int[]{3, 7, 8, 5, 10, 17}, pnet, mon);
        ArrayList<Task> TaskList = new ArrayList<>();

        TaskList.add(new Task
            (
             new String[]{"TrenSeVaDeA", "T1", "T2", "T0", "T3", "T7"},
             pnet,
             mon,
             "tren "
             ));

        //No deberia ser un solo thread
        TaskList.add(new Task
            (
             new String[]{"llegaauto_A", "pasonivel_auto", "sevaauto_A"},
             pnet,
             mon,
             "autos"
             ));

        //Estacion A
        TaskList.add(new Task(new String[]{"llegagente_A"}, pnet, mon, "LlegaGenteA"));
        TaskList.add(new Task(new String[]{"subirMAQ"}, pnet, mon, "subeGenteMaqA"));
        TaskList.add(new Task(new String[]{"subirVAG"}, pnet, mon, "subeGenteVagA"));
        TaskList.add(new Task(new String[]{"bajarMAQ"}, pnet, mon, "bajaGenteMaqA"));
        TaskList.add(new Task(new String[]{"bajarVAG"}, pnet, mon, "bajaGenteVagA"));
        TaskList.add(new Task(new String[]{"T10"}, pnet, mon, "noMasGenteA"));
        TaskList.add(new Task(new String[]{"T17"}, pnet, mon, "noMasLugarA"));

        //Estacion B
        TaskList.add(new Task(new String[]{"T4"}, pnet, mon, "LlegaGenteB"));
        TaskList.add(new Task(new String[]{"T5"}, pnet, mon, "subeGenteMaqB"));
        TaskList.add(new Task(new String[]{"T6"}, pnet, mon, "subeGenteVagB"));
        TaskList.add(new Task(new String[]{"T8"}, pnet, mon, "bajaGenteMaqB"));
        TaskList.add(new Task(new String[]{"T9"}, pnet, mon, "bajaGenteVagB"));
        TaskList.add(new Task(new String[]{"T12"}, pnet, mon, "noMasGenteB"));
        TaskList.add(new Task(new String[]{"T11"}, pnet, mon, "noMasLugarB"));

        ThreadGroup tg = new ThreadGroup("Task Threads");

        ArrayList<Thread> ThreadList = new ArrayList<>();
        Thread TaskThread;
        for(Task task: TaskList){
            TaskThread = new Thread(tg, task, "Th-"+task.getName());
            TaskThread.start();
            ThreadList.add(TaskThread);
        }

        Thread th_check = new Thread(check, "CheckerThread");
        th_check.start();

        try{
            for(Thread th: ThreadList)
                th.join();
            th_check.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
