package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.icu.text.RelativeDateTimeFormatter;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;
import org.firstinspires.ftc.teamcode.CLUtils.ParallelActions;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;


@TeleOp(name = "Primary", group = "Robot")
public class SampleOp extends OpMode {

    CommonRobot comBot;

    double targetspeed = 0.43;
    double currentTime = 0;
    boolean shooterenabled = false;
    double openTime = .3;
    double closedTime = 0;
    boolean isOpen = true;
    public boolean ButtersMode = true;

    private ParallelActions continuous;

    public AimAssist autoAim = AimAssist.OFF;

    public enum AimAssist {
        OFF, ON, Disabling;

        public AimAssist next() {
            switch (this) {
                case ON:
                    return Disabling;
                case OFF:
                    return ON;
                case Disabling:
                    return OFF;
            }
            return null;
        }
    }

    ChassisControl.AlignmentGrid ag = ChassisControl.AlignmentGrid.FTC;

    @Override
    public void init() {
        continuous = new ParallelActions(true,
                () -> {
                    comBot.localizer.update();
                    comBot.update();
                    checkControls();
                    return false;
                }
        );
        CommonRobot.startingPose = FieldPositions.Pose.BLUEGOALSTART.get();
        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);
        comBot.chassisControl.zero();
    }

    @Override
    public void loop() {
        currentTime = getRuntime();
        continuous.run();

//        double setspeed = 0;
//        if (shooterenabled) {
//            setspeed = targetspeed;
//        }
//        if ((isOpen && closedTime <= currentTime)) {
//            comBot.ballRelease.setPosition(.9);
//            isOpen = false;
//            targetspeed -= .08;
//        }
//        comBot.SetShootSpeed(setspeed);

        telemetry.addLine("Shooter Speed: " + Utils.DoubleToString(comBot.ss.cRPS));
        telemetry.addLine("Butters Mode: " + ButtersMode);
        telemetry.addLine("Aim Bot: " + autoAim);


        if (autoAim == AimAssist.ON && comBot.Goal != null) {
            comBot.chassisControl.aimAt(comBot.localizer.getPose(), comBot.Goal);
        } else if (autoAim == AimAssist.Disabling) {
            comBot.chassisControl.clearPID();
            autoAim = autoAim.next();
        }
        if (ButtersMode) {
            comBot.chassisControl.alignment = ChassisControl.AlignmentGrid.Robot;
            comBot.drive();
        } else {
            comBot.chassisControl.alignment = ag;
            comBot.driveFieldRelative();
        }
    }

    private void checkControls() {
        comBot.chassisControl.forward = -(gamepad1.left_stick_y * Math.abs(gamepad1.left_stick_y));
        comBot.chassisControl.strafe = (gamepad1.left_stick_x * Math.abs(gamepad1.left_stick_x));
        comBot.chassisControl.rotate = (gamepad1.right_stick_x * Math.abs(gamepad1.right_stick_x));
        if (gamepad1.yWasPressed()) {
            shooterenabled = !shooterenabled;
        }
        double power = 0;
        if (gamepad1.left_bumper) {
            power = -0.5;
//            targetspeed -= .05;
//            if (targetspeed < 0) {
//                targetspeed = 0;
//            }
        }
        if (gamepad1.right_bumper) {
            power = 0.5;
//            targetspeed += .05;
//            if (targetspeed > 1) {
//                targetspeed = 1;
//            }
        }
        comBot.intake.setPower(power);

        if (gamepad1.xWasPressed()) {
//            comBot.ballRelease.setPosition(.7);
//            isOpen = true;
//            closedTime = currentTime + openTime;
//            targetspeed += .08;
            continuous.addAction(comBot.ss.shoot::run);
        }
        if (gamepad1.bWasPressed()) {
            autoAim = autoAim.next();
        }
        if (gamepad1.dpadUpWasPressed()) {
            ButtersMode = !ButtersMode;
        }
        if (gamepad1.dpadRightWasPressed()) {
            ag = ChassisControl.AlignmentGrid.Blue;
            comBot.Goal = FieldPositions.Pose.BLUEGOAL.get();
        }
        if (gamepad1.dpadLeftWasPressed()) {
            ag = ChassisControl.AlignmentGrid.Red;
            comBot.Goal = FieldPositions.Pose.REDGOAL.get();
        }
        if (gamepad1.dpadDownWasPressed()) {
            comBot.localizer.setPose(ag == ChassisControl.AlignmentGrid.Red ? FieldPositions.Pose.REDPLAYER.get() : FieldPositions.Pose.BLUEPLAYER.get());
        }
        if(gamepad2.right_trigger>0){
            //spin intake to pull in balls
        }else if(gamepad2.left_trigger>0){
            //spin intake to eject balls
        }
    }

}
