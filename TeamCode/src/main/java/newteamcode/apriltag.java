package newteamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "apriltag", group = "TeleOp")
public class apriltag extends OpMode {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void init() {
        aprilTag = new AprilTagProcessor.Builder().build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .enableLiveView(true)
                .setAutoStopLiveView(false)
                .build();

        FtcDashboard.getInstance().startCameraStream(visionPortal, 0);
    }

    @Override
    public void loop() {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        telemetry.addData("Tag Detected", detections.size());

        for (AprilTagDetection detection : detections) {
            telemetry.addData("ID", detection.id);
            if (detection.ftcPose != null) {
                telemetry.addData("X (inch)", detection.ftcPose.x);
                telemetry.addData("Y (inch)", detection.ftcPose.y);
                telemetry.addData("Yaw (deg)", detection.ftcPose.yaw);
            }
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    public List<AprilTagDetection> getDetections() {
        return aprilTag.getDetections();
    }
}