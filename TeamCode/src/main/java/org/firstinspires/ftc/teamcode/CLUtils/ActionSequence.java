package org.firstinspires.ftc.teamcode.CLUtils;

import android.util.Log;

import com.qualcomm.robotcore.util.ElapsedTime;

public class ActionSequence extends AtomicAction{
    private int index=0;
    private AtomicAction[] actions;

    public ActionSequence(AtomicAction... actions){
        super();
        this.actions=actions;
    }
    @Override
    protected void internalExecute(Void unused){
        actions[index].execute.accept(null);
        if(actions[index].isComplete.get()){
            actions[index].end.accept(null);
            index++;
        }
    }

    @Override
    protected boolean internalIsComplete(){
        return index== actions.length;
    }
    @Override
    protected void internalEnd(Void unused){
        index=0;
        for (AtomicAction action:actions) {
            action.end.accept(null);
        }
    }

//    public boolean run(){
//        actions[index].
//        if(actions[index].get()){
//            Log.d("ActionsSequence","completed index: "+index);
//            index++;
//        }
//        if(index==actions.length){
//            index=0;
//            return true;
//        }
//        return false;
//    }

    public static class Wait{
        private ElapsedTime wait = new ElapsedTime();
        private double waitForMillis=0;

        private boolean done(){
            Log.d("Actions","waiting for "+waitForMillis);
            return waitForMillis<=wait.milliseconds();
        }

        public AtomicAction setTimer(double millis){
            waitForMillis=millis;
            return new ActionSequence(
                    new AtomicAction((unused)-> wait.reset()),
                    new AtomicAction(unused -> {},this::done)
            );
        }
    }
}
