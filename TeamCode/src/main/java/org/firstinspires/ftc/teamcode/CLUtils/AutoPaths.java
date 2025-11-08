package org.firstinspires.ftc.teamcode.CLUtils;


public class AutoPaths {

    /*

     */
    public static Path[] Paths = new Path[]{
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
        BlueAudience,
        BlueRow1,       //intended for use with GenerateFromCurrent
    }

    public static Path getPath(PATH p){return Paths[p.ordinal()];}
}
