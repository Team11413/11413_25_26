package org.firstinspires.ftc.teamcode.CLUtils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.AutoAnonymous25_26.CommonRobot;
import org.firstinspires.ftc.teamcode.AutoAnonymous25_26.ShooterSystem;

import java.util.ArrayList;

public class Testing extends OpMode {

    public ArrayList<double[]> scales = new ArrayList<double[]>();
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
        ss.update(.07,10);
    }

    public void testPIDControls(Gamepad gp1){
        if(gp1.dpadRightWasPressed()){
            //cycle right on the scale
        }
        if(gp1.dpadLeftWasPressed()){
            //cycle left on the scale
        }
        if(gp1.dpadUpWasPressed()){
            //cycle up through the scales
        }
        if(gp1.dpadDownWasPressed()){
            //cycle down through the scales
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
