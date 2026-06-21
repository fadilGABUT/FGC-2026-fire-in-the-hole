package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp
public class testing extends OpMode {
    private DcMotorEx DTkanan, DTkiri, BallFeed, HookSlide, SlideShoot, HookLift, ShooterKanan, ShooterKiri;
    private Servo      stopper;
    private static final int    LIFT_TICKS        = 400;
    private static final double LIFT_POWER        = 0.8;
    private static final double SLIDE_SHOOT_POWER = 0.8;

    private static final double TARGET_SHOOTER_VELO = 3000.0;

    public static final double P = 15.0;
    public static final double I = 3.0;
    public static final double D = 1.0;
    public static final double F = 23.0;

    private boolean liftUp    = false;
    private boolean lastDU     = false;
    private boolean lastDD     = false;
    private boolean lastY1     = false; // Menggunakan lastY1 untuk Driver 1
    private boolean shooterOn = false;

    private static final double TICKS_PER_REV = 28.0;

    @Override
    public void init() {

        // --- 1. HARDWARE MAPPING ---
        DTkanan = hardwareMap.get(DcMotorEx.class, "DTKanan");
        DTkiri = hardwareMap.get(DcMotorEx.class, "DTKiri");
        BallFeed = hardwareMap.get(DcMotorEx.class, "Intake");
        HookSlide = hardwareMap.get(DcMotorEx.class, "HookSlide");
        SlideShoot = hardwareMap.get(DcMotorEx.class, "SlideShoot");
        HookLift = hardwareMap.get(DcMotorEx.class, "HookLift");
        ShooterKanan = hardwareMap.get(DcMotorEx.class, "Shooter1");
        ShooterKiri = hardwareMap.get(DcMotorEx.class, "Shooter2");
        stopper = hardwareMap.get(Servo.class, "Stopper");

        // --- 2. MOTOR DIRECTION ---
        DTkiri.setDirection(DcMotor.Direction.REVERSE);
        HookLift.setDirection(DcMotor.Direction.REVERSE);

        // Sesuai permintaan: Keduanya diset FORWARD secara hardware
        ShooterKiri.setDirection(DcMotor.Direction.REVERSE);

        // --- 3. ZERO POWER BEHAVIOR ---
        DTkanan.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTkiri.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BallFeed.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        HookSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SlideShoot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        HookLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ShooterKanan.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        ShooterKiri.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // --- 4. RUN MODES ---
        BallFeed.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        SlideShoot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ShooterKanan.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ShooterKiri.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Injeksi koefisien PIDF Shooter
        PIDFCoefficients pidfCoeffs = new PIDFCoefficients(P, I, D, F);
        ShooterKanan.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoeffs);
        ShooterKiri.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoeffs);

        HookLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        HookLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Robot Ready");
        telemetry.update();

    }
        @Override
        public void loop(){
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
            DTkanan.setVelocity(leftPower*2200);
            DTkiri.setVelocity(rightPower*2200);
            // ── Stopper Servo Control (GP2 - Tombol B) ────────────────
            if (gamepad2.x) {
                stopper.setPosition(0.5);
            } else if (gamepad2.b) {
                stopper.setPosition(0.25);
            }
            // ── Kontrol Intake Mandiri (GP1 - LT / RT) ────────────────

            if (gamepad1.right_trigger > 0.1) {
                BallFeed.setPower(gamepad1.right_trigger);
            } else if (gamepad1.left_trigger > 0.1) {
                BallFeed.setPower(-gamepad1.left_trigger);
            } else {
                BallFeed.setPower(0.0);
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
                ShooterKanan.setVelocity(TARGET_SHOOTER_VELO);
                ShooterKiri.setVelocity(TARGET_SHOOTER_VELO);
            } else if (shootManualIn > 0.1) {
                // Kontrol Manual Maju via Right Trigger GP2
                ShooterKanan.setPower(shootManualIn);
                ShooterKiri.setPower(shootManualIn);
            } else if (shootManualOut > 0.1) {
                // Kontrol Manual Mundur via Left Trigger GP2
                ShooterKanan.setPower(-shootManualOut);
                ShooterKiri.setPower(-shootManualOut);
            } else {
                ShooterKanan.setPower(0.0);
                ShooterKiri.setPower(0.0);
            }

            // ── Slider Struktur Shooter Mandiri (GP2 - Tombol Y / A) ──
            // Sesuai permintaan: Y untuk naik/maju, A untuk turun/mundur
            if (gamepad2.y) {
                SlideShoot.setPower(SLIDE_SHOOT_POWER);
            } else if (gamepad2.a) {
                SlideShoot.setPower(-SLIDE_SHOOT_POWER);
            }else {
                SlideShoot.setPower(0);
            }

            // ── HookSlide Control (GP2 - Left Stick) ──────────────────
            HookSlide.setPower(-gamepad2.left_stick_y);

            // ── HookLift Auto/Manual Control (GP2) ────────────────────
            boolean duPressed = gamepad2.dpad_up   && !lastDU;
            boolean ddPressed = gamepad2.dpad_down && !lastDD;

            if (gamepad2.left_bumper) {
                HookLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                HookLift.setPower(LIFT_POWER);
            } else if (gamepad2.right_bumper) {
                HookLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                HookLift.setPower(-LIFT_POWER);
            } else if (duPressed) {
                liftUp = true;
                HookLift.setTargetPosition(LIFT_TICKS);
                HookLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                HookLift.setPower(LIFT_POWER);
            } else if (ddPressed) {
                liftUp = false;
                HookLift.setTargetPosition(0);
                HookLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                HookLift.setPower(LIFT_POWER);
            } else {
                if (HookLift.getMode() == DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                    HookLift.setPower(0.0);
                }
            }
            lastDU = gamepad2.dpad_up;
            lastDD = gamepad2.dpad_down;

            // ── Data Telemetry Feedback ───────────────────────────────
            double rpmM7 = (Math.abs(ShooterKanan.getVelocity()) / TICKS_PER_REV) * 60.0;

            telemetry.addLine("---- Intake System ----");
            telemetry.addData("Intake Power", "%.2f", BallFeed.getPower());

            telemetry.addLine("---- Shooter System ----");
            telemetry.addData("Auto Status (GP1-Y)", shooterOn ? "ON (Auto 3000 RPM)" : "OFF");

            telemetry.addLine("---- Slider & Hook Mechanisms ----");
            telemetry.addData("Slider M5 (GP2 Y/A)", "%.2f", SlideShoot.getPower());

            telemetry.addLine("---- Stopper Servo ----");
            telemetry.addData("Stopper Button", gamepad2.b ? "ACTIVE (B)" : "RELEASED");

            FtcDashboard dashboard = FtcDashboard.getInstance();
            Telemetry dashboardTelemetry = dashboard.getTelemetry();

            dashboardTelemetry.addData("Intake Power", BallFeed.getPower());
            dashboardTelemetry.addData("Shooter RPM", "%.0f RPM", rpmM7);
            dashboardTelemetry.addData("target shooter RPM", TARGET_SHOOTER_VELO);
            dashboardTelemetry.addData("Servopos",stopper.getPosition());
            dashboardTelemetry.update();

            telemetry.update();
        }
    }