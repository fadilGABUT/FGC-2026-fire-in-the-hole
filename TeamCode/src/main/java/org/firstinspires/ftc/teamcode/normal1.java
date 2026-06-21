package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp
public class normal1 extends LinearOpMode {
    public static  float stopperpos;
    private DcMotor    motor1;   // DTKanan
    private DcMotor    motor2;   // DTkiri
    private DcMotor    motor3;   // Intake Mandiri (BallFeed)
    private DcMotor    motor4;   // HookSlide
    private DcMotor    motor5;   // Slider Struktur Shooter (SlideShoot)
    private DcMotorEx  motor6;   // HookLift
    private DcMotorEx  motor7;   // Shooter1 (Kanan)
    private DcMotorEx  motor8;   // Shooter2 (Kiri)
    private Servo      stopper;

    private static final int    LIFT_TICKS        = 400;
    private static final double LIFT_POWER        = 0.8;
    private static final double SLIDE_SHOOT_POWER = 1.0;

    private static final double TARGET_SHOOTER_VELO = 1800.0;

    private static final double VELO_P = 180.0;
    private static final double VELO_I = 0.0;
    private static final double VELO_D = 0.0;
    private static final double VELO_F = 14.0;

    private boolean liftUp    = false;
    private boolean lastDU     = false;
    private boolean lastDD     = false;
    private boolean lastY1     = false; // Menggunakan lastY1 untuk Driver 1
    private boolean shooterOn = false;

    private static final double TICKS_PER_REV = 28.0;

    @Override
    public void runOpMode() {

        // --- 1. HARDWARE MAPPING ---
        motor1 = hardwareMap.get(DcMotor.class,   "DTKanan");
        motor2 = hardwareMap.get(DcMotor.class,   "DTKiri");
        motor3 = hardwareMap.get(DcMotor.class,   "Intake");
        motor4 = hardwareMap.get(DcMotor.class,   "HookSlide");
        motor5 = hardwareMap.get(DcMotor.class,   "SlideShoot");
        motor6 = hardwareMap.get(DcMotorEx.class, "HookLift");
        motor7 = hardwareMap.get(DcMotorEx.class, "Shooter1");
        hardwareMap.get(DcMotorEx.class, "Shooter2");
        motor8 = hardwareMap.get(DcMotorEx.class, "Shooter2");
        stopper = hardwareMap.get(Servo.class,    "Stopper");

        // --- 2. MOTOR DIRECTION ---
        motor1.setDirection(DcMotor.Direction.FORWARD);
        motor2.setDirection(DcMotor.Direction.REVERSE);
        motor3.setDirection(DcMotor.Direction.FORWARD);
        motor4.setDirection(DcMotor.Direction.FORWARD);
        motor5.setDirection(DcMotor.Direction.FORWARD);
        motor6.setDirection(DcMotor.Direction.REVERSE);

        // Sesuai permintaan: Keduanya diset FORWARD secara hardware
        motor7.setDirection(DcMotor.Direction.FORWARD);
        motor8.setDirection(DcMotor.Direction.FORWARD);

        // --- 3. ZERO POWER BEHAVIOR ---
        motor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor3.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor4.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor5.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor6.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor7.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor8.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // --- 4. RUN MODES ---
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor5.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor7.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor8.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Injeksi koefisien PIDF Shooter
        PIDFCoefficients pidfCoeffs = new PIDFCoefficients(VELO_P, VELO_I, VELO_D, VELO_F);
        motor7.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoeffs);
        motor8.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoeffs);

        motor6.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor6.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Robot Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // ── Drivetrain Control (GP1) ──────────────────────────────
            double drive = -gamepad1.left_stick_y;
            double turn  = -gamepad1.right_stick_x;

            double leftPower  = drive - turn;
            double rightPower = drive + turn;

            double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (max > 1.0) {
                leftPower  /= max;
                rightPower /= max;
            }
            motor1.setPower(leftPower);
            motor2.setPower(rightPower);
            // ── Stopper Servo Control (GP2 - Tombol B) ────────────────
            if (gamepad2.x) {
                stopper.setPosition(0.5);
            } else if (gamepad2.b) {
                stopper.setPosition(0.15);
            }
            // ── Kontrol Intake Mandiri (GP1 - LT / RT) ────────────────
            double feedForwardPower = gamepad1.right_trigger;
            double feedReversePower = gamepad1.left_trigger;

            if (feedForwardPower > 0.1) {
                motor3.setPower(feedForwardPower);
            } else if (feedReversePower > 0.1) {
                motor3.setPower(-feedReversePower);
            } else {
                motor3.setPower(0.0);
            }

            // ── Kontrol Shooter Campuran (GP1 - Toggle Y & GP2 Trigger Manual) ──
            // Pindah pembacaan toggle otomatis ke gamepad1.y
            boolean currentY1 = gamepad1.y;
            if (currentY1 && !lastY1) {
                shooterOn = !shooterOn;
            }
            lastY1 = currentY1;

            double shootManualIn  = gamepad2.right_trigger;
            double shootManualOut = gamepad2.left_trigger;

            if (shooterOn) {
                // Mode Otomatis Aktif (M7 positif, M8 dinegatifkan secara software agar berputar berhadapan)
                motor7.setVelocity(TARGET_SHOOTER_VELO);
                motor8.setVelocity(-TARGET_SHOOTER_VELO);
            } else if (shootManualIn > 0.1) {
                // Kontrol Manual Maju via Right Trigger GP2
                motor7.setPower(shootManualIn);
                motor8.setPower(-shootManualIn);
            } else if (shootManualOut > 0.1) {
                // Kontrol Manual Mundur via Left Trigger GP2
                motor7.setPower(-shootManualOut);
                motor8.setPower(shootManualOut);
            } else {
                motor7.setPower(0.0);
                motor8.setPower(0.0);
            }

            // ── Slider Struktur Shooter Mandiri (GP2 - Tombol Y / A) ──
            // Sesuai permintaan: Y untuk naik/maju, A untuk turun/mundur
            double slideShootPower = 0.0;
            if (gamepad2.y) {
                slideShootPower = SLIDE_SHOOT_POWER;
            } else if (gamepad2.a) {
                slideShootPower = -SLIDE_SHOOT_POWER;
            }
            motor5.setPower(slideShootPower);

            // ── HookSlide Control (GP2 - Left Stick) ──────────────────
            double hookPower = -gamepad2.left_stick_y;
            motor4.setPower(hookPower);

            // ── HookLift Auto/Manual Control (GP2) ────────────────────
            boolean duPressed = gamepad2.dpad_up   && !lastDU;
            boolean ddPressed = gamepad2.dpad_down && !lastDD;

            if (gamepad2.left_bumper) {
                motor6.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor6.setPower(LIFT_POWER);
            } else if (gamepad2.right_bumper) {
                motor6.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor6.setPower(-LIFT_POWER);
            } else if (duPressed) {
                liftUp = true;
                motor6.setTargetPosition(LIFT_TICKS);
                motor6.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor6.setPower(LIFT_POWER);
            } else if (ddPressed) {
                liftUp = false;
                motor6.setTargetPosition(0);
                motor6.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor6.setPower(LIFT_POWER);
            } else {
                if (motor6.getMode() == DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                    motor6.setPower(0.0);
                }
            }
            lastDU = gamepad2.dpad_up;
            lastDD = gamepad2.dpad_down;

            // ── Data Telemetry Feedback ───────────────────────────────
            double currentVeloM7 = Math.abs(motor7.getVelocity());
            double currentVeloM8 = Math.abs(motor8.getVelocity());
            double rpmM7 = (currentVeloM7 / TICKS_PER_REV) * 60.0;
            double rpmM8 = (currentVeloM8 / TICKS_PER_REV) * 60.0;

            telemetry.addLine("---- Drivetrain ----");
            telemetry.addData("L Pwr", "%.2f", leftPower);
            telemetry.addData("R Pwr", "%.2f", rightPower);

            telemetry.addLine("---- Intake System ----");
            telemetry.addData("Intake Power", "%.2f", motor3.getPower());

            telemetry.addLine("---- Shooter System ----");
            telemetry.addData("Auto Status (GP1-Y)", shooterOn ? "ON (Auto 3000 RPM)" : "OFF");
            telemetry.addData("Manual RT (Maju)", "%.2f", shootManualIn);
            telemetry.addData("Manual LT (Mundur)", "%.2f", shootManualOut);
            telemetry.addData("M7 Real Speed", "%.0f RPM", rpmM7);
            telemetry.addData("M8 Real Speed", "%.0f RPM", rpmM8);

            telemetry.addLine("---- Slider & Hook Mechanisms ----");
            telemetry.addData("Slider M5 (GP2 Y/A)", "%.2f", motor5.getPower());
            telemetry.addData("HookSlide Pwr", "%.2f", hookPower);

            telemetry.addLine("---- Stopper Servo ----");
            telemetry.addData("Stopper Button", gamepad2.b ? "ACTIVE (B)" : "RELEASED");

            FtcDashboard dashboard = FtcDashboard.getInstance();
            Telemetry dashboardTelemetry = dashboard.getTelemetry();

            dashboardTelemetry.addData("servopos",stopper.getPosition());
            dashboardTelemetry.addData("M7 Real Speed", "%.0f RPM", rpmM7);
            dashboardTelemetry.update();

            telemetry.update();
        }
    }
}