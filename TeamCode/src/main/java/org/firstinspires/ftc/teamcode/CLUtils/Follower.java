package org.firstinspires.ftc.teamcode.CLUtils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Follower {

    ILocalizer ILocalizer;
    Path path;
    Pose2D currentLocation;
    double loopTime=0;
    ChassisControl cc;
    Pose2D targetPose;

    public Follower(ChassisControl cc){
        this.cc=cc;
    }

    public void update(double lastLoopTime){
        loopTime=lastLoopTime;
        path.elapsedTime+=loopTime;
        updatePosition();
        updateControl();
    }

    public double estimateMinPathTime(){
        double timeStepIntegral=2/3.0;
        double avgFSpeed= cc.forwardMaxSpeed*timeStepIntegral;
        double avgSSpeed= cc.strafeMaxSpeed*timeStepIntegral;
        return 2*path.totalLength/(avgFSpeed*avgSSpeed);
    }

    private void updatePosition(){
        if(ILocalizer ==null){
            currentLocation=targetPose;
            return;
        }
        ILocalizer.update();
        currentLocation= ILocalizer.getPose();
    }

    private void updateControl(){
        targetPose = path.GetPoint(accurateEndTimeStep());
        //distance to target
        double f= targetPose.getX(DistanceUnit.INCH)-currentLocation.getX(DistanceUnit.INCH);
        double s= targetPose.getY(DistanceUnit.INCH)-currentLocation.getY(DistanceUnit.INCH);
        double r= targetPose.getHeading(AngleUnit.RADIANS)-currentLocation.getHeading(AngleUnit.RADIANS);
        cc.updateInputFromDistance(f,s,r,loopTime);
    }

    private double getCosTimeStep(){
        //gives a larger portion of the time to start up and slow down
        return (Math.cos(Math.min(path.elapsedTime/path.duration,1)*Math.PI)/-2)+0.5;
    }

    private double accurateEndTimeStep(){
        return (2-(path.elapsedTime/path.duration))*(path.elapsedTime/path.duration);
    }
}
