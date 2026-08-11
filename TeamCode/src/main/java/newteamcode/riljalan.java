package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "riljalan", group = "TeleOp")
public class riljalan extends OpMode {

    fieldrive fieldrive = new fieldrive();
    p2drive p2drive = new p2drive();
    multi2 multi2 = new multi2();
    @Override
    public void init() {
        fieldrive.init(hardwareMap);
        p2drive.init(hardwareMap);
        multi2.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        boolean isP2Moving = Math.abs(gamepad2.left_stick_y) > 0.05 || 
                             Math.abs(gamepad2.left_stick_x) > 0.05 || 
                             Math.abs(gamepad2.right_stick_x) > 0.05;
        if (isP2Moving) {
            p2drive.drive(gamepad2);
        } else {
            fieldrive.drive(gamepad1);
        }

        multi2.loop(gamepad2,telemetry);
    }
}