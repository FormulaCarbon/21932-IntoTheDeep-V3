package subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

@Config
public class Claw {

    private Servo claw;
    private boolean ispressed = false;
    public static double closed = 0.8, open = 0.5;

    public Claw(HardwareMap hardwareMap, HashMap<String, String> config) {
        claw = hardwareMap.servo.get(config.get("claw"));
    }

    public void update(boolean gp) {
        if (gp && !ispressed) {
            if (claw.getPosition() == closed) {
                claw.setPosition(open);
            }
            else {
                claw.setPosition(closed);
            }
        }
        ispressed = gp;
    }

    public void directSet(double p) {
        claw.setPosition(p);
    }

}