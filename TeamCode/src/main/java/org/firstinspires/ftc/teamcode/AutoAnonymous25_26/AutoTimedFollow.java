package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;
import org.firstinspires.ftc.teamcode.CLUtils.Follower;
import org.firstinspires.ftc.teamcode.CLUtils.Path;

@Autonomous(name="Follow Blue Goal", group="Robot", preselectTeleOp = "Primary")
public class AutoTimedFollow extends LinearOpMode {

    CommonRobot comBot;
    Follower follower;
    double ct=0;
    double lt=0;
    double looptime=0.007;

    @Override
    public void runOpMode() throws InterruptedException {
        CommonRobot.startingPose= FieldPositions.Pose.BLUEGOALCLOSE.get();
        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);
        comBot.chassisControl.alignment= ChassisControl.AlignmentGrid.FTC;
        follower = new Follower(comBot.chassisControl);
        follower.localizer= comBot.localizer;

        telemetry.addLine("Wait for paths to finish generating");
        telemetry.update();
        Path moveToFirstRow =Path.GeneratePath(
                FieldPositions.Pose.BLUEGOALCLOSE.get(),
                FieldPositions.Pose.BLUESPIKE1START.get(),
                FieldPositions.Pose.BLUESPIKE1END.get(),
                FieldPositions.Pose.BLUEGOALCLOSE.get());

        long waitTime = 0;

        while (!isStarted() && !isStopRequested()){
            comBot.localizer.update();
            comBot.update();

            telemetry.addData("Current wait time", waitTime);
            telemetry.addData("Press A to add wait time","");
            telemetry.update();

            if (gamepad1.aWasPressed()){
                waitTime++;
            }
        }

        // begin commands
        sleep(waitTime * 1000);

        follower.newPath(moveToFirstRow,20);
        follow();
    }
private void  shoot3(){
    comBot.leftShooter.setPower(.47);
    comBot.shootflipper();
    sleep(200);
    comBot.readyflipper();

    }
    private void follow(){
        ct = getRuntime();
        lt = ct-looptime;
        while(opModeIsActive()&&!follower.isComplete()){
            ct = getRuntime();
            looptime=ct-lt;
            lt=ct;
            comBot.update();
            follower.update(looptime);
            comBot.driveFieldRelative();
            Log.d("PathTesting","AutoTimedFollow - elapsed: "+
                    String.format("%.4f",follower.elapsedTime)+
                    "| target: "+
                    String.format("%.4f",follower.targetPose.getX(DistanceUnit.INCH))+
                    " | Dist: "+
                    String.format("%.4f",follower.targetPose.getX(DistanceUnit.INCH)-follower.currentLocation.getX(DistanceUnit.INCH)));

            telemetry.update();
        }
        comBot.chassisControl.zero();
        comBot.drive();
    }
}
