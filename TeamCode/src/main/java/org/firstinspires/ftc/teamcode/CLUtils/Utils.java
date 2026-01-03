package org.firstinspires.ftc.teamcode.CLUtils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Utils {

    public static double invScaledLerp(double target, double[] scale){
        double p = scale.length-1;
        for (int i = (int) p; i >0; i--) {
            p--;
            if(target>i){
                p+=(target-scale[i])/(scale[i+1]-scale[i]);
                break;
            }
        }
        return p;
    }

    /*
    This accepts a target greater than 1,
    and will return the exact scale value if the target is within the tolerance.
     */
    public static double scaledLerp(double t, double[] scale, double tolerance){
        int idx=(int)t;
        double dif= t-idx;
        if(dif<tolerance){
            return scale[idx];
        }
        if(dif>1-tolerance){
            return scale[idx+1];
        }
        return scale[idx]+(scale[idx+1]-scale[idx])*dif;
    }

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
