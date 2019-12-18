package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PWMOutputController;
import com.qualcomm.robotcore.hardware.PwmControl;
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
    public DcMotor leftIntakeMotor;
    public DcMotor rightIntakeMotor;
    public DcMotor liftArmMotor;
    public CRServo gripCRServo;
    public CRServo rollCRServo;
    public CRServo pitchCRServo;
    public CRServo leftFoundationCRServo;
    public CRServo rightFoundationCRServo;

    public int liftArmMotorLevelCount = 0;
    public final int LIFTARMMOTORMAXPOSITION = 150;
    public final int LIFTARMMOTORMINPOSITION = -15;

    public final double LEFTFOUNDATIONSERVODOWNPOSITION = 0;
    public final double RIGHTFOUNDATIONSERVODOWNPOSITION = 0;
    public final double LEFTFOUNDATIONSERVORESETPOSITION = 0.5;
    public final double RIGHTFOUNDATIONSERVORESETPOSITION = 0.5;

    public final double WHEELDIAMETER = 3.54331;




    public void robotHardwareMapInit(HardwareMap Map)
    {
        leftBackMotor = Map.dcMotor.get("leftBackMotor");
        leftFrontMotor = Map.dcMotor.get("leftFrontMotor");
        rightFrontMotor = Map.dcMotor.get("rightFrontMotor");
        rightBackMotor = Map.dcMotor.get("rightBackMotor");
        leftIntakeMotor = Map.dcMotor.get("leftIntakeMotor");
        rightIntakeMotor = Map.dcMotor.get("rightIntakeMotor");
        liftArmMotor = Map.dcMotor.get("liftArmMotor");
        gripCRServo = Map.crservo.get("gripCRServo");
        rollCRServo = Map.crservo.get("rollCRServo");
        pitchCRServo = Map.crservo.get("pitchCRServo");
        leftFoundationCRServo = Map.crservo.get("leftFoundationCRServo");
        rightFoundationCRServo = Map.crservo.get("rightFoundationCRServo");
    }

    void teleOpInit()
    {
        leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftIntakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightIntakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        liftArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        //This sets the robot to drive straight by default
        leftBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftIntakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFoundationCRServo.setDirection(CRServo.Direction.REVERSE);
        pitchCRServo.setDirection(CRServo.Direction.REVERSE);
    }

    void autoInit()
    {
        leftFoundationCRServo.setDirection(CRServo.Direction.REVERSE);
    }

}