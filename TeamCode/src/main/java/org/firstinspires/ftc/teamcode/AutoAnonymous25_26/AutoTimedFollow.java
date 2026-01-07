package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.app.Notification;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.CLUtils.ActionSequence;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;
import org.firstinspires.ftc.teamcode.CLUtils.Follower;
import org.firstinspires.ftc.teamcode.CLUtils.Path;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

import java.util.function.Supplier;

@Autonomous(name="Follow Blue Goal", group="Robot", preselectTeleOp = "Primary")
public class AutoTimedFollow extends LinearOpMode {

    CommonRobot comBot;
    Follower follower;

    private ActionSequence Auto1;
    private ActionSequence follow;
    private ActionSequence moveToShootBlue;
    private ActionSequence shootThree;
    private Path currentPath;
    public double speed=20; //follows the given path at 20 inches per second.
    public AutoTimedFollow() {
        follow= new ActionSequence(
                ()->{
                    follower.newPath(currentPath, speed);
                    return true;
                },
                ()->{
                    if(follower.isComplete()){
                        return true;
                    }
                    follower.update(Utils.getLoopTime());
                    comBot.driveFieldRelative();
                    Log.d("PathTesting","AutoTimedFollow - elapsed: "+
                            String.format("%.4f",follower.elapsedTime)+
                            "| target: "+
                            String.format("%.4f",follower.targetPose.getX(DistanceUnit.INCH))+
                            " | Dist: "+
                            String.format("%.4f",follower.targetPose.getX(DistanceUnit.INCH)-follower.currentLocation.getX(DistanceUnit.INCH)));
                    return false;
                },
                ()->{
                    comBot.chassisControl.zero();
                    comBot.drive();
                    return true;
                }
        );
        moveToShootBlue = new ActionSequence(
                ()->{
                    currentPath=Path.GeneratePath(comBot.localizer.getPose(),FieldPositions.Pose.BLUEGOALSCORE.get());
                    return true;
                },
                follow::run
        );
        shootThree= new ActionSequence(
                ShooterSystem.instance.shoot::run,
                ShooterSystem.instance.shoot::run,
                ShooterSystem.instance.shoot::run
        );
        Auto1 = new ActionSequence(
                moveToShootBlue::run,
                shootThree::run
        );
    }



    @Override
    public void runOpMode() throws InterruptedException {
        CommonRobot.startingPose= FieldPositions.Pose.BLUEGOALSTART.get();
        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);
        comBot.chassisControl.alignment= ChassisControl.AlignmentGrid.FTC;
        comBot.Goal=FieldPositions.Pose.BLUEGOAL.get();
        follower = new Follower(comBot.chassisControl);
        follower.localizer= comBot.localizer;

        telemetry.addLine("Wait for paths to finish generating");
        telemetry.update();
        Path moveToFirstRow =Path.GeneratePath(
                FieldPositions.Pose.BLUEGOALSTART.get(),
                FieldPositions.Pose.BLUEGOALSCORE.get());

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
        while(!isStopRequested()&&opModeIsActive()){
            if(!Auto1.run()){
                break;
            }
        }
    }
}
