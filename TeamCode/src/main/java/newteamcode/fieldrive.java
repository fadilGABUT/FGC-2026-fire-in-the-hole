package newteamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class fieldrive {
    public DcMotor FLmotor, FRmotor, BLmotor, BRmotor;
    public IMU imu;

    public static double EXPAND_SPEED_MULTIPLIER = 1.0;

    public void init(HardwareMap hwmap){
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

        FLmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FRmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BLmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BRmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu = hwmap.get(IMU.class, "imu");
        RevHubOrientationOnRobot Revorientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT);

        imu.initialize(new IMU.Parameters(Revorientation));
    }

    public void resetYaw() {
        imu.resetYaw();
    }

    public void drive(double forward, double strafe, double rotate){
        double FLpower = forward + strafe + rotate;
        double BLpower = forward - strafe + rotate;
        double FRpower = forward - strafe - rotate;
        double BRpower = forward + strafe - rotate;

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
