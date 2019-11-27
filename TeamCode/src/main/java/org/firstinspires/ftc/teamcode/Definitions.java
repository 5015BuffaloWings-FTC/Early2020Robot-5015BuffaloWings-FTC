package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This is NOT an opmode.
 *
 * This class is used to define all the specific hardware for a single robot.
 * In this case that robot is a Mecanum driven Robot.
 * Mecanum robots utilize 4 motor driven wheels.
 *
 */
public class Definitions
{
    public DcMotor leftBackMotor;
    public DcMotor leftFrontMotor;
    public DcMotor rightBackMotor;
    public DcMotor rightFrontMotor;
//    public DcMotor leftIntake;
//    public DcMotor rightIntake;

    public void robotHardwareMapInit(HardwareMap Map)
    {
        leftBackMotor = Map.dcMotor.get("leftBackMotor");
        leftFrontMotor = Map.dcMotor.get("leftFrontMotor");
        rightFrontMotor = Map.dcMotor.get("rightFrontMotor");
        rightBackMotor = Map.dcMotor.get("rightBackMotor");

//        leftIntake = Map.dcMotor.get("leftIntake");
//        rightIntake = Map.dcMotor.get("rightIntake");
    }

    void teleOpInit()
    {
        leftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        leftIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        rightIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
//        leftIntake.setDirection(DcMotorSimple.Direction.REVERSE);
//        rightIntake.setDirection(DcMotorSimple.Direction.FORWARD);
    }
}//2240 ticks Aaed4ln/////AAABmS8KgU/Rvk8jpcgMg/A8+Iop8Zc9/OW+2Cs/fUVcQ83W1DCtxB+B9mgxrXp6l3UpeQi3TsPOOkJEaFKm1TJTuIe7RjPTWBMZLsY+YFQHjkb/0b2o0MRZfUV0rCMc/JcTDjz2e2s4dkhdx+EZt/uFQhOaYHV7jQplXiQTzFgv3jwADOEzdf7xzgehFTxj/p/IVxURKeHkcKRNxghiwtrBSC/t+E8+9se/oHe2eejxAtOltuwGsN0XPmA/Cmq6+IAgltLm/KQXo84VsesCQSb41AamATz9aKoLEpWYPP5qRl9fetXMUwvpmtijN6Qv9sHtABApOCv1GMSiaos3Unu6STz5jznJJbfSI7lnTR8sVQls


