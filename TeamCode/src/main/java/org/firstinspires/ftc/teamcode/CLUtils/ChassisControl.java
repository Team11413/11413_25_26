package org.firstinspires.ftc.teamcode.CLUtils;

public class ChassisControl {
    public double forward, strafe, rotate;
    //inches/sec, inches/sec, radians/sec
    //find these values with real world testing
    public double forwardMaxSpeed, strafeMaxSpeed, rotationMaxSpeed;
    public ChassisControl(double fMax, double sMax, double rMax){
        forwardMaxSpeed=fMax;
        strafeMaxSpeed=sMax;
        rotationMaxSpeed =rMax;
        forward=0;
        strafe=0;
        rotate=0;
    }

    public void updateInputFromDistance(double f, double s, double r, double loopTime){
        forward= f/(forwardMaxSpeed*loopTime);
        strafe= s/(strafeMaxSpeed*loopTime);
        rotate= r/(rotationMaxSpeed*loopTime);
        //cap to unit vector
        double scalar = Math.sqrt(forward*forward+strafe*strafe);
        if(scalar>1){
            forward=forward/scalar;
            strafe=strafe/scalar;
        }
        rotate=Math.min(r,1);
    }
}
