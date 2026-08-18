package newteamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class apriltagVision {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    public void init(HardwareMap hardwareMap) {
        aprilTag = new AprilTagProcessor.Builder()
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .setTagLibrary(AprilTagGameDatabase.getCurrentGameTagLibrary())
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
    }

    public void loop(Telemetry telemetry) {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        telemetry.addData("Status", "AprilTag Running");
        telemetry.addData("Tags Detected", detections.size());

        for (AprilTagDetection detection : detections) {
            telemetry.addData("ID", detection.id);

            if (detection.ftcPose != null) {
                telemetry.addData("X", "%.2f in", detection.ftcPose.x);
                telemetry.addData("Y", "%.2f in", detection.ftcPose.y);
                telemetry.addData("Z", "%.2f in", detection.ftcPose.z);
                telemetry.addData("Yaw", "%.2f deg", detection.ftcPose.yaw);
                telemetry.addData("Pitch", "%.2f deg", detection.ftcPose.pitch);
                telemetry.addData("Roll", "%.2f deg", detection.ftcPose.roll);
            } else {
                telemetry.addData("Pose Status", "ftcPose is NULL");
            }
        }
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}