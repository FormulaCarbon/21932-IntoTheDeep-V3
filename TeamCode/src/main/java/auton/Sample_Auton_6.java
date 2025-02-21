package auton;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import subsystems.ActiveIntake;
import subsystems.Extension;
import subsystems.Pivot;
import subsystems.SpecMec;
import subsystems.Util;
import subsystems.Wrist;

@Config
@Autonomous
public class Sample_Auton_6 extends OpMode {
    private Follower follower;
    Util util = new Util();

    Pivot pivot;
    Extension extension;
    Wrist wrist;
    ActiveIntake intake;

    public static double preloadBasketX = 14, preloadBasketY = 130, basket330X = 15, basket330Y = 132, block1X = 32, block1Y = 124, block2X = 32, block2Y = 132, block3X = 36, block3Y = 132, controlX = 57, controlY = 125, subX = 66, subY = 96;

    private Timer pathTimer, actionTimer, opmodeTimer;

    /** This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method. */
    private int pathState;

    /* Create and Define Poses + Paths
     * Poses are built with three constructors: x, y, and heading (in Radians).
     * Pedro uses 0 - 144 for x and y, with 0, 0 being on the bottom left.
     * (For Into the Deep, this would be Blue Observation Zone (0,0) to Red Observation Zone (144,144).)
     * Even though Pedro uses a different coordinate system than RR, you can convert any roadrunner pose by adding +72 both the x and y.
     * This visualizer is very easy to use to find and create paths/pathchains/poses: <https://pedro-path-generator.vercel.app/>
     * Lets assume our robot is 18 by 18 inches
     * Lets assume the Robot is facing the human player and we want to score in the bucket */

    private final Pose startPose = new Pose(8.000, 112.000, Math.toRadians(270));
    private final Pose preloadBasketPose = new Pose(preloadBasketX, preloadBasketY, Math.toRadians(315));

    private final Pose block1Pose = new Pose(block1X, block1Y, Math.toRadians(-1)); // uses calc tan heading

    private final Pose basket330Pose = new Pose(basket330X, basket330Y, Math.toRadians(330));

    private final Pose block2Pose = new Pose(block2X, block2Y, Math.toRadians(0));

    private final Pose block3Pose = new Pose(block3X, block3Y, Math.toRadians(39));

    private final Pose subPathControlPose = new Pose(controlX, controlY, Math.toRadians(-1));

    private final Pose subPickupPose = new Pose(subX, subY, Math.toRadians(270));


    private PathChain scorePreload, getBlock1, scoreBlock1, getBlock2, scoreBlock2, getBlock3, scoreBlock3, getSubBlock, scoreSubBlock, park;

    private double angle1 = util.calculateTangentHeading(preloadBasketPose, block1Pose);
    private double angle2 = util.calculateTangentHeading(basket330Pose, block2Pose);

    public static double finishTurn1 = 0.5, finishTurn2 = 0.5, finishTurn3 = 0.5, finishTurnSub = 0.5, finishTurnScore = 0.5;
    public static double preloadExtensionPar = 0.1, b0WristBackPar = 0.9, b0ScorePar = 0.91, b0RetractPar = 0.2, b0PivotDownPar = 0;
    public static double b1ExtensionPar = 0.1, b1WristBackPar = 0.9, b1ScorePar = 0.91, b1RetractPar = 0.2, b1PivotDownPar = 0;
    public static double b2ExtensionPar = 0.1, b2WristBackPar = 0.9, b2ScorePar = 0.91,b2RetractPar = 0.2, b2PivotDownPar = 0;
    public static double b3ExtensionPar = 0.1, b3WristBackPar = 0.9, b3ScorePar = 0.91,b3RetractPar = 0.2, b3PivotDownPar = 0;

    public static double maxPow1 = 0.5;

    public void buildPaths() {
        scorePreload = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(startPose),
                                new Point(16.691, 122.338, Point.CARTESIAN),
                                new Point(preloadBasketPose)
                        )
                )
                .setLinearHeadingInterpolation(startPose.getHeading(), preloadBasketPose.getHeading())
                .addParametricCallback(0, () -> pivot.setPos("Basket"))
                .addParametricCallback(preloadExtensionPar, () -> extension.setPos("Basket"))
                .addParametricCallback(b0WristBackPar, () -> wrist.setPos("Basket"))
                .addParametricCallback(b0ScorePar, () -> intake.setAuto(false))
                .addParametricCallback(b0ScorePar, () -> intake.outtake())
                .build();

        getBlock1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(preloadBasketPose),
                                new Point(block1Pose)
                        )
                )
                .setLinearHeadingInterpolation(preloadBasketPose.getHeading(), angle1, finishTurn1)
                .addParametricCallback(b0RetractPar, () -> extension.setPos("Idle"))
                .addParametricCallback(0, () -> wrist.setPos("Intake"))
                .addParametricCallback(b0PivotDownPar, () -> pivot.setPos("Down"))
                .addParametricCallback(0.5, () -> intake.setAuto(true))
                .build();

        scoreBlock1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(block1Pose),
                                new Point(basket330Pose)
                        )
                )
                .setLinearHeadingInterpolation(angle1, basket330Pose.getHeading())
                .addParametricCallback(0, () -> pivot.setPos("Basket"))
                .addParametricCallback(b1ExtensionPar, () -> extension.setPos("Basket"))
                .addParametricCallback(b1WristBackPar, () -> wrist.setPos("Basket"))
                .addParametricCallback(b1ScorePar, () -> intake.setAuto(false))
                .addParametricCallback(b1ScorePar, () -> intake.outtake())
                .build();

        getBlock2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(basket330Pose),
                                new Point(block2Pose)
                        )
                )
                .setLinearHeadingInterpolation(basket330Pose.getHeading(), angle2, finishTurn2)
                .addParametricCallback(b1RetractPar, () -> extension.setPos("Idle"))
                .addParametricCallback(0, () -> wrist.setPos("Intake"))
                .addParametricCallback(b1PivotDownPar, () -> pivot.setPos("Down"))
                .addParametricCallback(0.5, () -> intake.setAuto(true))
                .build();

        scoreBlock2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(block2Pose),
                                new Point(basket330Pose)
                        )
                )
                .setLinearHeadingInterpolation(angle2, basket330Pose.getHeading())
                .addParametricCallback(0, () -> pivot.setPos("Basket"))
                .addParametricCallback(b2ExtensionPar, () -> extension.setPos("Basket"))
                .addParametricCallback(b2WristBackPar, () -> wrist.setPos("Basket"))
                .addParametricCallback(b2ScorePar, () -> intake.setAuto(false))
                .addParametricCallback(b2ScorePar, () -> intake.outtake())
                .build();

        getBlock3 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(basket330Pose),
                                new Point(27.344, 125.179, Point.CARTESIAN),
                                new Point(block3Pose)
                        )
                )
                .setLinearHeadingInterpolation(basket330Pose.getHeading(), block3Pose.getHeading(), finishTurn3)
                .addParametricCallback(b2RetractPar, () -> extension.setPos("Idle"))
                .addParametricCallback(0, () -> wrist.setPos("Intake"))
                .addParametricCallback(b2PivotDownPar, () -> pivot.setPos("Down"))
                .addParametricCallback(0.5, () -> intake.setAuto(true))
                .build();

        scoreBlock3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(block3Pose),
                                new Point(preloadBasketPose)
                        )
                )
                .setLinearHeadingInterpolation(block3Pose.getHeading(), preloadBasketPose.getHeading())
                .addParametricCallback(0, () -> pivot.setPos("Basket"))
                .addParametricCallback(b3ExtensionPar, () -> extension.setPos("Basket"))
                .addParametricCallback(b3WristBackPar, () -> wrist.setPos("Basket"))
                .addParametricCallback(b3ScorePar, () -> intake.setAuto(false))
                .addParametricCallback(b3ScorePar, () -> intake.outtake())
                .build();

        getSubBlock = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(preloadBasketPose),
                                new Point(subPathControlPose),
                                new Point(subPickupPose)
                        )
                )
                .setLinearHeadingInterpolation(preloadBasketPose.getHeading(), subPickupPose.getHeading(), finishTurnSub)
                .addParametricCallback(b3RetractPar, () -> extension.setPos("Idle"))
                .addParametricCallback(b3PivotDownPar, () -> pivot.setPos("Down"))
                .addParametricCallback(0.5, () -> intake.setAuto(true))

                .build();

        scoreSubBlock = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(subPickupPose),
                                new Point(subPathControlPose),
                                new Point(preloadBasketPose)
                        )
                )
                .setLinearHeadingInterpolation(subPickupPose.getHeading(), preloadBasketPose.getHeading(), finishTurnScore)
                .build();

        park = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(preloadBasketPose),
                                new Point(53.000, 106.000, Point.CARTESIAN)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.setMaxPower(1);
                follower.followPath(scorePreload);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    follower.setMaxPower(maxPow1);
                    follower.followPath(getBlock1, true);
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    follower.setMaxPower(1);
                    follower.followPath(scoreBlock1, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    follower.setMaxPower(maxPow1);
                    follower.followPath(getBlock2, true);
                    setPathState(4);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    follower.setMaxPower(1);
                    follower.followPath(scoreBlock2, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    follower.setMaxPower(maxPow1);
                    follower.followPath(getBlock3, true);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    follower.setMaxPower(1);
                    follower.followPath(scoreBlock3, true);
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    follower.setMaxPower(1);
                    follower.followPath(getSubBlock, true);
                    setPathState(8);
                }
                break;
            case 8:
                if (!follower.isBusy()) {
                    follower.followPath(scoreSubBlock, true);
                    setPathState(9);
                }
                break;
            case 9:
                if (!follower.isBusy()) {
                    follower.followPath(getSubBlock, true);
                    setPathState(10);
                }
                break;
            case 10:
                if (!follower.isBusy()) {
                    follower.followPath(scoreSubBlock, true);
                    setPathState(11);
                }
                break;
            case 11:
                if (!follower.isBusy()) {
                    follower.followPath(park, true);
                    setPathState(12);
                }
                break;
            case 12:
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        pivot = new Pivot(hardwareMap, util.deviceConf);
        extension = new Extension(hardwareMap, util.deviceConf);
        wrist = new Wrist(hardwareMap, util.deviceConf);
        intake = new ActiveIntake(hardwareMap, util.deviceConf);
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        pivot.setPos("Start");
        wrist.setPos("Start");
        buildPaths();
    }

    @Override
    public void loop() {
        follower.update();
        pivot.update();
        wrist.update();
        extension.update();
        intake.update();
        autonomousPathUpdate();
        telemetry.addData("Path State", pathState);
        telemetry.addData("Position", follower.getPose().toString());
        telemetry.addData("eror", pivot.getError());
        telemetry.update();
        follower.drawOnDashBoard();
        intake.auto("Red");
    }

    @Override
    public void start() {
        setPathState(0);
        pivot.setPos("Basket");
        wrist.setPos("Idle");
    }

    @Override
    public void init_loop() {
        pivot.update();
        wrist.update();
    }
    


}
