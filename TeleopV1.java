package org.firstinspires.ftc.teamcode.TeleopCodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotTeleop.Mecanum;

@TeleOp (name = "TeleopV1")
public class TeleopV1 extends OpMode {
    Mecanum drive = new Mecanum();
    double forward, strafe, rotate;

    Servo Flicker1;
    Servo Flicker2;
    Servo Flicker3;

    DcMotor Intake;



    @Override
    public void init() {
        drive.init(hardwareMap);

        Flicker1 = hardwareMap.get(Servo.class,"Flicker1");
        Flicker2 = hardwareMap.get(Servo.class,"Flicker2");
        Flicker3 = hardwareMap.get(Servo.class,"Flicker3");

        Intake = hardwareMap.get(DcMotor.class,"Intake");

        //REVERSE AS NECCESARY
        Flicker1.setDirection(Servo.Direction.REVERSE);
        Flicker2.setDirection(Servo.Direction.FORWARD);
        Flicker3.setDirection(Servo.Direction.REVERSE);
        Flicker1.setPosition(0);
        Flicker2.setPosition(0);
        Flicker3.setPosition(0);

    }

    @Override
    public void loop() {
        //MECANUM
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        //drive.fieldCentric(forward, strafe, rotate);

        //FLICKERS
        if (gamepad2.a){
            Flicker1.setPosition(0.695);
        }
        if (gamepad2.b){
            Flicker2.setPosition(0.695);
        }
        if (gamepad2.x){
            Flicker3.setPosition(0.695);
        }
        if (gamepad2.y){
            Flicker1.setPosition(0);
            Flicker2.setPosition(0);
            Flicker3.setPosition(0);
        }


        //INTAKE
        Intake.setPower(gamepad2.right_stick_y*0.7);

    }
}

