package teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import subsystems.Util;
import subsystems.Wrist;

@TeleOp
@Config
public class FullDepoTest extends LinearOpMode {

    Util util = new Util();


    @Override
    public void runOpMode() throws InterruptedException {
        Wrist wrist = new Wrist(hardwareMap, util.deviceConf);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_down) {
                wrist.setBicepPos("Intake");
            }
            if (gamepad1.dpad_up) {
                wrist.setBicepPos("Basket");
            }
            if (gamepad1.dpad_left) {
                wrist.setBicepPos("Idle");
            }
            if (gamepad1.dpad_right) {
                wrist.setBicepPos("Start");
            }

            if (gamepad1.a) {
                wrist.setForearmPos("Intake");
            }
            if (gamepad1.y) {
                wrist.setForearmPos("Basket");
            }
            if (gamepad1.x) {
                wrist.setForearmPos("Idle");
            }
            if (gamepad1.y) {
                wrist.setForearmPos("Start");
            }

            wrist.update();

        }
    }
}