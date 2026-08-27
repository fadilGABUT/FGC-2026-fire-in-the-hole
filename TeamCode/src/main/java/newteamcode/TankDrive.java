package newteamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TankDrive {
    public DcMotor DTL, DTR, Intake1, Intake2;

    public static double DEADZONE = 0.05;
    public static double EXPO_POWER = 3.0;

    double drive;
    double turn;

    public void init(HardwareMap hwmap) {
        DTL = hwmap.get(DcMotor.class, "DTL");
        DTR = hwmap.get(DcMotor.class, "DTR");
        Intake1 = hwmap.get(DcMotor.class, "Int1");
        Intake2 = hwmap.get(DcMotor.class, "Int2");
        Intake1.setDirection(DcMotor.Direction.FORWARD);
        Intake2.setDirection(DcMotor.Direction.REVERSE);

        DTL.setDirection(DcMotor.Direction.REVERSE);
        DTR.setDirection(DcMotor.Direction.FORWARD);

        DTL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void drive(Gamepad gamepad) {
        drive = processInput(gamepad.left_stick_y);
        turn = processInput(-gamepad.right_stick_x);

        double leftPower = drive + turn;
        double rightPower = drive - turn;

        double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (max > 1.0) {
            leftPower /= max;
            rightPower /= max;
        }

        DTL.setPower(leftPower);
        DTR.setPower(rightPower);

        Intake1.setPower(gamepad.right_trigger - gamepad.left_trigger);
        Intake2.setPower(gamepad.right_trigger - gamepad.left_trigger);
    }

    private double processInput(double input) {
        if (Math.abs(input) < DEADZONE) {
            return 0.0;
        }
        return Math.signum(input) * Math.pow(Math.abs(input), EXPO_POWER);
    }
}