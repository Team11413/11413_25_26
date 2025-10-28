package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class CommonRobot {
    public static CommonRobot INSTANCE;
    public DcMotor [] DriveMotors = new DcMotor[4];
    public DcMotor leftShooter;
    public DcMotor rightShooter;
    public Servo ballRelease;
    public IMU imu;

    Telemetry telemetry;
    int FL=0, FR=1, BL=2, BR=3;

    public static CommonRobot getCommonRobot(HardwareMap hardwareMap, Telemetry telemetry) {

        if (INSTANCE == null) {
            INSTANCE = new CommonRobot(hardwareMap, telemetry);
        }
     return INSTANCE;
    }
    public CommonRobot(HardwareMap hardwareMap, Telemetry tel) {
        telemetry = tel;

        DriveMotors[FL] = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        DriveMotors[FR] = hardwareMap.get(DcMotor.class, "driveFrontRight");
        DriveMotors[BL] = hardwareMap.get(DcMotor.class, "driveBackLeft");
        DriveMotors[BR] = hardwareMap.get(DcMotor.class, "driveBackRight");

        leftShooter = hardwareMap.get(DcMotor.class, "leftShooter");
        rightShooter = hardwareMap.get(DcMotor.class, "rightShooter");
        leftShooter.setDirection(DcMotor.Direction.REVERSE);
        rightShooter.setDirection(DcMotor.Direction.REVERSE);

        ballRelease = hardwareMap.get(Servo.class, "ballRelease");

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        DriveMotors[FL].setDirection(DcMotor.Direction.REVERSE);
        DriveMotors[BL].setDirection(DcMotor.Direction.REVERSE);

        // This uses RUN_USING_ENCODER to be more accurate.   If you don't have the encoder
        // wires, you should remove these
        for(int i = 0; i <4; i++){
            DriveMotors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        imu = hardwareMap.get(IMU.class, "imu");
        // This needs to be changed to match the orientation on your robot
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }

    public void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
//        double theta = Math.atan2(forward, right);
//        double r = Math.hypot(right, forward);
//
//        // Second, rotate angle by the angle the robot is pointing
//        theta = AngleUnit.normalizeRadians(theta -
//                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
//        telemetry.addLine("IMU angle theta: "+theta);
//
//        // Third, convert back to cartesian
//        double newForward = r * Math.sin(theta);
//        double newRight = r * Math.cos(theta);
        double theta = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        double newForward = forward*Math.cos(theta)-right*Math.sin(theta);
        double newRight = forward*Math.sin(theta)+right*Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    // Thanks to FTC16072 for sharing this code!!
    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double [] motorPowers = {
                forward + right + rotate,//FL
                forward - right - rotate,//FR
                forward - right + rotate,//BR
                forward + right - rotate//BL
        };

        double maxPower = 1.0;
        double maxSpeed = 1.0;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        for(int i = 0; i<4; i++){
            maxPower = Math.max(maxPower, Math.abs(motorPowers[i]));
        }

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        for(int i = 0; i<4; i++){
            DriveMotors[i].setPower(maxSpeed*(motorPowers[i]/maxPower));
        }
    }

}
