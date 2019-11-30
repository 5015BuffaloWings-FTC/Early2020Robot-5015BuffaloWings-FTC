

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: PushbotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forwards, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backwards for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This methods assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Auto")

public class Auto extends LinearOpMode {

    /* Declare OpMode members. */
    Definitions robot = new Definitions();
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 0.69 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;
    static final double     STRAFE_SPEED            = 0.5;

    @Override
    public void runOpMode() {

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.robotHardwareMapInit(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        robot.leftBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightBackMotor.setMode((DcMotor.RunMode.STOP_AND_RESET_ENCODER));
        robot.rightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.leftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                robot.leftBackMotor.getCurrentPosition(),
                robot.leftFrontMotor.getCurrentPosition());
                robot.rightBackMotor.getCurrentPosition();
                robot.rightFrontMotor.getCurrentPosition();
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        //encoderDrive(DRIVE_SPEED,  12,  12, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        //encoderDrive(TURN_SPEED,   20, -20, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        encoderDrive(STRAFE_SPEED, 20, 4);
        encoderDrive(STRAFE_SPEED, -20, 4);
        //encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        robot.leftBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        robot.leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.rightBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        int newLeftBackTarget;
        int newLeftFrontTarget;
        int newRightBackTarget;
        int newRightFrontTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftBackTarget = robot.leftBackMotor.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newLeftFrontTarget = robot.leftFrontMotor.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightBackTarget = robot.rightBackMotor.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newRightFrontTarget = robot.rightFrontMotor.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            robot.leftBackMotor.setTargetPosition(newLeftBackTarget);
            robot.leftFrontMotor.setTargetPosition(newLeftFrontTarget);
            robot.rightBackMotor.setTargetPosition(newRightBackTarget);
            robot.rightFrontMotor.setTargetPosition(newRightFrontTarget);

            // Turn On RUN_TO_POSITION
            robot.leftBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftBackMotor.setPower(Math.abs(speed));
            robot.leftFrontMotor.setPower(Math.abs(speed));
            robot.rightBackMotor.setPower(Math.abs(speed));
            robot.rightFrontMotor.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftBackMotor.isBusy() && robot.leftFrontMotor.isBusy() && robot.rightBackMotor.isBusy() && robot.rightFrontMotor.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftBackTarget,  newLeftFrontTarget, newRightBackTarget, newRightFrontTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        robot.leftBackMotor.getCurrentPosition(),
                        robot.leftFrontMotor.getCurrentPosition(),
                        robot.rightBackMotor.getCurrentPosition(),
                        robot.rightFrontMotor.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.leftBackMotor.setPower(0);
            robot.leftFrontMotor.setPower(0);
            robot.rightBackMotor.setPower(0);
            robot.rightFrontMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.leftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250); //Pause after each movement
        }
    }

    public void encoderDrive(double speed, double distance, double timeoutS) {

        //Distance decides direction. + is right. - is left.

        int newLeftBackTarget;
        int newLeftFrontTarget;
        int newRightBackTarget;
        int newRightFrontTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            if(distance > 0)
            {
                robot.leftBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.rightBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.rightFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            }
            else if(distance < 0)
            {
                robot.leftBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.leftFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.rightBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            }

            newLeftBackTarget = robot.leftBackMotor.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
            newLeftFrontTarget = robot.leftFrontMotor.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
            newRightBackTarget = robot.rightBackMotor.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
            newRightFrontTarget = robot.rightFrontMotor.getCurrentPosition() + (int)(distance * COUNTS_PER_INCH);
            robot.leftBackMotor.setTargetPosition(newLeftBackTarget);
            robot.leftFrontMotor.setTargetPosition(newLeftFrontTarget);
            robot.rightBackMotor.setTargetPosition(newRightBackTarget);
            robot.rightFrontMotor.setTargetPosition(newRightFrontTarget);

            // Turn On RUN_TO_POSITION
            robot.leftBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightBackMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.rightFrontMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.leftBackMotor.setPower(Math.abs(speed));
            robot.leftFrontMotor.setPower(Math.abs(speed));
            robot.rightBackMotor.setPower(Math.abs(speed));
            robot.rightFrontMotor.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.leftBackMotor.isBusy() && robot.leftFrontMotor.isBusy() && robot.rightBackMotor.isBusy() && robot.rightFrontMotor.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftBackTarget,  newLeftFrontTarget, newRightBackTarget, newRightFrontTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        robot.leftBackMotor.getCurrentPosition(),
                        robot.leftFrontMotor.getCurrentPosition(),
                        robot.rightBackMotor.getCurrentPosition(),
                        robot.rightFrontMotor.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.leftBackMotor.setPower(0);
            robot.leftFrontMotor.setPower(0);
            robot.rightBackMotor.setPower(0);
            robot.rightFrontMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.leftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.rightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250); //Pause after each movement
        }
    }
}
