package org.firstinspires.ftc.teamcode.CLUtils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class ParallelActions extends AtomicAction{
    private boolean remove=false;
    private int completed = 0;
    private ArrayList<AtomicAction> actions = new ArrayList<AtomicAction>();

    private ArrayList<Integer> toRemove = new ArrayList<Integer>();
    private ArrayList<Boolean> finished = new ArrayList<Boolean>();

    public ParallelActions(Boolean removeWhenFinished, AtomicAction... args){
        super();
        actions.addAll(Arrays.asList(args));
        for (AtomicAction action:actions) {
            finished.add(false);
        }
        remove=removeWhenFinished;
    }

    public void addAction(AtomicAction action){
        if(!actions.contains(action)){
            actions.add(action);
            finished.add(false);
        }
    }

    @Override
    protected void internalExecute(Void unused) {
        completed=0;
        for (int i = 0; i < actions.size(); i++) {
            if(!finished.get(i)){
                Log.d("ActionsParallel","Running Index "+i);
                actions.get(i).run();
                if(actions.get(i).isComplete.get()){
                    completed++;
                    finished.set(i,true);
                    toRemove.add(i);
                    Log.d("ActionsParallel","Index "+i+" Done");
                }else{
//                    Log.d("ActionsParallel","not Done");
                }
            }else{
                completed++;
            }
        }
        if(remove) {
            for (int tR:toRemove) {
                finished.remove(tR);
                actions.remove(tR).finish();
            }
            toRemove.clear();
        }
    }
    @Override
    protected boolean internalIsComplete(){
        return (remove && actions.isEmpty())||(!remove &&completed==actions.size());
    }

    @Override
    protected void internalEnd(Void unused) {
        if(remove){
            finished.clear();
            toRemove.clear();
            actions.clear();
            return;
        }
        for (int i = 0; i < finished.size(); i++) {
            finished.set(i,false);
        }
        for (AtomicAction action:actions) {
            action.end.accept(null);
        }
    }
}
