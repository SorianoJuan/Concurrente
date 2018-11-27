package main;

import java.util.ArrayList;

public class Main{
    public static void main(String[] args){
        PetriNet pnet = new PetriNet
            (
             "./includes/4_estaciones_FINAL/4_estaciones_FINAL_incidence.csv",
             "./includes/4_estaciones_FINAL/4_estaciones_FINAL_marking.csv",
             "./includes/4_estaciones_FINAL/4_estaciones_FINAL_inhibitor.csv",
             "./includes/4_estaciones_FINAL/4_estaciones_FINAL_time.csv",
             ""
             );
        Monitor mon = new Monitor(pnet);
        mon.stopAfterTransitionsFired(10000);
        mon.setVerboseLevel(10);

        InvariantChecker check = new InvariantChecker
            (
             mon,
             10000,
             new PInvariant(new int[]{6,8,31}, 1), // AUTOS A
             new PInvariant(new int[]{4,10,11}, 1), // AUTOS B
             new PInvariant(new int[]{27,36,42,37,28,43,30,35,7,39,32,44}, 1), // TREN
             new PInvariant(new int[]{38,24,25,26,29,18,1,17,19,12,14}, 100), // GENTE
             new PInvariant(new int[]{2,27}, 1), //ESTACION A
             new PInvariant(new int[]{0,28}, 1), //ESTACION B
             new PInvariant(new int[]{5,30}, 1), //ESTACION C
             new PInvariant(new int[]{3,32}, 1) //ESTACION D
             );
        check.setVerboseLevel(10);

        ArrayList<Task> TaskList = new ArrayList<>();

        TaskList.add(new Task
                     (
                      new String[] {
                          "BAJABAR_A",
                          "LEVANTABAR_A",
                          "LLEGATREN_B",
                          "SALETREN_B",
                          "LLEGATREN_C",
                          "SALETREN_C",
                          "BAJARBAR_C",
                          "LEVANTARBAR_C",
                          "LLEGATREN_D",
                          "SALETREN_D",
                          "LLEGATREN_A",
                          "SALETREN_A"
                      },
                      pnet,
                      mon,
                      "tren"
                      ));

        //No deberia ser un solo thread
        TaskList.add(new Task
                     (
                      new String[]{
                          "LLEGA_AUTO_A",
                          "AUTO_CRUZA_A",
                          "AUTO_SE_VA_A"
                      },
                      pnet,
                      mon,
                      "auto A"
                      ));

        //No deberia ser un solo thread
        TaskList.add(new Task
                     (
                      new String[]{
                          "LLEGA_AUTO_B",
                          "AUTO_CRUZA_B",
                          "AUTO_SE_VA_B"
                      },
                      pnet,
                      mon,
                      "auto B"
                      ));
        
        //Estacion A
        TaskList.add(new Task(new String[]{"LLEGAGENTE_A"}, pnet, mon, "LlegaGenteA"));
        TaskList.add(new Task(new String[]{"SUBIRMAQ"}, pnet, mon, "subeGenteMaqA"));
        TaskList.add(new Task(new String[]{"SUBIRVAG"}, pnet, mon, "subeGenteVagA"));
        TaskList.add(new Task(new String[]{"BAJARMAQ"}, pnet, mon, "bajaGenteMaqA"));
        TaskList.add(new Task(new String[]{"BAJARVAG"}, pnet, mon, "bajaGenteVagA"));
        TaskList.add(new Task(new String[]{"!GENTE_A"}, pnet, mon, "noMasGenteA"));
        TaskList.add(new Task(new String[]{"LLENO_A"}, pnet, mon, "noMasLugarA"));
        TaskList.add(new Task(new String[]{"10SEG_A"}, pnet, mon, "10SEG_A"));

        //Estacion B
        TaskList.add(new Task(new String[]{"LLEGAGENTE_B"}, pnet, mon, "LlegaGenteB"));
        TaskList.add(new Task(new String[]{"SUBEMAQ_B"}, pnet, mon, "subeGenteMaqB"));
        TaskList.add(new Task(new String[]{"SUBEVAG_B"}, pnet, mon, "subeGenteVagB"));
        TaskList.add(new Task(new String[]{"BAJAMAQ_B"}, pnet, mon, "bajaGenteMaqB"));
        TaskList.add(new Task(new String[]{"BAJAVAG_B"}, pnet, mon, "bajaGenteVagB"));
        TaskList.add(new Task(new String[]{"!GENTE_B"}, pnet, mon, "noMasGenteB"));
        TaskList.add(new Task(new String[]{"LLENO_B"}, pnet, mon, "noMasLugarB"));
        TaskList.add(new Task(new String[]{"10SEG_B"}, pnet, mon, "10SEG_B"));

        //Estacion C
        TaskList.add(new Task(new String[]{"LLEGAGENTE_C"}, pnet, mon, "LlegaGenteC"));
        TaskList.add(new Task(new String[]{"SUBEMAQ_C"}, pnet, mon, "subeGenteMaqC"));
        TaskList.add(new Task(new String[]{"SUBEVAG_C"}, pnet, mon, "subeGenteVagC"));
        TaskList.add(new Task(new String[]{"BAJAMAQ_C"}, pnet, mon, "bajaGenteMaqC"));
        TaskList.add(new Task(new String[]{"BAJAVAG_C"}, pnet, mon, "bajaGenteVagC"));
        TaskList.add(new Task(new String[]{"!GENTE_C"}, pnet, mon, "noMasGenteC"));
        TaskList.add(new Task(new String[]{"LLENO_C"}, pnet, mon, "noMasLugarC"));
        TaskList.add(new Task(new String[]{"10SEG_C"}, pnet, mon, "10SEG_C"));

        //Estacion D
        TaskList.add(new Task(new String[]{"LLEGAGENTE_D"}, pnet, mon, "LlegaGenteD"));
        TaskList.add(new Task(new String[]{"SUBEMAQ_D"}, pnet, mon, "subeGenteMaqD"));
        TaskList.add(new Task(new String[]{"SUBEVAG_D"}, pnet, mon, "subeGenteVagD"));
        TaskList.add(new Task(new String[]{"BAJAMAQ_D"}, pnet, mon, "bajaGenteMaqD"));
        TaskList.add(new Task(new String[]{"BAJAVAG_D"}, pnet, mon, "bajaGenteVagD"));
        TaskList.add(new Task(new String[]{"!GENTE_D"}, pnet, mon, "noMasGenteD"));
        TaskList.add(new Task(new String[]{"LLENO_D"}, pnet, mon, "noMasLugarD"));
        TaskList.add(new Task(new String[]{"10SEG_D"}, pnet, mon, "10SEG_D"));

        //OTROS
        TaskList.add(new Task(new String[]{"SUBIR_GENTE_MAQ"}, pnet, mon, "subeMaq"));
        TaskList.add(new Task(new String[]{"SUBIR_GENTE_VAG"}, pnet, mon, "subeVag"));
        TaskList.add(new Task(new String[]{"BAJAR_GENTE_MAQ"}, pnet, mon, "bajaMaq"));
        TaskList.add(new Task(new String[]{"BAJAR_GENTE_VAG"}, pnet, mon, "bajaVag"));
        


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
