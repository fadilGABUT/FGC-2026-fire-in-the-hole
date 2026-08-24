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
public class multi3 {
    public DcMotorEx ShooterL;
    private DcMotorEx ShooterR;
    public DcMotor Feed;
    public static double highVelocity = 2100;
    public static double minvelocity = 1400;
    public static double minreverseVelo = -1400;


    public boolean lastRB = false, lastLB = false, shooterOn = false, feedOn = false, lastX = false, shooterReverseOn = false;

    public void init(HardwareMap hwmap, Telemetry telemetry) {


        ShooterL = hwmap.get(DcMotorEx.class, "ShooterL");
        ShooterR = hwmap.get(DcMotorEx.class, "ShooterR");
        ShooterL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                ShooterL.setVelocityPIDFCoefficients(1.622, 0.162, 0, 16.221);
                ShooterL.setDirection(DcMotor.Direction.REVERSE);
        
        ShooterR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                ShooterR.setVelocityPIDFCoefficients(1.622, 0.162, 0, 16.221);
                

        Feed = hwmap.get(DcMotor.class, "Feed");
        

        

        telemetry.addData("Status", "Robot Ready");
        telemetry.update();
    }

    // public void loop(Gamepad gamepad, Telemetry telemetry) {
    //     boolean currentRB = gamepad.right_bumper;
    //     if (currentRB && !lastRB) {
    //         shooterOn = !shooterOn;
    //          if (shooterOn) {
    //             shooterReverseOn = false;
    //         }
    //     }
    //     lastRB = currentRB;
       
    //     boolean currentX = gamepad.x;
    //     if (currentX && !lastX) {
    //         shooterReverseOn = !shooterReverseOn;
    //         if (shooterReverseOn) {
    //             shooterOn = false; 
    //         }
    //     }
    //     lastX = currentX;

    //     boolean currentLB = gamepad.left_bumper;
    //     if (currentLB && !lastLB) {
    //         if (shooterOn || shooterReverseOn) {
    //             feedOn = !feedOn;
    //         }
    //     }
    //     lastLB = currentLB;

    //     if (!shooterOn && !shooterReverseOn) {
    //         feedOn = false;
    //     }

    //     if (feedOn) {

    //         boolean isSpeedReady = false;
    //         if (shooterOn) {
    //         isSpeedReady = ShooterL.getVelocity() >= minvelocity;
    //         } else if (shooterReverseOn) {
                
    //             isSpeedReady = ShooterL.getVelocity() <= minreverseVelo;
    //         }
    //         boolean isDistanceOK = sensorDistance.getDistance(DistanceUnit.CM) >= 30;

    //     if (feedOn) {
    //         if (isDistanceOK || isSpeedReady) {
    //             Feed.setPower(1.0); 
    //         } else {
    //             Feed.setPower(0.0);
    //         }
    //     } else {
    //         Feed.setPower(0.0);
    //     }
    //     } else {
    //         Feed.setPower(0.0);
    //     }
    //     if (shooterOn) {
    //         ShooterL.setVelocity(highVelocity);
    //     } else if (shooterReverseOn) {
    //         ShooterL.setVelocity(-highVelocity);
    //     } else {
    //         ShooterL.setVelocity(0);
    //     }

    //     telemetry.addData("Feed On", feedOn);
    //     telemetry.addData("Shooter Forward", shooterOn);
    //     telemetry.addData("Shooter Reverse", shooterReverseOn);

    //     FtcDashboard dashboard = FtcDashboard.getInstance();
    //     Telemetry dashboardTelemetry = dashboard.getTelemetry();
    //     dashboardTelemetry.addData("ball", sensorDistance.getDistance(DistanceUnit.CM));
    //     dashboardTelemetry.addData("velocity", ShooterL.getVelocity());
    //     dashboardTelemetry.update();
    // }
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
            boolean isSpeedLReady = Math.abs(ShooterL.getVelocity()) >= minvelocity;
            boolean isSpeedRReady = Math.abs(ShooterR.getVelocity()) >= minvelocity;
            boolean isSpeedReady = isSpeedLReady || isSpeedRReady;

            if (isSpeedReady) {
                Feed.setPower(1.0);
            } else {
                Feed.setPower(0.0);
            }
        } else {
            Feed.setPower(0.0);
        }

        if (shooterOn) {
            ShooterL.setVelocity(highVelocity);
            ShooterR.setVelocity(highVelocity);
        } else {
            ShooterL.setVelocity(0);
            ShooterR.setVelocity(0);
        }

        telemetry.addData("Feed On", feedOn);
        telemetry.addData("Shooter On", shooterOn);
        telemetry.addData("Shooter Velocity", Math.abs(ShooterL.getVelocity()));
        telemetry.addData("ShooterR Velocity", Math.abs(ShooterR.getVelocity()));

        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();
        dashboardTelemetry.addData("velocity", Math.abs(ShooterR.getVelocity()));
        dashboardTelemetry.addData("velocity", Math.abs(ShooterL.getVelocity()));
        dashboardTelemetry.update();
    }
}