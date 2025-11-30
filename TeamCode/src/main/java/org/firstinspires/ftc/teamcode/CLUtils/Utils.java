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
    public static String DoubleToString(double d){
        return String.format("%.4g",d);
    }

    public static String PoseToString(Pose2D p){
        return "x: "+DoubleToString(p.getX(DistanceUnit.INCH))+" | y: "+DoubleToString(p.getY(DistanceUnit.INCH))+" | h: "+DoubleToString(p.getHeading(AngleUnit.DEGREES));
    }

    public static Pose2D RotatePose(Pose2D start, double theta) {
        double f=start.getX(DistanceUnit.INCH);
        double s=start.getY(DistanceUnit.INCH);
        return Utils.PoseInDeg(f*Math.cos(theta)- s*Math.sin(theta),
        f*Math.sin(theta)+s*Math.cos(theta),
                start.getHeading(AngleUnit.DEGREES));
    }
}
