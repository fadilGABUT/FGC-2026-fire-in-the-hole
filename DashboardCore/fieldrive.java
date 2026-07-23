package org.firstinspires.ftc.teamcode.kendil;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class fieldrive {
    public DcMotor FLmotor, FRmotor, BLmotor, BRmotor;
    public IMU imu;

    public void init(HardwareMap hwmap){
        FLmotor = hwmap.get(DcMotor.class,"FLmotor");
        FRmotor = hwmap.get(DcMotor.class,"FRmotor");
        BLmotor = hwmap.get(DcMotor.class,"BLmotor");
        BRmotor = hwmap.get(DcMotor.class,"BRmotor");

        FLmotor.setDirection(DcMotor.Direction.REVERSE);
        BLmotor.setDirection(DcMotor.Direction.REVERSE);


        FLmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FRmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BLmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        BRmotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu = hwmap.get(IMU.class, "imu");
        RevHubOrientationOnRobot Revorientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT);

        imu.initialize(new IMU.Parameters(Revorientation));

    }
        public void drive(double forward, double strafe, double rotate){
        double FLpower = forward + strafe + rotate;
        double BLpower = forward - strafe + rotate;
        double FRpower = forward - strafe - rotate;
        double BRpower = forward + strafe - rotate;

        double maxpower = 1;
        double maxspeed = 1;

        maxpower = Math.max(maxpower, Math.abs(FLpower));
        maxpower = Math.max(maxpower, Math.abs(BLpower));
        maxpower = Math.max(maxpower, Math.abs(FRpower));
        maxpower = Math.max(maxpower, Math.abs(BRpower));

        FLmotor.setPower(maxspeed*(FLpower/maxpower));
        BLmotor.setPower(maxspeed*( BLpower/maxpower));
        FRmotor.setPower(maxspeed*(FRpower/maxpower));
        BRmotor.setPower(maxspeed*(BRpower/maxpower));
    }

    public void drivefieldrelative(double forward, double strafe,double rotate){
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta
        - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        double newforward = r * Math.sin(theta);
        double newstrafe = r * Math.cos(theta);

        this.drive(newforward, newstrafe, rotate);
    }
}
