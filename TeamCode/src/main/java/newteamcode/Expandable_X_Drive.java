package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "E(X)PAND_NIH_LHO", group = "Linear OpMode")
public class Expandable_X_Drive extends LinearOpMode {

    private DcMotorEx DTLF;
    private DcMotorEx DTRF;
    private DcMotorEx DTLB;
    private DcMotorEx DTRB;
    private IMU imu;

    public static double EXPAND_SPEED_MULTIPLIER = 0.6;
    public static final double JOYSTICK_DEADZONE = 0.05;

    @Override
    public void runOpMode() {

        DTLF = hardwareMap.get(DcMotorEx.class, "DTLF");
        DTRF = hardwareMap.get(DcMotorEx.class, "DTRF");
        DTLB = hardwareMap.get(DcMotorEx.class, "DTLB");
        DTRB = hardwareMap.get(DcMotorEx.class, "DTRB");
        imu = hardwareMap.get(IMU.class, "imu");

        DTLF.setDirection(DcMotorSimple.Direction.REVERSE);
        DTLB.setDirection(DcMotorSimple.Direction.REVERSE);
        DTRF.setDirection(DcMotorSimple.Direction.FORWARD);
        DTRB.setDirection(DcMotorSimple.Direction.FORWARD);

        DTLF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTRF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTLB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        DTRB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));
        imu.initialize(parameters);

        telemetry.addData("Status", "X DRIVE EXPAND");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            
            double axial   =  gamepad1.left_stick_y;  
            double lateral = -gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            if (gamepad1.b) {
                imu.resetYaw();
            }
            
            if (Math.abs(axial) < JOYSTICK_DEADZONE) axial = 0.0;
            if (Math.abs(lateral) < JOYSTICK_DEADZONE) lateral = 0.0;
            if (Math.abs(yaw) < JOYSTICK_DEADZONE) yaw = 0.0;

            double joyMag = Math.hypot(lateral, axial);

            if (joyMag > 1.0) {
                axial /= joyMag;
                lateral /= joyMag;
            }

            double flPower, frPower, blPower, brPower;

            if (gamepad1.a) {
                lateral = 0;
                yaw = 0;

                flPower = -axial * EXPAND_SPEED_MULTIPLIER;
                frPower = -axial * EXPAND_SPEED_MULTIPLIER;
                blPower =  axial * EXPAND_SPEED_MULTIPLIER;
                brPower =  axial * EXPAND_SPEED_MULTIPLIER;
            } else {
                double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

                double rotX = lateral * Math.cos(-heading) - axial * Math.sin(-heading);
                double rotY = lateral * Math.sin(-heading) + axial * Math.cos(-heading);

                flPower = rotY + rotX + yaw;
                frPower = rotY - rotX - yaw;
                blPower = rotY - rotX + yaw;
                brPower = rotY + rotX - yaw;
            }

            double max = Math.max(Math.abs(flPower), Math.abs(frPower));
            max = Math.max(max, Math.abs(blPower));
            max = Math.max(max, Math.abs(brPower));

            if (max > 1.0) {
                flPower /= max;
                frPower /= max;
                blPower /= max;
                brPower /= max;
            }

            DTLF.setPower(flPower);
            DTRF.setPower(frPower);
            DTLB.setPower(blPower);
            DTRB.setPower(brPower);

            telemetry.addData("Mode Aktif", gamepad1.a ? "EXTEND" : "FIELD RELATIVE");
            telemetry.addData("Power FL | FR", "%.2f | %.2f", flPower, frPower);
            telemetry.addData("Power BL | BR", "%.2f | %.2f", blPower, brPower);
            telemetry.update();
        }
    }
}