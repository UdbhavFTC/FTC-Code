package org.firstinspires.ftc.teamcode.Backends;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "servotest")
public class Servotest extends OpMode {

    Servo angleServo;

    public void init(){
        angleServo = hardwareMap.get(Servo.class,"angleServo");

    }

    public void loop(){

        if (gamepad1.a){
            angleServo.setPosition(0.3);
        }
        if (gamepad1.a){
            angleServo.setPosition(0);
        }

    }
}
