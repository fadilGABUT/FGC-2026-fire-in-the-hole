package newteamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;import com.qualcomm.robotcore.hardware.DcMotorSimple;import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp(name = "TestDriveBase", group = "Test")
public class TestDriveBase extends LinearOpMode {

    private DcMotor DL;
    private DcMotor DR;
    private DcMotor DTL;
    private DcMotor DTR;
    private CRServo Hold1;
    private CRServo Hold2;

    @Override
    public void runOpMode() {

        DL = hardwareMap.get(DcMotor.class, "DL");
        DR = hardwareMap.get(DcMotor.class, "DR");
        DTL = hardwareMap.get(DcMotor.class, "DTL");
        DTR = hardwareMap.get(DcMotor.class, "DTR");
        Hold1 = hardwareMap.get(CRServo.class, "Hold1");
        Hold2 = hardwareMap.get(CRServo.class, "Hold2");

        DL.setDirection(DcMotor.Direction.FORWARD);
        DR.setDirection(DcMotor.Direction.REVERSE);
        DTL.setDirection(DcMotor.Direction.FORWARD);
        DTR.setDirection(DcMotor.Direction.REVERSE);
        Hold1.setDirection(CRServo.Direction.FORWARD);
        Hold2.setDirection(CRServo.Direction.REVERSE);

        DL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        DR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        DTL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Robot Ready (2x Analog Servo)");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {

            double drive = gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;

            double leftPower = drive + turn;
            double rightPower = drive - turn;

            double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (max > 1.0) {
                leftPower /= max;
                rightPower /= max;
            }
            double backLeftPower;
            double backRightPower;
            if (gamepad1.a) {
                backLeftPower = -gamepad1.left_stick_y;
                backRightPower = -gamepad1.left_stick_y;
            }else{
                backLeftPower = leftPower;
                backRightPower = rightPower;
            }
            DL.setPower(leftPower);
            DR.setPower(rightPower);
            DTL.setPower(backLeftPower);
            DTR.setPower(backRightPower);

            // Servo
            double servoPower = gamepad1.right_trigger - gamepad1.left_trigger;
            Hold1.setPower(servoPower);
            Hold2.setPower(servoPower);


            telemetry.addData("L Pwr", "%.2f", leftPower);
            telemetry.addData("R Pwr", "%.2f", rightPower);
            telemetry.addData("Belakang", gamepad1.a ? "DIEM (Brake)" : "IKUT JALAN");
            telemetry.addData("Servo Pwr", "%.2f", servoPower);
            telemetry.update();
        }
    }
    }