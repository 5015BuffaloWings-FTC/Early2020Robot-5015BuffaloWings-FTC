package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This is NOT an opmode.
 *
 * This class is used to define all the specific hardware for a single robot.
 * In this case that robot is a Mecanum driven Robot.
 * Mecanum robots utilize 4 motor driven wheels. Each of these wheels 
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 *
 */
public class Definitions
{
    public DcMotor leftBackMotor;
    public DcMotor leftFrontMotor;
    public DcMotor rightBackMotor;
    public DcMotor rightFrontMotor;

    public void robotHardwareMapInit(HardwareMap Map)
    {
        leftBackMotor = Map.dcMotor.get("leftBackMotor");
        leftFrontMotor = Map.dcMotor.get("leftFrontMotor");
        rightBackMotor = Map.dcMotor.get("rightBackMotor");
        rightFrontMotor = Map.dcMotor.get("rightFrontMotor");
    }

    void runWithOutEncoders()
    {
        leftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}

