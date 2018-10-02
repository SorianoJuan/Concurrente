public class Transition{
    private static int last_id = 0;
    private int id;
    private String name;

    public Transicion(String name){
        this.id = Transicion.last_id++;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }
}
