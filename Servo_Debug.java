package org.firstinspires.ftc.teamcode.Backends;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp (name = "angle+power")
public class Servo_Debug extends OpMode {

    DcMotorEx leftShooter, rightShooter;
    Servo angleServo;
    boolean shooterOn = false;
    double power = 0.5;
    double step = 0.1;

    double servoPos = 0.0;        // middle position
    double servoStep = 0.05;      // how much angle changes each press

    boolean lastA = false;
    boolean lastUp = false;
    boolean lastDown = false;
    boolean lastLB = false;
    boolean lastRB = false;

    double servoinitialpos = 0;

    AprilTagWebcam aprilTagwebcam = new AprilTagWebcam();


    @Override
    public void init() {
        angleServo = hardwareMap.get(Servo.class,"angleServo");
        angleServo.setPosition(servoinitialpos);
        telemetry.addLine("This code will help you find precise servo input values for components requiring varying positions without cooking the servo");
        telemetry.addLine("You do NOT need to be a programmer to work with this code. This has been made specifically so you dont have to change values in the code nor dump any code");
        telemetry.addLine("Pressing gamepad a will increase the servos position by 0.05");
        telemetry.addLine("Pressing gamepad b will decrease the servos position by 0.05");
        telemetry.addLine("The current position  of the servo will be constantly displayed in the ServoPosition Line");
        telemetry.addLine("Instructions are given once running the code on how to save higher or lower values for the servo");

        aprilTagwebcam.init(hardwareMap, telemetry);

        leftShooter = hardwareMap.get(DcMotorEx.class, "leftShooter");
        rightShooter = hardwareMap.get(DcMotorEx.class, "rightShooter");
        leftShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Reverse left motor if needed
        leftShooter.setDirection(DcMotorSimple.Direction.REVERSE);
        rightShooter.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addLine("Shooter + Servo Ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        aprilTagwebcam.update();
        AprilTagDetection id20 = aprilTagwebcam.getTagBySpecificId(20);
        aprilTagwebcam.displayDetectionTelemetry(id20);

        if (gamepad2.a){
            servoinitialpos = servoinitialpos + 0.01;
            angleServo.setPosition(servoinitialpos);
        }
        if (gamepad2.b){
            servoinitialpos = servoinitialpos - 0.01;
            angleServo.setPosition(servoinitialpos);

        }
        //Clamping
        servoinitialpos = Math.max(0, Math.min(1, servoinitialpos));
        angleServo.setPosition(servoinitialpos);

        telemetry.addData("ServoPosition","%.3f",servoinitialpos);
        updateTelemetry(telemetry);

        if (gamepad1.dpad_down){
            telemetry.addLine("Press dpad down to print the lower value of the servo position at any given point using the program");
            telemetry.addData("LowerPosition","%.3f",servoinitialpos);
            updateTelemetry(telemetry);
        }
        if (gamepad2.dpad_down){
            telemetry.addLine("Press dpad up to print the higher value of the servo position at any given point using the program");
            telemetry.addData("UpperPosition","%.3f",servoinitialpos);
            updateTelemetry(telemetry);
        }
        // -------- Shooter Toggle: A --------
        boolean a = gamepad1.a;
        if (a && !lastA) shooterOn = !shooterOn;
        lastA = a;

        // -------- Power Up --------
        boolean up = gamepad1.dpad_up;
        if (up && !lastUp) {
            power += step;
            if (power > 1) power = 1;
        }
        lastUp = up;

        // -------- Power Down --------
        boolean down = gamepad1.dpad_down;
        if (down && !lastDown) {
            power -= step;
            if (power < 0) power = 0;
        }
        lastDown = down;

        /*
        // -------- Servo Angle Control --------
        boolean lb = gamepad1.left_bumper;
        if (lb && !lastLB) {
            servoPos -= servoStep;
            if (servoPos < 0) servoPos = 0;
        }
        lastLB = lb;

        boolean rb = gamepad1.right_bumper;
        if (rb && !lastRB) {
            servoPos += servoStep;
            if (servoPos > 1) servoPos = 1;
        }
        lastRB = rb;

        angleServo.setPosition(servoPos); */

        // -------- Apply Shooter Power --------
        if (shooterOn) {
            leftShooter.setPower(power);
            rightShooter.setPower(power);

        } else {
            leftShooter.setPower(0);
            rightShooter.setPower(0);
        }


        // -------- Telemetry --------
        telemetry.addData("Shooter", shooterOn ? "ON" : "OFF");
        telemetry.addData("Shooter Power", "%.2f", power);
        telemetry.addData("Servo Position", "%.2f", servoPos);
        telemetry.addData("Ticks",".2f",leftShooter.getCurrentPosition(), rightShooter.getCurrentPosition());
        telemetry.update();


    }
}

