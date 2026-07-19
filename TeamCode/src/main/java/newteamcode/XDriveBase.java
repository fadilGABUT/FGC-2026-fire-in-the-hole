package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "E(X)PAND_NIH_LHO", group = "Linear OpMode")
public class XDriveBase extends LinearOpMode {

    private DcMotorEx DTLF = null;
    private DcMotorEx DTRF = null;
    private DcMotorEx DTLB = null;
    private DcMotorEx DTRB = null;

    public static double EXPAND_SPEED_MULTIPLIER = 0.6;

    @Override
    public void runOpMode() {

        DTLF = hardwareMap.get(DcMotorEx.class, "DTLF");
        DTRF = hardwareMap.get(DcMotorEx.class, "DTRF");
        DTLB = hardwareMap.get(DcMotorEx.class, "DTLB");
        DTRB = hardwareMap.get(DcMotorEx.class, "DTRB");

        DTLF.setDirection(DcMotorSimple.Direction.REVERSE);
        DTLB.setDirection(DcMotorSimple.Direction.REVERSE);
        DTRF.setDirection(DcMotorSimple.Direction.FORWARD);
        DTRB.setDirection(DcMotorSimple.Direction.FORWARD);

        DTLF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTRF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTLB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTRB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Drivebase Ready (OnBot Java Version)");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Mapping arah joystick yang sudah disesuaikan nilainya (+/-)
            double axial   =  gamepad1.left_stick_y;
            double lateral = -gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            // Hitung total nilai kombinasi stick kiri menggunakan Math.hypot (Pythagoras)
            double joyMag = Math.hypot(lateral, axial);

            // Jika total kombinasi stick melebihi 1.0 (saat berada di pojok kotak), normalisasikan nilainya
            if (joyMag > 1.0) {
                axial /= joyMag;
                lateral /= joyMag;
            }

            double flPower, frPower, blPower, brPower;

            // Logika pembagian fungsi tombol kontroler
            if (gamepad1.a) {
                // Saat tombol A ditekan, fungsi jalan biasa dimatikan
                lateral = 0;
                yaw = 0;

                // Output distribusi daya motor dibalik agar: Stick Atas = Melar, Stick Bawah = Menguncup
                flPower = axial * EXPAND_SPEED_MULTIPLIER;
                frPower = axial * EXPAND_SPEED_MULTIPLIER;
                blPower = -axial * EXPAND_SPEED_MULTIPLIER;
                brPower = -axial * EXPAND_SPEED_MULTIPLIER;
            } else {
                // Rumus standar pembagian daya kinetik Holonomic X-Drive 8-Directional
                flPower = axial + lateral + yaw;
                frPower = axial - lateral - yaw;
                blPower = axial - lateral + yaw;
                brPower = axial + lateral - yaw;
            }

            // Aturan normalisasi matematika untuk menjaga kestabilan voltase motor (range -1.0 sampai 1.0)
            double max = Math.max(Math.abs(flPower), Math.abs(frPower));
            max = Math.max(max, Math.abs(blPower));
            max = Math.max(max, Math.abs(brPower));

            if (max > 1.0) {
                flPower /= max;
                frPower /= max;
                blPower /= max;
                brPower /= max;
            }

            // Pengiriman final command daya listrik ke masing-masing port motor
            DTLF.setPower(flPower);
            DTRF.setPower(frPower);
            DTLB.setPower(blPower);
            DTRB.setPower(brPower);

            // Output data monitoring ke perangkat Driver Station
            String modeSekarang = gamepad1.a ? "MODUL EXPAND/RETRACT" : "MODUL DRIVING NORMAL";
            telemetry.addData("Mode Aktif", modeSekarang);
            telemetry.addData("Power FL | FR", "%.2f | %.2f", flPower, frPower);
            telemetry.addData("Power BL | BR", "%.2f | %.2f", blPower, brPower);
            telemetry.update();
        }
    }
}