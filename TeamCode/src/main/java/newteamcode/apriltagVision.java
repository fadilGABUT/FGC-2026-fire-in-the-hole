package newteamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import global.first.IgnitingInnovationGameDatabase;

public class apriltagVision {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    public void init(HardwareMap hardwareMap) {
        aprilTag = new AprilTagProcessor.Builder()
                .setTagLibrary(IgnitingInnovationGameDatabase.getIgnitingInnovationTagLibrary())
                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
    }

    public AprilTagDetection getDetectionById(int targetId) {
        if (aprilTag == null) return null;
        List<AprilTagDetection> detections = aprilTag.getDetections();
        for (AprilTagDetection detection : detections) {
            if (detection.metadata != null && detection.id == targetId) {
                return detection;
            }
        }
        return null;
    }

    public void loop(Telemetry telemetry) {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        telemetry.addData("Status", "AprilTag Running");
        telemetry.addData("Tags Detected", detections.size());

        for (AprilTagDetection detection : detections) {
            if (detection.metadata != null && detection.ftcPose != null) {
                telemetry.addData("ID", detection.id);
                telemetry.addData("Name", detection.metadata.name);
                telemetry.addData("X", "%.2f cm", detection.ftcPose.x);
                telemetry.addData("Y", "%.2f cm", detection.ftcPose.y);
                telemetry.addData("Z", "%.2f cm", detection.ftcPose.z);
                telemetry.addData("Pitch", "%.2f deg", detection.ftcPose.pitch);
                telemetry.addData("Roll", "%.2f deg", detection.ftcPose.roll);
                telemetry.addData("Yaw", "%.2f deg", detection.ftcPose.yaw);
                telemetry.addData("Range", "%.2f cm", detection.ftcPose.range);
                telemetry.addData("Bearing", "%.2f deg", detection.ftcPose.bearing);
                telemetry.addData("Elevation", "%.2f deg", detection.ftcPose.elevation);
            } else {
                telemetry.addData("ID Unknown", detection.id);
                telemetry.addData("Center X", detection.center.x);
                telemetry.addData("Center Y", detection.center.y);
            }
        }
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}