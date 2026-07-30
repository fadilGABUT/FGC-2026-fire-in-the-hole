package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "riljalan", group = "TeleOp")
public class riljalan extends OpMode {

    fieldrive fieldrive = new fieldrive();
    multi2 multi2 = new multi2();
    double forward, strafe, rotate;

    @Override
    public void init() {
        fieldrive.init(hardwareMap);
        multi2.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        forward = this.gamepad1.left_stick_y;
        strafe = -this.gamepad1.left_stick_x;
        rotate = -this.gamepad1.right_stick_x;

        fieldrive.drive(forward, strafe, rotate);

        multi2.loop(this.gamepad1,telemetry);
    }
}