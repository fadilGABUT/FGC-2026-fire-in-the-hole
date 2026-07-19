package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "low latency", group = "TeleOp")
public class lowlatency extends OpMode {
    multi1 multi1 = new multi1();
    multi2 multi2 = new multi2();

    @Override
    public void init() {
        multi1.init(hardwareMap);
        multi2.init(hardwareMap,telemetry);
    }
    @Override
    public void loop(){
        multi1.loop(gamepad1);
        multi2.loop(gamepad1);
    }}