package org.firstinspires.ftc.teamcode.Backends;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ColorSensorBackend {
    NormalizedColorSensor colorSensor1;
    NormalizedColorSensor colorSensor2;
    NormalizedColorSensor colorSensor3;

    DistanceSensor distancesensor1;
    DistanceSensor distancesensor2;
    DistanceSensor distanceSensor3;

    public static class SensorResult {
        public int sensorId;      // 1, 2, or 3
        public detectedColor color;
        public float red, green, blue;  // Normalized RGB values
    }

    public enum detectedColor {
        PURPLE_INDIRECT,
        PURPLE_DIRECT,
        GREEN_INDIRECT,
        GREEN_DIRECT,
        UNKNOWN
    }

    public static class DistanceResult{
        public Distance distance;
    }
    public enum Distance {
        DISTANCE_THRESHOLD,
        UNKOWN,
    }


    public ColorSensorBackend(HardwareMap hwMap) {
        colorSensor1 = hwMap.get(NormalizedColorSensor.class, "colorSensor1");
        colorSensor1.setGain(6);

        colorSensor2 = hwMap.get(NormalizedColorSensor.class,"colorSensor2");
        colorSensor2.setGain(6);

        colorSensor3 = hwMap.get(NormalizedColorSensor.class, "colorSensor3");
        colorSensor3.setGain(6);

        distancesensor1 = hwMap.get(DistanceSensor.class,"colorSensor1");
        distancesensor2 = hwMap.get(DistanceSensor.class,"colorSensor2");
        distanceSensor3 = hwMap.get(DistanceSensor.class,"colorSensor3");
    }

    public SensorResult[] getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors1 = colorSensor1.getNormalizedColors(); //return 4 values: Gives Red Green Blue Alphas(Brightness)
        NormalizedRGBA colors2 = colorSensor2.getNormalizedColors(); //return 4 values: Gives Red Green Blue Alphas(Brightness)
        NormalizedRGBA colors3 = colorSensor3.getNormalizedColors(); //return 4 values: Gives Red Green Blue Alphas(Brightness)


        float normred1, normgreen1, normblue1;
        normred1 = colors1.red / colors1.alpha;
        normgreen1 = colors1.green / colors1.alpha;
        normblue1 = colors1.blue / colors1.alpha;

        float normred2, normgreen2, normblue2;
        normred2 = colors2.red / colors2.alpha;
        normgreen2 = colors2.green / colors2.alpha;
        normblue2 = colors2.blue / colors2.alpha;

        float normred3, normgreen3, normblue3;
        normred3 = colors3.red / colors3.alpha;
        normgreen3 = colors3.green / colors3.alpha;
        normblue3 = colors3.blue / colors3.alpha;

        telemetry.addData("red", normred1);
        telemetry.addData("green", normgreen1);
        telemetry.addData("blue", normblue1);


        //Find values for green and purple
       /* Red, Green, Blue
       GREEN DIRECT BALL: (red: <0.08, green: >0.31, blue: <0.23)
       GREEN INDIRECT BALL: (red: <0.08, green: <0.16, blue: <0.14)
       PURPLE DIRECT BALL: (red: <0.21, green: <0.22, blue: >0.31)
       PURPLE INDIRECT BALL: (red: >0.06, green: >0.1, blue: >0.1)
        */

        SensorResult[] results = new SensorResult[3];

        results[0] = new SensorResult();
        results[0].sensorId = 1;
        results[0].color = classifyColor(normred1, normgreen1, normblue1, telemetry);
        results[0].red = normred1;
        results[0].green = normgreen1;
        results[0].blue = normblue1;

        results[1] = new SensorResult();
        results[1].sensorId = 2;
        results[1].color = classifyColor(normred2, normgreen2, normblue2, telemetry);
        results[1].red = normred2;
        results[1].green = normgreen2;
        results[1].blue = normblue2;

        results[2] = new SensorResult();
        results[2].sensorId = 3;
        results[2].color = classifyColor(normred3, normgreen3, normblue3, telemetry);
        results[2].red = normred3;
        results[2].green = normgreen3;
        results[2].blue = normblue3;


        telemetry.addData("S1", results[0].color);
        telemetry.addData("S2", results[1].color);
        telemetry.addData("S3", results[2].color);



        return results;



    }
    private detectedColor classifyColor(float normred, float normgreen, float normblue, Telemetry telemetry) {
        double distance1 = distancesensor1.getDistance(DistanceUnit.CM);
        double distance2 = distancesensor2.getDistance(DistanceUnit.CM);
        double distance3 = distanceSensor3.getDistance(DistanceUnit.CM);
        telemetry.addData("distance1",distance1);
        telemetry.addData("distance2",distance2);
        telemetry.addData("distance3",distance3);

        //GREEN DIRECT
        if (normred >= 0.053 && normgreen >= 0.19 && normblue >= 0.138 && (distance1 < 3 || distance2 < 3 || distance3 < 3)) {
            return ColorSensorBackend.detectedColor.GREEN_DIRECT;
        }

        //GREEN INDIRECT
        if (normred < 0.053 && normgreen < 0.19 && normblue < 0.138 && (distance1 < 3 || distance2 < 3 || distance3 < 3)){
            return ColorSensorBackend.detectedColor.GREEN_INDIRECT;
        }

        //PURPLE DIRECT
        if (normred >= 0.25 && normgreen >= 0.283 && normblue >= 0.41 && (distance1 < 2 || distance2 < 2 || distance3 < 2)) {
            return ColorSensorBackend.detectedColor.PURPLE_DIRECT;
        }

        //PURPLE INDIRECT
        if (normred < 0.25 && normred > 0.09 && normgreen < 0.283 && normgreen > 0.167 && normblue < 0.41 && normblue > 0.45 && (distance1 > 2 || distance2 < 2 || distance3 < 2)) {
            return ColorSensorBackend.detectedColor.PURPLE_INDIRECT;
        }




        return ColorSensorBackend.detectedColor.UNKNOWN;


    }





}





