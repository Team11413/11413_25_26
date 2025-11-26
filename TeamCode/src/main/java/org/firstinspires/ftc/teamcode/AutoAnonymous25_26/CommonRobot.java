package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.util.Log;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;
import org.firstinspires.ftc.teamcode.CLUtils.GBPinPointILocalizer;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

public class CommonRobot {
    public static CommonRobot INSTANCE;
    public DcMotor [] DriveMotors = new DcMotor[4];
    public DcMotor leftShooter;
    public DcMotor rightShooter;
    public Servo ballRelease;
    public IMU imu;
    public GBPinPointILocalizer localizer;

    public ChassisControl chassisControl=new ChassisControl(60,50,1);

    public double initialHeading = 0;
    public double[] centerOfMass=new double[]{6,-1};
    public double[] edgeLengths= new double[]{16,16};
    //Forward, Right, Clockwise
    public double[] motorAdjust= new double[]{1,1,1};

    Telemetry telemetry;
    HardwareMap hardwareMap;
    int FL=0, FR=1, BL=2, BR=3;

    public static CommonRobot getCommonRobot(HardwareMap hardwareMap, Telemetry telemetry) {

        if (INSTANCE == null) {
            INSTANCE = new CommonRobot(hardwareMap, telemetry);
        }
        INSTANCE.init(telemetry);
     return INSTANCE;
    }
    public CommonRobot(HardwareMap hm, Telemetry tel) {
        telemetry = tel;
        hardwareMap = hm;

        DriveMotors[FL] = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        DriveMotors[FR] = hardwareMap.get(DcMotor.class, "driveFrontRight");
        DriveMotors[BL] = hardwareMap.get(DcMotor.class, "driveBackLeft");
        DriveMotors[BR] = hardwareMap.get(DcMotor.class, "driveBackRight");

        leftShooter = hardwareMap.get(DcMotor.class, "leftShooter");
        rightShooter = hardwareMap.get(DcMotor.class, "rightShooter");


        ballRelease = hardwareMap.get(Servo.class, "ballRelease");


        imu = hardwareMap.get(IMU.class, "imu");


        // This needs to be changed to match the orientation on your robot
    }

    public void init(Telemetry tel){
        telemetry=tel;
        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
//        localizer = new GBPinPointILocalizer(hardwareMap);
//        localizer.init();
        imu.initialize(new IMU.Parameters(new
                RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)));
        imu.resetYaw();
        DriveMotors[FR].setDirection(DcMotor.Direction.REVERSE);
        DriveMotors[BR].setDirection(DcMotor.Direction.REVERSE);
        leftShooter.setDirection(DcMotor.Direction.REVERSE);
        rightShooter.setDirection(DcMotor.Direction.REVERSE);

        // This uses RUN_USING_ENCODER to be more accurate.   If you don't have the encoder
        // wires, you should remove these
        for(int i = 0; i <4; i++){
            DriveMotors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            DriveMotors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        centerOfMass=new double[]{0,0};
        edgeLengths= new double[]{18,18};
        //Forward, Right, Clockwise
        motorAdjust= new double[]{1,1,1};
        motorAdjust[0]= 1;//Math.sqrt(1+10*Math.abs(centerOfMass[1])/edgeLengths[1]);
        motorAdjust[1]= 1;//centerOfMass[1]>=0?motorAdjust[0]:1/motorAdjust[0];
        motorAdjust[2]= 1;//Math.sqrt(1+10*Math.abs(centerOfMass[0])/edgeLengths[0]);
        motorAdjust[2]= 1;//centerOfMass[0]>=0?motorAdjust[1]:1/motorAdjust[1];
        Log.d("DriveTesting","F: "+motorAdjust[0]+" S: "+motorAdjust[1]);

    }

    public void driveFieldRelative(){
        driveFieldRelative(chassisControl.forward, chassisControl.strafe, chassisControl.rotate);
    }

    public void driveFieldRelative(double forward, double right, double rotate) {
        double theta = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
//        double theta = localizer.getPose().getHeading(AngleUnit.RADIANS);
        double newForward = forward*Math.cos(theta)-right*Math.sin(theta);
        double newRight = forward*Math.sin(theta)+right*Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    public void update(){
//        localizer.update();
        telemetry.addLine("Imu facing "+imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
//        telemetry.addLine("Forward: "+localizer.getPose().getX(DistanceUnit.MM));
//        telemetry.addLine("Sideways: "+localizer.getPose().getY(DistanceUnit.MM));
//        telemetry.addLine("heading: "+localizer.getPose().getHeading(AngleUnit.DEGREES));
    }

    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double [] motorPowers = {
                forward + right  + rotate,//FL
                forward - right - rotate,//FR
                forward - right + rotate,//BR
                forward  + right  - rotate//BL
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
