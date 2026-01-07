package org.firstinspires.ftc.teamcode.CLUtils;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.function.Supplier;

public class ActionSequence {
    private int index=0;
    private Supplier<Boolean>[] actions;

    public ActionSequence(Supplier<Boolean>... actions){
        this.actions=actions;
    }

    public boolean run(){
        if(actions[index].get()){
            index++;
        }
        if(index==actions.length){
            index=0;
            return true;
        }
        return false;
    }

    public static class Wait{
        private ElapsedTime wait = new ElapsedTime();
        private double waitForMillis=0;

        private boolean done(){
            return waitForMillis<=wait.milliseconds();
        }

        public Supplier<Boolean> setTimer(double millis){
            return new ActionSequence(
                    ()->{
                        waitForMillis=millis;
                        wait.reset();
                        return true;
                    },
                    this::done
            )::run;
        }
    }
}
