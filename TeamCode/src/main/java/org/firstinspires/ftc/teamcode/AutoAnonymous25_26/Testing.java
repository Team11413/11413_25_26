package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.CLUtils.AtomicAction;
import org.firstinspires.ftc.teamcode.CLUtils.ParallelActions;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

@TeleOp(name = "Testing", group = "Robot")

public class Testing extends OpMode {

    public int index = 0;
    public double increment = 0.01;
    public ShooterSystem ss = ShooterSystem.instance;
    public CommonRobot combot;

    private ParallelActions continuous;

    public Testing() {

    }

    @Override
    public void init() {
//        combot = CommonRobot.getCommonRobot(hardwareMap,telemetry);
        ss.init(hardwareMap, telemetry);
        ss.enabled = true;
        Utils.resetLoopTimer(this::getRuntime);
        continuous = new ParallelActions(true,
                new AtomicAction((unused)->Utils.resetLoopTimer(this::getRuntime)),
                new AtomicAction((unused)->{
                    ss.testUpdate(Utils.getLoopTime(), ss.dScale[index]);
//                    ss.aScale[index] = ss.cRPS;
                    telemetry.addLine("increment: "+increment);
                },
                        ()->false),
                new AtomicAction(this::testFFControls));
    }

    @Override
    public void loop() {
//        combot.update();
        if (gamepad1.xWasPressed()) {
            continuous.addAction(ss.shoot);
        }
        continuous.run();
    }

    public boolean testFFControls() {
        if (gamepad1.dpadRightWasPressed()) {
            //cycle right on the scale

        }
        if (gamepad1.dpadLeftWasPressed()) {
            //cycle left on the scale

        }
        if (gamepad1.dpadUpWasPressed()) {
            //raise Index
            index = (index + 1) % 7;
        }
        if (gamepad1.dpadDownWasPressed()) {
            //lower Index
            index = (index + 6) % 7;
        }
        if (gamepad1.rightBumperWasPressed()) {
            //apply increment up
            ss.fScale[index] = Math.min(Math.max(ss.fScale[index] + increment, 0), 1);
        }
        if (gamepad1.leftBumperWasPressed()) {
            //apply increment down
            ss.fScale[index] = Math.min(Math.max(ss.fScale[index] - increment, 0), 1);
        }
        if (gamepad1.aWasPressed()) {
            //reduce increment size
            increment = increment * 10;
        }
        if (gamepad1.bWasPressed()) {
            //increase increment size
            increment = increment / 10;
        }
        if(gamepad1.yWasPressed()){
            continuous.addAction(new AtomicAction(this::testPIDControls));
            index=0;
            return true;
        }

        telemetry.addLine("Current FF: " + ss.fScale[index]);
        return false;
    }

    public boolean testPIDControls(){
        if(gamepad1.dpadUpWasPressed()){
            //cycle PID
            index = (index + 1) % 3;
        }
        if (gamepad1.aWasPressed()) {
            //reduce increment size
            increment = increment * 10;
        }
        if (gamepad1.bWasPressed()) {
            //increase increment size
            increment = increment / 10;
        }
        if (gamepad1.rightBumperWasPressed()) {
            //apply increment up
            switch (index){
                case 0: ss.p+=increment;
                break;
                case 1: ss.i+=increment;
                break;
                case 2: ss.d+=increment;
                break;
            }
        }
        if (gamepad1.leftBumperWasPressed()) {
            //apply increment down
            switch (index){
                case 0: ss.p-=increment;
                    break;
                case 1: ss.i-=increment;
                    break;
                case 2: ss.d-=increment;
                    break;
            }
        }
        if(gamepad1.yWasPressed()){
            continuous.addAction(new AtomicAction(this::testFFControls));
            return true;
        }
        switch(index){
            case 0:
                telemetry.addLine("P: "+ss.p);
                telemetry.addLine("i: "+ss.i);
                telemetry.addLine("d: "+ss.d);
                break;
            case 1:
                telemetry.addLine("p: "+ss.p);
                telemetry.addLine("I: "+ss.i);
                telemetry.addLine("d: "+ss.d);
                break;
            case 2:
                telemetry.addLine("p: "+ss.p);
                telemetry.addLine("i: "+ss.i);
                telemetry.addLine("D: "+ss.d);
                break;
        }
        return false;
    }
}
