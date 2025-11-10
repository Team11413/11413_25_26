package org.firstinspires.ftc.teamcode.CLUtils;


public class AutoPaths {

    /*

     */
    public static Path[] Paths = new Path[]{
            //TestForward
            Path.NonGeneratedPath(
                    Utils.PoseInDeg(1,0,0),
                    Utils.PoseInDeg(10,0,0)
            ),
            //BlueAudience
            Path.GeneratePath(5,
                    Utils.PoseInDeg(63,12,180),
                    Utils.PoseInDeg(59,16,225)
            ),
            //BlueRow1
            Path.NonGeneratedPath(
                    Utils.PoseInDeg(-12,-24,90),
                    Utils.PoseInDeg(-12,-36,90),
                    Utils.PoseInDeg(-12,-48,90)
            )
    };

    public enum PATH {
        TestForward,
        BlueAudience,
        BlueRow1,       //intended for use with GenerateFromCurrent
    }

    public static Path getPath(PATH p){return Paths[p.ordinal()];}
}
