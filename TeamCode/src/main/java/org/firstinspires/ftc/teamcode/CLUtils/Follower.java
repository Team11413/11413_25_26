package org.firstinspires.ftc.teamcode.CLUtils;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Follower {

    public ILocalizer localizer;
    Path path;
    public Pose2D currentLocation;
    double loopTime=0;
    public double elapsedTime=0;
    double duration=0;
    ChassisControl cc;
    public Pose2D targetPose = Utils.PoseInDeg(0,0,0);

    public Follower(ChassisControl cc){
        this.cc=cc;
    }

    /*
    ips is Inches/second
     */
    public void newPath(Path p,double ips){
        path=p;
        loopTime=0;
        elapsedTime=0;
        duration= path.totalLength/ips;
    }

    public void update(double lastLoopTime){
        loopTime=lastLoopTime;
        elapsedTime+=loopTime;
        updatePosition();
        updateControl();
    }

    public boolean isComplete(){
        return elapsedTime>=duration;
    }

    private void updatePosition(){
        if(localizer ==null){
            currentLocation=targetPose;
            return;
        }
        localizer.update();
        currentLocation= localizer.getPose();

    }

    private void updateControl(){
        targetPose = path.GetPoint(elapsedTime/duration);//accurateEndTimeStep());
        Log.d("Pathing","current - "+Utils.PoseToString(currentLocation));
        Log.d("Pathing","target - "+Utils.PoseToString(targetPose));
        //distance to target
        double f= targetPose.getX(DistanceUnit.INCH)-currentLocation.getX(DistanceUnit.INCH);
        double s= targetPose.getY(DistanceUnit.INCH)-currentLocation.getY(DistanceUnit.INCH);
        double r = targetPose.getHeading(AngleUnit.RADIANS);
        r= r-currentLocation.getHeading(AngleUnit.RADIANS);
        //if the absolute rotation is >180 then we are rotating the wrong direction.
        r= Math.signum(r)*(Math.abs(r)%(Math.PI));
        Log.d("PathTesting","Diff - F="+Utils.DoubleToString(f)+"| S="+Utils.DoubleToString(s)+"| R="+Utils.DoubleToString(r));
        cc.updateInputFromDistance(f,s,r,loopTime);
    }
}
