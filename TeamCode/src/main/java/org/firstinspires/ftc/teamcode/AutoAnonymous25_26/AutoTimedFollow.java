package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.CLUtils.ActionSequence;
import org.firstinspires.ftc.teamcode.CLUtils.AtomicAction;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;
import org.firstinspires.ftc.teamcode.CLUtils.Follower;
import org.firstinspires.ftc.teamcode.CLUtils.Path;
import org.firstinspires.ftc.teamcode.CLUtils.RaceActions;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

@Autonomous(name = "Follow Blue Goal", group = "Robot", preselectTeleOp = "Primary")
public class AutoTimedFollow extends LinearOpMode {

    CommonRobot comBot;
    Follower follower;

    private AtomicAction Auto1;
    private AtomicAction follow;
    private AtomicAction leaveStart;
    private AtomicAction moveToShootBlue;
    private AtomicAction Pickupspikeone;
    private AtomicAction shootThree;
    private AtomicAction autoAim;
    private Path currentPath;
    private ActionSequence.Wait ShotPrep = new ActionSequence.Wait();
    public double speed = 110; //follows the given path at 20 inches per second.

    public AutoTimedFollow() {

    }

    private void prep() {
        follow = new ActionSequence(
                new AtomicAction((unused) -> {
                    follower.newPath(currentPath, speed);
                }),
                new AtomicAction((unused) -> {
                    follower.update(Utils.getLoopTime());
                    comBot.driveFieldRelative();
                    Log.d("PathTesting", "AutoTimedFollow - elapsed: " +
                            String.format("%.4f", follower.elapsedTime) +
                            "| target: " +
                            String.format("%.4f", follower.targetPose.getX(DistanceUnit.INCH)) +
                            " | Dist: " +
                            String.format("%.4f", follower.targetPose.getX(DistanceUnit.INCH) - follower.currentLocation.getX(DistanceUnit.INCH)));
                },
                        follower::isComplete,
                        (end) -> {
                            comBot.chassisControl.zero();
                            comBot.drive();
                        }
                ));
        leaveStart = new ActionSequence(
                new AtomicAction((unused) -> {
                    currentPath = Path.GeneratePath(comBot.localizer.getPose(), FieldPositions.Pose.BLUEGOALOFFSET.get());
                    speed = 120;
                }),
                follow
        );
        moveToShootBlue = new ActionSequence(
                new AtomicAction((unused) -> {
                    currentPath = Path.GeneratePath(comBot.localizer.getPose(), FieldPositions.Pose.BLUEGOALSCORE.get());
                    speed = 120;
                }),
                follow
        );
        autoAim = new AtomicAction(
                (unused) -> {
                    comBot.chassisControl.aimAt(comBot.localizer.getPose(), comBot.Goal);
                    comBot.driveFieldRelative();
                },
                () -> false,
                (unused) -> {
                    comBot.chassisControl.zero();
                    comBot.drive();
                }
        );
        Pickupspikeone = new ActionSequence(
                new AtomicAction((unused) -> {
                    currentPath = Path.GeneratePath(comBot.localizer.getPose(), FieldPositions.Pose.BLUESPIKE1START.get(), FieldPositions.Pose.BLUESPIKE1END.get());
                    speed = 100;
                }),
                follow
        );


        shootThree = new RaceActions(autoAim, ShooterSystem.instance.shootThree);
        Auto1 = new RaceActions(
                new AtomicAction((unused) -> comBot.update(), () -> false),
                new AtomicAction(
                        (unused) -> {
                            comBot.intake.setPower(.5);
                        },
                        () -> false,
                        (unused) -> {
                            comBot.intake.setPower(0);
                        }
                ),
                new ActionSequence(
                        leaveStart,
                        moveToShootBlue,
                        ShotPrep.setTimer(200),
                        shootThree,
                        Pickupspikeone,
                        moveToShootBlue,
                        shootThree
                )
        );
    }


    @Override
    public void runOpMode() throws InterruptedException {
        CommonRobot.startingPose = FieldPositions.Pose.BLUEGOALSTART.get();
        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);
        comBot.ss.enabled = false;
        comBot.chassisControl.alignment = ChassisControl.AlignmentGrid.FTC;
        comBot.Goal = FieldPositions.Pose.BLUEGOAL.get();
        follower = new Follower(comBot.chassisControl);
        follower.localizer = comBot.localizer;

        telemetry.addLine("Wait for paths to finish generating");
        telemetry.update();
        prep();

        long waitTime = 0;

        while (!isStarted() && !isStopRequested()) {
            comBot.localizer.update();
            comBot.update();

            telemetry.addLine("Current wait time " + waitTime);
            telemetry.addLine("Press A to add wait time");
            telemetry.update();

            if (gamepad1.aWasPressed()) {
                waitTime++;
            }
        }
        telemetry.addLine("exiting init");
        telemetry.update();

        // begin commands
//        sleep(waitTime * 1000);
        Utils.resetLoopTimer(this::getRuntime);
        comBot.ss.enabled = true;
        while (!isStopRequested() && opModeIsActive()) {
            Utils.getLoopTime();
            Auto1.run();
            telemetry.update();
            if (Auto1.isComplete.get()) {
                break;
            }
        }
    }
}
