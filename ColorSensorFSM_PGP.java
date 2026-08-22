package org.firstinspires.ftc.teamcode.IndexingFSMs;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Backends.ColorSensorBackend;

@TeleOp(name="ColorSensorCode_PGP")
public class ColorSensorFSM_PGP extends OpMode {

    private ColorSensorBackend colorSensorBackend;

    final double timerforshoot = 0.75;

    final double flickerdown = 0.5;

    private enum Color_Sensor_PGP {
        TRIGGER_PGP,
        SHOOTPURPLE1_PGP,
        FLICKER1DOWN_PGP,
        BREAK1_PGP,
        SHOOTGREEN_PGP,
        FLICKER2DOWN_PGP,
        BREAK2_PGP,
        SHOOTPURPLE2_PGP,
        FLICKER3DOWN_PGP,
    }

    //PURPLE GREEN PURPLE
    ColorSensorFSM_PGP.Color_Sensor_PGP color_sensor_pgp = ColorSensorFSM_PGP.Color_Sensor_PGP.TRIGGER_PGP;
    ElapsedTime color_sensor_pgp_timer = new ElapsedTime();

    Servo Flicker1;
    Servo Flicker2;
    Servo Flicker3;

    private int usedSensorGreen = 0;
    private int usedSensorPurple1 = 0;

    private void flickSensor(int sensorId) {
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
        Flicker1 = hardwareMap.get(Servo.class, "Flicker1");
        Flicker2 = hardwareMap.get(Servo.class, "Flicker2");
        Flicker3 = hardwareMap.get(Servo.class, "Flicker3");

        color_sensor_pgp_timer.reset();

        Flicker1.setPosition(0);
        Flicker2.setPosition(0);
        Flicker3.setPosition(0);

        colorSensorBackend = new ColorSensorBackend(hardwareMap);

    }

    @Override
    public void loop() {
        ColorSensorBackend.SensorResult[] sensors = colorSensorBackend.getDetectedColor(telemetry);

        if (gamepad2.b) {
            Flicker1.setPosition(0.695);
            color_sensor_pgp_timer.reset();
            color_sensor_pgp = Color_Sensor_PGP.TRIGGER_PGP;
            //Can integrate the turret override on press of button as well in main
        }
        if (gamepad2.x) {
            Flicker2.setPosition(0.695);
            color_sensor_pgp_timer.reset();
            color_sensor_pgp = Color_Sensor_PGP.TRIGGER_PGP;
        }
        if (gamepad2.y) {
            Flicker3.setPosition(0.695);
            color_sensor_pgp_timer.reset();
            color_sensor_pgp = Color_Sensor_PGP.TRIGGER_PGP;

        }
        switch (color_sensor_pgp) {
            case TRIGGER_PGP:
                if (gamepad2.a) {
                    color_sensor_pgp_timer.reset();
                    color_sensor_pgp = Color_Sensor_PGP.SHOOTPURPLE1_PGP;
                }
                break;
            case SHOOTPURPLE1_PGP:
                for (ColorSensorBackend.SensorResult sensor : sensors) {
                    if (sensor.color == ColorSensorBackend.detectedColor.PURPLE_DIRECT || sensor.color == ColorSensorBackend.detectedColor.PURPLE_INDIRECT) {
                        int sensorId = sensor.sensorId;
                        flickSensor(sensorId);
                        usedSensorPurple1 = sensor.sensorId;
                        color_sensor_pgp_timer.reset();
                        color_sensor_pgp = Color_Sensor_PGP.FLICKER1DOWN_PGP;
                        break;
                    }
                } break;
            case FLICKER1DOWN_PGP:
                if (color_sensor_pgp_timer.seconds() >= flickerdown){
                    Flicker1.setPosition(0);
                    Flicker2.setPosition(0);
                    Flicker3.setPosition(0);
                    color_sensor_pgp_timer.reset();
                    color_sensor_pgp = Color_Sensor_PGP.BREAK1_PGP;
                } break;
            case BREAK1_PGP:
                if (color_sensor_pgp_timer.seconds() >= timerforshoot) {
                    color_sensor_pgp_timer.reset();
                    color_sensor_pgp = Color_Sensor_PGP.SHOOTGREEN_PGP;
                    break;
                }
            case SHOOTGREEN_PGP:
                for (ColorSensorBackend.SensorResult sensor : sensors) {
                    if (sensor.color == ColorSensorBackend.detectedColor.GREEN_DIRECT || sensor.color == ColorSensorBackend.detectedColor.GREEN_INDIRECT &&
                            sensor.sensorId != usedSensorPurple1) {
                        int sensorId = sensor.sensorId;
                        flickSensor(sensorId);
                        usedSensorGreen = sensor.sensorId;
                        color_sensor_pgp_timer.reset();
                        color_sensor_pgp = Color_Sensor_PGP.FLICKER2DOWN_PGP;
                        break;
                    }
                } break;
            case FLICKER2DOWN_PGP:
                if (color_sensor_pgp_timer.seconds() >= flickerdown) {
                    Flicker1.setPosition(0);
                    Flicker2.setPosition(0);
                    Flicker3.setPosition(0);
                    color_sensor_pgp_timer.reset();
                    color_sensor_pgp = Color_Sensor_PGP.BREAK2_PGP;
                } break;
            case BREAK2_PGP:
                if (color_sensor_pgp_timer.seconds() >= timerforshoot){
                    color_sensor_pgp_timer.reset();
                    color_sensor_pgp = Color_Sensor_PGP.SHOOTPURPLE2_PGP;
                    break;
                }
            case SHOOTPURPLE2_PGP:
                for (ColorSensorBackend.SensorResult sensor : sensors) {
                    if (sensor.color == ColorSensorBackend.detectedColor.PURPLE_DIRECT || sensor.color == ColorSensorBackend.detectedColor.PURPLE_INDIRECT &&
                            sensor.sensorId != usedSensorGreen &&
                            sensor.sensorId != usedSensorPurple1) {
                        int sensorId = sensor.sensorId;
                        flickSensor(sensorId);
                        color_sensor_pgp_timer.reset();
                        color_sensor_pgp = Color_Sensor_PGP.FLICKER3DOWN_PGP;
                        break;
                    }
                } break;
            case FLICKER3DOWN_PGP:
                if (color_sensor_pgp_timer.seconds() >= flickerdown) {
                    Flicker1.setPosition(0);
                    Flicker2.setPosition(0);
                    Flicker3.setPosition(0);
                    color_sensor_pgp_timer.reset();
                    color_sensor_pgp = Color_Sensor_PGP.TRIGGER_PGP;
                } break;

        }

    }
}


