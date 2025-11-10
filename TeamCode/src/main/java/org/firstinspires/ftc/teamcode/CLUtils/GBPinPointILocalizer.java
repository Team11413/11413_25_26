package org.firstinspires.ftc.teamcode.CLUtils;


import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

import java.util.Objects;


/*
Follow instructions on https://www.gobilda.com/content/user_manuals/3110-0002-0001%20User%20Guide.pdf?srsltid=AfmBOorM1PlzS2GLNSg2hNrXD6W5brI_C8CQShsL7EHB5A29S8V3XJBa
for setup.
 */
public class GBPinPointILocalizer implements ILocalizer {

    public static class Params {
        public double forwardOdoOffset = 0.0; // y position of the parallel encoder (in tick units)
        public double strafeOdoOffset = 0.0; // x position of the perpendicular encoder (in tick units)
    }

    public static Params PARAMS = new Params();

    /*
    Center field is 0,0
    Positive X is towards the Audience
    Positive Y is towards the Blue Alliance
    Positive heading is Counter-Clockwise
     */
    private Pose2D txPinpointRobot;
    GoBildaPinpointDriver pinpointDriver;

    public GBPinPointILocalizer(HardwareMap hardwareMap){
        pinpointDriver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
    }

    public void init(Pose2D start){
        pinpointDriver.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpointDriver.setOffsets(PARAMS.forwardOdoOffset, PARAMS.strafeOdoOffset, DistanceUnit.MM);

        // TODO: reverse encoder directions if needed
        GoBildaPinpointDriver.EncoderDirection forwardEncoderDir = GoBildaPinpointDriver.EncoderDirection.FORWARD;
        GoBildaPinpointDriver.EncoderDirection strafeEncoderDir = GoBildaPinpointDriver.EncoderDirection.FORWARD;

        pinpointDriver.setEncoderDirections(forwardEncoderDir, strafeEncoderDir);
//        pinpointDriver.setEncoderResolution(19.89436789,DistanceUnit.MM);

        pinpointDriver.resetPosAndIMU();
        pinpointDriver.setPosition(start);
        txPinpointRobot = start;
    }

    /*
    default start uses 0,0,0
     */
    public void init(){
        init(Utils.PoseInRad(0,0,0));
    }

    @Override
    public void setPose(Pose2D pose) {
        pinpointDriver.setPosition(pose);
    }

    @Override
    public Pose2D getPose() {
        return txPinpointRobot;
    }

    @Override
    public void update() {
        pinpointDriver.update();
        if (Objects.requireNonNull(pinpointDriver.getDeviceStatus()) == GoBildaPinpointDriver.DeviceStatus.READY) {
            txPinpointRobot = Utils.PoseInRad(pinpointDriver.getPosX(DistanceUnit.INCH), pinpointDriver.getPosY(DistanceUnit.INCH), pinpointDriver.getHeading(UnnormalizedAngleUnit.RADIANS));
        }
    }
}
