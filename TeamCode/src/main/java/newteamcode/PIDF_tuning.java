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
    public static double highVelocity = 2000;
    public static double lowVelocity = 1800;
    private double curtargetvelocity = 0;
    public static double F;
    public static double P;
    @Override
    public void init(){
        motor1 = hardwareMap.get(DcMotorEx.class, "Shooter1");
        motor2 = hardwareMap.get(DcMotorEx.class, "Shooter2");
        motor2.setDirection(DcMotorSimple.Direction.REVERSE);
        PIDFCoefficients pid = new PIDFCoefficients(P,0,0,F);
        motor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pid);
        motor2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pid);
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
            motor2.setPower(1);
        }

        PIDFCoefficients pid = new PIDFCoefficients(P,0,0,F);
        motor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pid);
        motor2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pid);
        motor1.setVelocity(curtargetvelocity);
        motor2.setVelocity(curtargetvelocity);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();

        dashboardTelemetry.addData("RPM", motor1.getVelocity()/28*60);
        dashboardTelemetry.addData("velocity", motor1.getVelocity());
        dashboardTelemetry.addData("target", curtargetvelocity);
        dashboardTelemetry.update();
        telemetry.update();
    }
}
