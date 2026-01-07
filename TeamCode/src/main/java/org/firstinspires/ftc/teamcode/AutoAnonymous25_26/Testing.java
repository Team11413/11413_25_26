package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.CLUtils.ParallelActions;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

@TeleOp(name = "Testing", group = "Robot")

public class Testing extends OpMode {

    public int index = 0;
    public double increment = 0.01;
    public ShooterSystem ss = ShooterSystem.instance;
    public CommonRobot combot;

    private ParallelActions continuous;

    public Testing(){

    }

    @Override
    public void init() {
//        combot = CommonRobot.getCommonRobot(hardwareMap,telemetry);
        ss.init(hardwareMap,telemetry);
        Utils.resetLoopTimer(this::getRuntime);
        continuous=new ParallelActions(true,
                ()->{
                    ss.testUpdate(Utils.getLoopTime(), ss.dScale[index]);
                    ss.aScale[index] = ss.cRPS;
                    return false;
                },
                this::testPIDControls);
    }

    @Override
    public void loop() {
//        combot.update();
        continuous.run();
        telemetry.addLine("Current Distance: " + ss.dScale[index]);
        telemetry.addLine("Current RPS: " + ss.cRPS);
        telemetry.addLine("Current FF: " + ss.fScale[index]);
    }
    public boolean testPIDControls(){
        if(gamepad1.dpadRightWasPressed()){
            //cycle right on the scale

        }
        if(gamepad1.dpadLeftWasPressed()){
            //cycle left on the scale

        }
        if (gamepad1.dpadUpWasPressed()) {
            //raise Index
            index = (index + 1)%7;
        }
        if(gamepad1.dpadDownWasPressed()){
            //lower Index
            index = (index + 6)%7;
        }
        if(gamepad1.rightBumperWasPressed()){
            //apply increment up
            ss.fScale[index] = Math.min( Math.max( ss.fScale[index] + increment, 0), 1);
        }
        if(gamepad1.leftBumperWasPressed()){
            //apply increment down
            ss.fScale[index] = Math.min( Math.max( ss.fScale[index] - increment, 0), 1);
        }
        if(gamepad1.aWasPressed()){
            //reduce increment size
            increment = increment *10;
        }
        if(gamepad1.bWasPressed()){
            //increase increment size
            increment = increment /10;
        }
        if(gamepad1.xWasPressed()){
            continuous.addAction(ss.shoot::run);
        }
        return false;
    }
}
