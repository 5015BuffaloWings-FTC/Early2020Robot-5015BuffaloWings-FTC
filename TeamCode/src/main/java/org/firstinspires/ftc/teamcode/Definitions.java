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
    public Servo gripServo;
    public Servo rollServo;
    public Servo pitchServo;
    public CRServo leftFoundationServo;
    public CRServo rightFoundationServo;

    public int liftArmMotorLevelCount = 0;
    public final int LIFTARMMOTORMAXPOSITION = 1500;
    public final int LIFTARMMOTORMINPOSITION = 300;
    public final int LIFTARMMOTORRESETPOSITION = 500;
    public final int LIFTARMMOTORLEVEL1POSITION = 600;
    public final int LIFTARMMOTORLEVEL2POSITION = 600;
    public final int LIFTARMMOTORLEVEL3POSITION = 600;
    public final int LIFTARMMOTORLEVEL4POSITION = 600;
    public final int LIFTARMMOTORLEVEL5POSITION = 600;

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
        gripServo = Map.servo.get("gripServo");
        rollServo = Map.servo.get("rollServo");
        pitchServo = Map.servo.get("pitchServo");
        leftFoundationServo = Map.crservo.get("leftFoundationServo");
        rightFoundationServo = Map.crservo.get("rightFoundationServo");
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

        //This sets the robot to drive straight by default
        leftBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftIntakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFoundationServo.setDirection(CRServo.Direction.REVERSE);
    }

}