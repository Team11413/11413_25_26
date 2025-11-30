package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="GoalSide", group="Robot")
public class AutoOP2 extends LinearOpMode {
    static final double     COUNTS_PER_MOTOR_REV    = 537.7 ;   // eg: GoBILDA 312 RPM Yellow Jacket
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    CommonRobot comBot;
    @Override
    public void runOpMode() throws InterruptedException {
        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);

        while (opModeInInit()) {
//            telemetry.addData(">", "Robot Heading = %4.0f", getHeading());
            telemetry.update();
        }
//        comBot.imu.resetYaw();
        sleep(250);

        // begin commands
//        comBot.SetShootSpeed(.5);
        timeFieldDrive(0.7,0,0,1.0);

        while(opModeIsActive()){

        }
    }

    private void shoot(){
        comBot.ballRelease.setPosition(1);
        comBot.SetShootSpeed(.55);
        sleep(250);
        comBot.ballRelease.setPosition(.7);
        comBot.SetShootSpeed(.5);

    }

    private void timeDrive(double forward,
                           double right,
                           double rotate,
                           double time) {
        if (opModeIsActive()) {
            time+=getRuntime();
            comBot.drive(forward, right, rotate);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() && getRuntime()<=time) {
                continue;
            }

            // Stop all motion & Turn off RUN_TO_POSITION
            comBot.drive(0,0,0);
        }
    }

    private void timeFieldDrive(double forward,
                                double right,
                                double rotate,
                                double time) {
        if (opModeIsActive()) {
            time+=getRuntime();
            comBot.driveFieldRelative(forward, right, rotate);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() && getRuntime()<=time) {
                comBot.driveFieldRelative(forward, right, rotate);
            }

            // Stop all motion & Turn off RUN_TO_POSITION
            comBot.drive(0,0,0);
        }
    }
}
