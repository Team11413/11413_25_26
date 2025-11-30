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
        REDGOALCLOSE(Utils.PoseInDeg(-36,-36,-45)),
        BLUEGOALCLOSE(Utils.PoseInDeg(-36,-36,-135)),
        BLUESPIKE1START(Utils.PoseInDeg(-24,-24,-90)),
        BLUESPIKE1END(Utils.PoseInDeg(-24,-30,-90));
        private final Pose2D pose;
         Pose(Pose2D p){
            pose=p;
        }
        public Pose2D get(){return pose;}
    }
}
