package org.firstinspires.ftc.teamcode.CLUtils;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.PinpointLocalizer;


/*
Follow instructions on https://www.gobilda.com/content/user_manuals/3110-0002-0001%20User%20Guide.pdf?srsltid=AfmBOorM1PlzS2GLNSg2hNrXD6W5brI_C8CQShsL7EHB5A29S8V3XJBa
for setup.
 */
public class GBPinPointLocalizer implements Localizer{

    public static class Params {
        public double parYTicks = 0.0; // y position of the parallel encoder (in tick units)
        public double perpXTicks = 0.0; // x position of the perpendicular encoder (in tick units)
    }

    public static PinpointLocalizer.Params PARAMS = new PinpointLocalizer.Params();

    private Pose2d txWorldPinpoint;
    private Pose2d txPinpointRobot = new Pose2d(0, 0, 0);
    GoBildaPinpointDriver pinpointDriver;

    public GBPinPointLocalizer(HardwareMap hardwareMap){
        pinpointDriver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
    }

    public void init(Pose2d start){
        double mmPerTick= 19.894;// 2000 ticks/rev divided by (32 mm diam * Pi)
        pinpointDriver.setEncoderResolution(1 / mmPerTick, DistanceUnit.MM);
        pinpointDriver.setOffsets(mmPerTick * PARAMS.parYTicks, mmPerTick * PARAMS.perpXTicks, DistanceUnit.MM);

        // TODO: reverse encoder directions if needed
        GoBildaPinpointDriver.EncoderDirection forwardEncoderDir = GoBildaPinpointDriver.EncoderDirection.FORWARD;
        GoBildaPinpointDriver.EncoderDirection strafeEncoderDir = GoBildaPinpointDriver.EncoderDirection.FORWARD;

        pinpointDriver.setEncoderDirections(forwardEncoderDir, strafeEncoderDir);

        pinpointDriver.resetPosAndIMU();

        txWorldPinpoint = start;
    }

    public void init(){
        init(txPinpointRobot);
    }

    @Override
    public void setPose(Pose2d pose) {

    }

    @Override
    public Pose2d getPose() {
        return null;
    }

    @Override
    public PoseVelocity2d update() {
        return null;
    }
}
