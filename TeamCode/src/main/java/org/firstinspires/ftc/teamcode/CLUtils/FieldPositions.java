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
        strafe offset in x, 1.31 in towrd back
     */
    public enum Pose{
        CENTER(Utils.PoseInDeg(0,0,0)),
        BLUEGOAL(Utils.PoseInDeg(-64,-64, -128)),
        BLUEGOALSTART(Utils.PoseInDeg(-59.19,-47.05,50)),
        BLUEGOALOFFSET(Utils.PoseInDeg(-69,-50,52)),
        BLUEGOALSCORE(Utils.PoseInDeg(-22.41,-17.04,-133)),
        BLUESPIKE1START(Utils.PoseInDeg(-12,-28.61,-90)),
        BLUESPIKE1END(Utils.PoseInDeg(-12,-50,-90)),
        BLUESPIKE2START(Utils.PoseInDeg(12,-28,-90)),
        BLUESPIKE2END(Utils.PoseInDeg(12,-50,-90)),
        BLUESPIKE3START(Utils.PoseInDeg(36,-28,-90)),
        BLUESPIKE3END(Utils.PoseInDeg(36,-50,-90)),
        BLUEAUDIENCESTART(Utils.PoseInDeg(67,-27,-180)),
        BLUEAUDIENCESCORE(Utils.PoseInDeg(65,-29,-180)),
        BLUEPLAYER(Utils.PoseInDeg(64,61,179.7)),
        REDGOAL(Utils.PoseInDeg(-62,63, 128)),
        REDGOALSTART(Utils.PoseInDeg(-59.19,47.05,-50)),
        REDGOALOFFSET(Utils.PoseInDeg(-69,50,-52)),
        REDGOALSCORE(Utils.PoseInDeg(-22.41,17.04,133)),
        REDSPIKE1START(Utils.PoseInDeg(-12,28.61,90)),
        REDSPIKE1END(Utils.PoseInDeg(-12,50,90)),
        REDSPIKE2START(Utils.PoseInDeg(12,28,90)),
        REDSPIKE2END(Utils.PoseInDeg(12,50,90)),
        REDSPIKE3START(Utils.PoseInDeg(36,28,90)),
        REDSPIKE3END(Utils.PoseInDeg(36,50,90)),
        REDAUDIENCESTART(Utils.PoseInDeg(67,29,180)),
        REDAUDIENCESCORE(Utils.PoseInDeg(65,27,180)),
        REDPLAYER(Utils.PoseInDeg(64,-61,-179.7));

        private final Pose2D pose;
         Pose(Pose2D p){
            pose=p;
        }
        public Pose2D get(){return pose;}
    }
}
