package newteamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class multi1 {
    public DcMotor DTR,DTL,Int;
    double forward, turn;
    public void init(HardwareMap hwmap){

        DTR = hwmap.get(DcMotor.class,   "DTR");
        DTL = hwmap.get(DcMotor.class,   "DTL");
        DTR.setDirection(DcMotor.Direction.REVERSE);
        Int = hwmap.get(DcMotor.class, "Int");
    }
    public void loop(Gamepad gamepad){

        forward = -gamepad.left_stick_y;
        turn = -gamepad.right_stick_x;

        double leftPower  = forward + turn;
        double rightPower = forward - turn;

        double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (max > 1.0) {
            leftPower  /= max;
            rightPower /= max;
        }
        DTR.setPower(leftPower);
        DTL.setPower(rightPower);
        double intakePower = gamepad.right_trigger - gamepad.left_trigger;
        Int.setPower(intakePower);
    }
}
