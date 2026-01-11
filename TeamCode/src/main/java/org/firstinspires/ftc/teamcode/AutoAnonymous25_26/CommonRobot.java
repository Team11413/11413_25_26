package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.GBPinPointLocalizer;
import org.firstinspires.ftc.teamcode.CLUtils.PID;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;
import org.slf4j.helpers.Util;

public class CommonRobot {
    public static CommonRobot INSTANCE;
    public long lastLoop =0;
    public double loopTime=0;
    public VoltageSensor battery;
    public DcMotor [] DriveMotors = new DcMotor[4];
    public DcMotor intake;
    public ShooterSystem ss;
    public Servo ballRelease;
    public GBPinPointLocalizer localizer;

    public ChassisControl chassisControl=new ChassisControl(180,160,4*Math.PI);
    public double[] centerOfMass=new double[]{6,-1};
    public double[] edgeLengths= new double[]{16,16};
    //Forward, Right, Clockwise
    public double[] motorAdjust= new double[]{1,1,1};
    public static Pose2D startingPose= Utils.PoseInDeg(0,0,0);
    public Pose2D Goal;

    Telemetry telemetry;
    HardwareMap hardwareMap;
    int FL=0, FR=1, BL=2, BR=3;

    public static CommonRobot getCommonRobot(HardwareMap hardwareMap, Telemetry telemetry) {

        if (INSTANCE == null) {
            INSTANCE = new CommonRobot();
        }
        INSTANCE.init(hardwareMap, telemetry);
     return INSTANCE;
    }
    private CommonRobot() {
    }

    public void init(HardwareMap hm, Telemetry tel){
        telemetry = tel;
        hardwareMap = hm;

        battery = hardwareMap.voltageSensor.iterator().next();

        DriveMotors[FL] = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        DriveMotors[FR] = hardwareMap.get(DcMotor.class, "driveFrontRight");
        DriveMotors[BL] = hardwareMap.get(DcMotor.class, "driveBackLeft");
        DriveMotors[BR] = hardwareMap.get(DcMotor.class, "driveBackRight");
        intake = hardwareMap.get(DcMotor.class, "intake");

        ss=ShooterSystem.instance;
        ss.init(hm, tel);
        ballRelease = hardwareMap.get(Servo.class, "ballRelease");
//        imu = hardwareMap.get(IMU.class, "imu");
        localizer = new GBPinPointLocalizer(hardwareMap);
        localizer.init(startingPose);
//        imu.initialize(new IMU.Parameters(new
//                RevHubOrientationOnRobot(
//                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
//                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)));
//        imu.resetYaw();
        DriveMotors[FR].setDirection(DcMotor.Direction.REVERSE);
        DriveMotors[BR].setDirection(DcMotor.Direction.REVERSE);

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
        //update Chassis inputs to
        Pose2D control = chassisControl.getControlAs(ChassisControl.AlignmentGrid.Field);
        //this uses inches and degrees as dummy units because those are the units being used when the values are stored.
        //they actually represent control inputs from -1 to 1
        driveFieldRelative(control.getX(DistanceUnit.INCH), control.getY(DistanceUnit.INCH), control.getHeading(AngleUnit.DEGREES));
    }

    public void drive(){
        drive(chassisControl.forward, chassisControl.strafe, chassisControl.rotate);
    }

    public void driveFieldRelative(double forward, double right, double rotate) {
//        double theta = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        double theta = localizer.getPose().getHeading(AngleUnit.RADIANS);
        double newForward = forward*Math.cos(theta)-right*Math.sin(theta);
        double newRight = forward*Math.sin(theta)+right*Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    public void update(){
        localizer.update();
        startingPose= localizer.getPose();
        if(Goal!=null) {
            ss.update(Utils.getLoopTime(), Utils.dist(startingPose, Goal, DistanceUnit.INCH));
        }
        telemetry.addLine("Pose - x: "+Utils.DoubleToString(startingPose.getX(DistanceUnit.INCH))+" | "+
                "y: "+Utils.DoubleToString(startingPose.getY(DistanceUnit.INCH))+" | "+
                "heading: "+Utils.DoubleToString(startingPose.getHeading(AngleUnit.DEGREES)));
        telemetry.addLine("CC - x: "+Utils.DoubleToString(chassisControl.forward)+" | "+
                "y: "+Utils.DoubleToString(chassisControl.strafe)+" | "+
                "heading: "+Utils.DoubleToString(chassisControl.rotate));
        telemetry.addLine("Coord System: "+chassisControl.alignment.name());
//        telemetry.addLine(battery.getDeviceName()+": "+Utils.DoubleToString(battery.getVoltage()));
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
        Log.d("Driving","drive - F="+Utils.DoubleToString(forward)+"| S="+Utils.DoubleToString(right)+"| R="+Utils.DoubleToString(rotate)+"| Scale="+Utils.DoubleToString(maxPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        for(int i = 0; i<4; i++){
            DriveMotors[i].setPower(maxSpeed*(motorPowers[i]/maxPower));
        }
    }

//    public void SetShootSpeed(double power){
//        power = power * 12 / battery.getVoltage();
//        leftShooter.setPower(power);
//    }
public void readyflipper(){
ballRelease.setPosition(0.9);
}
public void shootflipper(){
     ballRelease.setPosition(0.7);
}

PID shooterPID = new PID(0.8, 0.01, 0.1);
double mmPerTick = 96 * Math.PI / 28;
double targetVelocity = 40;

}

