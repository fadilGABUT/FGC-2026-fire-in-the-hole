package newteamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class multi2 {
    public DcMotorEx ShooterL;
    public DcMotor Feed;
    public DcMotor Intake;
    public static double highVelocity = 2100;
    public static double minvelocity = 1800;
    public boolean lastRB = false;
    public boolean lastLB = false;
    public boolean shooterOn = false;
    public boolean feedOn = false;
    private DistanceSensor sensorDistance;

    public void init(HardwareMap hwmap, Telemetry telemetry) {
        // you can use this as a regular DistanceSensor.
        sensorDistance = hwmap.get(DistanceSensor.class, "dis");

        // you can also cast this to a Rev2mDistanceSensor if you want to use added
        // methods associated with the Rev2mDistanceSensor class.
        Rev2mDistanceSensor sensorTimeOfFlight = (Rev2mDistanceSensor) sensorDistance;

        telemetry.update();


        ShooterL = hwmap.get(DcMotorEx.class, "ShooterL");

        ShooterL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        ShooterL.setVelocityPIDFCoefficients(1.622,0.162,0,16.221);
        Feed = hwmap.get(DcMotor.class, "Feed");
        Intake = hwmap.get(DcMotor.class, "Int");

        telemetry.addData("Status", "Robot Ready");
        telemetry.update();
    }

    public void loop(Gamepad gamepad, Telemetry telemetry) {
        telemetry.addData(">>", "Press start to continue");
        telemetry.update();
        telemetry.addData("deviceName", sensorDistance.getDeviceName() );
        telemetry.addData("range", String.format("%.01f mm", sensorDistance.getDistance(DistanceUnit.MM)));
        telemetry.addData("range", String.format("%.01f cm", sensorDistance.getDistance(DistanceUnit.CM)));
        telemetry.addData("range", String.format("%.01f m", sensorDistance.getDistance(DistanceUnit.METER)));
        telemetry.addData("range", String.format("%.01f in", sensorDistance.getDistance(DistanceUnit.INCH)));
        telemetry.update();
        boolean currentRB = gamepad.right_bumper;
        if (currentRB && !lastRB) {
            shooterOn = !shooterOn;
        }
        lastRB = currentRB;

        if(!shooterOn){
            feedOn = false;
        }else{
            boolean currentLB = gamepad.left_bumper;
            if (currentLB && !lastLB) {
                feedOn = !feedOn;
            }
            lastLB = currentLB;
        }
        if (shooterOn) {
            ShooterL.setVelocity(highVelocity);
        }else {
            ShooterL.setVelocity(0);
        }

        if(gamepad.dpad_down){
            Feed.setPower(-1.0);
        }else if (feedOn&&ShooterL.getVelocity() >= minvelocity) {
            Feed.setPower(1.0);
        }else {
            Feed.setPower(0.0);
            double intakePower = gamepad.right_trigger - gamepad.left_trigger;
            Intake.setPower(intakePower);
        }

        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();

        dashboardTelemetry.addData("velocity", ShooterL.getVelocity());
        dashboardTelemetry.update();
    }
}
