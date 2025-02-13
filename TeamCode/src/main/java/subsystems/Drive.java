package subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;

@Config
public class Drive {

    private DcMotor frontLeft, backLeft, frontRight, backRight;

    private double x, y, rx, d;

    public static double curveFactor = 1.5, maxLinear = 1, maxRot = 0.5, slowLin = 0.3, slowRot = 0.25, fastLin = 1, fastRot = 0.5;

    public static double flMult = 1, blMult = 1, frMult = 1, brMult = 1;
    public Drive(HardwareMap hwMap, HashMap<String, String> config) {
        frontLeft = hwMap.dcMotor.get(config.get("frontLeft"));
        backLeft = hwMap.dcMotor.get(config.get("backLeft"));
        frontRight = hwMap.dcMotor.get(config.get("frontRight"));
        backRight = hwMap.dcMotor.get(config.get("backRight"));

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void getXYZ(double x, double y, double rx) {
        this.x = Math.signum(x) * Math.pow(Math.abs(x), curveFactor) * maxLinear;
        this.y = Math.signum(-y) * Math.pow(Math.abs(y), curveFactor) * maxLinear;
        this.rx = Math.signum(-rx) * Math.pow(Math.abs(rx), curveFactor) * maxRot;
    }

    public void update() {
        d = Math.max((Math.abs(y) + Math.abs(x) + Math.abs(rx)) * Math.max(Math.max(flMult, blMult), Math.max(frMult, brMult)), 1);
        frontLeft.setPower( ((y + x + rx) * flMult) / d );
        backLeft.setPower( ((y - x + rx) * blMult) / d );
        frontRight.setPower( ((y - x - rx) * frMult) / d );
        backRight.setPower( ((y + x - rx) * brMult) / d );
    }

    public void slowModeOn() {
        maxLinear = slowLin;
        maxRot = slowRot;
    }

    public void slowModeOf() {
        maxLinear = fastLin;
        maxRot = fastRot;
    }
}
