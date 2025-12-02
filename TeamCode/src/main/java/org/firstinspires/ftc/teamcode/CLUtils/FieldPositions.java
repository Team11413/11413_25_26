package org.firstinspires.ftc.teamcode.CLUtils;


import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class FieldPositions {

    /*
        These positions are using the FTC coordinates, defined as:
        Standing in the center of the field and facing the audience is 0,0 with a heading of 0
        moving toward the audience is positive x
        moving left is positive y
        rotating counter-clockwise is positive heading
     */
    public enum Pose{
        REDGOALCLOSE(Utils.PoseInDeg(-58,60,135)),
        BLUEGOALCLOSE(Utils.PoseInDeg(-58,-60,-135)),
        BLUESPIKE1START(Utils.PoseInDeg(-24,-24,-90)),
        BLUESPIKE1END(Utils.PoseInDeg(-24,-30,-90)),
        BLUEGOAL(Utils.PoseInDeg(-72,-72, -135)),
        REDGOAL(Utils.PoseInDeg(-72,72,135)),
        BLUEPLAYER(Utils.PoseInDeg(63,63,0)),
        REDPLAYER(Utils.PoseInDeg(-63,-63,0));
        private final Pose2D pose;
         Pose(Pose2D p){
            pose=p;
        }
        public Pose2D get(){return pose;}
    }
}
