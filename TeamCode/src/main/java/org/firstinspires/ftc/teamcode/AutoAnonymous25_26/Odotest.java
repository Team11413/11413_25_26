package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.CLUtils.GBPinPointILocalizer;

@TeleOp(name = "Odotest", group = "Robot")
public class Odotest extends OpMode {
    GBPinPointILocalizer GB;

    @Override
    public void init() {
    }

    @Override
    public void loop() {
        if(GB==null){
            GB = new GBPinPointILocalizer(hardwareMap);
        }
        GB.update();
    telemetry.addLine("x;"+GB.getPose().getX(DistanceUnit.INCH));
    telemetry.addLine("y+"+GB.getPose().getY(DistanceUnit.INCH));   }
}
