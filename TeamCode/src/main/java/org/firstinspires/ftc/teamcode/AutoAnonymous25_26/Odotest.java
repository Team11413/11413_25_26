package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.CLUtils.GBPinPointLocalizer;

@TeleOp(name = "Odotest", group = "Robot")
public class Odotest extends OpMode {
    GBPinPointLocalizer GB;

    @Override
    public void init() {
    }

    @Override
    public void loop() {
        if(GB==null){
            GB = new GBPinPointLocalizer(hardwareMap);
        }
        GB.update();
    telemetry.addLine("x;"+GB.getPose().getX(DistanceUnit.INCH));
    telemetry.addLine("y+"+GB.getPose().getY(DistanceUnit.INCH));   }
}
