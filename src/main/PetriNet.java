package main;

import org.apache.commons.math3.analysis.function.StepFunction;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.summary.Product;

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
    private RealVector transitionsVector;
    private RealMatrix incidenceMatrix;
    private RealVector markingVector;
    private int [][] policy;

    public PetriNet(String incidenceFile, String markingFile, String transitionsFile, String policyFile) {
        this.incidence = parseFile(incidenceFile);
        this.incidenceMatrix = parseFile_new(incidenceFile);
        this.marking = parseFile(markingFile);
        this.markingVector = parseFile_new(markingFile).getRowVector(1);
        this.transitionsVector = this.generateSensibilizedTransitionsVector();
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

    public RealMatrix parseFile_new (String fileName){
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine ();
            String [] items = line.split(",");
            items = Arrays.copyOfRange (items, 1, items.length); //Discarding first empty object
            int columnas = items.length ;
            ArrayList<double []> linelist = new ArrayList<> ();
            while ((line = br.readLine()) != null){
                items = line.split(",");
                items = Arrays.copyOfRange (items, 1, items.length); //Discarding first column
                linelist.add(Arrays.stream(items).mapToDouble(Double::parseDouble).toArray());
            }
            RealMatrix m = MatrixUtils.createRealMatrix(linelist.toArray(new double [linelist.size()][columnas]));
            br.close ();
            return m;
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

    public RealVector getSensibilizedTransitionVector(){
        return this.transitionsVector;
    }
    private RealVector generateSensibilizedTransitionsVector(){
        RealVector result = new ArrayRealVector(this.incidenceMatrix.getColumnDimension());
        RealVector ones = new ArrayRealVector(this.incidenceMatrix.getColumnDimension(), 1.0);
        RealMatrix auxMatrix = this.markingVector.outerProduct(ones);
        Product prod = new Product();
        auxMatrix = auxMatrix.add(this.incidenceMatrix);
        for(int i=0; i<auxMatrix.getColumnDimension(); i++){
            RealVector auxVector = auxMatrix.getColumnVector(i);
            auxVector.mapToSelf(new StepFunction(new double[]{-1., 0.}, new double[]{0., 1.}));
            result.setEntry(i, prod.evaluate(auxVector.toArray(), 0, auxVector.getDimension()));
        }

        return result;
    }
}
