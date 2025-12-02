package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Blue Audience", group="Robot")
public class AutoOP4 extends LinearOpMode {
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

        // begin commands
        comBot.leftShooter.setPower(0.75);
        comBot.ballRelease.setPosition(1);
        sleep (4000);
        shoot();
        sleep(750);
        shoot();
        sleep(750);
        shoot();
        comBot.leftShooter.setPower(0);
        sleep(600);
        timeFieldDrive(-0.3,0,0,1);

    }

    private void shoot() {
        comBot.ballRelease.setPosition(0.5);
        sleep (400);
        comBot.ballRelease.setPosition(1);
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
                telemetry.addLine("Time: "+getRuntime());
                telemetry.update();
            }

            // Stop all motion & Turn off RUN_TO_POSITION
            comBot.drive(0,0,0);
        }
    }
}
