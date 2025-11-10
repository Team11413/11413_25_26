package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@TeleOp(name = "Robot: Field Relative Mecanum Drive", group = "Robot")
public class SampleOp extends OpMode {

    CommonRobot comBot;

    double targetspeed = 0.6;
    boolean shooterenabled = false;
    double openTime= .3;
    double closedTime=0;
    boolean isOpen=true;
    boolean ButtersMode=true;

    @Override
    public void init() {

        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);
        Log.d("sloth","What about this?");
    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
        double currentTime = getRuntime();
        comBot.update();

        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (gamepad1.aWasPressed()) {
            comBot.imu.resetYaw();
        }
        if (gamepad1.yWasPressed()){
            shooterenabled=!shooterenabled;
        }
        if (gamepad1.leftBumperWasPressed()){
            targetspeed-=.05;
            if (targetspeed<0){
                targetspeed=0;
            }
        }
        if (gamepad1.rightBumperWasPressed()){
            targetspeed+=.05;
            if (targetspeed>1){
                targetspeed=1;
            }
        }

        if (gamepad1.xWasPressed()) {
            comBot.ballRelease.setPosition(1);
            isOpen=true;
            closedTime = currentTime+openTime;
        }

        if (gamepad1.bWasPressed()||(isOpen&&closedTime<=currentTime)) {
            comBot.ballRelease.setPosition(.7);
            isOpen=false;
        }
        if (gamepad1.dpadUpWasPressed()){
            ButtersMode=!ButtersMode;
        }
        telemetry.addLine("Butter's Mode: "+ (ButtersMode?"on":"off"));


        double setspeed=0;
        if(shooterenabled){
            setspeed=targetspeed;
        }

        telemetry.addLine("Shooter Speed: "+setspeed);

        comBot.leftShooter.setPower(setspeed);
        comBot.rightShooter.setPower(setspeed);


        if(ButtersMode){
            comBot.drive(-gamepad1.left_stick_y*Math.abs(gamepad1.left_stick_y), gamepad1.left_stick_x*Math.abs(gamepad1.left_stick_x), gamepad1.right_stick_x*Math.abs(gamepad1.right_stick_x));
        }else{
            comBot.driveFieldRelative(-gamepad1.left_stick_y*Math.abs(gamepad1.left_stick_y), gamepad1.left_stick_x*Math.abs(gamepad1.left_stick_x), gamepad1.right_stick_x*Math.abs(gamepad1.right_stick_x));
        }

//        for (int i = 0; i < 4; i++){
//            telemetry.addLine("Motor " + i + " Encoder Count: " + comBot.DriveMotors[i].getCurrentPosition());
//        }
    }

}
