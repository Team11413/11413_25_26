package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.CLUtils.ActionSequence;
import org.firstinspires.ftc.teamcode.CLUtils.AtomicAction;
import org.firstinspires.ftc.teamcode.CLUtils.PIDF;
import org.firstinspires.ftc.teamcode.CLUtils.ParallelActions;
import org.firstinspires.ftc.teamcode.CLUtils.RaceActions;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;


public class ShooterSystem{
    public static ShooterSystem instance = new ShooterSystem();
    private DcMotor shooter;
    public Servo ballRelease;
    private Telemetry tel;
    /*
    dScale are a set of chosen distances from the goal.
    aScale is a set of angular velocities that are found to shoot consistently at the chosen distances
    aScale can be prepopulated with calculated values, to at least be close to the ideal.
    fScale are the power settings that will eventually stabilize at the found velocities
     */
    public final double[] dScale = new double[]{0,40,70,90,110,130,150};
    public final double[] aScale = new double[]{48,52,56,62,66,72,78};
    public final double[] fScale = new double[]{.58,.60,.64,.66,.68,.72,.78};
    private double target = 0;
    private double lastError=0;
    public double distanceToGoal = -1;
    public double cRPS = 0;
    private double tRPS = 0;
    private double pTicks=0;
    private double cTicks=0;
    private int ticksPerRevolution = 28;
    private double power = 0;
    public double speedAdjust=0;
    public boolean enabled = false;
    public double p=0;
    public double i=0;
    public double d=0;
    private PIDF velPIDF = new PIDF(5,()-> p,()->i,()->d, this::getFeedForward);
    private double averageError=0;
    private double allowedErrorPercent=.1;
    public ActionSequence shoot;
    public ActionSequence shootThree;
    public ParallelActions launch;
    public ParallelActions reset;
    public ParallelActions quickReset;
    public RaceActions atSpeed;
    private ActionSequence.Wait shotTimer = new ActionSequence.Wait();
    private ActionSequence.Wait spinUpTimer = new ActionSequence.Wait();

    private double[] looptimes = new double[]{0,0,0,0,0};
    private double[] rotations = new double[]{0,0,0,0,0};
    private double[] errors = new double[]{0,0,0,0,0};
    private int averageindex=0;
    private ShooterSystem(){
        //make this a singleton
        atSpeed = new RaceActions(new AtomicAction(this::atSpeed), spinUpTimer.setTimer(200));
        launch= new ParallelActions(false,new AtomicAction(this::shootflipper), shotTimer.setTimer(350));
        reset= new ParallelActions(false, new AtomicAction(this::readyflipper), shotTimer.setTimer(350));
        quickReset= new ParallelActions(false,reset,new AtomicAction(this::atSpeed));
        shoot= new ActionSequence(
                atSpeed,
                launch,
                reset
        );
        shootThree= new ActionSequence(
                atSpeed,
                launch,
                quickReset,
                launch,
                quickReset,
                launch,
                reset
        );
    }

    public boolean atSpeed(){
        Log.d("Actions","atSpeed");
        return Math.abs(averageError)<tRPS*allowedErrorPercent;}

    public void setPowers(double power){
        this.power=power;
        setPowers();
    }
    private void setPowers(){
        shooter.setPower(power);//this should be multiplied by 12/voltage to negate battery adjustments
    }

    public void init(HardwareMap hm, Telemetry tm){
        tel = tm;
        shooter = hm.get(DcMotor.class,"leftShooter");
        ballRelease = hm.get(Servo.class, "ballRelease");
    }

    public void update(double loopTime, double goalDist){
        distanceToGoal=goalDist;
        pTicks= cTicks;
        cTicks= -shooter.getCurrentPosition();
        cRPS=0;

        rotations[averageindex]= ((cTicks - pTicks) / ticksPerRevolution);
        looptimes[averageindex]= loopTime;
        averageindex=(averageindex+1)%5;
         double sumR=0;
         double sumL=0;
        for (int j = 0; j < 5; j++) {
        sumR+=rotations[j];
        sumL+=looptimes[j];
        }

        if(sumL>0) {
            cRPS = sumR/sumL;
        }
        target = Utils.invScaledLerp(distanceToGoal,dScale);
        tRPS = Utils.scaledLerp(target,aScale,.001)+speedAdjust;
        errors[averageindex]=tRPS-cRPS;
        lastError=0;
        for (double er:errors) {
            lastError+=er;
        }
        lastError=lastError/5;
        power=0;
        if(enabled){
            power=velPIDF.calculate(lastError);
            averageError=velPIDF.averageError()/ticksPerRevolution;
        }
        setPowers();


        tel.addLine("Distance to goal: "+ Utils.DoubleToString(distanceToGoal));
        tel.addLine("Rotations per second: "+Utils.DoubleToString(cRPS));
        tel.addLine("Target speed: "+ Utils.DoubleToString(tRPS));
        tel.addLine("Power: "+Utils.DoubleToString(power));

    }

    public void testUpdate(double loopTime, double goalDist){
        update(loopTime,goalDist);
        tel.addLine("Average Error: "+Utils.DoubleToString(averageError));
        if(Math.abs(lastError)<=tRPS*allowedErrorPercent){
            tel.addLine("Stable Speed found: "+(tRPS-lastError));
        }

    }

    public double getFeedForward() {
        return Utils.scaledLerp(Utils.invScaledLerp(distanceToGoal,dScale),fScale,.01);
    }

    public boolean readyflipper(){
        Log.d("Actions","readyFlipper");
        ballRelease.setPosition(0.9);
        return true;
    }
    public boolean shootflipper(){
        Log.d("Actions","shootFlipper");
        ballRelease.setPosition(0.7);
        return true;
    }

}
