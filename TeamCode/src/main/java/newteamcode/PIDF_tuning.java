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
    public DcMotorEx motor1, motor2;
    public static double highVelocity = 1700;
    public static double lowVelocity = 1500;
    private double curtargetvelocity = 0;
    @Override
    public void init(){
        motor1 = hardwareMap.get(DcMotorEx.class, "ShooterL");
        PIDFCoefficients pid = new PIDFCoefficients(1.622,0.162,0,16.221);
        motor1.setVelocityPIDFCoefficients( 1.622, 0.162, 0,16.221);
    }
    @Override
    public void loop(){
        if (gamepad1.yWasPressed()){
            if (curtargetvelocity == highVelocity){
                curtargetvelocity = lowVelocity;
            }else {
                curtargetvelocity = highVelocity;
            }
        } else if (gamepad1.x) {
            motor1.setPower(1);
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
