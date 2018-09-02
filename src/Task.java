public class Task implements Runnable{
    private Monitor mon;
    private ArrayList<Integer> tlist;

    public Task(ArrayList<Integer> TList, Monitor mon){
        this.tlist = Tlist;
        this.mon = mon;
    }

    @Override
    public void run(){
        while(true){
            this.mon.exec(this.tlist);
        }
    }
}
