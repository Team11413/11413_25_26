package org.firstinspires.ftc.teamcode.CLUtils;


import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class FieldPositions {

    /*
        These positions are using the FTC coordinates, defined as:
        Standing in the center of the field and facing the audience is 0,0 with a heading of 0
        moving toward the audience is positive x
        moving left is positive y
        rotating counter-clockwise is positive heading
        The field ranges from 72,72 to -72,-72
        Robot offset from back right 6.4565, 8.74
        Robot offset from front left 10.9015, 8.819
        forward offset in y, 7.6085 in toward right
        strafe offset in x, 1.31 in toward back
     */
    public enum Pose{
        CENTER(Utils.PoseInDeg(0,0,0)),
        BLUEGOAL(Utils.PoseInDeg(-69,-69, -135)),
        BLUEGOALSTART(Utils.PoseInDeg(-58,-60,-135)),
        BLUEGOALSCORE(Utils.PoseInDeg(-60,-60,-135)),
        BLUESPIKE1LINEUP(Utils.PoseInDeg(-24,-20,-90)),
        BLUESPIKE1START(Utils.PoseInDeg(-24,-24,-90)),
        BLUESPIKE1END(Utils.PoseInDeg(-24,-30,-90)),
        BLUESPIKE2LINEUP(Utils.PoseInDeg(0,-20,-90)),
        BLUESPIKE2START(Utils.PoseInDeg(0,-24,-90)),
        BLUESPIKE2END(Utils.PoseInDeg(0,-30,-90)),
        BLUESPIKE3LINEUP(Utils.PoseInDeg(24,-20,-90)),
        BLUESPIKE3START(Utils.PoseInDeg(24,-24,-90)),
        BLUESPIKE3END(Utils.PoseInDeg(24,-30,-90)),
        BLUEAUDIENCESTART(Utils.PoseInDeg(63,-12,-180)),
        BLUEPLAYER(Utils.PoseInDeg(72-6.4565,72-8.74,-180)),
        REDGOAL(Utils.PoseInDeg(-69,69, 135)),
        REDGOALSTART(Utils.PoseInDeg(-58,60,135)),
        REDGOALSCORE(Utils.PoseInDeg(-60,60,135)),
        REDSPIKE1LINEUP(Utils.PoseInDeg(-24,20,90)),
        REDSPIKE1START(Utils.PoseInDeg(-24,24,90)),
        REDSPIKE1END(Utils.PoseInDeg(-24,30,90)),
        REDSPIKE2LINEUP(Utils.PoseInDeg(0,20,90)),
        REDSPIKE2START(Utils.PoseInDeg(0,24,90)),
        REDSPIKE2END(Utils.PoseInDeg(0,30,90)),
        REDSPIKE3LINEUP(Utils.PoseInDeg(24,20,90)),
        REDSPIKE3START(Utils.PoseInDeg(24,24,90)),
        REDSPIKE3END(Utils.PoseInDeg(24,30,90)),
        REDAUDIENCESTART(Utils.PoseInDeg(63,12,180)),
        REDPLAYER(Utils.PoseInDeg(72-6.4565,-72+8.819,0));
        private final Pose2D pose;
         Pose(Pose2D p){
            pose=p;
        }
        public Pose2D get(){return pose;}
    }
}
