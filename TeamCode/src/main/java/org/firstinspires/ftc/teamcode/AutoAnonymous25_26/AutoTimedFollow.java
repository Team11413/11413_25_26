package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.CLUtils.AutoPaths;
import org.firstinspires.ftc.teamcode.CLUtils.Follower;
import org.firstinspires.ftc.teamcode.CLUtils.Path;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

@Autonomous(name="Robot: Timed Follow", group="Robot")
public class AutoTimedFollow extends LinearOpMode {

    CommonRobot comBot;

    Follower follower;



    @Override
    public void runOpMode() throws InterruptedException {
        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);

        follower = new Follower(comBot.chassisControl);
        Log.d("PathTesting","Am I stuck in this loop?");
        while (!opModeIsActive()) {
            comBot.update();
            telemetry.update();
        }
        Log.d("PathTesting","Nope");

        // begin commands
        //timeFieldDrive(0.5,0,-.1,3.1);
        Path p =Path.GeneratePath(1,
                Utils.PoseInDeg(0,0,0),
                Utils.PoseInDeg(36,0,0));
        Log.d("PathTesting","auto path generated");
        follower.newPath(p);
        p.duration= follower.estimateMinPathTime();
        Log.d("PathTesting", "Estimated path time: "+p.duration);

        double looptime=0.007;
        double ct = getRuntime();
        double lt = ct-looptime;
        while(opModeIsActive()&&!follower.isComplete()){
            ct = getRuntime();
            looptime=ct-lt;
            lt=ct;
            follower.update(looptime);
            comBot.driveFieldRelative();
            Log.d("PathTesting","AutoTimedFollow - elapsed: "+
                    String.format("%.4f",p.elapsedTime)+
                    "| target: "+
                    String.format("%.4f",follower.targetPose.getX(DistanceUnit.INCH))+
                    " | Dist: "+
                    String.format("%.4f",follower.targetPose.getX(DistanceUnit.INCH)-follower.currentLocation.getX(DistanceUnit.INCH)));
            comBot.update();
            telemetry.update();
        }
        while(opModeIsActive()){
            sleep(10);
            comBot.update();
            telemetry.update();
        }
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
