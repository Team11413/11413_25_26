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
    public final double[] dScale = new double[]{0,15,30,45,60,75,90};
    public final double[] aScale = new double[]{42,48,54,60,66,72,78};
    public final double[] fScale = new double[]{.42,.48,.54,.6,.66,.72,.78};
    private double target = 0;
    private double lastError=0;
    public double distanceToGoal = -1;
    public double cRPS = 0;
    private double tRPS = 0;
    private double pTicks=0;
    private double cTicks=0;
    private int ticksPerRevolution = 28;
    private double power = 0;
    public boolean enabled = false;
    public double p=0;
    public double i=0;
    public double d=0;
    private PIDF velPIDF = new PIDF(5,()-> p,()->i,()->d, this::getFeedForward);
    public ActionSequence shoot;
    public ActionSequence shootThree;
    public ParallelActions launch;
    public ParallelActions reset;
    public ParallelActions quickReset;
    public RaceActions atSpeed;
    private ActionSequence.Wait shotTimer = new ActionSequence.Wait();
    private ActionSequence.Wait spinUpTimer = new ActionSequence.Wait();

    private ShooterSystem(){
        //make this a singleton
        atSpeed = new RaceActions(new AtomicAction(this::atSpeed), spinUpTimer.setTimer(100));
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
        return Math.abs(lastError)<5;}

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
        if(loopTime>0) {
            cRPS = ((cTicks - pTicks) / ticksPerRevolution) / loopTime;
        }
        target = Utils.invScaledLerp(distanceToGoal,dScale);
        tRPS = Utils.scaledLerp(target,aScale,.001);
        lastError = tRPS-cRPS;
        power=0;
        if(enabled){
            power=velPIDF.calculate(lastError);
        }
        setPowers();


        tel.addLine("Distance to goal: "+ Utils.DoubleToString(distanceToGoal));
        tel.addLine("Rotations per second: "+Utils.DoubleToString(cRPS));
        tel.addLine("Target speed: "+ Utils.DoubleToString(tRPS));
        tel.addLine("Power: "+Utils.DoubleToString(power));

    }

    public void testUpdate(double loopTime, double goalDist){
        update(loopTime,goalDist);
        tel.addLine("Error: "+Utils.DoubleToString(lastError/ticksPerRevolution));
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
