import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import main.PetriNet ;
import main.Transition ;

public class PetriNetTest {
    private PetriNet petrinet;

    @Before
    public void setUp(){
        petrinet = new PetriNet
            (
             "./includes/test/incidence.csv",
             "./includes/test/marking.csv",
             "./includes/test/transitions.csv",
             "./includes/test/policy.csv"
             );
        Transition.resetId();
    }

    @Test
    public void parseFileTest(){
        RealMatrix expectedincidence = MatrixUtils.createRealMatrix
            (new double[][] {
                {-1., 0., 1., 0.},
                { 0.,-1., 0., 1.},
                { 1., 0.,-1., 0.},
                { 0., 1., 0.,-1.},
                { 0.,-1., 1., 0.},
                { 0., 1.,-1., 0.}
            });
/*
        int[][] expectedmarking = {
                { 1, 1, 0, 0, 0, 4}
        };

        int[][] expectedpolicy = {
                { 1, 0, 0, 0},
                { 0, 1, 0, 0},
                { 0, 0, 1, 0},
                { 0, 0, 0, 1}
        };
*/
        assertEquals(petrinet.getIncidenceMatrix(), expectedincidence);

    }

    @Test
    public void generateTransitionListTest(){
        ArrayList<Transition> testedlist = new ArrayList<>();

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

    @Test
    public void getNextTransitionTest(){
        //TODO: hacer un test con politica custom
        Transition expected = petrinet.getTransitionList().get(0);
        Transition obtained = petrinet.getNextTransition();

        assertEquals(expected, obtained);
    }

    @Test
    public void triggerTest(){
        RealVector expected_marking = MatrixUtils.createRealVector(new double[]{0., 1., 1., 0., 0., 4.});
        RealVector expected_transitions = MatrixUtils.createRealVector(new double[]{0., 0., 1., 0.});

        Transition t = petrinet.getTransitionList().get(0);
        petrinet.trigger(t);

        assertEquals(expected_marking, petrinet.getMarkingVector());
        assertEquals(expected_transitions, petrinet.getTransitionsVector());
    }

    @Test
    public void checkPInvariantTest(){
        assertTrue(this.petrinet.checkPInvariant(new int[]{1,5}, 5));
        assertTrue(this.petrinet.checkPInvariant(new int[]{1,0}, 2));
    }

}
