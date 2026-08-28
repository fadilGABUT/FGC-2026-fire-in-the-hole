package newteamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class pinwheeldrive {
    public DcMotor DTX1, DTX2, DTY1, DTY2, Intake;
    public static double MAX_SPEED = 0.85;

    public void init(HardwareMap hwmap) {
        DTX1   = hwmap.get(DcMotor.class, "DTX1");
        DTX2   = hwmap.get(DcMotor.class, "DTX2");
        DTY1   = hwmap.get(DcMotor.class, "DTY1");
        DTY2   = hwmap.get(DcMotor.class, "DTY2");
        Intake = hwmap.get(DcMotor.class, "Int");

        DTX1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTX2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTY1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTY2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        DTX2.setDirection(DcMotor.Direction.REVERSE);
        DTY2.setDirection(DcMotor.Direction.REVERSE);
        Intake.setDirection(DcMotor.Direction.REVERSE);
    }

    public void drive(Gamepad gamepad) {
        double rawY = -gamepad.left_stick_y;
        double rawX = gamepad.left_stick_x;
        double rotate = gamepad.right_stick_x;

        double magnitude = Math.hypot(rawX, rawY);
        double curvedMagnitude = Math.pow(magnitude, 3);

        double forward = 0;
        double strafe = 0;

        if (magnitude > 0) {
            forward = (rawY / magnitude) * curvedMagnitude;
            strafe  = (rawX / magnitude) * curvedMagnitude;
        }

        rotate = Math.pow(rotate, 3);

        double dtx1Power = strafe + rotate;
        double dtx2Power = strafe - rotate;
        double dty1Power = forward + rotate;
        double dty2Power = forward - rotate;

        double maxPower = Math.max(1.0, Math.max(Math.abs(dtx1Power),
                          Math.max(Math.abs(dtx2Power),
                          Math.max(Math.abs(dty1Power), Math.abs(dty2Power)))));

        DTX1.setPower(MAX_SPEED * (dtx1Power / maxPower));
        DTX2.setPower(MAX_SPEED * (dtx2Power / maxPower));
        DTY1.setPower(MAX_SPEED * (dty1Power / maxPower));
        DTY2.setPower(MAX_SPEED * (dty2Power / maxPower));

        Intake.setPower(gamepad.right_trigger - gamepad.left_trigger);
    }
}