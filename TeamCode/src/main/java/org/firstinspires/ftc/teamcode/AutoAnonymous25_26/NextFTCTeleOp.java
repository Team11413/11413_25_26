package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp(name= "NextFTC Testing")
public class NextFTCTeleOp extends NextFTCOpMode {

    public NextFTCTeleOp(){
        addComponents(
                new SubsystemComponent()
        );
    }

    @Override
    public void onStartButtonPressed(){
        MecanumDriveSS.INSTANCE.onStart();
    }
}
