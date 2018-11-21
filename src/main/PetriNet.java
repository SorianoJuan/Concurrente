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
import java.util.Random;
import java.util.concurrent.Callable;

public class PetriNet{

    private ArrayList<Transition> tlist;

    private RealVector transitions;
    private RealVector prev_transitions;
    private RealVector timestamps;
    private RealMatrix intervals;

    private RealVector marking;
    private RealMatrix incidence;
    private RealMatrix inhibition;
    private RealMatrix policy;

    private Random rand;
    private Callable<Transition> nextTransitionMethod;

    public PetriNet(String incidenceFile, String markingFile, String inhibitionFile, String timeFile, String policyFile) {
        this.incidence = parseFile(incidenceFile);
        this.marking = parseFile(markingFile).getRowVector(1);
        if(inhibitionFile != null && !inhibitionFile.isEmpty()){
            this.inhibition = parseFile(inhibitionFile);
        }else{
            this.inhibition = MatrixUtils.createRealMatrix
                (
                 this.incidence.getRowDimension(),
                 this.incidence.getColumnDimension()
                 );
        }
        if(timeFile != null && !timeFile.isEmpty()){
            this.intervals = parseFile(timeFile);
        }else{
            this.intervals = MatrixUtils.createRealMatrix
                (
                 this.incidence.getColumnDimension(),
                 2
                 );
            this.intervals.setColumnVector
                (
                 1,
                 new ArrayRealVector(this.intervals.getRowDimension(), Double.MAX_VALUE)
                 );
        }
        if(policyFile != null && !policyFile.isEmpty()){
            this.policy = parseFile(policyFile);
            this.rand = null;
            this.nextTransitionMethod = this::getNextPriorityTransition;
        }else{
            this.policy = null;
            this.rand = new Random();
            this.nextTransitionMethod = this::getNextRandomTransition;
        }
        this.transitions = this.generateSensibilizedTransitionsVector();
        this.prev_transitions = new ArrayRealVector(this.transitions.getDimension());
        this.timestamps = new ArrayRealVector(this.transitions.getDimension(), (double)System.currentTimeMillis());
        this.policy = MatrixUtils.createRealIdentityMatrix(this.transitions.getDimension());
        this.tlist = generateTransitionList(incidenceFile);
    }

    public ArrayList<Transition> getTransitionList(){
        return this.tlist;
    }

    public int getAmountTransitions(){
        return tlist.size();
    }

    public boolean isReady(Transition t){
        return (this.getReadyTransitionsVector().getEntry(t.getId())==1.);
    }
    public boolean isSensibilized(Transition t){
        return (this.transitions.getEntry(t.getId())==1.);
    }

    public Transition getNextTransition(){
        Transition t = null;
        try {
            t = this.nextTransitionMethod.call();
        } catch(Exception e){
            System.out.println("Error: Error de entrada/salida");
            System.exit(-1);
        }
        return t;
    }

    private Transition getNextRandomTransition(){
        RealVector readyTransitions = this.getReadyTransitionsVector();
        int dim = readyTransitions.getDimension();
        int r=0;
        for(int i = 0; i < dim; i++){
            r = this.rand.nextInt(dim);
            if(readyTransitions.getEntry(r) == 1) break;
        }
        return this.tlist.get(r);
    }

    private Transition getNextPriorityTransition(){
        RealVector aux = this.policy.operate(this.getReadyTransitionsVector());
        int i;
        for(i = 0; i<aux.getDimension(); i++){
            if(aux.getEntry(i) > 0) break;
        }
        aux.set(0.);
        aux.setEntry(i, 1.);
        aux = this.policy.transpose().operate(aux);
        return tlist.get(aux.getMaxIndex());
    }

    public void trigger(Transition t){
        RealVector triggeredTransition = new ArrayRealVector(this.transitions.getDimension());
        triggeredTransition.setEntry(t.getId(), 1.);
        this.marking = this.marking.add(this.incidence.operate(triggeredTransition));
        this.prev_transitions = this.transitions;
        this.transitions = this.generateSensibilizedTransitionsVector();
        this.updateTimeStamps();
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
            for (String item : items) transitionList.add(new Transition(item));
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

    private void updateTimeStamps(){
        double now = (double)System.currentTimeMillis();
        StepFunction PosToOne = new StepFunction(new double[]{0., 1}, new double[]{0., 1.});
        RealVector newSensibilizedTransitions = this.transitions.subtract(this.prev_transitions).map(PosToOne);
        this.timestamps = this.timestamps.subtract(newSensibilizedTransitions.ebeMultiply(this.timestamps.mapSubtract(now)));
    }

    private RealVector getReadyTransitionsVector(){
        StepFunction negToZeroElseOne = new StepFunction(new double[]{-1., 0.}, new double[]{0., 1.});
        RealVector nowVector = new ArrayRealVector(this.transitions.getDimension(), (double)System.currentTimeMillis());
        RealVector transcurredTime = nowVector.subtract(this.timestamps);
        RealVector moreThanAlpha = transcurredTime.subtract(this.intervals.getColumnVector(0)).map(negToZeroElseOne);
        RealVector lessThanBeta = this.intervals.getColumnVector(1).subtract(transcurredTime).map(negToZeroElseOne);
        return this.transitions.ebeMultiply(lessThanBeta.ebeMultiply(moreThanAlpha));
    }


    private RealVector generateSensibilizedTransitionsVector(){
        /*
         * Este codigo seria mucho mas lindo si esta libreria
         * tuviera mas operaciones matriciales. Pero solo tiene
         * las funciones necesarias en vectores, asi que se trabaja
         * columna a columna en un bucle.
         *
         * La idea es la siguiente:
         *
         * 1)Se arma una matriz extendiendo el vector de marcado
         * hasta que tenga una dimension = matriz de incidencia
         * 2) Se suman ambas matrices, si alguna columna de
         * la matriz resultante tiene algun valor negativo quiere
         * decir que que no habian suficientes tokens para disparar
         * esa transicion. Entonces se mapean todos los valores < 0
         * a 0 y todos los valores >= 0 a 1 y se reducen las columnas
         * de la matriz haciendo producto entre sus elementos, el vector
         * que se obtiene indica con un 1 las transiciones que se pueden
         * disparar.
         *
         * Inhibidores:
         * 1) Se extiende el vector de marcado igual que en el caso anterior
         * pero ahora todos los valores > 0 se mapean a 1.
         * 2) Se multiplica elemento a elemento esa matriz con la matriz de
         * inhibidores (C).
         * 3) Se toma la matriz resultado de la suma y mapeo explicada para el
         * caso sin inhibidores (B) y se calcula A = B*(1-C)
         * 4) Se hace la misma reduccion explicada mas arriba y se optiene el
         * vector de transiciones que se pueden disparar.
         */
        Product prod = new Product();

        StepFunction negToZeroElseOne = new StepFunction(new double[]{-1., 0.}, new double[]{0., 1.});
        StepFunction PosToOne = new StepFunction(new double[]{0., 1}, new double[]{0., 1.});

        RealVector result = new ArrayRealVector(this.incidence.getColumnDimension());
        RealVector ones = new ArrayRealVector(this.incidence.getColumnDimension(), 1.0);

        RealMatrix ExtendedMarking = this.marking.outerProduct(ones);
        RealVector MappedMarking = this.marking.map(PosToOne);

        RealMatrix allCasesMarking = ExtendedMarking.add(this.incidence);

        ones = new ArrayRealVector(this.incidence.getRowDimension(), 1.);
        for(int i=0; i<this.incidence.getColumnDimension(); i++){
            RealVector oneCaseMarking = allCasesMarking.getColumnVector(i);
            oneCaseMarking.mapToSelf(negToZeroElseOne);

            RealVector inhibitionMask = MappedMarking.ebeMultiply(this.inhibition.getColumnVector(i));

            oneCaseMarking = oneCaseMarking.ebeMultiply(ones.subtract(inhibitionMask));

            result.setEntry(i, prod.evaluate(oneCaseMarking.toArray(), 0, oneCaseMarking.getDimension()));
        }

        return result;
    }

    public boolean checkPInvariant(PInvariant inv){
        int[] plist = inv.getPlaceArray();
        int value = inv.getValue();
        RealVector vaux = new ArrayRealVector(this.incidence.getRowDimension());
        Arrays.stream(plist).forEach(i -> vaux.setEntry(i, 1.));
        return value == (int) vaux.dotProduct(this.marking);
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

    public RealMatrix getIncidenceMatrix(){
        return this.incidence;
   }
}
