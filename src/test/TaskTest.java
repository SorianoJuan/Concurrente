import main.Monitor;
import main.PetriNet;
import main.Task;
import main.Transition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TaskTest{
    private PetriNet pnet;
    private Monitor mon;

    @Before
    public void setUp(){
        this.pnet = new PetriNet
            (
             "./includes/test/incidence.csv",
             "./includes/test/marking.csv",
             "./includes/test/transitions.csv",
             "./includes/test/policy.csv"
             );
        this.mon = new Monitor(this.pnet);
        Transition.resetId();
    }

    @Test
    public void ConstructorTest(){
        Task asd = new Task(new int[]{0,2,3}, this.pnet, this.mon);
        Transition[] expected = new Transition[]
            {
                this.pnet.getTransitionList().get(0),
                this.pnet.getTransitionList().get(2),
                this.pnet.getTransitionList().get(3),
            };
        assertArrayEquals(asd.getTransitionArray(), expected);
    }
}
