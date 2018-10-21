package concurrency;

import java.util.ArrayList;

public class PetriNet{

    private ArrayList<Transition> tlist;

    private Integer [][] incidence;
    private Integer [][] marking;
    private Integer [][] transitions;
    private Integer [][] policy;

    public PetriNet(String incidenceFile, String markingFile, String transitionsFile, String policyFile){
        this.incidence = parseFile (incidenceFile);
        this.marking = parseFile (markingFile);
        this.transitions = parseFile (transitionsFile);
        this.policy = parseFile (policyFile);
        this.tlist = generateTransitionList(incidenceFile);
    }

    public ArrayList<Transition> getTransitionList(){
        return this.tlist;
    }

    public int getAmountTransitions(){
        return tlist.size();
    }

    public boolean isSensibilized(Transition t){
        return (transitions[t.getId()]==1);
    }

    public Transition getNextTransition(){
        Integer [][] aux = new Integer [transitions.length][transitions[0].length];
        aux = matmul(policy, transitions);
        int i = 0; 
        for (i=0; i<aux.length; i++){
            if (aux[i][0]==1){
                break;
            }
        }
        Arrays.fill (aux,0);
        aux[i][0] = 1;
        aux = matmul(Matrix.transpose(policy),aux);
        for (i=0; i<aux.length; i++){
            if (aux[i][0]==1){
                break;
            }
        }
        return tlist.get(i);
    }

    public void trigger(Transition t){
        Integer [t.length][t[0].length] triggeredTransition;
        Arrays.fill(triggeredTransition, 0);
        triggeredTransition [t.getId()][0] = 1;
        this.marking = Matrix.sum(marking, Matrix.matmul(incidence, triggeredTransition));
        // TODO: actualizar vectores de transiciones
    }

    public Integer[][] parseFile (String fileName){
        BufferedReader br = new BufferedReader (new FileReader(fileName));
        String line = br.readLine ();
        String [] items = line.split(",");
        int filas = Integer.parseInt (items [0]);
        int columnas = Integer.parseInt (items [1]);
        array = new int [filas][columnas];
        for (int i = 0; i< filas; i++){
            line = br.readLine ();
            items = line.split (",");
            for (int j = 0; j < columnas; j++){
                array [i][j] = Integer.parseInt (items[j]);
            }
        }
        br.close ();
        return array;
    }

    private ArrayList<Transition> generateTransitionList (String transitionFile){
        BufferedReader br = new BufferedReader (new FileReader(transitionFile));
        String line = br.readLine ();
        String [] items = line.split(",");
        transitionList = new ArrayList<Transition>;
        for (int i = 0; i< items.length(); i++){
            transitionList.add(new Transition (items[i]));
        }
        br.close ();
        return transitionList;
    }
}