package newteamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp
public class PIDF_tuning extends OpMode {
    public DcMotorEx motor1,Feed;
    public static double highVelocity = 1700;
    public static double lowVelocity = 1500;
    public static double MIN_SHOOTER_VELO = 1500;
    public static double p,i,d;
    private double curtargetvelocity = 0;

    @Override
    public void init(){
        motor1 = hardwareMap.get(DcMotorEx.class, "ShooterL");
        Feed = hardwareMap.get(DcMotorEx.class,   "Feed");

    }
    @Override
    public void loop(){
        motor1.setVelocityPIDFCoefficients( p, i, d,16.221);
        if (gamepad1.yWasPressed()){
            if (curtargetvelocity == highVelocity){
                curtargetvelocity = lowVelocity;
            }else {
                curtargetvelocity = highVelocity;
            }
        } else if (gamepad1.x) {
            motor1.setPower(1);
        } else if (gamepad1.a) {
            motor1.setVelocity(0);
        }
        if (gamepad1.dpad_down) {
            Feed.setPower(-1.0);
        } else if (gamepad1.dpad_left) {
            Feed.setPower(-0.8);
        } else if (gamepad1.dpad_up && motor1.getVelocity() >= MIN_SHOOTER_VELO) {
            Feed.setPower(1.0);
        } else if (gamepad1.dpad_right) {
            Feed.setPower(-0.8);
        } else {
            Feed.setPower(0.0);
        }
        motor1.setVelocity(curtargetvelocity);
        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();

        dashboardTelemetry.addData("RPM", motor1.getVelocity()/28*60);
        dashboardTelemetry.addData("velocity", motor1.getVelocity());
        dashboardTelemetry.addData("target", curtargetvelocity);
        dashboardTelemetry.update();
        telemetry.update();
    }
}
