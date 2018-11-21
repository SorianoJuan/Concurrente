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
    private RealMatrix inhibition;
    private RealMatrix policy;

    public PetriNet(String incidenceFile, String markingFile, String inhibitionFile, String policyFile) {
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

        RealVector oneCaseMarking;
        RealVector inhibitionMask;
        RealVector result = new ArrayRealVector(this.incidence.getColumnDimension());
        RealVector ones = new ArrayRealVector(this.incidence.getColumnDimension(), 1.0);

        RealMatrix ExtendedMarking = this.marking.outerProduct(ones);
        RealVector MappedMarking = this.marking.map(PosToOne);

        RealMatrix allCasesMarking = ExtendedMarking.add(this.incidence);

        ones = new ArrayRealVector(this.incidence.getRowDimension(), 1.);
        for(int i=0; i<this.incidence.getColumnDimension(); i++){
            oneCaseMarking = allCasesMarking.getColumnVector(i);
            oneCaseMarking.mapToSelf(negToZeroElseOne);

            inhibitionMask = MappedMarking.ebeMultiply(this.inhibition.getColumnVector(i));

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
