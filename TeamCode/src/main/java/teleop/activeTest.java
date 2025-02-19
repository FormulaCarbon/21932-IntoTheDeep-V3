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
import com.qualcomm.robotcore.util.ElapsedTime;

import subsystems.ActiveIntake;
import subsystems.Util;

@TeleOp
@Config
public class activeTest extends LinearOpMode {
    public static double change = 0.01;
    public static String servoName = "Turn", avoid = "Blue";

    ElapsedTime spitTimer = new ElapsedTime();
    boolean spitting = false;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Util util = new Util();
        ActiveIntake intake = new ActiveIntake(hardwareMap, util.deviceConf);
        boolean auto = false;

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
            if (gamepad1.dpad_left) {
                intake.hold();
            }

            if (gamepad1.a) {
                intake.unclamp();
            }
            if (gamepad1.b) {
                intake.clamp();
            }

            if (gamepad1.x) {
                auto = !auto;
            }

            /*if (auto || spitting)
            {
                intake.intake();
                if (intake.getBlockColor().equals(avoid) || spitting)
                {
                    if (!spitting)
                    {
                        spitTimer.reset();
                        spitting = true;
                    }
                    intake.spit(spitTimer.seconds());
                    auto = false;

                }
                else if (!intake.getBlockColor().equals("None")) {
                    intake.hold();
                    auto = false;
                    spitting = false;
                }

            }*/
            if (auto) {
                intake.intake();
                if (intake.getBlockColor().equals(avoid)) {
                    intake.unclamp();
                }
                else {
                    intake.clamp();
                }

                if (!intake.getBlockColor().equals(avoid) && !intake.getBlockColor().equals("None")) {
                    intake.off();
                    auto = false;
                }


            }





            intake.update();

            telemetry.addData("red", intake.getColors().red);
            telemetry.addData("blue", intake.getColors().blue);
            telemetry.addData("green", intake.getColors().green);
            telemetry.addData("alpha", intake.getColors().alpha);
            telemetry.addData("distance", intake.getDistance());
            telemetry.addData("block", intake.getBlockColor());
            telemetry.addData("auto", auto);
            telemetry.addData("have",!intake.getBlockColor().equals("None"));
            telemetry.addData("redD", intake.getDelta("Red"));
            telemetry.addData("blueD", intake.getDelta("Blue"));
            telemetry.addData("greenD", intake.getDelta("Green"));
            telemetry.addData("time", intake.time());
            telemetry.addData("blueL", intake.getLastColors().blue);
            telemetry.addData("greenL", intake.getLastColors().green);
            telemetry.addData("alphaL", intake.getLastColors().alpha);
            telemetry.addData("spitting", spitting);
            telemetry.addData("spittingt", spitTimer.seconds());

            telemetry.update();


        }
    }
}