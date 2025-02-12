package teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import subsystems.Claw;
import subsystems.Drive;
import subsystems.Extension;
import subsystems.Pivot;
import subsystems.SpecMec;
import subsystems.Util;
import subsystems.Wrist;

@TeleOp
@Config
public class IntoTheDeep extends LinearOpMode {



    private int incr = 1;
    boolean incrUpdate = false;

    public static int maxSampleSteps = 8, maxSpecimenSteps = 4, maxIntakeSteps = 3;

    boolean pivotReady, wristReady, extensionReady, swapReady, cycleReady, clawReady, turnReady;
    boolean wristManual = false, extensionManual = false, pivotManual = false;

    String sequence = "Sample";

    public static int pchange = 400;
    public static int pchange2 = 2000;

    @Override
    public void runOpMode() throws InterruptedException {
        Util util = new Util();
        Drive drive = new Drive(hardwareMap, util.deviceConf);
        Pivot pivot = new Pivot(hardwareMap, util.deviceConf);
        Extension extension = new Extension(hardwareMap, util.deviceConf);
        Wrist wrist = new Wrist(hardwareMap, util.deviceConf);
        Claw claw = new Claw(hardwareMap, util.deviceConf);
        SpecMec specMec = new SpecMec(hardwareMap, util.deviceConf);
        specMec.setPosition("Intake", "Intake");
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        waitForStart();

        wrist.setRotationPos(0);
        specMec.idleClaw();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            turnReady = pivotReady = wristReady = extensionReady = swapReady = cycleReady = clawReady = true;
            drive.getXYZ(gamepad1.left_stick_x, gamepad1.left_stick_y, -gamepad1.right_stick_x);

            increment(gamepad1.right_bumper, gamepad1.left_bumper, sequence);
            setPositions(incr, sequence, pivot, extension, wrist, specMec, pivotManual, extensionManual);

            /*if (gamepad1.x && wristReady) {
                wristManual = true;
                wrist.setBicepPos("Intake");
                wrist.setForearmPos("Intake");
                wristReady = false;
            }
            else if (gamepad1.y && wristReady) {
                wristManual = true;
                wrist.setBicepPos("Basket");
                wrist.setForearmPos("Basket");
                wristReady = false;
            }*/

            /*if (gamepad2.dpad_up && turnReady) {
                wrist.setRotationPos(0);
                turnReady = false;
            }
            else if (gamepad2.dpad_left && turnReady) {
                wrist.setRotationPos(3);
                turnReady = false;
            }
            else if (gamepad2.dpad_down && turnReady) {
                wrist.setRotationPos(2);
                turnReady = false;
            }
            else if (gamepad2.dpad_right && turnReady) {
                wrist.setRotationPos(1);
                turnReady = false;
            }*/

            if (gamepad2.right_bumper) {
                sequence = "Specimen";
                incr = 0;
            }
            if (gamepad2.left_bumper) {
                //specMec.setPosition("Start", "Intake");
                specMec.idleClaw();
                sequence = "Sample";
                incr = 0;
            }
            if (gamepad2.a) {
                //specMec.setPosition("Start", "Intake");
                specMec.idleClaw();
                sequence = "Intake";
                incr = 0;
            }
            if (gamepad2.b) {
                incr = -5;
            }

            if (gamepad1.dpad_up && turnReady) {
                wrist.setRotationPos(0);
                turnReady = false;
            }
            else if (gamepad1.dpad_left && turnReady) {
                wrist.setRotationPos(3);
                turnReady = false;
            }
            else if (gamepad1.dpad_down && turnReady) {
                wrist.setRotationPos(2);
                turnReady = false;
            }
            else if (gamepad1.dpad_right && turnReady) {
                wrist.setRotationPos(1);
                turnReady = false;
            }

            if (gamepad1.b && pivotReady && extensionReady && wristReady) {
                incr = -1;
            }

            if (gamepad1.y && extensionReady) {
                incr = -2;
            }

            if (gamepad1.x && extensionReady) {
                incr = -3;
            }

            if (gamepad2.dpad_down && extensionReady) {
                extension.setDirectPos(extension.getCurrentPos() - 50);
                extensionManual = true;
                extensionReady = false;
            }


            if ((gamepad1.left_trigger > 0.1)) {
                drive.slowModeOn();
            }

            if ((gamepad1.left_trigger < 0.1) && !gamepad2.y) {
                drive.slowModeOf();
            }

            if (gamepad2.y) {
                drive.slowModeOn();
            }

            if (gamepad2.x) {
                drive.slowModeOf();
            }

            if (gamepad2.right_bumper || gamepad2.left_bumper || gamepad1.right_bumper || gamepad1.left_bumper)
            {
                wristManual = false;
                extensionManual = pivotManual =  false;

            }





            drive.update();
            pivot.update();
            extension.update();
            wrist.update();
            claw.update(gamepad1.a);
            specMec.update();
            specMec.updateClaw();


            telemetry.addData("incr", incr);
            telemetry.addData("seq", sequence);
            telemetry.addData("tar", pivot.getTarget());
            telemetry.addData("cur", pivot.getCurrent());
            telemetry.addData("pow", pivot.getPower());
            telemetry.addData("epow", extension.getPower());
            telemetry.addData("vel", pivot.getVelocity());
            telemetry.addData("extension vel", extension.getVelocity());
            telemetry.addData("error", extension.getError());
            telemetry.addData("red", specMec.getColors().red);
            telemetry.addData("blue", specMec.getColors().blue);

            telemetry.update();
        }
    }

    public void increment(boolean upFlag, boolean downFlag, String sequence) {
        if (sequence.equals("Sample")) {
            if (downFlag && !incrUpdate && incr > 0) {
                incr -= 1;
                incrUpdate = true;
            } else if (upFlag && !incrUpdate) {
                incr += 1;
                incrUpdate = true;
            } else if (!upFlag && !downFlag) {
                incrUpdate = false;
            }

            if (incr > maxSampleSteps) {
                incr = 0;
            }
        }
        else if (sequence.equals("Specimen")) {
            if (downFlag && !incrUpdate && incr > 0) {
                incr -= 1;
                incrUpdate = true;
            } else if (upFlag && !incrUpdate) {
                incr += 1;
                incrUpdate = true;
            } else if (!upFlag && !downFlag) {
                incrUpdate = false;
            }

            if (incr > maxSpecimenSteps) {
                incr = 0;
            }
        }
        else if (sequence.equals("Intake")) {
            if (downFlag && !incrUpdate && incr > 0) {
                incr -= 1;
                incrUpdate = true;
            } else if (upFlag && !incrUpdate) {
                incr += 1;
                incrUpdate = true;
            } else if (!upFlag && !downFlag) {
                incrUpdate = false;
            }

            if (incr > maxIntakeSteps) {
                incr = 0;
            }
        }
    }

    public void setPositions(int pos, String sequence, Pivot pivot, Extension extension, Wrist wrist, SpecMec specMec, boolean pMan, boolean eMan)  {
        if (sequence.equals("Sample")) {
            switch (pos) {
                case 0: // Idle
                    if (!pMan) {
                        pivot.setPos("Idle");
                        pivot.setkP("Normal");
                    }
                    if (!eMan) {
                        extension.setPos("Idle");
                    }
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle");
                    break;
                case 1: // Sample Intake: Down, Unextended
                    //if (extension.getCurrentPos() < 100) {
                    pivot.setPos("Down");
                    pivot.setkP("Normal");
                    //}
                    extension.setPos("Idle");
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle");

                    break;
                case 2: // Sample Extend
                    pivot.setPos("Down");
                    pivot.setkP("Extended");
                    extension.setPos("Intake");
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle");
                    break;
                case 3: // Flip Down
                    pivot.setPos("Down");
                    pivot.setkP("Extended");
                    extension.setPos("Intake");
                    wrist.setBicepPos("Intake");
                    wrist.setForearmPos("Intake");
                    break;
                case 4: // Flip Up
                    pivot.setPos("Down");
                    pivot.setkP("Extended");
                    extension.setPos("Intake");
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle");
                    break;
                case 5: // Pullout
                    pivot.setPos("Down");
                    pivot.setkP("Normal");
                    extension.setPos("Idle");
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle2");
                    wrist.setRotationPos(0);
                    break;
                case 6: // Idle
                    pivot.setPos("Idle");
                    pivot.setkP("Normal");
                    extension.setPos("Idle");
                    wrist.setBicepPos("Basket");
                    wrist.setForearmPos("Basket");
                    break;
                case 7: // High Basket
                    pivot.setPos("Basket");
                    pivot.setkP("Extended");
                    //if (pivot.getCurrent() > 315) {
                    extension.setPos("Basket");
                    //}
                    wrist.setBicepPos("Basket");
                    wrist.setForearmPos("Basket");
                    break;
                case 8: // Flip Out
                    pivot.setPos("Basket");
                    pivot.setkP("Extended");
                    extension.setPos("Basket");
                    wrist.setBicepPos("Intake");
                    wrist.setForearmPos("Intake");
                    break;
                case -1: // Hang Pivot Position
                    wrist.setBicepPos("Intake");
                    wrist.setForearmPos("Intake");
                    pivot.setPos("Idle");
                    extension.setPos("Idle");
                    break;
                case -2: // Hang Extend
                    pivot.setPos("Hang");
                    extension.setPos("Hang");
                    break;
                case -3: // Hang Retract
                    extension.setPos("Retract");
                    break;
                case -5:

                    pivot.setDirectPos(-2100);
                    break;
                case -6:
                    pivot.setDirectPos(pivot.getTarget() - pchange2);
                    break;

            }
        }
        else if (sequence.equals("Specimen")) {
            switch (pos) {
                case 0:
                    specMec.setPosition("Intake", "Intake");
                    specMec.openClaw();
                    break;
                case 1:
                    specMec.setPosition("Intake", "Intake");
                    //specMec.checkSensor();
                    specMec.closeClaw();
                    break;
                case 2:
                    specMec.setPosition("Idle", "Score");
                    specMec.closeClaw();
                    break;
                case 3:
                    specMec.setPosition("Score", "Score");
                    specMec.closeClaw();
                    break;
                case 4:
                    specMec.setPosition("Score", "Score");
                    specMec.openClaw();
                    break;
            }
        }
        else if (sequence.equals("Intake")) {
            switch(pos) {
                case 0: // Sample Intake: Down, Unextended
                    //if (extension.getCurrentPos() < 100) {
                    pivot.setPos("Down");
                    pivot.setkP("Normal");
                    //}
                    extension.setPos("Idle");
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle");

                    break;
                case 1: // Sample Extend
                    pivot.setPos("Down");
                    pivot.setkP("Extended");
                    extension.setPos("Intake");
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle");
                    break;
                case 2: // Flip Down
                    pivot.setPos("Down");
                    pivot.setkP("Extended");
                    extension.setPos("Intake");
                    wrist.setBicepPos("Intake");
                    wrist.setForearmPos("Intake");
                    break;
                case 3: // Flip Up
                    pivot.setPos("Down");
                    pivot.setkP("Extended");
                    extension.setPos("Intake");
                    wrist.setBicepPos("Idle");
                    wrist.setForearmPos("Idle");
                    break;
                /*case 4: // Pullout
                    pivot.setPos("Down");
                    pivot.setkP("Normal");
                    extension.setPos("Idle");
                    wrist.setBicepPos("Basket");
                    wrist.setForearmPos("Basket");
                    wrist.setRotationPos(0);
                    break;*/
            }
        }

    }
}
