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
    double forward;
    double strafe;
    double rotate;

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

        forward = -gamepad.left_stick_y;
        strafe = gamepad.left_stick_x;
        rotate = gamepad.right_stick_x;

        double rotForward = forward;
        double rotStrafe = strafe;

        if (isFieldCentric) {
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            rotStrafe = strafe * Math.cos(-botHeading) - forward * Math.sin(-botHeading);
            rotForward = strafe * Math.sin(-botHeading) + forward * Math.cos(-botHeading);
        }

        double FLpower = rotForward + rotStrafe + rotate;
        double BLpower = rotForward - rotStrafe + rotate;
        double FRpower = rotForward - rotStrafe - rotate;
        double BRpower = rotForward + rotStrafe - rotate;

        double maxpower = 1.0;
        double maxspeed = 1.0;

        maxpower = Math.max(maxpower, Math.abs(FLpower));
        maxpower = Math.max(maxpower, Math.abs(BLpower));
        maxpower = Math.max(maxpower, Math.abs(FRpower));
        maxpower = Math.max(maxpower, Math.abs(BRpower));

        FLmotor.setPower(maxspeed * (FLpower / maxpower));
        BLmotor.setPower(maxspeed * (BLpower / maxpower));
        FRmotor.setPower(maxspeed * (FRpower / maxpower));
        BRmotor.setPower(maxspeed * (BRpower / maxpower));

        Intake.setPower(gamepad.left_trigger - gamepad.right_trigger);
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