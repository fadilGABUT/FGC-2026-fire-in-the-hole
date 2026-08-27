package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "swastika", group = "TeleOp")
public class riljalanswastika extends OpMode {

    pinwheeldrive drivebase = new pinwheeldrive();
    multi2 multi2 = new multi2();

    @Override
    public void init() {
        drivebase.init(hardwareMap);
        multi2.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        drivebase.drive(gamepad1);
        multi2.loop(gamepad2, telemetry);

        telemetry.addData("Drive Mode", "Swastika Pinwheel Active");
        telemetry.addData("Intake Control", "Gamepad 1 Triggers");
    }
}