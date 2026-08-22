package org.firstinspires.ftc.teamcode.RobotTeleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class IntakeRoller extends OpMode {

    private DcMotor Intake;


    @Override
    public void init() {
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        Intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry.addLine("Move the right joystick along y axis to operate intake roller");
    }

    @Override
    public void loop() {
        Intake.setPower(-gamepad1.left_trigger);
        Intake.setPower(gamepad1.right_trigger);
    }
}
