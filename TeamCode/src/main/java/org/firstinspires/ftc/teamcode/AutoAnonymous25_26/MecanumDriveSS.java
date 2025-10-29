package org.firstinspires.ftc.teamcode.AutoAnonymous25_26;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;


import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.MotorEx;

public class MecanumDriveSS implements Subsystem {
    public static final MecanumDriveSS INSTANCE = new MecanumDriveSS();

//    public static MecanumDriveSS getInstance(HardwareMap hm) {
//        if (INSTANCE == null) {
//            INSTANCE = new MecanumDriveSS(hm);
//        }
//        return INSTANCE;
//    }
    private MotorEx[] motors = {
            new MotorEx("frontLeftDrive"),
            new MotorEx("frontRightDrive"),
            new MotorEx("backLeftDrive"),
            new MotorEx("backRightDrive")
    };
    private MecanumDriveSS(){
    }

    public void onStart(){
        Command driverControlled = new MecanumDriverControlled(
                motors[0],motors[1],motors[2],motors[3],
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX()
        );
        driverControlled.schedule();
    }

}
