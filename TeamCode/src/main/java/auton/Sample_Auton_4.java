package auton;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import subsystems.Claw;
import subsystems.Extension;
import subsystems.Pivot;
import subsystems.Util;
import subsystems.Wrist;

/**
 * 0+4 Bucket Auton
 * @author FormulaCarbon - 21932 Forged In Iron
 * @version 0.1, 2/12/2025
 */

@Autonomous(name = "4 Sample", group = "Sample")
public class Sample_Auton_4 extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTImer, opmodeTimer;

    private int pathState;

    private final Pose startPose = new Pose(6.000, 112.500, Math.toRadians(270));

    private final Pose basketPose = new Pose(14.000, 130.000, Math.toRadians(315));

    private final Pose block1Pos = new Pose();
    private final Pose block2Pose = new Pose();
    private final Pose block3Pose = new Pose();

    private final Pose parkPose = new Pose();

    private Util util = new Util();
    private Pivot pivot = new Pivot(hardwareMap, util.deviceConf);
    private Extension extension = new Extension(hardwareMap, util.deviceConf);
    private Wrist wrist = new Wrist(hardwareMap, util.deviceConf);
    private Claw claw = new Claw(hardwareMap, util.deviceConf);

    private PathChain drop0, park, grab1, grab2, grab3, drop1, drop2, drop3;

    public static double pivotUp = 0.1, extensionUp0 = 0.4;

    public void buildPaths() {
        // Score Preload
        drop0 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(startPose),
                                new Point(19.096, 122.176, Point.CARTESIAN), // Control Point
                                new Point(basketPose)
                        )
                )
                .setLinearHeadingInterpolation(startPose.getHeading(), basketPose.getHeading())
                .build();

    }



    @Override
    public void init() {

    }

    @Override
    public void loop() {

    }
}
