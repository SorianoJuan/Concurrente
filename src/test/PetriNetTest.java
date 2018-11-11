import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import main.PetriNet ;
import main.Transition ;

public class PetriNetTest {
    private PetriNet petrinet;

    private ArrayList<Transition> testedlist;

    @Before
    public void setUp(){
        petrinet = new PetriNet
            (
             "./includes/incidence.csv",
             "./includes/marking.csv",
             "./includes/transitions.csv",
             "./includes/policy.csv"
             );
        Transition.resetId();
    }

    @Test
    public void parseFileTest(){
        int[][] expectedincidence = {
                { -1, 0, 1, 0},
                { 0, -1, 0, 1},
                { 1, 0, -1, 0},
                { 0, 1, 0, -1},
                { 0, -1, 1, 0},
                { 0, 1, -1, 0}
        };
/*
        int[][] expectedmarking = {
                { 1, 1, 0, 0, 4}
        };

        int[][] expectedpolicy = {
                { 1, 0, 0, 0},
                { 0, 1, 0, 0},
                { 0, 0, 1, 0},
                { 0, 0, 0, 1}
        };
*/
        assertTrue(Arrays.deepEquals(petrinet.parseFile("./includes/incidence.csv"), expectedincidence));

    }

    @Test
    public void generateTransitionListTest(){
        testedlist = new ArrayList<>();

        testedlist.add(new Transition("Producir1"));
        testedlist.add(new Transition("Consumir1"));
        testedlist.add(new Transition("Producir2"));
        testedlist.add(new Transition("Consumir2"));

        assertEquals(testedlist, petrinet.getTransitionList());
    }

    @Test
    public void isSensibilizedTest(){
        assertTrue(petrinet.isSensibilized(petrinet.getTransitionList().get(0)));
    }

    @Test
    public void getAmountTransitionsTest(){
        assertEquals(petrinet.getAmountTransitions(),4);
    }

    @Test
    public void generateSensibilizedTransitionVectorTest(){
        RealVector expected = MatrixUtils.createRealVector(new double[]{1.0, 0.0, 0.0, 0.0});
        RealVector obtained = petrinet.getSensibilizedTransitionVector();
        assertEquals(expected, obtained);
    }

    // @Test
    // public void triggerTest(){
    //     assertTrue(true);
    //     //TODO: hacer testing de trigger
    // }

}
