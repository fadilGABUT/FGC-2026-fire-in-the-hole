package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "V4jalan", group = "TeleOp")
public class V4jalan extends OpMode {

    TankDrive TankDrive = new TankDrive();
    multi3 multi3 = new multi3();
   

    @Override
    public void init() {
        TankDrive.init(hardwareMap);
        multi3.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        TankDrive.drive(gamepad1);
        multi3.loop(gamepad2, telemetry);
    }
}
