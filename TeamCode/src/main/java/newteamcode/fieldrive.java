package newteamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class fieldrive {
    public DcMotor FLmotor, FRmotor, BLmotor, BRmotor,Intake;

    public static double EXPAND_SPEED_MULTIPLIER = 1.0;
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


        Intake = hwmap.get(DcMotor.class, "Int");
    }

    public void drive(Gamepad gamepad){

        forward = gamepad.left_stick_y;
        strafe = -gamepad.left_stick_x;
        rotate = -gamepad.right_stick_x;

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
