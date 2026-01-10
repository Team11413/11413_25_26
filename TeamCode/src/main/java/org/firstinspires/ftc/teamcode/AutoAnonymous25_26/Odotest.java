package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.CLUtils.FieldPositions;
import org.firstinspires.ftc.teamcode.CLUtils.GBPinPointLocalizer;
import org.firstinspires.ftc.teamcode.CLUtils.Utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

@TeleOp(name = "Odotest", group = "Robot")
public class Odotest extends OpMode {
    GBPinPointLocalizer GB;
    ArrayList<Pose2D> recordedPoints = new ArrayList<Pose2D>();
    int index = 0;
    int idx=0;

    @Override
    public void init() {
        GB = new GBPinPointLocalizer(hardwareMap);
        GB.setPose(FieldPositions.Pose.CENTER.get());
    }

    @Override
    public void loop() {
        GB.update();
        if (gamepad1.dpadDownWasPressed()) {
            GB.setPose(FieldPositions.Pose.CENTER.get());
        }
        if (gamepad1.dpadUpWasPressed()) {
            recordedPoints.set(index, GB.getPose());
        }
        if (gamepad1.dpadRightWasPressed()) {
            index = (index + 1) % recordedPoints.size();
        }
        if (gamepad1.dpadLeftWasPressed()) {
            index = (index + recordedPoints.size() - 1) % recordedPoints.size();
        }
        if (gamepad1.yWasPressed()) {
            recordedPoints.add(GB.getPose());
        }
        telemetry.addLine("x:" + GB.getPose().getX(DistanceUnit.INCH));
        telemetry.addLine("y:" + GB.getPose().getY(DistanceUnit.INCH));
        telemetry.addLine("heading: " + GB.getPose().getHeading(AngleUnit.DEGREES));
        telemetry.addLine("Point List");
        idx=0;
        String list = recordedPoints.stream().map((point) -> {
            String prePost="";
            if(idx==index){
                prePost="_";
            }
            idx++;
            return prePost+Utils.DoubleToString(point.getX(DistanceUnit.INCH)) + ", " + Utils.DoubleToString(point.getY(DistanceUnit.INCH)) + ", " + Utils.DoubleToString(point.getHeading(AngleUnit.DEGREES))+prePost;
        }).collect(Collectors.joining(" | "));
        telemetry.addLine(list);
        if(gamepad1.bWasPressed()){
            Log.d("Pose Tests",list);
        }
    }
}
