import java.util.ArrayList;

public class Tren {
    private ArrayList<Lugar> Lugares;
    private Lugar currentPlace;
    private int indexPlace;
    private int personasMaquina;
    private int personasVagon;
    private int lugaresMaquina;
    private int lugaresVagon;

    public Tren(int lugaresMaquina, int lugaresVagon){
        this.Lugares = new ArrayList<>();
        this.Lugares.add(new Estacion());
        this.Lugares.add(new Estacion());
        this.Lugares.add(new Estacion());
        this.Lugares.add(new Estacion());
        this.indexPlace = 0;
        this.currentPlace = Lugares.get(indexPlace);
        this.lugaresMaquina = lugaresMaquina;
        this.lugaresVagon = lugaresVagon;
    }

    public void avanzar(){
        this.indexPlace = (this.indexPlace + 1) % this.Lugares.size();
    }

    public int subirMaquina() throws IllegalStateException{
        if (this.lugaresMaquina == 0){
            throw  new IllegalStateException("no hay mas lugar troesma");
        }

        this.lugaresMaquina -= 1;
        this.personasMaquina += 1;

        return this.lugaresMaquina;
    }

    public int bajarMaquina() throws IllegalStateException{
        if (this.personasMaquina == 0){
            throw  new IllegalStateException("no hay mas gente troesma");
        }

        this.lugaresMaquina += 1;
        this.personasMaquina -= 1;

        return this.lugaresMaquina;
    }

    public int subirVagon() throws IllegalStateException{
        if (this.lugaresVagon == 0){
            throw  new IllegalStateException("no hay mas lugar troesma");
        }

        this.lugaresVagon -= 1;
        this.personasVagon += 1;

        return this.lugaresVagon;
    }

    public int bajarVagon() throws IllegalStateException{
        if (this.personasVagon == 0){
            throw  new IllegalStateException("no hay mas gente troesma");
        }

        this.lugaresVagon += 1;
        this.personasVagon -= 1;

        return this.lugaresVagon;
    }
}
