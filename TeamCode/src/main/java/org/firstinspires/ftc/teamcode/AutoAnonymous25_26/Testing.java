package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.AutoAnonymous25_26.CommonRobot;
import org.firstinspires.ftc.teamcode.AutoAnonymous25_26.ShooterSystem;

import java.util.ArrayList;

@TeleOp(name = "Testing", group = "Robot")

public class Testing extends OpMode {

    public int index = 0;
    public double scale = 0.01;
    public ShooterSystem ss = ShooterSystem.instance;
    public CommonRobot combot;

    public Testing(){

    }

    @Override
    public void init() {
        combot = CommonRobot.getCommonRobot(hardwareMap,telemetry);
        ss.init(hardwareMap,telemetry);

    }

    @Override
    public void loop() {
        combot.update();
        ss.testUpdate(.07, ss.dScale[index]);
        ss.aScale[index] = ss.cRPS;
        telemetry.addLine("Current Distance: " + ss.dScale[index]);
        telemetry.addLine("Current RPS: " + ss.cRPS);
        telemetry.addLine("Current FF: " + ss.fScale[index]);
        testPIDControls(gamepad1);
    }
    public void testPIDControls(Gamepad gp1){
        if(gp1.dpadRightWasPressed()){
            //cycle right on the scale
            ss.fScale[index] = Math.min( Math.max( ss.fScale[index] + scale, 0), 1);
        }
        if(gp1.dpadLeftWasPressed()){
            //cycle left on the scale
            ss.fScale[index] = Math.min( Math.max( ss.fScale[index] - scale, 0), 1);
        }
        if (gp1.dpadUpWasPressed()) {
            //raise Index
            index = (index + 1)%7;
        }
        if(gp1.dpadDownWasPressed()){
            //lower Index
            index = (index + 6)%7;

        }
        if(gp1.rightBumperWasPressed()){
            //apply increment up
        }
        if(gp1.leftBumperWasPressed()){
            //apply increment down
        }
        if(gp1.aWasPressed()){
            //reduce increment size
        }
        if(gp1.bWasPressed()){
            //increase increment size
        }
    }
}
