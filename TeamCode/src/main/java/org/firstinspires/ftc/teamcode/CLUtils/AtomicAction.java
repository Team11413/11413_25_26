package org.firstinspires.ftc.teamcode.CLUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AtomicAction {
    public Supplier<Boolean> isComplete;
    public Consumer<Void> execute;
    public Consumer<Void> end;

    protected AtomicAction(){
        isComplete=this::internalIsComplete;
        execute=this::internalExecute;
        end=this::internalEnd;
    }

    public AtomicAction(Consumer<Void> execute, Supplier<Boolean> isComplete, Consumer<Void> end){
        this.execute=execute;
        this.isComplete=isComplete;
        this.end=end;
    }
    public AtomicAction(Consumer<Void> execute, Supplier<Boolean> isComplete){
        this.execute=execute;
        this.isComplete=isComplete;
        this.end=this::internalEnd;
    }
    public AtomicAction(Consumer<Void> execute){
        this.execute=execute;
        this.isComplete=this::internalIsComplete;
        this.end=this::internalEnd;
    }
    public AtomicAction(Supplier<Boolean> isComplete){
        this.execute=this::internalExecute;
        this.isComplete=isComplete;
        this.end=this::internalEnd;
    }

    protected void internalExecute(Void unused) {}
    protected boolean internalIsComplete(){return true;}
    protected void internalEnd(Void unused){}

    public void run(){
        execute.accept(null);
    }
    public void finish(){
        end.accept(null);
    }
}
