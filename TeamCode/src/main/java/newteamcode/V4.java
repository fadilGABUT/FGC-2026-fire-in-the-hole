package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "V4 H-DRIVE", group = "TeleOp")
public class V4 extends OpMode {

    private DcMotor DTL = null;
    private DcMotor DTR = null;
    private DcMotor DTS = null;


    private static final double STRAFE_POWER_SCALE = 1.0;

    @Override
    public void init() {
        DTL = hardwareMap.get(DcMotor.class, "left_drive");
        DTR = hardwareMap.get(DcMotor.class, "right_drive");
        DTS = hardwareMap.get(DcMotor.class, "strafe_drive");

        DTL.setDirection(DcMotor.Direction.REVERSE);
        DTR.setDirection(DcMotor.Direction.FORWARD);
        
   
        DTS.setDirection(DcMotor.Direction.FORWARD);

        DTL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        double leftPower = drive + turn;
        double rightPower = drive - turn;
        double strafePower = strafe * STRAFE_POWER_SCALE;

        double max = Math.max(Math.abs(leftPower), Math.max(Math.abs(rightPower), Math.abs(strafePower)));
        if (max > 1.0) {
            leftPower /= max;
            rightPower /= max;
            strafePower /= max;
        }

        DTL.setPower(leftPower);
        DTR.setPower(rightPower);
        DTS.setPower(strafePower);

        telemetry.addData("Left Power", leftPower);
        telemetry.addData("Right Power", rightPower);
        telemetry.addData("Strafe Power (Core Hex)", strafePower);
    }
}