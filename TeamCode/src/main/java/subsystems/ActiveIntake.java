package subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

@Config
public class ActiveIntake {

    private CRServo spin;
    private Servo clamp;
    ColorRangefinder sensor;
    NormalizedRGBA colors;
    public static int ledBrightness = 100;
    public static double clampUp = 0.27, clampDown = 0.45;

    private double tarPower, tarPos = clampUp;

    public ActiveIntake(HardwareMap hwMap, HashMap<String, String> config) {
        spin = hwMap.crservo.get(config.get("spinner"));
        clamp = hwMap.servo.get(config.get("clamp"));
        sensor = new ColorRangefinder(hwMap.get(RevColorSensorV3.class, config.get("colorSensor")));
        sensor.setLedBrightness(ledBrightness);
        colors = sensor.emulator.getNormalizedColors();
    }

    public void update()
    {
        spin.setPower(tarPower);
        clamp.setPosition(tarPos);
        colors = sensor.emulator.getNormalizedColors();
    }

    public void clamp() {
        tarPos = clampDown;
    }

    public void unclamp() {
        tarPos = clampUp;
    }

    public void intake() {
        tarPower = 1.0;
    }

    public void outtake() {
        tarPower = -1.0;
    }

    public void off() {
        tarPower = 0;
    }

    public NormalizedRGBA getColors() {
        return colors;
    }

    public double getDistance() {
        return sensor.readDistance();
    }
}
