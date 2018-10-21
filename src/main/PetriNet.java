package main;

import java.util.ArrayList;

public class PetriNet{
    private ArrayList<Transition> tlist;
    private int counter;
    private int counter_aux;

    public PetriNet(){
        this.counter = 0;
        this.counter_aux = this.counter;
        this.tlist = new ArrayList<Transition>();
        this.tlist.add(new Transition("Incremento"));
        this.tlist.add(new Transition("Decremento"));
    }

    public ArrayList<Transition> getTransitionList(){
        return this.tlist;
    }

    public int getAmountTransitions(){
        return 2;
    }

    public boolean isSensibilized(Transition t){
        return (counter - counter_aux >= 2*t.getId());
    }

    public Transition getNextTransition(){
        if(counter - counter_aux >= 2){
            return this.tlist.get(1);
        }else{
            return this.tlist.get(0);
        }
    }

    public void trigger(Transition t){
        if(t.getId() == 0){
            this.counter++;
            System.out.println("Incrementando contador: "+this.counter);
        }else if(t.getId() == 1){
            this.counter_aux = --this.counter;
            System.out.println("Decrementando contador: "+this.counter);
        }
    }
}
