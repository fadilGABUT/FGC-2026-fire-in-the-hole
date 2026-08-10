package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "riljalan", group = "TeleOp")
public class riljalan extends OpMode {

    fieldrive fieldrive = new fieldrive();
    multi2 multi2 = new multi2();
    @Override
    public void init() {
        fieldrive.init(hardwareMap);
        multi2.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        fieldrive.drive(gamepad1);

        multi2.loop(gamepad2,telemetry);
    }
}