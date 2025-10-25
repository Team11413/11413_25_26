package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

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

    double targetspeed = 0.5;
    boolean shooterenabled = false;

    @Override
    public void init() {

        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
//        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

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

        if (gamepad1.xWasPressed()) {
            comBot.ballRelease.setPosition(0);
        }

        if (gamepad1.bWasPressed()) {
            comBot.ballRelease.setPosition(1);
        }

        if (gamepad1.rightBumperWasPressed()){
            targetspeed+=.05;
            if (targetspeed>1){
                targetspeed=1;
            }
        }


        double setspeed=0;
        if(shooterenabled){
            setspeed=targetspeed;
        } else {
            setspeed = 0.5;
        }

        comBot.leftShooter.setPower(setspeed);
        comBot.rightShooter.setPower(setspeed);
        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)
//        if (gamepad1.left_bumper) {
//            drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
//        } else {
//            driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
//        }
        comBot.driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        for (int i = 0; i < 4; i++){
            telemetry.addLine("Motor " + i + " Encoder Count: " + comBot.DriveMotors[i].getCurrentPosition());
        }
        telemetry.addLine("Imu facing "+comBot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
    }

}
