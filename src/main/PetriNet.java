package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PetriNet{

    private ArrayList<Transition> tlist;

    private int [][] incidence;
    private int [][] marking;
    private int [][] transitions;
    private int [][] policy;

    public PetriNet(String incidenceFile, String markingFile, String transitionsFile, String policyFile) {
        this.incidence = parseFile(incidenceFile);
        this.marking = parseFile(markingFile);
        //this.transitions = parseFile (transitionsFile); Hay que inicializarlo como dios manda
        this.policy = parseFile(policyFile);
        this.tlist = generateTransitionList(incidenceFile);
    }

    public ArrayList<Transition> getTransitionList(){
        return this.tlist;
    }

    public int getAmountTransitions(){
        return tlist.size();
    }

    public boolean isSensibilized(Transition t){
        return (transitions[t.getId()][0]==1);
    }

    public Transition getNextTransition(){
        int [][] aux = new int [transitions.length][transitions[0].length];
        aux = Matrix.matmul(policy, transitions);
        int i;
        for (i=0; i<aux.length; i++){
            if (aux[i][0]==1){
                break;
            }
        }
        Arrays.fill (aux,0);
        aux[i][0] = 1;
        aux = Matrix.matmul(Matrix.transpose(policy),aux);
        for (i=0; i<aux.length; i++){
            if (aux[i][0]==1){
                break;
            }
        }
        return tlist.get(i);
    }

    public void trigger(Transition t){
        int [][] triggeredTransition = new int [transitions.length][transitions[0].length];
        Arrays.fill(triggeredTransition, 0);
        triggeredTransition [t.getId()][0] = 1;
        this.marking = Matrix.sum(marking, Matrix.matmul(incidence, triggeredTransition));
        // TODO: actualizar vectores de transiciones
    }

    public int [][] parseFile (String fileName){
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine ();
            String [] items = line.split(",");
            items = Arrays.copyOfRange (items, 1, items.length); //Discarding first empty object
            int columnas = items.length ;
            ArrayList<int []> linelist = new ArrayList<> ();
            while ((line = br.readLine()) != null){
                items = line.split(",");
                items = Arrays.copyOfRange (items, 1, items.length); //Discarding first column
                linelist.add(Arrays.stream(items).mapToInt(Integer::parseInt).toArray());
            }
            int [][] array = linelist.toArray(new int [linelist.size()][columnas]);
            br.close ();
            return array;
        }
        catch(FileNotFoundException e){
            System.out.println("Error: Archivo "+fileName+" no encontrado.");
            System.exit(-1);
        }
        catch(IOException e){
            System.out.println("Error: Error de entrada/salida");
            System.exit(-1);
        }
        return null;
    }

    private ArrayList<Transition> generateTransitionList (String transitionFile){
        try{
            BufferedReader br = new BufferedReader (new FileReader(transitionFile));
            String line = br.readLine ();
            String [] items = line.split(",");
            items = Arrays.copyOfRange (items, 1, items.length); //Discarding first empty object
            ArrayList<Transition> transitionList = new ArrayList<>();
            for (int i = 0; i< items.length; i++){
                transitionList.add(new Transition (items[i]));
            }
            br.close ();
            return transitionList;
        }
        catch(FileNotFoundException e){
            System.out.println("Error: Archivo "+transitionFile+" no encontrado.");
            System.exit(-1);
        }
        catch(IOException e){
            System.out.println("Error: Error de entrada/salida");
            System.exit(-1);
        }
        return null;
    }
}
