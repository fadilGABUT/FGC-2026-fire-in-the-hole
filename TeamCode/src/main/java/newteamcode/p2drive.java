package newteamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class p2drive {
    public DcMotor FLmotor, FRmotor, BLmotor, BRmotor;

    public static double SPEED_MULTIPLIER = 0.2;

    double forward;
    double strafe;
    double rotate;

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
    }

    public void drive(Gamepad gamepad){
        forward = -gamepad.left_stick_y * SPEED_MULTIPLIER;
        strafe = -gamepad.left_stick_x * SPEED_MULTIPLIER;
        rotate = -gamepad.right_stick_x * SPEED_MULTIPLIER;

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
}