package teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import subsystems.Extension;
import subsystems.Util;

@TeleOp
@Config
public class extensionTest extends LinearOpMode {
    public static int tarPos = 1500;
    @Override
    public void runOpMode() throws InterruptedException {
        Util util = new Util();
        Extension extension = new Extension(hardwareMap, util.deviceConf);
        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            if (gamepad1.a)
            {
                extension.setDirectPos(tarPos);
            }
            if (gamepad1.b)
            {
                extension.setDirectPos(0);
            }

            extension.update();
            telemetry.addData("Pos", extension.getCurrentPos());
            telemetry.update();


        }
    }
}