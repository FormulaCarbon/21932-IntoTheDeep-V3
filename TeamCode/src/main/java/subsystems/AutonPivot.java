package subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.trajectory.TrapezoidProfile;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import subsystems.Util;

import java.util.HashMap;

@Config
public class AutonPivot {
    private DcMotorEx leftPivot, rightPivot;
    RevTouchSensor reset;

    public static double power = 0, lastPower = power;

    private int pos;
    private int curLeft = 0, lAngle = curLeft;
    private int lta = 0;

    public static double kP = 0.002, kI = 0, kD = 0, k = 0, extendedKp = 0.003, zeroKp = 0.005, slowKp = 0.02;

    PIDController pidController = new PIDController(kP, kI, kD);

    TrapezoidProfile profile;
    TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(Util.MAX_PIVOT_VELOCITY, Util.MAX_PIVOT_AUTO_ACCEL);

    ElapsedTime fullTimer = new ElapsedTime();
    ElapsedTime velTimer = new ElapsedTime();

    public static HashMap<String, Integer> positions = new HashMap<String, Integer>();
    public static HashMap<String, Double> kPs = new HashMap<String, Double>();

    double aVelocity, indexedPosition = 0;

    public AutonPivot(HardwareMap hwMap, HashMap<String, String> config) {
        leftPivot = hwMap.get(DcMotorEx.class,config.get("leftPivot"));
        rightPivot = hwMap.get(DcMotorEx.class,config.get("rightPivot"));

        rightPivot.setDirection(DcMotorSimple.Direction.REVERSE);

        leftPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        leftPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        reset = hwMap.get(RevTouchSensor.class, config.get("reset"));

        profile = new TrapezoidProfile(constraints, new TrapezoidProfile.State(0, 0));

        positions.put("Down", 0);
        positions.put("Basket", 2100);
        positions.put("Idle", 2100);
        positions.put("Start", 1370);
        positions.put("Hang", 2124);
        positions.put("Lift", 2260);

        kPs.put("Normal", kP);
        kPs.put("Extended", extendedKp);
    }

    public void update() {

        curLeft = leftPivot.getCurrentPosition();
        //curRight = rightPivot.getCurrentPosition();
        //checkReset();
        if (pos != lta) {
            profile = new TrapezoidProfile(constraints, new TrapezoidProfile.State(pos, 0), new TrapezoidProfile.State(curLeft, aVelocity));
            fullTimer.reset();
        }

        indexedPosition = profile.calculate(fullTimer.seconds()).position;

        pidController.setSetPoint(indexedPosition);

        power = pidController.calculate(curLeft) + (k * Math.cos(pos));

        power = checkReset(power);

        if (!Util.inThresh(power, lastPower, 0.001)) {
            applyPower(power);
            lastPower = power;
        }


        lta = pos;

        aVelocity = (curLeft-lAngle)/velTimer.seconds();
        lAngle = curLeft;
        velTimer.reset();
    }

    public void applyPower(double power) {
        leftPivot.setPower(power);
        rightPivot.setPower(power);
    }

    public void setPos(String pos) {
        this.pos = positions.get(pos);
    }

    public void setkP(String kP) {
        pidController.setP(kPs.get(kP));
    }

    public int getTarget() {
        return pos;
    }

    public int getCurrent() {
        return leftPivot.getCurrentPosition();
    }

    public double getPower() {
        return power;
    }

    public double getError() {
        return pos - leftPivot.getCurrentPosition();
    }

    public double getVelocity() {
        return leftPivot.getVelocity();
    }

    public double checkReset(double power) {
        if (reset.isPressed()) {

            if (pos == positions.get("Down")) {
                return 0.0;
            }

        }
        return power;
    }

}
