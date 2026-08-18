package newteamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class apriltagVision {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    public void init(HardwareMap hardwareMap) {
        aprilTag = new AprilTagProcessor.Builder()
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
                telemetry.addData("X", "%.2f", detection.ftcPose.x);
                telemetry.addData("Y", "%.2f", detection.ftcPose.y);
                telemetry.addData("Z", "%.2f", detection.ftcPose.z);
                telemetry.addData("Yaw", "%.2f", detection.ftcPose.yaw);
                telemetry.addData("Pitch", "%.2f", detection.ftcPose.pitch);
                telemetry.addData("Roll", "%.2f", detection.ftcPose.roll);
            }
        }
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}