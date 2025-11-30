package org.firstinspires.ftc.teamcode.CLUtils;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class ChassisControl {
    public double forward, strafe, rotate;
    //inches/sec, inches/sec, radians/sec
    //find these values with real world testing
    public double forwardMaxSpeed, strafeMaxSpeed, rotationMaxSpeed;
    PID xControl = new PID(.8,0,1);
    PID yControl = new PID(.8,0,1);
    PID thetaControl=new PID(1,0,0);
    public AlignmentGrid alignment = AlignmentGrid.Robot;

    public void zero() {
        forward=0;
        strafe=0;
        rotate=0;
        clearPID();
    }

    public void clearPID() {
        xControl.zero();
        yControl.zero();
        thetaControl.zero();
    }

    public enum AlignmentGrid{
        Robot(0),
        FTC(0),
        Field(0),
        Blue(Math.PI/2),
        Red(3*Math.PI/2);
        private final double relativeAngle;
        AlignmentGrid(double ra){
            relativeAngle=ra;
        }
        public double get(){return relativeAngle;}
    }
    public ChassisControl(double fMax, double sMax, double rMax){
        forwardMaxSpeed=fMax;
        strafeMaxSpeed=sMax;
        rotationMaxSpeed =rMax;
        forward=0;
        strafe=0;
        rotate=0;
    }

    public Pose2D getControlAs(AlignmentGrid ag){
        if(ag==alignment){
            return Utils.PoseInDeg(forward,strafe,rotate);
        }
        if(alignment==AlignmentGrid.Robot){
            return null;
        }
        double mult= alignment==AlignmentGrid.FTC?-1:1;
        return Utils.RotatePose(Utils.PoseInDeg(forward,mult*strafe,mult*rotate),alignment.get()-ag.get());
    }

    public void updateInputFromDistance(double f, double s, double r, double loopTime){
        f= f/(forwardMaxSpeed*loopTime);
        s= s/(strafeMaxSpeed*loopTime);
        r= r/(rotationMaxSpeed*loopTime);
        //cap to unit vector
        double scalar = Math.sqrt(f*f+s*s);
        if(scalar>1){
            f=f/scalar;
            s=s/scalar;
        }
        r=Math.max(Math.min(r,1),-1);

        forward=xControl.getPID(f);
        strafe=yControl.getPID(s);
        rotate=thetaControl.getPID(r);
        Log.d("PathTesting","ChassisControl - F="+String.format("%.4f",forward)+"| S="+String.format("%.4f",strafe)+"| R="+String.format("%.4g",rotate)+"| Scalar="+String.format("%.4g",scalar));
    }

    /*
        current and target are in FTC coordinates,
        this will apply PID control to keep the desired orientation.
     */
    public void aimAt(Pose2D current, Pose2D target){
        //x and y are being switch from ftc coordinates to a standard coord grid
        //where blue side and facing the audience is positive x and y
        double x= -(tolerance(target.getY(DistanceUnit.INCH)-current.getY(DistanceUnit.INCH),1));
        double y= tolerance(target.getX(DistanceUnit.INCH)-current.getX(DistanceUnit.INCH),1);
        double headingToTarget=target.getHeading(AngleUnit.RADIANS);
        //if none of the below conditions are true, then x==0 and y==0 so we use the target heading
        if(x==0&&y<0){
            //we are between the audience and the goal and should face the judges
            headingToTarget=-Math.PI;
        }else if(x!=0){
            //we are in a much easier location to identify
            //always gives -90 to 90 which is valid if y>0 when we are on the back wall
            //but otherwise we need to adjust the heading.
            headingToTarget=Math.atan(y/x);
            if(y!=0){
                //this is what runs most of the time and it intensifies the angle by 90 degrees.
                //if it was positive it adds 90, if it was negative it adds -90
                headingToTarget-=Math.signum(y)*Math.signum(headingToTarget)*Math.PI/2;
            }
        }
        //now get the error
        rotate=thetaControl.getPID(tolerance(smartRadianDiff(headingToTarget,current.getHeading(AngleUnit.RADIANS)),.1));
        rotate=alignment!=AlignmentGrid.FTC?-rotate:rotate;
        Log.d("AimBot","x: "+Utils.DoubleToString(x)+" | y: "+Utils.DoubleToString(y)+" | Current Heading: "+Utils.DoubleToString(current.getHeading(AngleUnit.DEGREES))+" | targetHeading: "+Utils.DoubleToString(headingToTarget*180/Math.PI));
//        Log.d("AimBot","targetHeading: "+Utils.DoubleToString(headingToTarget*180/Math.PI)+" | PID rotation command: "+Utils.DoubleToString(rotate));
    }

    private double smartRadianDiff(double t, double c){
        double diff=t-c;
        if(Math.abs(diff)>Math.PI){
            diff+=Math.signum(c)*2*Math.PI;
        }
        return diff;
    }

    private double tolerance(double in, double t){
        return Math.abs(in)>t?in:0;
    }
}
