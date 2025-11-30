package org.firstinspires.ftc.teamcode.CLUtils;

public class PID {
    double p;
    double i;
    double d;
    double []accumulated=new double[5];
    int pIndex=0;
    int sIndex=4;
    int tIndex=3;
    int qIndex=2;

    public PID(double proportion, double integral, double damping){
        p=proportion;
        i=integral;
        d=damping;
    }
    public double getPID(double error){
        accumulated[pIndex]=error;
        double intError= accumulated[0]+accumulated[1]+accumulated[2]+accumulated[3]+accumulated[4];
//        double derError= 2*error-3*accumulated[sIndex]+accumulated[tIndex];
        double derError= 3*error-4*accumulated[sIndex]+3*accumulated[tIndex]-accumulated[qIndex];
        qIndex=tIndex;
        tIndex=sIndex;
        sIndex=pIndex;
        pIndex=(pIndex+1)%5;
        return p*error+i*intError+d*derError;
    }

    public void zero() {
        for(int i=0;i<5;i++){
            accumulated[i]=0;
        }
    }
}
