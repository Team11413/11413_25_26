package org.firstinspires.ftc.teamcode.CLUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class ParallelActions {
    private boolean remove=false;
    private ArrayList<Supplier<Boolean>> actions = new ArrayList<Supplier<Boolean>>();

    private ArrayList<Integer> toRemove = new ArrayList<Integer>();
    private ArrayList<Boolean> finished = new ArrayList<Boolean>();

    public ParallelActions(Boolean removeWhenFinished, Supplier<Boolean>... args){
        actions.addAll(Arrays.asList(args));
        for (Supplier<Boolean> action:actions) {
            finished.add(false);
        }
        remove=removeWhenFinished;
    }

    public void addAction(Supplier<Boolean> action){
        if(actions.contains(action)){
            return;
        }
        actions.add(action);
        finished.add(false);
    }

    public boolean run(){
        int count=0;
        for (int i = 0; i < actions.size(); i++) {
            if(!finished.get(i)){
                if(actions.get(i).get()){
                    count++;
                    finished.set(i,true);
                    toRemove.add(i);
                }
            }else{
                count++;
            }
        }
        if(remove) {
            for (int i = toRemove.size() - 1; i > 0; i--) {
                int tR = toRemove.get(i);
                actions.remove(tR);
                finished.remove(tR);
            }
            toRemove.clear();
        }
        return (remove && actions.isEmpty())||(!remove &&count==actions.size());
    }
}
