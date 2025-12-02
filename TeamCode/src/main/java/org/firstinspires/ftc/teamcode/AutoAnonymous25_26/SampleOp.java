package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.icu.text.RelativeDateTimeFormatter;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.ChassisControl;


@TeleOp(name = "Primary", group = "Robot")
public class SampleOp extends OpMode {

    CommonRobot comBot;

    double targetspeed = 0.6;
    double currentTime=0;
    boolean shooterenabled = false;
    double openTime= .3;
    double closedTime=0;
    boolean isOpen=true;
    public boolean ButtersMode=true;

    public AimAssist autoAim=AimAssist.OFF;
    public enum AimAssist{
        OFF,ON,Disabling;
        public AimAssist next(){
            switch (this){
                case ON:return Disabling;
                case OFF:return ON;
                case Disabling:return OFF;
            }
            return null;
        }
    }
    public Pose2D Goal;
    ChassisControl.AlignmentGrid ag = ChassisControl.AlignmentGrid.FTC;

    @Override
    public void init() {
        CommonRobot.startingPose= FieldPositions.Pose.BLUEGOALCLOSE.get();
        comBot = CommonRobot.getCommonRobot(hardwareMap, telemetry);
        comBot.chassisControl.zero();
    }

    @Override
    public void loop() {
        currentTime = getRuntime();
        comBot.localizer.update();
        comBot.update();
        checkControls();

        double setspeed=0;
        if(shooterenabled){
            setspeed=targetspeed;
        }
        if ((isOpen&&closedTime<=currentTime)) {
            comBot.ballRelease.setPosition(.9);
            isOpen=false;
            targetspeed-=.08;
        }
        comBot.SetShootSpeed(setspeed);

        telemetry.addLine("Shooter Speed: "+setspeed);
        telemetry.addLine("Butters Mode: "+ButtersMode);
        telemetry.addLine("Aim Bot: "+autoAim);



        if(autoAim==AimAssist.ON&&Goal!=null){
            comBot.chassisControl.aimAt(comBot.localizer.getPose(),Goal);
        }else if(autoAim==AimAssist.Disabling){
            comBot.chassisControl.clearPID();
            autoAim= autoAim.next();
        }
        if(ButtersMode){
            comBot.chassisControl.alignment= ChassisControl.AlignmentGrid.Robot;
            comBot.drive();
        }else{
            comBot.chassisControl.alignment= ag;
            comBot.driveFieldRelative();
        }
    }

    private void checkControls(){
        comBot.chassisControl.forward = -(gamepad1.left_stick_y*Math.abs(gamepad1.left_stick_y));
        comBot.chassisControl.strafe = (gamepad1.left_stick_x*Math.abs(gamepad1.left_stick_x));
        comBot.chassisControl.rotate= (gamepad1.right_stick_x*Math.abs(gamepad1.right_stick_x));
        if (gamepad1.yWasPressed()){
            shooterenabled=!shooterenabled;
        }
        if (gamepad1.leftBumperWasPressed()){
            targetspeed-=.05;
            if (targetspeed<0){
                targetspeed=0;
            }
        }
        if (gamepad1.rightBumperWasPressed()){
            targetspeed+=.05;
            if (targetspeed>1){
                targetspeed=1;
            }
        }
        if (gamepad1.xWasPressed()) {
            comBot.ballRelease.setPosition(.7);
            isOpen=true;
            closedTime = currentTime+openTime;
            targetspeed+=.08;
        }
        if(gamepad1.bWasPressed()){
            autoAim= autoAim.next();
        }
        if (gamepad1.dpadUpWasPressed()){
            ButtersMode=!ButtersMode;
        }
        if(gamepad1.dpadRightWasPressed()){
            ag = ChassisControl.AlignmentGrid.Blue;
            Goal=FieldPositions.Pose.BLUEGOALCLOSE.get();
        }
        if(gamepad1.dpadLeftWasPressed()){
            ag = ChassisControl.AlignmentGrid.Red;
            Goal=FieldPositions.Pose.REDGOALCLOSE.get();
        }
        if (gamepad1.dpadDownWasPressed()) {
            comBot.localizer.setPose(ag == ChassisControl.AlignmentGrid.Red? FieldPositions.Pose.REDPLAYER.get(): FieldPositions.Pose.BLUEPLAYER.get());
        }
    }

}
