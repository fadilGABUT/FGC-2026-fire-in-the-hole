package newteamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Config
@TeleOp(name = "normal1", group = "TeleOp")
public class normal1 extends LinearOpMode {

    private static final Logger log = LoggerFactory.getLogger(normal1.class);
    public DcMotor    DTR;
    public DcMotor    DTL;
    private DcMotor    Int;
    private DcMotor    Feed;
    private DcMotorEx  ShooterL;

    public DcMotor Storage1;
    private DcMotor Storage2;

    public static double TURN_MULTIPLIER = 0.5;

    public static  double TARGET_SHOOTER_VELO = 1700.0;
    public static double MIN_SHOOTER_VELO = 1500.0;
    public static  double VELO_P = 180.0;
    public static final double VELO_I = 0.0;
    public static final double VELO_D = 0.0;
    public static  double VELO_F = 15.0;
    public static double FEED_TRIGGER_RPM = 1800.0;

    private boolean lastRB     = false;
    private boolean shooterOn = false;

    private static final double TICKS_PER_REV = 28.0;

    @Override
    public void runOpMode() {

        // HARDWARE MAPPING
        DTR = hardwareMap.get(DcMotor.class,   "DTR");
        DTL = hardwareMap.get(DcMotor.class,   "DTL");
        Int = hardwareMap.get(DcMotor.class,   "Int");
        Feed = hardwareMap.get(DcMotor.class,   "Feed");
        ShooterL = hardwareMap.get(DcMotorEx.class, "ShooterL");
        Storage1 = hardwareMap.get(DcMotor.class, "Storage1");
        Storage2 = hardwareMap.get(DcMotor.class, "Storage2");
        // MOTOR DIRECTION
        DTR.setDirection(DcMotor.Direction.REVERSE);
        DTL.setDirection(DcMotor.Direction.FORWARD);
        Int.setDirection(DcMotor.Direction.FORWARD);
        Feed.setDirection(DcMotor.Direction.FORWARD);
        ShooterL.setDirection(DcMotor.Direction.FORWARD);

        // ZERO POWER BEHAVIOR
        DTR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Int.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Feed.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ShooterL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Storage1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        //  RUN MODES
        Int.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Feed.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ShooterL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoeffs = new PIDFCoefficients(VELO_P, VELO_I, VELO_D, VELO_F);
        ShooterL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoeffs);

        telemetry.addData("Status", "Robot Ready");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            ElapsedTime looptimer = new ElapsedTime();
            double Looptimer = looptimer.milliseconds();
            looptimer.reset();

            // Drivetrain
            double drive = -gamepad1.left_stick_y;
            double turn  = -gamepad1.right_stick_x * TURN_MULTIPLIER;

            double leftPower  = drive + turn;
            double rightPower = drive - turn;

            double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (max > 1.0) {
                leftPower  /= max;
                rightPower /= max;
            }
            DTR.setPower(leftPower);
            DTL.setPower(rightPower);

            double currentVeloM8 = Math.abs(ShooterL.getVelocity());
            double rpmM8 = (currentVeloM8 / TICKS_PER_REV) * 60.0;

            // Feeder Motor
            if (gamepad2.dpad_down) {
                Feed.setPower(-1.0);
            } else if (gamepad2.dpad_left) {
                Feed.setPower(-0.8);
            } else if (gamepad2.dpad_up && ShooterL.getVelocity() >= MIN_SHOOTER_VELO) {
                Feed.setPower(1.0);
            } else if (gamepad2.dpad_right) {
                Feed.setPower(-0.8);
            } else {
                Feed.setPower(0.0);
            }

            // ── Intake
            double intakePower = gamepad1.right_trigger - gamepad1.left_trigger;
            Int.setPower(intakePower);

            // ── Storage
            if (gamepad2.x) {
                Storage1.setPower(0.5);
            } else if (gamepad2.y) {
                Storage1.setPower(-0.5);
            } else {
                Storage1.setPower(0.0);

            if (gamepad2.b) {
                Storage2.setPower(1.0);
            } else if (gamepad2.a) {
                Storage2.setPower(-1.0);
            } else {
                Storage2.setPower(0.0);
            }

            // ── Shooter (GP2 - BUMPER)
            boolean currentRB = gamepad2.right_bumper;
            if (currentRB && !lastRB) {
                shooterOn = !shooterOn;
            }
            lastRB = currentRB;
            if (shooterOn) {
                ShooterL.setVelocity(TARGET_SHOOTER_VELO);
            } else if (gamepad2.left_bumper) {
                ShooterL.setPower(-0.8);
            } else {
                ShooterL.setPower(0.0);
            }

            // ── Telemetry
            telemetry.addLine("Hellow Drivers");
            telemetry.addLine("---- Drivetrain ----");
            telemetry.addData("L Pwr", "%.2f", leftPower);
            telemetry.addData("R Pwr", "%.2f", rightPower);

            telemetry.addLine("---- Intake System ----");
            telemetry.addData("Intake Pwr", "%.2f", Int.getPower());

            telemetry.addLine("---- Shooter System ----");
            telemetry.addData("Auto Status", shooterOn ? "ON" : "OFF");
            telemetry.addData("Shooter RPM", "%.0f", rpmM8);

            String feederStatus = "STOP";
            if (gamepad2.dpad_down) {
                feederStatus = "REVERSE 100% (Dpad-Down)";
            } else if (gamepad2.dpad_left) {
                feederStatus = "REVERSE 60% (Dpad-Left)";
            } else if (gamepad2.dpad_up) {
                feederStatus = (rpmM8 >= FEED_TRIGGER_RPM) ? "FORWARD 100% (Dpad-Up)" : "BLOCKED (RPM LOW)";
            } else if (gamepad2.dpad_right) {
                feederStatus = (rpmM8 >= FEED_TRIGGER_RPM) ? "FORWARD 60% (Dpad-Right)" : "BLOCKED (RPM LOW)";
            }
            telemetry.addData("Feeder M5", feederStatus);

            telemetry.addLine("---- Storage ----");
            telemetry.addData("Storage 1 (X)", gamepad2.x ? "ON" : "OFF");
            telemetry.addData("Storage 2 (Y)", gamepad2.y ? "ON" : "OFF");
            telemetry.addData("Storage 3 (A)", gamepad2.a ? "ON" : "OFF");
            telemetry.addData("Storage 4 (B)", gamepad2.b ? "ON" : "OFF");

            // ── FtcDashboard ────────────────────────────────────
            FtcDashboard dashboard = FtcDashboard.getInstance();
            Telemetry dashboardTelemetry = dashboard.getTelemetry();
            dashboardTelemetry.addData("Shooter RPM", "%.0f", ShooterL.getVelocity());
            dashboardTelemetry.addData("Target RPM", TARGET_SHOOTER_VELO);
            dashboardTelemetry.addData("looptime", Looptimer);
            dashboardTelemetry.addData("loop frequency", 1000 / Looptimer);
            dashboardTelemetry.update();

            telemetry.update();
        }
    }
}
    }