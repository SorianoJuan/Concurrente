public class Estacion extends Lugar{
    private int genteEsperando = 0;

    public int subirGente() throws IllegalStateException{
        if (this.genteEsperando - 1 < 0){
            throw new IllegalStateException("que haces guachin");
        }
        this.genteEsperando -= 1;
        return genteEsperando;
    }

    public void llegaGente(){
        this.genteEsperando += 1;
    }

}
