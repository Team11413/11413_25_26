package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.CLUtils.ActionSequence;
import org.firstinspires.ftc.teamcode.CLUtils.AtomicAction;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;
import org.firstinspires.ftc.teamcode.CLUtils.Follower;
import org.firstinspires.ftc.teamcode.CLUtils.Path;
import org.firstinspires.ftc.teamcode.CLUtils.RaceActions;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

@Autonomous(name = "Smart Auto", group = "Robot", preselectTeleOp = "Primary")
public class AutoTimedFollow extends LinearOpMode {

    CommonRobot comBot;
    Follower follower;
    boolean blueSide = true;
    boolean goalSide = true;
    Pose2D scorePose;
    Pose2D spike1S;
    Pose2D spike1E;
    Pose2D spike2S;
    Pose2D spike2E;
    Pose2D spike3S;
    Pose2D spike3E;
    Pose2D exitPose;
    Pose2D offsetPose;


    private AtomicAction GoalAuto;
    private AtomicAction AudienceAuto;
    private AtomicAction follow;
    private AtomicAction leaveStart;
    private AtomicAction moveToShootBlue;
    private AtomicAction Pickupspikeone;
    private AtomicAction shootThree;
    private AtomicAction autoAim;
    private AtomicAction exitZone;
    private AtomicAction boost;
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
                    currentPath = Path.GeneratePath(comBot.localizer.getPose(), exitPose);
                    speed = 120;
                }),
                follow
        );
        moveToShootBlue = new ActionSequence(
                new AtomicAction((unused) -> {
                    currentPath = Path.GeneratePath(comBot.localizer.getPose(), scorePose);
                    speed = 120;
                }),
                follow
        );
        exitZone=new ActionSequence(
                new AtomicAction((unused)->{
                    currentPath = Path.GeneratePath(comBot.localizer.getPose(), exitPose);
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
                    currentPath = Path.GeneratePath(comBot.localizer.getPose(), spike1S, spike1E);
                    speed = 100;
                }),
                follow
        );
        boost = new AtomicAction(
                (unused) -> {
                    comBot.ss.speedAdjust = .2;
                },
                () -> false,
                (unused) -> {
                    comBot.ss.speedAdjust = 0;
                });


        shootThree = new RaceActions(autoAim, ShooterSystem.instance.shootThree);
        GoalAuto = new RaceActions(
                new AtomicAction((unused) -> comBot.update(), () -> false),
                new AtomicAction(
                        (unused) -> {
                            comBot.intake.setPower(.7);
                        },
                        () -> false,
                        (unused) -> {
                            comBot.intake.setPower(0);
                        }
                ),
                new ActionSequence(
                        leaveStart,
                        moveToShootBlue,
                        ShotPrep.setTimer(1000),
                        new RaceActions(shootThree,boost),
                        Pickupspikeone,
                        moveToShootBlue,
                        moveToShootBlue,
                        shootThree,
                        leaveStart
                )
        );
        AudienceAuto=new RaceActions(
                new AtomicAction((unused) -> comBot.update(), () -> false),
                exitZone
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
            if (gamepad1.dpadUpWasPressed()) {
                goalSide = true;
            }
            if (gamepad1.dpadDownWasPressed()) {
                goalSide = false;
            }
            if (gamepad1.dpadRightWasPressed()) {
                blueSide = true;
            }
            if (gamepad1.dpadLeftWasPressed()) {
                blueSide = false;
            }

            if (blueSide) {
                if (goalSide) {
                    telemetry.addLine("Auto type: Blue Goal");
                } else {
                    telemetry.addLine("Auto type: Blue Audience");
                }
            } else {
                if (goalSide) {
                    telemetry.addLine("Auto type: Red Goal");
                } else {
                    telemetry.addLine("Auto type: Red Audience");
                }
            }

            telemetry.addLine("Current wait time " + waitTime);
            telemetry.addLine("Press A to add wait time");
            telemetry.update();

            if (gamepad1.aWasPressed()) {
                waitTime++;
            }
        }
        telemetry.addLine("exiting init");
        telemetry.update();
        if (goalSide) {
            if (blueSide) {
                comBot.localizer.setPose(FieldPositions.Pose.BLUEGOALSTART.get());
                CommonRobot.startingPose = FieldPositions.Pose.BLUEGOALSTART.get();
                scorePose = FieldPositions.Pose.BLUEGOALSCORE.get();
                comBot.Goal = FieldPositions.Pose.BLUEGOAL.get();
                spike1S = FieldPositions.Pose.BLUESPIKE1START.get();
                spike1E = FieldPositions.Pose.BLUESPIKE1END.get();
                exitPose=FieldPositions.Pose.BLUEGOALEXIT.get();
                offsetPose=FieldPositions.Pose.BLUEGOALOFFSET.get();
            } else {
                comBot.localizer.setPose(FieldPositions.Pose.REDGOALSTART.get());
                CommonRobot.startingPose = FieldPositions.Pose.BLUEGOALSTART.get();
                scorePose = FieldPositions.Pose.REDGOALSCORE.get();
                comBot.Goal = FieldPositions.Pose.REDGOAL.get();
                spike1S = FieldPositions.Pose.REDSPIKE1START.get();
                spike1E = FieldPositions.Pose.REDSPIKE1END.get();
                exitPose=FieldPositions.Pose.REDGOALEXIT.get();
                offsetPose=FieldPositions.Pose.REDGOALOFFSET.get();
            }
        } else {
            if (blueSide) {
                comBot.localizer.setPose(FieldPositions.Pose.BLUEAUDIENCESTART.get());
                CommonRobot.startingPose = FieldPositions.Pose.BLUEGOALSTART.get();
                scorePose = FieldPositions.Pose.BLUEAUDIENCESCORE.get();
                comBot.Goal = FieldPositions.Pose.BLUEGOAL.get();
                spike1S = FieldPositions.Pose.BLUESPIKE1START.get();
                spike1E = FieldPositions.Pose.BLUESPIKE1END.get();
                exitPose=FieldPositions.Pose.BLUEAUDIENCEEXIT.get();
            } else {
                comBot.localizer.setPose(FieldPositions.Pose.REDAUDIENCESTART.get());
                CommonRobot.startingPose = FieldPositions.Pose.BLUEGOALSTART.get();
                scorePose = FieldPositions.Pose.REDAUDIENCESCORE.get();
                comBot.Goal = FieldPositions.Pose.REDGOAL.get();
                spike1S = FieldPositions.Pose.REDSPIKE1START.get();
                spike1E = FieldPositions.Pose.REDSPIKE1END.get();
                exitPose=FieldPositions.Pose.REDAUDIENCEEXIT.get();
            }
        }


        // begin commands
//        sleep(waitTime * 1000);
        Utils.resetLoopTimer(this::getRuntime);
        comBot.ss.enabled = true;
        while (!isStopRequested() && opModeIsActive()) {
            Utils.getLoopTime();
            if(goalSide){
                GoalAuto.run();
            }else{
                AudienceAuto.run();
            }
            telemetry.update();
            if (GoalAuto.isComplete.get()||AudienceAuto.isComplete.get()) {
                break;
            }
        }
    }
}
