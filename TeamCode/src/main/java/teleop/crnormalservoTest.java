package teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Config
public class crnormalservoTest extends LinearOpMode {
    public static double change = 0.001;
    public static String crServoName = "spin";
    public static String servoName = "servo";

    @Override
    public void runOpMode() throws InterruptedException {
        CRServo crServo = hardwareMap.get(CRServo.class, crServoName);
        Servo servo = hardwareMap.get(Servo.class, servoName);
        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            if (gamepad1.dpad_right)
            {
                crServo.setPower(crServo.getPower() + change);
            }
            if (gamepad1.dpad_left)
            {
                crServo.setPower(crServo.getPower() - change);
            }
            if (gamepad1.dpad_up)
            {
                crServo.setPower(1);
            }
            if (gamepad1.dpad_down)
            {
                crServo.setPower(-1);
            }
            if (gamepad1.x)
            {
                crServo.setPower(0);
            }
            if (gamepad1.a)
            {
                servo.setPosition(0);
            }
            if (gamepad1.y)
            {
                servo.setPosition(1);
            }
            if (gamepad1.b)
            {
                servo.setPosition(servo.getPosition() + change);
            }
            if (gamepad1.x)
            {
                servo.setPosition(servo.getPosition() - change);
            }
            telemetry.addData("crPos", crServo.getPower());
            telemetry.addData("pos", servo.getPosition());

            telemetry.update();


        }
    }
}