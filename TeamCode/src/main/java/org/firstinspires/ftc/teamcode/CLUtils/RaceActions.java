package org.firstinspires.ftc.teamcode.CLUtils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class RaceActions extends AtomicAction{
    private ArrayList<AtomicAction> actions = new ArrayList<AtomicAction>();
    private boolean complete = false;


    public RaceActions(AtomicAction... args) {
        super();
        actions.addAll(Arrays.asList(args));
    }

    public void addAction(AtomicAction action) {
        if (actions.contains(action)) {
            return;
        }
        actions.add(action);
    }

    @Override
    protected void internalExecute(Void unused) {
        for (int i = 0; i < actions.size(); i++) {
            actions.get(i).execute.accept(null);
            if (actions.get(i).isComplete.get()){
                complete=true;
            }
        }
    }

    @Override
    protected boolean internalIsComplete() {
        return complete;
    }

    @Override
    protected void internalEnd(Void unused) {
        for (AtomicAction action:actions) {
            action.finish();
        }
        complete=false;
    }

//    public boolean run() {
//        int count = 0;
//        for (AtomicAction action : actions) {
//            action.execute.accept(null);
//            if (action.isComplete.get()) {
//                Log.d("ActionsRace", count + " won the race");
//                return true;
//            }
//            Log.d("ActionsRace", count + " still racing");
//            count++;
//        }
//        return false;
//    }

}
