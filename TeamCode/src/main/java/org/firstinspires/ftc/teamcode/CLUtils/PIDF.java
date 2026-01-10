package org.firstinspires.ftc.teamcode.CLUtils;

import java.util.Arrays;
import java.util.function.Supplier;

public class PIDF {
    /*
    Tune these values in order of f, p, d, i
    for Velocity tune the f so that it will eventually stabilize at the desired velocity after some time
    for position tune f so that friction does not slow it down, or so gravity does not cause it to drop
    then tune p to reach the target faster, until it just starts to oscillate
    then tune d to remove the oscillations
    only add i if the target is not being reached, or if you expect other external forces to apply.
     */
    public Supplier<Double> fGain;
    public Supplier<Double> pGain;
    public Supplier<Double> dGain;
    public Supplier<Double> iGain;



    private final double[] history;
    private int pIndex=1;
    private int sIndex=0;
    private int tIndex=0;
    private int qIndex=0;
    private double errorSum=0;

    public PIDF(int historyLength, Supplier<Double> p, Supplier<Double> i,Supplier<Double> d,Supplier<Double> f){
        pGain=p;
        iGain=i;
        dGain=d;
        fGain=f;
        history=new double[Math.max(historyLength,4)];
    }

    public void clear(){
        Arrays.fill(history, 0);
    }

    public double calculate(double error){
        history[pIndex]=error;
        errorSum= 0;
        for (double v : history) {
            errorSum += v;
        }
        double derError= 3*error-4*history[sIndex]+3*history[tIndex]-history[qIndex];
        qIndex=tIndex;
        tIndex=sIndex;
        sIndex=pIndex;
        pIndex=increment(pIndex);
        return pGain.get()*error+ iGain.get()*errorSum+ dGain.get()*derError+ fGain.get();
    }

    public double averageError(){
        return errorSum/history.length;
    }

    private int increment(int val){
        return (val+1)%history.length;
    }
}
