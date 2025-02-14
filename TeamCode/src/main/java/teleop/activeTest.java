package teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import subsystems.ActiveIntake;
import subsystems.Util;

@TeleOp
@Config
public class activeTest extends LinearOpMode {
    public static double change = 0.01;
    public static String servoName = "Turn";

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Util util = new Util();
        ActiveIntake intake = new ActiveIntake(hardwareMap, util.deviceConf);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            if (gamepad1.dpad_down) {
                intake.intake();
            }
            if (gamepad1.dpad_up) {
                intake.outtake();
            }
            if (gamepad1.dpad_right) {
                intake.off();
            }

            if (gamepad1.a) {
                intake.unclamp();
            }
            if (gamepad1.b) {
                intake.clamp();
            }

            intake.update();

            telemetry.addData("red", intake.getColors().red);
            telemetry.addData("blue", intake.getColors().blue);
            telemetry.addData("green", intake.getColors().green);
            telemetry.addData("alpha", intake.getColors().alpha);
            telemetry.addData("distance", intake.getDistance());

            telemetry.update();


        }
    }
}