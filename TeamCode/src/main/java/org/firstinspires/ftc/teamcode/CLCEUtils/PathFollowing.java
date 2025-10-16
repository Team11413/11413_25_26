package org.firstinspires.ftc.teamcode.CLCEUtils;

import java.util.ArrayList;
import java.util.List;

public class PathFollowing {

    public static class Trajectory{
        public double simX, simY, simRot;
        public Trajectory(double x, double y, double rot){
            simX=x;
            simY=y;
            simRot=rot;
        }
    }
    public static class Point2D{
        public double x, y;
        public Point2D(double x, double y){
            this.x=x;
            this.y=y;
        }
        public Point2D Lerp(Point2D end, double t){
            return new Point2D((end.x-x)*t+x, (end.y-y)*t+y);
        }
    }

    public static class PathEvent{
        public double time;
        public Point2D heading;
    }

    public static class Path{
        public Spline spline;
        public double Duration;
        public List<PathEvent> headings;

    }

    /*
    If you do not have odometry, the starting point of the first line should always be 0,0
     */
    public static class Spline{
        public List<Point2D> points = new ArrayList<>();
    }
    /*
    A simple method of getting a target percent is to decide a duration it should take to travel a given spline
    using a 5 second travel time as an example you would use something like this in your loop:
    currentTime=0;
    targetTime=5;
    myspline= ...;
    currentPos=...;
    loop{
        currentTime+=1;
        currentPos=target;
        target=PointOnSpline(myspline,currentTime/targetTime;

     }
     */
    public static Point2D PointOnSpline(Spline spline, double targetPercent){
        int pointCount= spline.points.size();
        if(pointCount==0) {
            return new Point2D(0, 0);
        }
        int newSize = pointCount/2;
        if(pointCount%2==1){
            newSize++;
        }else{
            pointCount++;
        }
        newSize*=pointCount;
        List<Point2D> points = new ArrayList<>(newSize);
        points.addAll(spline.points);
        int nextSet = points.size()-1;
        int index = 0;
        while(index<nextSet){
            points.add(points.get(index).Lerp(points.get(index+1),targetPercent));
            index+=1;
            if(index==nextSet){
                nextSet=points.size()-1;
                index+=1;
            }
        }
        return points.get(index);
    }

    /*
    If you have some form of odometry, you can use that for your curPos,
    otherwise you can use the previous target as your curPos
     */
    public static Trajectory PosTargetToTrajectory(Point2D curPos, Point2D target, double maxSpeed){
        if(curPos.x==target.x&&curPos.y==target.y){
            return new Trajectory(0,0,0);
        }
        double xDist = target.x-curPos.x;
        double yDist = target.y- curPos.y;
        double scalar= xDist*xDist+yDist*yDist;
        // divide by maxSpeed when it is larger than the scalar,
        // otherwise divide by scalar
        // this ensures the final result is inside the 0-1 unit vector range
        double speedRatio= Math.max(scalar,maxSpeed);
        return new Trajectory(xDist/speedRatio, yDist/speedRatio, 0);
    }
}
