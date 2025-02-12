package teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Config
public class dualServoTest extends LinearOpMode {
    public static double change = 0.001;
    public static String servoName1 = "pivot";
    public static String servoName2 = "smallPivot";

    @Override
    public void runOpMode() throws InterruptedException {
        Servo servo1 = hardwareMap.get(Servo.class, servoName1);
        Servo servo2 = hardwareMap.get(Servo.class, servoName2);
        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            if (gamepad1.dpad_right)
            {
                servo1.setPosition(servo1.getPosition() + change);
            }
            if (gamepad1.dpad_left)
            {
                servo1.setPosition(servo1.getPosition() - change);
            }
            if (gamepad1.dpad_up)
            {
                servo1.setPosition(1);
            }
            if (gamepad1.dpad_down)
            {
                servo1.setPosition(0);
            }

            if (gamepad1.b)
            {
                servo2.setPosition(servo2.getPosition() + change);
            }
            if (gamepad1.x)
            {
                servo2.setPosition(servo2.getPosition() - change);
            }
            if (gamepad1.y)
            {
                servo2.setPosition(1);
            }
            if (gamepad1.a)
            {
                servo2.setPosition(0);
            }

            if (gamepad1.right_bumper)
            {
                servo1.setPosition(1);
                servo2.setPosition(1);
            }
            if (gamepad1.left_bumper)
            {
                servo1.setPosition(0);
                servo2.setPosition(0);
            }

            if (gamepad2.dpad_right)
            {
                servo1.setPosition(servo1.getPosition() + change);
                servo2.setPosition(servo2.getPosition() + change);
            }
            if (gamepad2.dpad_left)
            {
                servo1.setPosition(servo1.getPosition() - change);
                servo2.setPosition(servo2.getPosition() - change);
            }
            if (gamepad2.dpad_up)
            {
                servo1.setPosition(1);
                servo2.setPosition(1);
            }
            if (gamepad2.dpad_down)
            {
                servo1.setPosition(0);
                servo2.setPosition(0);
            }

            telemetry.addData("Pos1", servo1.getPosition());
            telemetry.addData("pos2",  servo2.getPosition());

            telemetry.update();


        }
    }
}