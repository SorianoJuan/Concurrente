public class Monitor{
    private PetriNet pnet;
    //private politica

    public void exec(ArrayList<Integer> tlist){
        synchronized(pnet){
            //TODO: convertir vector
            trans = this.calculate(tlist); //calculate es metodo de RdP o monitor?
            if(trans != 0){
                pnet.disparar(trans);
            }else{
                this.encolar();
            }

        }
    }

}
