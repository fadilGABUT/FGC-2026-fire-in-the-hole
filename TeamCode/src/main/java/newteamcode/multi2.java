package newteamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class multi2 {
    public DcMotorEx ShooterL;
    public DcMotor Feed;
    public static double highVelocity = 2100;
    public static double minvelocity = 1400;
    private DistanceSensor sensorDistance;

    public boolean lastRB = false, lastLB = false, shooterOn = false, feedOn = false;

    public void init(HardwareMap hwmap, Telemetry telemetry) {
        sensorDistance = hwmap.get(DistanceSensor.class, "dis");

        ShooterL = hwmap.get(DcMotorEx.class, "ShooterL");
        ShooterL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        ShooterL.setVelocityPIDFCoefficients(1.622, 0.162, 0, 16.221);
        Feed = hwmap.get(DcMotor.class, "Feed");

        Feed.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Robot Ready");
        telemetry.update();
    }

    public void loop(Gamepad gamepad, Telemetry telemetry) {
        boolean currentRB = gamepad.right_bumper;
        if (currentRB && !lastRB) {
            shooterOn = !shooterOn;
        }
        lastRB = currentRB;

        boolean currentLB = gamepad.left_bumper;
        if (currentLB && !lastLB) {
            if (shooterOn) {
                feedOn = !feedOn;
            }
        }
        lastLB = currentLB;

        if (!shooterOn) {
            feedOn = false;
        }

        if (feedOn) {
            if (sensorDistance.getDistance(DistanceUnit.CM) >= 30 || ShooterL.getVelocity() >= minvelocity) {
                Feed.setPower(1.0);
            } else {
                Feed.setPower(0.0);
            }
        } else {
            Feed.setPower(0.0);
        }

        if (shooterOn) {
            ShooterL.setVelocity(highVelocity);
        } else {
            ShooterL.setVelocity(0);
        }

        telemetry.addData("f", feedOn);
        telemetry.addData("S", shooterOn);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();
        dashboardTelemetry.addData("ball", sensorDistance.getDistance(DistanceUnit.CM));
        dashboardTelemetry.addData("velocity", ShooterL.getVelocity());
        dashboardTelemetry.update();
    }
}