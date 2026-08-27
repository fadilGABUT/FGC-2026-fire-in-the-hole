package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "riljalan", group = "TeleOp")
public class riljalan extends OpMode {

    fieldrive fieldrive = new fieldrive();
    p2drive p2drive = new p2drive();
    multi2 multi2 = new multi2();
    apriltagVision apriltag = new apriltagVision();

    boolean isFieldRelative = true; 
    boolean lastOptionsState = false;

    public static final double DESIRED_Y_CM   = 53.0;
    public static final double DESIRED_X_CM   = -2.75;
    public static final double DESIRED_YAW_DEG = 0.0;

    @Override
    public void init() {
        fieldrive.init(hardwareMap);
        p2drive.init(hardwareMap);
        multi2.init(hardwareMap, telemetry);
        apriltag.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.options && !lastOptionsState) {
            isFieldRelative = !isFieldRelative;
        }
        lastOptionsState = gamepad1.options;

        if (gamepad1.share) {
            fieldrive.resetHeading();
        }

        AprilTagDetection tag104 = apriltag.getDetectionById(104);

        boolean isP2Moving = Math.abs(gamepad2.left_stick_y) > 0.05 || 
                             Math.abs(gamepad2.left_stick_x) > 0.05 || 
                             Math.abs(gamepad2.right_stick_x) > 0.05;

        if (isP2Moving) {
            p2drive.drive(gamepad2);
        } else if (tag104 != null && tag104.ftcPose != null) {
            double forwardError = tag104.ftcPose.y - DESIRED_Y_CM;
            double strafeError  = tag104.ftcPose.x - DESIRED_X_CM;
            double yawError     = tag104.ftcPose.yaw - DESIRED_YAW_DEG;

            fieldrive.driveToAprilTag(forwardError, strafeError, yawError);
            telemetry.addData("Auto Tracking", "Target 104 Active");
        } else {
            fieldrive.drive(gamepad1, isFieldRelative);
            telemetry.addData("Auto Tracking", "Search / Manual Drive");
        }

        multi2.loop(gamepad2, telemetry);
        apriltag.loop(telemetry);

        telemetry.addData("Drive Mode", isFieldRelative ? "FIELD RELATIVE" : "ROBOT CENTRIC");
    }

    @Override
    public void stop() {
        apriltag.stop();
    }
}