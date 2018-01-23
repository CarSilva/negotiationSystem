package Controling_time;

public class ExchangeOpen implements Runnable{
    private boolean working;

    public ExchangeOpen(boolean working){
        this.working = working;
    }

    @Override
    public void run() {
        this.working = true;
    }
}
