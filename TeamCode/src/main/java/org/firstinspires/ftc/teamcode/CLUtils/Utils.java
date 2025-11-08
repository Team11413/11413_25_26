package org.firstinspires.ftc.teamcode.CLUtils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Utils {

    public static Pose2D PoseInRad(double x, double y, double r){
        return new Pose2D(DistanceUnit.INCH, x, y, AngleUnit.RADIANS, r);
    }
    public static Pose2D PoseInDeg(double x, double y, double r){
        return new Pose2D(DistanceUnit.INCH, x, y, AngleUnit.DEGREES, r);
    }
}
