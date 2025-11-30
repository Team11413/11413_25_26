package org.firstinspires.ftc.teamcode.CLUtils;


import android.util.Log;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;


public class Path {

    public Pose2D[] points;
    public double[] lineLengths;
    public double totalLength=0;

    /*
    There should be at least 2 targets to designate a start and end point.
     */
    public static Path GeneratePath(Pose2D... targets){
        long start=System.currentTimeMillis();
        if(targets.length<2){
            return null;
        }
        Path p = new Path();
        Pose2D zeroth = extendLine(targets[1], targets[0], 1);
        Pose2D last = extendLine(targets[targets.length-2],targets[targets.length-1],1);
        p.points=new Pose2D[targets.length+2];
        p.points[0]=zeroth;
        p.points[targets.length+1]=last;
        System.arraycopy(targets,0,p.points,1,targets.length);
        p.FillLineLengths();
        Log.d("Pathing","Path - Took "+(System.currentTimeMillis()-start)+"ms to generate a path with "+targets.length+" points");
        return p;
    }

    public Pose2D GetPoint(double t){
        if(t<=0){
            return points[2];
        }
        if(t>=1){
            return points[points.length-2];
        }
        double ot=t;
        double desiredLength= totalLength*t;
        int segment=0;
        while(lineLengths[segment]<desiredLength){
            desiredLength-=lineLengths[segment];
            segment++;
        }
        t=desiredLength/lineLengths[segment];
//        Log.d("PathTesting","total progress: "+String.format("%.4f",ot)+" chooses Line Segment: "+segment+" with partial progress: "+t);
        return CubicHermiteInterp(segment,t);
    }

    private void FillLineLengths(){
        int segments = points.length-3;
        lineLengths= new double[segments];
        for(int i= 0; i<segments;i++){
            lineLengths[i]=EstimateSegmentLength(i,5);
            totalLength+=lineLengths[i];
        }
    }

    private double EstimateSegmentLength(int segment, int resolution){
        Pose2D[] interPoints= new Pose2D[resolution];
        for(int i=0; i<resolution;i++){
            double t = i/(double)resolution;
            t= SmoothStep(t);
            interPoints[i]= CubicHermiteInterp(segment,t);
        }
        double length=0;
        for(int i=0;i<resolution-1;i++){
            double dX=interPoints[i+1].getX(DistanceUnit.INCH)-interPoints[i].getX(DistanceUnit.INCH);
            double dY=interPoints[i+1].getY(DistanceUnit.INCH)-interPoints[i].getY(DistanceUnit.INCH);
            length+= Math.sqrt(dX*dX+dY*dY);
        }
        return length;
    }

    private static Pose2D extendLine(Pose2D from, Pose2D to, double dist) {
        double deltaY = to.getY(DistanceUnit.INCH) - from.getY(DistanceUnit.INCH);
        double deltaX = to.getX(DistanceUnit.INCH) - from.getX(DistanceUnit.INCH);
        double scale = dist / Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return new Pose2D(DistanceUnit.INCH, to.getX(DistanceUnit.INCH) + deltaX * scale, to.getY(DistanceUnit.INCH) + deltaY * scale, AngleUnit.DEGREES, to.getHeading(AngleUnit.DEGREES));
    }

    private double SmoothStep(double t){
        return t*t*(3-2*t);
    }

    /*
    Returns the point represented by t% along the path from p1 to p2
     */
    private Pose2D CubicHermiteInterp(int start, double t){
        double[] vals = new double[12];
        for(int i = 0; i<4; i++){
            vals[i*3]=points[start+i].getX(DistanceUnit.INCH);
            vals[i*3+1]=points[start+i].getY(DistanceUnit.INCH);
            vals[i*3+2]=points[start+i].getHeading(AngleUnit.DEGREES);
        }
        double[] out = new double[3];
        for(int i = 0; i <3; i++){
            double A = vals[i];
            double B = vals[i+3];
            double C = vals[i+6];
            double D = vals[i+9];
            double a = (-A+(3*B)-(3*C)+D)/2.0;
            double b = A-(5*B)/2.0+2*C-D/2.0;
            double c = (-A+C)/2.0;
            out[i]=a*t*t*t+b*t*t+c*t+B;
        }
        return new Pose2D(DistanceUnit.INCH,out[0],out[1],AngleUnit.DEGREES,out[2]);
    }
}
