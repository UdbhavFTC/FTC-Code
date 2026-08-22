package org.firstinspires.ftc.teamcode.RobotTeleop;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class Mecanum {

    private DcMotor frontLeft, backLeft, frontRight, backRight;
    private IMU imu;

    public void init(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);

        imu.initialize(new IMU.Parameters(RevOrientation));


    }

    public void drive (double forward, double strafe, double rotate){

        double fLeftPower = forward + strafe + rotate;
        double fRightPower = forward - strafe - rotate;
        double bLeftPower = forward - strafe + rotate;
        double bRightPower = forward + strafe - rotate;

        double maxPower = 1;
        double maxSpeed = 1;

        maxPower = Math.max(maxPower, Math.abs(fLeftPower));
        maxPower = Math.max(maxPower, Math.abs(fRightPower));
        maxPower = Math.max(maxPower, Math.abs(bLeftPower));
        maxPower = Math.max(maxPower, Math.abs(bRightPower));

        frontLeft.setPower(maxSpeed * (fLeftPower / maxPower));
        frontRight.setPower(maxSpeed * (fRightPower / maxPower));
        backLeft.setPower(maxSpeed * (bLeftPower / maxPower));
        backRight.setPower(maxSpeed * (bRightPower / maxPower));
    }


    public void fieldCentric (double forward, double strafe, double rotate){

        double theta = Math.atan2(forward, strafe);
        double radius = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newForward = radius * Math.sin(theta);
        double newStrafe = radius * Math.cos(theta);

        this.drive(newForward, newStrafe, rotate);

    }

}
