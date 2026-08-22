package org.firstinspires.ftc.teamcode.TeleopCodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Backends.ColorSensorBackend;
import org.firstinspires.ftc.teamcode.IndexingFSMs.DistanceFSM;
import org.firstinspires.ftc.teamcode.RobotTeleop.Mecanum;

@TeleOp (name = "TeleopV3_GPP (ColorSensor+Mecanum+DistanceSensor")
public class TeleopV3 extends OpMode {
    Mecanum drive = new Mecanum();
    double forward, strafe, rotate;

    private ColorSensorBackend colorSensorBackend;

    final double timerforshoot = 0.75;

    final double flickerdown = 0.5;

    final double small = 0.1;

    private enum Color_Sensor_GPP {
        TRIGGER_GPP,
        SHOOTGREEN_GPP,
        FLICKER1DOWN_GPP,
        BREAK1_GPP,
        SHOOTPURPLE1_GPP,
        FLICKER2DOWN_GPP,
        BREAK2_GPP,
        SHOOTPURPLE2_GPP,
        FLICKER3DOWN_GPP,
    }

    //DISTANCE SENSOR FSM

    private enum DistanceSensorFSM {
        TRIGGER_DIST,
        FLICKER1_DIST,
        FLICKER1DOWN_DIST,
        BREAK1_DIST,
        FLICKER2_DIST,
        FLICKER2DOWN_DIST,
        BREAK2_DIST,
        FLICKER3_DIST,
        FLICKER3DOWN_DIST,
    }

    Servo Flicker1;
    Servo Flicker2;
    Servo Flicker3;
    Servo angleServo;

    DcMotor Intake;
    DcMotor leftShooter;
    DcMotor rightShooter;
    //DcMotor TurretBoy;

    private IMU imu;

    private int usedSensorGreen = 0;
    private int usedSensorPurple1 = 0;


    DistanceSensor distancesensor1;
    DistanceSensor distancesensor2;
    DistanceSensor distanceSensor3;

    Color_Sensor_GPP color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
    ElapsedTime color_sensor_gpp_timer = new ElapsedTime();

    DistanceSensorFSM distanceSensorFSM = DistanceSensorFSM.TRIGGER_DIST;
    ElapsedTime distanceSensorFSM_timer = new ElapsedTime();

    double power;
    double shooterpower = 0.7;
    double step = 0.1;
    double servoinitialpos = 0;


    private void flickSensor(int sensorId) {

        //COLOR SENSOR LOGIC
        switch (sensorId) {
            case 1:
                Flicker1.setPosition(0.695);
                break;
            case 2:
                Flicker2.setPosition(0.695);
                break;
            case 3:
                Flicker3.setPosition(0.695);
                break;
        }
    }


    @Override
    public void init() {
        angleServo = hardwareMap.get(Servo.class,"angleServo");

        //MECANUM
        drive.init(hardwareMap);

        Flicker1 = hardwareMap.get(Servo.class, "Flicker1");
        Flicker2 = hardwareMap.get(Servo.class, "Flicker2");
        Flicker3 = hardwareMap.get(Servo.class, "Flicker3");

        distancesensor1 = hardwareMap.get(DistanceSensor.class,"colorSensor1");
        distancesensor2 = hardwareMap.get(DistanceSensor.class,"colorSensor2");
        distanceSensor3 = hardwareMap.get(DistanceSensor.class,"colorSensor3");

        imu = hardwareMap.get(IMU.class,"imu");

        Flicker1.setDirection(Servo.Direction.REVERSE);
        Flicker2.setDirection(Servo.Direction.FORWARD);
        Flicker3.setDirection(Servo.Direction.REVERSE);

        Intake = hardwareMap.get(DcMotorEx.class,"Intake");
        leftShooter = hardwareMap.get(DcMotor.class,"leftShooter");
        rightShooter = hardwareMap.get(DcMotor.class,"rightShooter");
        rightShooter.setDirection(DcMotorSimple.Direction.REVERSE);

        //TurretBoy = hardwareMap.get(DcMotor.class,"TurretBoy");
        //TurretBoy.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        color_sensor_gpp_timer.reset();

        Flicker1.setPosition(0);
        Flicker2.setPosition(0);
        Flicker3.setPosition(0);

        colorSensorBackend = new ColorSensorBackend(hardwareMap);


    }

    @Override
    public void loop() {

        if (gamepad2.dpad_up){
            leftShooter.setPower(0.7);
            rightShooter.setPower(0.7);
        }
        if (gamepad2.dpad_left){
            shooterpower = shooterpower - step;
            leftShooter.setPower(Range.clip(shooterpower,0,1));
            rightShooter.setPower(Range.clip(shooterpower,0,1));
        }
        if (gamepad2.dpad_right){
            shooterpower = shooterpower + step;
            leftShooter.setPower(Range.clip(shooterpower,0,1));
            rightShooter.setPower(Range.clip(shooterpower,0,1));
        }
        if (gamepad2.dpad_down){
            leftShooter.setPower(0);
            rightShooter.setPower(0);
            telemetry.addLine("Power is 0");
        }
        telemetry.addData("Motorpower",shooterpower);

        //MECANUM
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drive.fieldCentric(forward, strafe, rotate);


        if (gamepad1.right_trigger > 0.1) {
            power = gamepad1.right_trigger; // Forward
        } else if (gamepad1.left_trigger > 0.1) {
            power = -gamepad1.left_trigger; // Reverse
        } else {
            power = 0.0;
        }

        // Apply power to the
        Intake.setPower(power);


        boolean up = gamepad1.dpad_up;
        /*
        if (gamepad2.dpad_down){
            leftShooter.setPower(0.7);
            rightShooter.setPower(0.7);

        }
        if (gamepad2.dpad_up){
            leftShooter.setPower(1);
            rightShooter.setPower(1);
        }

        if (gamepad2.dpad_right){
            leftShooter.setPower(0);
            rightShooter.setPower(0);
        } */

        //COLOR SENSOR LOGIC
        ColorSensorBackend.SensorResult[] sensors = colorSensorBackend.getDetectedColor(telemetry);

        if (gamepad2.y){
            Flicker1.setPosition(0);
            Flicker2.setPosition(0);
            Flicker3.setPosition(0);
            color_sensor_gpp_timer.reset();
            color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
        }

        switch (color_sensor_gpp) {
            case TRIGGER_GPP:
                if (gamepad2.a){
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.SHOOTGREEN_GPP;
                } break;
            case SHOOTGREEN_GPP:
                for (ColorSensorBackend.SensorResult sensor : sensors) {
                    if (sensor.color == ColorSensorBackend.detectedColor.GREEN_DIRECT || sensor.color == ColorSensorBackend.detectedColor.GREEN_INDIRECT) {
                        int sensorId = sensor.sensorId;
                        flickSensor(sensorId);
                        usedSensorGreen = sensor.sensorId;
                        color_sensor_gpp_timer.reset();
                        color_sensor_gpp = Color_Sensor_GPP.FLICKER1DOWN_GPP;
                        break;
                    }
                } break;
            case FLICKER1DOWN_GPP:
                if (color_sensor_gpp_timer.seconds() >= flickerdown) {
                    Flicker1.setPosition(0);
                    Flicker2.setPosition(0);
                    Flicker3.setPosition(0);
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.BREAK1_GPP;
                } break;
            case BREAK1_GPP:
                if (color_sensor_gpp_timer.seconds() >= timerforshoot){
                    Flicker1.setPosition(0);
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.SHOOTPURPLE1_GPP;
                } break;
            case SHOOTPURPLE1_GPP:
                for (ColorSensorBackend.SensorResult sensor : sensors) {
                    if (sensor.color == ColorSensorBackend.detectedColor.PURPLE_DIRECT || sensor.color == ColorSensorBackend.detectedColor.PURPLE_INDIRECT &&
                            sensor.sensorId != usedSensorGreen) {
                        int sensorId = sensor.sensorId;
                        usedSensorPurple1 = sensorId;
                        flickSensor(sensorId);
                        color_sensor_gpp_timer.reset();
                        color_sensor_gpp = Color_Sensor_GPP.FLICKER2DOWN_GPP;
                        break;
                    }
                } break;
            case FLICKER2DOWN_GPP:
                if (color_sensor_gpp_timer.seconds() >= flickerdown){
                    Flicker1.setPosition(0);
                    Flicker2.setPosition(0);
                    Flicker3.setPosition(0);
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.BREAK2_GPP;
                } break;
            case BREAK2_GPP:
                if (color_sensor_gpp_timer.seconds() >= timerforshoot) {
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.SHOOTPURPLE2_GPP;
                } break;
            case SHOOTPURPLE2_GPP:
                for (ColorSensorBackend.SensorResult sensor : sensors) {
                    if (sensor.color == ColorSensorBackend.detectedColor.PURPLE_DIRECT || sensor.color == ColorSensorBackend.detectedColor.PURPLE_INDIRECT &&
                            sensor.sensorId != usedSensorGreen &&
                            sensor.sensorId != usedSensorPurple1) {
                        int sensorId = sensor.sensorId;
                        flickSensor(sensorId);
                        color_sensor_gpp_timer.reset();
                        color_sensor_gpp = Color_Sensor_GPP.FLICKER3DOWN_GPP;
                        break;
                    }
                } break;
            case FLICKER3DOWN_GPP:
                if (color_sensor_gpp_timer.seconds() >= flickerdown){
                    Flicker1.setPosition(0);
                    Flicker2.setPosition(0);
                    Flicker3.setPosition(0);
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
                } break;

        }

        double distance1 = distancesensor1.getDistance(DistanceUnit.CM);
        double distance2 = distancesensor2.getDistance(DistanceUnit.CM);
        double distance3 = distanceSensor3.getDistance(DistanceUnit.CM);

        if (gamepad2.x){
            Flicker1.setPosition(0);
            Flicker2.setPosition(0);
            Flicker3.setPosition(0);
            distanceSensorFSM_timer.reset();
            distanceSensorFSM = DistanceSensorFSM.TRIGGER_DIST;
        }

        switch (distanceSensorFSM){
            case TRIGGER_DIST:
                if (gamepad2.b){
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.FLICKER1_DIST;
                } break;
            case FLICKER1_DIST:
                if (distance1 < 6 || distance2 < 6 || distance3 < 6){
                    Flicker1.setPosition(0.695);
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.FLICKER1DOWN_DIST;
                } break;
            case FLICKER1DOWN_DIST:
                if (distanceSensorFSM_timer.seconds() > flickerdown){
                    Flicker1.setPosition(0);
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.BREAK1_DIST;
                } break;
            case BREAK1_DIST:
                if (distanceSensorFSM_timer.seconds() > timerforshoot){
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.FLICKER2_DIST;
                } break;
            case FLICKER2_DIST:
                if (distance1 < 6 || distance2 < 6 || distance3 < 6){
                    Flicker2.setPosition(0.695);
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.FLICKER2DOWN_DIST;
                } break;
            case FLICKER2DOWN_DIST:
                if (distanceSensorFSM_timer.seconds() > flickerdown){
                    Flicker2.setPosition(0);
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.BREAK2_DIST;
                } break;
            case BREAK2_DIST:
                if (distanceSensorFSM_timer.seconds() > timerforshoot){
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.FLICKER3_DIST;
                } break;
            case FLICKER3_DIST:
                if (distance1 < 6 || distance2 < 6 || distance3 < 6){
                    Flicker3.setPosition(0.695);
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.FLICKER3DOWN_DIST;
                } break;
            case FLICKER3DOWN_DIST:
                if (distanceSensorFSM_timer.seconds() > flickerdown){
                    Flicker3.setPosition(0);
                    distanceSensorFSM_timer.reset();
                    distanceSensorFSM = DistanceSensorFSM.TRIGGER_DIST;
                } break;
        }

        if (gamepad1.dpad_down){
            imu.resetYaw();
        }
        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        telemetry.addData("Heading (deg)", Math.toDegrees(botHeading));
        telemetry.update();

    }

}





