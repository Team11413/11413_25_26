package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.CLUtils.ActionSequence;
import org.firstinspires.ftc.teamcode.CLUtils.PIDF;
import org.firstinspires.ftc.teamcode.CLUtils.SmartServo;
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
    private PIDF velPIDF = new PIDF(5,()-> 0.0,()->0.0,()->0.0, this::getFeedForward);
    public ActionSequence shoot;
    private ActionSequence.Wait shotTimer = new ActionSequence.Wait();

    private ShooterSystem(){
        //make this a singleton
        shoot= new ActionSequence(
                this::atSpeed,
                this::shootflipper,
                shotTimer.setTimer(100),
                this::readyflipper,
                shotTimer.setTimer(100)
        );
    }

    public boolean atSpeed(){return Math.abs(lastError)<5;}

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
        cTicks= shooter.getCurrentPosition();
        cRPS = ((cTicks-pTicks)/ticksPerRevolution)/loopTime;
        target = Utils.invScaledLerp(distanceToGoal,dScale);
        tRPS = Utils.scaledLerp(target,aScale,.001);
        lastError = tRPS-cRPS;
        power=velPIDF.calculate(lastError);


        tel.addLine("Distance to goal: "+ Utils.DoubleToString(distanceToGoal));
        tel.addLine("Rotations per second: "+Utils.DoubleToString(cRPS));
        tel.addLine("Target speed: "+ Utils.DoubleToString(tRPS));
        tel.addLine("Power: "+Utils.DoubleToString(power));
    }

    public void testUpdate(double loopTime, double goalDist){
        distanceToGoal=goalDist;
        pTicks= cTicks;
        cTicks= shooter.getCurrentPosition();
        cRPS = ((cTicks-pTicks)/ticksPerRevolution)/loopTime;
        setPowers(Utils.scaledLerp(Utils.invScaledLerp(goalDist, dScale), fScale, 0));
        tel.addLine("Distance to goal: "+ Utils.DoubleToString(distanceToGoal));
        tel.addLine("Rotations per second: "+Utils.DoubleToString(cRPS));
        tel.addLine("Target speed: "+ Utils.DoubleToString(tRPS));
        tel.addLine("Power: "+Utils.DoubleToString(power));
    }

    public double getFeedForward() {
        return Utils.scaledLerp(Utils.invScaledLerp(distanceToGoal,dScale),fScale,.01);
    }

    public boolean readyflipper(){
        ballRelease.setPosition(0.9);
        return true;
    }
    public boolean shootflipper(){
        ballRelease.setPosition(0.7);
        return true;
    }

}
