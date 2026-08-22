package org.firstinspires.ftc.teamcode.RobotTeleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Backends.AprilTagWebcam;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "Ja Morant")
public class JaMorant extends OpMode {

    Servo Flicker1;
    Servo Flicker2;
    Servo Flicker3;
    DcMotorEx leftShooter, rightShooter;
    Servo angleServo;

    boolean shooterOn = false;
    double power = 0.5;
    double step = 0.01;

    double servoPos = 0.0;        // middle position
    double servoStep = 0.05;      // how much angle changes each press

    boolean lastA = false;
    boolean lastUp = false;
    boolean lastDown = false;
    boolean lastLB = false;
    boolean lastRB = false;

    AprilTagWebcam aprilTagwebcam = new AprilTagWebcam();

    @Override
    public void init() {
        leftShooter = hardwareMap.get(DcMotorEx.class, "leftShooter");
        rightShooter = hardwareMap.get(DcMotorEx.class, "rightShooter");
        angleServo = hardwareMap.get(Servo.class, "angleServo");

        Flicker1 = hardwareMap.get(Servo.class,"Flicker1");
        Flicker2 = hardwareMap.get(Servo.class, "Flicker2");
        Flicker3 = hardwareMap.get(Servo.class, "Flicker3");

        Flicker1.setDirection(Servo.Direction.REVERSE);
        Flicker2.setDirection(Servo.Direction.FORWARD);
        Flicker3.setDirection(Servo.Direction.REVERSE);

        aprilTagwebcam.init(hardwareMap, telemetry);

        // Reverse left motor if needed
        leftShooter.setDirection(DcMotorSimple.Direction.REVERSE);
        rightShooter.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addLine("Shooter + Servo Ready");
        telemetry.update();

        Flicker1.setPosition(0);
        Flicker2.setPosition(0);
        Flicker3.setPosition(0);


    }

    @Override
    public void loop() {

        if (gamepad2.b){
            Flicker1.setPosition(0.695);
        } else {
            Flicker1.setPosition(0);
        }
        if (gamepad2.a){
            Flicker2.setPosition(0.695);
        } else {
            Flicker2.setPosition(0);
        }
        if (gamepad2.x){
            Flicker3.setPosition(0.695);
        } else {
            Flicker3.setPosition(0);
        }
        aprilTagwebcam.update();
        AprilTagDetection id20 = aprilTagwebcam.getTagBySpecificId(20);
        aprilTagwebcam.displayDetectionTelemetry(id20);

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

        angleServo.setPosition(servoPos);

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
        telemetry.update();
    }
}