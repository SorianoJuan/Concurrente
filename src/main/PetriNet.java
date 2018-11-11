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

    private RealVector transitions;
    private RealVector marking;
    private RealMatrix incidence;
    private RealMatrix policy;

    public PetriNet(String incidenceFile, String markingFile, String transitionsFile, String policyFile) {
        this.incidence = parseFile(incidenceFile);
        this.marking = parseFile(markingFile).getRowVector(1);
        this.transitions = this.generateSensibilizedTransitionsVector();
        this.policy = MatrixUtils.createRealIdentityMatrix(this.transitions.getDimension());
        this.tlist = generateTransitionList(incidenceFile);
    }

    public ArrayList<Transition> getTransitionList(){
        return this.tlist;
    }

    public int getAmountTransitions(){
        return tlist.size();
    }

    public boolean isSensibilized(Transition t){
        return (this.transitions.getEntry(t.getId())==1.);
    }

    public Transition getNextTransition(){
        RealVector aux = this.policy.operate(this.transitions);
        int i = aux.getMaxIndex(); //TODO: revisar que siempre devuelva el primero
        aux.set(0.);
        aux.setEntry(i, 1.);
        aux = this.policy.transpose().operate(aux);
        return tlist.get(aux.getMaxIndex());
    }

    public void trigger(Transition t){
        RealVector triggeredTransition = new ArrayRealVector(this.transitions.getDimension());
        triggeredTransition.setEntry(t.getId(), 1.);
        this.marking = this.marking.add(this.incidence.operate(triggeredTransition));
        this.transitions = this.generateSensibilizedTransitionsVector();
    }

    public RealMatrix parseFile (String fileName){
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

    private RealVector generateSensibilizedTransitionsVector(){
        RealVector result = new ArrayRealVector(this.incidence.getColumnDimension());
        RealVector ones = new ArrayRealVector(this.incidence.getColumnDimension(), 1.0);
        RealMatrix auxMatrix = this.marking.outerProduct(ones);

        Product prod = new Product();
        auxMatrix = auxMatrix.add(this.incidence);
        for(int i=0; i<auxMatrix.getColumnDimension(); i++){
            RealVector auxVector = auxMatrix.getColumnVector(i);
            auxVector.mapToSelf(new StepFunction(new double[]{-1., 0.}, new double[]{0., 1.}));
            result.setEntry(i, prod.evaluate(auxVector.toArray(), 0, auxVector.getDimension()));
        }

        return result;
    }

    public RealVector getSensibilizedTransitionVector(){
        return this.transitions;
    }

    public RealVector getMarkingVector(){
        return this.marking;
    }

    public RealVector getTransitionsVector(){
        return this.transitions;
    }
}
