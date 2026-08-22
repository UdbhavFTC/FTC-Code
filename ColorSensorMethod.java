package org.firstinspires.ftc.teamcode.Backends;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.text.Normalizer;
@TeleOp
public class ColorSensorMethod {
    NormalizedColorSensor colorSensor;

    DistanceSensor distancesensor;

    public enum detectedColor {
        PURPLE_INDIRECT,
        PURPLE_DIRECT,
        GREEN_INDIRECT,
        GREEN_DIRECT,
        UNKNOWN
    }

    public enum Distance {
        DISTANCE_THRESHOLD,
        UNKOWN,
    }

    public void init(HardwareMap hwMap) {
        colorSensor = hwMap.get(NormalizedColorSensor.class, "colorSensor1");
        colorSensor.setGain(6);

        distancesensor = hwMap.get(DistanceSensor.class,"colorSensor1");


    }

    public detectedColor getDetectedColor(Telemetry telemetry) {
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); //return 4 values: Gives Red Green Blue Alphas(Brightness)


        float normred, normgreen, normblue;
        normred = colors.red / colors.alpha;
        normgreen = colors.green / colors.alpha;
        normblue = colors.blue / colors.alpha;

        telemetry.addData("red", normred);
        telemetry.addData("green", normgreen);
        telemetry.addData("blue", normblue);




//Find values for green and purple
/* Red, Green, Blue
GREEN DIRECT BALL: (red: <0.069, green: <0.265, blue: <0.2)
GREEN INDIRECT BALL: (red: >0.09, green: >0.27, blue: <0.21)
PURPLE DIRECT BALL: (red: >0.385, green: >0.44, blue: >0.74)
PURPLE INDIRECT BALL: (red: <0.235, green: <0.36, blue: <0.335)
*/


//GREEN DIRECT
        if (normred > 0.1263 && normgreen > 0.4516 && normblue > 0.3282) {
            return detectedColor.GREEN_DIRECT;
        }

//GREEN INDIRECT
        if (normred < 0.0619 && normgreen < 0.1709 && normblue < 0.1369){
            return detectedColor.GREEN_INDIRECT;
        }

//PURPLE DIRECT
        if (normred > 0.2783 && normgreen > 0.3063 && normblue > 0.481) {
            return detectedColor.PURPLE_DIRECT;
        }

//PURPLE INDIRECT
        if (normred < 0.0859 && normgreen < 0.1079 && normblue < 0.1549) {
            return detectedColor.PURPLE_INDIRECT;
        }

        return detectedColor.UNKNOWN;


    }

    public Distance getDistance(Telemetry telemetry) {

        double detectedDist = distancesensor.getDistance(DistanceUnit.CM);
        telemetry.addData("distance", detectedDist);
        //DISTANCE_THRESHOLD
        if (detectedDist < 6) {
            return  Distance.DISTANCE_THRESHOLD;
        }

        return Distance.UNKOWN;
    }

}


