package newteamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class multii2_2 {
    public DcMotorEx ShooterL;
    public DcMotor Feed;
    public boolean lastRB = false;
    public boolean lastLB = false;
    public boolean shooterOn = false;
    public boolean feedOn = false;

    public void init(HardwareMap hwmap, Telemetry telemetry) {
        ShooterL = hwmap.get(DcMotorEx.class, "ShooterL");

        ShooterL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        ShooterL.setVelocityPIDFCoefficients(1.622,0.162,0,16.221);
        Feed = hwmap.get(DcMotor.class, "Feed");

        telemetry.addData("Status", "Robot Ready");
        telemetry.update();
    }

    public void loop(Gamepad gamepad) {
        boolean currentRB = gamepad.right_bumper;
        if (currentRB && !lastRB) {
            shooterOn = !shooterOn;
        }
        lastRB = currentRB;

        boolean currentLB = gamepad.left_bumper;
        if (currentLB && !lastLB) {
            feedOn = !feedOn;
        }
        lastLB = currentLB;

        if (shooterOn) {
            ShooterL.setVelocity(1700);
        }else {
            ShooterL.setVelocity(0);
        }

        if(gamepad.dpad_down){
            Feed.setPower(-1.0);
        }else if (feedOn&&ShooterL.getVelocity() >= 1500) {
            Feed.setPower(1.0);
        }else {
            Feed.setPower(0.0);
        }
    }
}
