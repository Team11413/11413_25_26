package org.firstinspires.ftc.teamcode.CLUtils;

import android.util.Log;

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
        forward= f*1.076/(forwardMaxSpeed*loopTime);
        strafe= s*1.076/(strafeMaxSpeed*loopTime);
        rotate= r*1.076/(rotationMaxSpeed*loopTime);
        //cap to unit vector
        double scalar = Math.sqrt(forward*forward+strafe*strafe);
        if(scalar>1){
            forward=forward/scalar;
            strafe=strafe/scalar;
        }
        rotate=Math.min(r,1);
        Log.d("PathTesting","ChassisControl - F="+String.format("%.4f",forward)+"| S="+String.format("%.4f",strafe)+"| R="+String.format("%.4g",rotate));
    }
}
