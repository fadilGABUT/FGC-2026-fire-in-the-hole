package newteamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Azkacuy", group = "TeleOp")
public class Azkacuy extends OpMode {
    multi1 multi1 = new multi1();
    multii2_2 multii2_2 = new multii2_2();

    @Override
    public void init() {
        multi1.init(hardwareMap);
        multii2_2.init(hardwareMap,telemetry);
    }
    @Override
    public void loop(){
        multi1.loop(gamepad1);
        multii2_2.loop(gamepad1);
    }}