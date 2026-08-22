package org.firstinspires.ftc.teamcode.IndexingFSMs;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Backends.ColorSensorBackend;

@TeleOp (name="ColorSensorCode_GPP")
public class ColorSensorFSM_GPP extends OpMode {
    private ColorSensorBackend colorSensorBackend;

    final double timer = 0.5;

    final double timerforshoot = 0.75;

    private enum Color_Sensor_GPP {
        TRIGGER_GPP,
        SHOOTGREEN_GPP,
        BREAK1_GPP,
        SHOOTPURPLE1_GPP,
        BREAK2_GPP,
        SHOOTPURPLE2_GPP,
        BREAK3_GPP,
        RESETFLICKERS_GPP
    }


    Servo Flicker1;
    Servo Flicker2;
    Servo Flicker3;

    private int usedSensorGreen = 0;
    private int usedSensorPurple1 = 0;

    //GREEN PURPLE PURPLE
    Color_Sensor_GPP color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
    ElapsedTime color_sensor_gpp_timer = new ElapsedTime();

    //PURPLE PURPLE GREEN

    private void flickSensor(int sensorId) {
        switch (sensorId) {
            case 1:
                Flicker1.setPosition(1.0);
                break;
            case 2:
                Flicker2.setPosition(1.0);
                break;
            case 3:
                Flicker3.setPosition(1.0);
                break;
        }
    }


    @Override
    public void init() {

        Flicker1 = hardwareMap.get(Servo.class, "Flicker1");
        Flicker2 = hardwareMap.get(Servo.class, "Flicker2");
        Flicker3 = hardwareMap.get(Servo.class, "Flicker3");

        color_sensor_gpp_timer.reset();

        Flicker1.setPosition(0);
        Flicker2.setPosition(0);
        Flicker3.setPosition(0);

        colorSensorBackend = new ColorSensorBackend(hardwareMap);


    }

    @Override
    public void loop() {

        ColorSensorBackend.SensorResult[] sensors = colorSensorBackend.getDetectedColor(telemetry);

        if (gamepad1.b){
            Flicker1.setPosition(1);
            color_sensor_gpp_timer.reset();
            color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
            //Can integrate the turret override on press of button as well in main
        }
        if (gamepad1.x){
            Flicker2.setPosition(1);
            color_sensor_gpp_timer.reset();
            color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
        }
        if (gamepad1.y){
            Flicker3.setPosition(1);
            color_sensor_gpp_timer.reset();
            color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
        }

        switch (color_sensor_gpp) {
            case TRIGGER_GPP:
                if (gamepad1.a){
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
                        color_sensor_gpp = Color_Sensor_GPP.BREAK1_GPP;
                        break;
                    }
                } break;
            case BREAK1_GPP:
                if (color_sensor_gpp_timer.seconds() >= timerforshoot){
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
                        color_sensor_gpp = Color_Sensor_GPP.BREAK2_GPP;
                        break;
                    }
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
                        color_sensor_gpp = Color_Sensor_GPP.BREAK3_GPP;
                        break;
                    }
                } break;
            case BREAK3_GPP:
                if (color_sensor_gpp_timer.seconds() >= timerforshoot){
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.RESETFLICKERS_GPP;
                } break;
            case RESETFLICKERS_GPP:
                if (color_sensor_gpp_timer.seconds() >= timer){
                    Flicker1.setPosition(0);
                    Flicker2.setPosition(0);
                    Flicker3.setPosition(0);
                    color_sensor_gpp_timer.reset();
                    color_sensor_gpp = Color_Sensor_GPP.TRIGGER_GPP;
                    break;
                } break;
        }

    }

}



