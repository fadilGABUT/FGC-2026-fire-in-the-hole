package newteamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class fieldrive {
    public DcMotor FLmotor, FRmotor, BLmotor, BRmotor, Intake;
    public IMU imu;

    public static double EXPAND_SPEED_MULTIPLIER = 1.0;
    public static double MAX_SPEED = 0.85;

    public void init(HardwareMap hwmap) {
        FLmotor = hwmap.get(DcMotor.class, "DTLF");
        FRmotor = hwmap.get(DcMotor.class, "DTRF");
        BLmotor = hwmap.get(DcMotor.class, "DTLB");
        BRmotor = hwmap.get(DcMotor.class, "DTRB");

        FLmotor.setDirection(DcMotor.Direction.REVERSE);
        BLmotor.setDirection(DcMotor.Direction.REVERSE);

        FLmotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FRmotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BLmotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BRmotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Intake = hwmap.get(DcMotor.class, "Int");

        imu = hwmap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        ));
        imu.initialize(parameters);
    }

    public void resetHeading() {
        imu.resetYaw();
    }

    public void drive(Gamepad gamepad, boolean isFieldCentric) {
        double forward = gamepad.left_stick_y;
        double strafe = -gamepad.left_stick_x;
        double rotate = -gamepad.right_stick_x;

        forward = Math.pow(forward, 3);
        strafe = Math.pow(strafe, 3);
        rotate = Math.pow(rotate, 3);

        double rotForward = forward;
        double rotStrafe = strafe;

        if (isFieldCentric) {
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            double r = Math.hypot(strafe, forward);
            double targetAngle = Math.atan2(forward, strafe);

            targetAngle -= botHeading;

            rotStrafe = r * Math.cos(targetAngle);
            rotForward = r * Math.sin(targetAngle);
        }

        double FLpower = rotForward + rotStrafe + rotate;
        double BLpower = rotForward - rotStrafe + rotate;
        double FRpower = rotForward - rotStrafe - rotate;
        double BRpower = rotForward + rotStrafe - rotate;

        double maxpower = Math.max(1.0, Math.max(Math.abs(FLpower),
                          Math.max(Math.abs(BLpower),
                          Math.max(Math.abs(FRpower), Math.abs(BRpower)))));

        FLmotor.setPower(MAX_SPEED * (FLpower / maxpower));
        BLmotor.setPower(MAX_SPEED * (BLpower / maxpower));
        FRmotor.setPower(MAX_SPEED * (FRpower / maxpower));
        BRmotor.setPower(MAX_SPEED * (BRpower / maxpower));

        Intake.setPower(gamepad.left_trigger - gamepad.right_trigger);
    }

    public void driveToAprilTag(double forwardError, double strafeError, double yawError) {
        double Kp_forward = 0.025;
        double Kp_strafe  = 0.025;
        double Kp_rotate  = 0.015;
        double maxAutoPower = 0.6;

        double forward = Math.max(-maxAutoPower, Math.min(maxAutoPower, forwardError * Kp_forward));
        double strafe  = Math.max(-maxAutoPower, Math.min(maxAutoPower, strafeError * Kp_strafe));
        double rotate  = Math.max(-maxAutoPower, Math.min(maxAutoPower, yawError * Kp_rotate));

        double FLpower = forward + strafe + rotate;
        double BLpower = forward - strafe + rotate;
        double FRpower = forward - strafe - rotate;
        double BRpower = forward + strafe - rotate;

        double maxpower = Math.max(1.0, Math.max(Math.abs(FLpower),
                          Math.max(Math.abs(BLpower),
                          Math.max(Math.abs(FRpower), Math.abs(BRpower)))));

        FLmotor.setPower(FLpower / maxpower);
        BLmotor.setPower(BLpower / maxpower);
        FRmotor.setPower(FRpower / maxpower);
        BRmotor.setPower(BRpower / maxpower);
    }

    public void expand(double forward) {
        double flPower = -forward * EXPAND_SPEED_MULTIPLIER;
        double frPower = -forward * EXPAND_SPEED_MULTIPLIER;
        double blPower =  forward * EXPAND_SPEED_MULTIPLIER;
        double brPower =  forward * EXPAND_SPEED_MULTIPLIER;

        FLmotor.setPower(flPower);
        FRmotor.setPower(frPower);
        BLmotor.setPower(blPower);
        BRmotor.setPower(brPower);
    }
}