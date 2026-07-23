package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import newteamcode.fieldrive;

@TeleOp
public class riljalan extends OpMode {
    fieldrive fieldrive = new fieldrive();
    double forward, strafe, rotate;


    @Override
    public void init() {
        fieldrive.init(hardwareMap);
    }

    @Override
    public void loop() {
        forward = -this.gamepad1.left_stick_y;
        strafe = this.gamepad1.left_stick_x;
        rotate = this.gamepad1.right_stick_x;

        fieldrive.drivefieldrelative(forward,strafe,rotate);
    }
}
