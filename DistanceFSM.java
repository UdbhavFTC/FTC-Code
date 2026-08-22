package org.firstinspires.ftc.teamcode.IndexingFSMs;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Backends.ColorSensorBackend;

@TeleOp (name = "DistanceFSM")
public class DistanceFSM extends OpMode {

    final double flickerdown = 0.5;

    final double timerforshoot = 0.75;

    Servo Flicker1;
    Servo Flicker2;
    Servo Flicker3;

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

    DistanceSensor distancesensor1;
    DistanceSensor distancesensor2;
    DistanceSensor distanceSensor3;

    NormalizedColorSensor colorSensor1;
    NormalizedColorSensor colorSensor2;
    NormalizedColorSensor colorSensor3;

    DistanceSensorFSM distanceSensorFSM = DistanceSensorFSM.TRIGGER_DIST;
    ElapsedTime distanceSensorFSM_timer = new ElapsedTime();

    public void init(){

        colorSensor1 = hardwareMap.get(NormalizedColorSensor.class,"colorSensor1");
        colorSensor2 = hardwareMap.get(NormalizedColorSensor.class,"colorSensor2");
        colorSensor3 = hardwareMap.get(NormalizedColorSensor.class,"colorSensor3");

        distancesensor1 = hardwareMap.get(DistanceSensor.class,"colorSensor1");
        distancesensor2 = hardwareMap.get(DistanceSensor.class,"colorSensor2");
        distanceSensor3 = hardwareMap.get(DistanceSensor.class,"colorSensor3");

        Flicker1 = hardwareMap.get(Servo.class, "Flicker1");
        Flicker2 = hardwareMap.get(Servo.class, "Flicker2");
        Flicker3 = hardwareMap.get(Servo.class, "Flicker3");

        Flicker1.setDirection(Servo.Direction.REVERSE);
        Flicker2.setDirection(Servo.Direction.FORWARD);
        Flicker3.setDirection(Servo.Direction.REVERSE);

        Flicker1.setPosition(0);
        Flicker2.setPosition(0);
        Flicker3.setPosition(0);
    }

    public void loop(){

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
              if (distance1 < 6){
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
              if (distance2 < 6){
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
              if (distance3 < 6){
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

    }
}
