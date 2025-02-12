package teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Config
public class crservoTest extends LinearOpMode {
    public static double change = 0.01;
    public static String servoName = "spin";

    @Override
    public void runOpMode() throws InterruptedException {
        CRServo servo = hardwareMap.get(CRServo.class, servoName);
        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            if (gamepad1.dpad_right)
            {
                servo.setPower(servo.getPower() + change);
            }
            if (gamepad1.dpad_left)
            {
                servo.setPower(servo.getPower() - change);
            }
            if (gamepad1.dpad_up)
            {
                servo.setPower(1);
            }
            if (gamepad1.dpad_down)
            {
                servo.setPower(-1);
            }
            if (gamepad1.x)
            {
                servo.setPower(0);
            }
            if (gamepad1.a)
            {
                servo.setDirection(CRServo.Direction.FORWARD);
            }
            if (gamepad1.b)
            {
                servo.setDirection(CRServo.Direction.REVERSE);
            }
            telemetry.addData("Pos", servo.getPower());
            telemetry.addData("Dir", servo.getDirection());

            telemetry.update();


        }
    }
}