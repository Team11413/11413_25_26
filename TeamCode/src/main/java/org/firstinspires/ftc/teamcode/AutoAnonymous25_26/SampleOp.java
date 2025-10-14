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
    // This declares the four motors needed
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;
    DcMotor [] DriveMotors = new DcMotor[4];
    int FL=0, FR=1, BL=2, BR=3;

    // This declares the IMU needed to get the current direction the robot is facing
    IMU imu;

    @Override
    public void init() {

        comBot = CommonRobot.getCommonRobot();
    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
//        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (gamepad1.a) {
            imu.resetYaw();
        }
        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)
//        if (gamepad1.left_bumper) {
//            drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
//        } else {
//            driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
//        }
        comBot.driveFieldRelative(-gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
    }

}
