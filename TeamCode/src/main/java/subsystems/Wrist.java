package subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

@Config
public class Wrist {

    private Servo rotation, forearm, bicep;

    private double bicepPos, forearmPos, rotationPos;

    public static HashMap<String, Double> bicepPositions = new HashMap<String, Double>();
    public static HashMap<String, Double> forearmPositions = new HashMap<String, Double>();

    public static double autoPos = 0.63, intakepos = 0, bBasket = 0.86, fBasket = 0.92, bIdle = 0.88, fIdle = 0.63;

    public static double[] rotationPositions = new double[5];

    public Wrist(HardwareMap hwMap, HashMap<String, String> config) {
        bicep = hwMap.servo.get(config.get("bicep"));
        forearm = hwMap.servo.get(config.get("forearm"));
        rotation = hwMap.servo.get(config.get("rotation"));

        bicepPositions.put("Intake",      0.8761);
        bicepPositions.put("Basket",      bBasket);
        bicepPositions.put("Idle",        0.8);
        bicepPositions.put("Start",       0.71);
        bicepPositions.put("Auton Idle",  bIdle);

        forearmPositions.put("Intake",      0.0);
        forearmPositions.put("Basket",      fBasket);
        forearmPositions.put("Idle",        0.0);
        forearmPositions.put("Idle2",       0.6);
        forearmPositions.put("Start",       1.0);
        forearmPositions.put("Auton Idle",  fIdle);

        rotationPositions[0] = 0.39;
        rotationPositions[1] = 0.66;
        rotationPositions[2] = 0.94;
        rotationPositions[3] = 0.12;
        rotationPositions[4] = autoPos;


    }

    public void update()
    {
        bicep.setPosition(bicepPos);
        forearm.setPosition(forearmPos);
        rotation.setPosition(rotationPos);
    }

    public void setBicepPos(String pos)
    {
        bicepPos = bicepPositions.get(pos);
    }
    public void setForearmPos(String pos)
    {
        forearmPos = forearmPositions.get(pos);
    }
    public void setRotationPos(int pos)
    {
        rotationPos = rotationPositions[pos];
    }

    public void setPos(String pos) {
        bicepPos = bicepPositions.get(pos);
        forearmPos = forearmPositions.get(pos);
    }

}
