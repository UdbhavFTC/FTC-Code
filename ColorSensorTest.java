package org.firstinspires.ftc.teamcode.Backends;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp (name = "ColorSensorTest")
public class ColorSensorTest extends OpMode {

    ColorSensorMethod ColorSensorBackend = new ColorSensorMethod();



    @Override
    public void init() {
        ColorSensorBackend.init(hardwareMap);
    }

    public void loop(){
        ColorSensorBackend.getDetectedColor(telemetry);
    }
}
