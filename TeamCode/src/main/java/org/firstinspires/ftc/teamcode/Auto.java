
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

/**
 *
 * This OpMode utilizes the SDK's example file "ConceptVuforiaSkyStoneNavigation.java" to locate skystone(s) during autonomous.
 * Using this information our robot can make decisions on its optimum auto path.
 *
 * "When images are located, Vuforia is able to determine the position and orientation of the
 * image relative to the camera.  This sample code then combines that information with a
 * knowledge of where the target images are on the field, to determine the location of the camera.
 * After gathering enough images, a final calculation then uses the location of the camera on the robot to determine the
 * robot's location and orientation on the field."
 *
 *
 * This OpMode utilizes the SDK's example file "PushbotAutoDriveByEncoder_Linear" to drive the robot.
 * Using encoders on each drive wheel, the robot can accurately maneuver on the field. Only speed, distance, and timeout inputs
 * are required for each "Step" in auto. There are other ways to perform encoder based moves, but this method is probably the simplest.
 *
 * Java Doc References
 * @see VuforiaLocalizer
 * @see VuforiaTrackableDefaultListener
 * see  skystone/doc/tutorial/FTC_FieldCoordinateSystemDefinition.pdf
 *
 *
 * Auto Planning: This should be more indepth in the near future
 *
 * Red Foundation side:
 *  Score Foundation
 *  Park
 *
 * Red Skystone side:
 *  Collect Skystone
 *  Drive to platform
 *  Score Skystone
 *  Park
 *
 * Blue Foundation side:
 *  Score Foundation
 *  Park
 *
 * Blue Skystone side:
 *  Collect Skystone
 *  Drive to platform
 *  Score Skystone
 *  Park
 */



@Autonomous(name="Vuforia Skystone Navigation")

public class Auto extends LinearOpMode {

    Definitions robot = new Definitions();
    private ElapsedTime runtime = new ElapsedTime();


    @Override public void runOpMode() {

        robot.robotHardwareMapInit(hardwareMap);
        robot.driveInit();
        /*
         * Retrieve the camera we are to use.
         *
         */
        robot.webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC phone);
         * If no camera monitor is desired, use the parameter-less constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = robot.VUFORIA_KEY;

        /**
         * We also indicate which camera on the RC we wish to use.
         */
        parameters.cameraName = robot.webcamName;

        //  Instantiate the Vuforia engine
        robot.vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        VuforiaTrackables targetsSkyStone = this.robot.vuforia.loadTrackablesFromAsset("Skystone");

        VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
        stoneTarget.setName("SkyStone");


        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);

        /**
         * In order for localization to work, we need to tell the system where each target is on the field, and
         * where the phone resides on the robot.  These specifications are in the form of <em>transformation matrices.</em>
         * Transformation matrices are a central, important concept in the math here involved in localization.
         * See <a href="https://en.wikipedia.org/wiki/Transformation_matrix">Transformation Matrix</a>
         * for detailed information. Commonly, you'll encounter transformation matrices as instances
         * of the {@link OpenGLMatrix} class.
         *
         * If you are standing in the Red Alliance Station looking towards the center of the field,
         *     - The X axis runs from your left to the right. (positive from the center to the right)
         *     - The Y axis runs from the Red Alliance Station towards the other side of the field
         *       where the Blue Alliance Station is. (Positive is from the center, towards the BlueAlliance station)
         *     - The Z axis runs from the floor, upwards towards the ceiling.  (Positive is above the floor)
         *
         * Before being transformed, each target image is conceptually located at the origin of the field's
         *  coordinate system (the center of the field), facing up.
         */

        // Set the position of the Stone Target.  Since it's not fixed in position, assume it's at the field origin.
        // Rotated it to to face forward, and raised it to sit on the ground correctly.
        // This can be used for generic target-centric approach algorithms
        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, robot.stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));


        //
        // Create a transformation matrix describing where the phone is on the robot.
        //
        // NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
        // Lock it into Portrait for these numbers to work.
        //
        // Info:  The coordinate frame for the robot looks the same as the field.
        // The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
        // Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
        //
        // The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
        // pointing to the LEFT side of the Robot.
        // The two examples below assume that the camera is facing forward out the front of the robot.

        // We need to rotate the camera around it's long axis to bring the correct camera forward.
        if (robot.CAMERA_CHOICE == BACK) {
            robot.phoneYRotate = -90;
        } else {
            robot.phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (robot.PHONE_IS_PORTRAIT) {
            robot.phoneXRotate = 90 ;
        }

        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT  = 9.0f * robot.mmPerInch;   // eg: Camera is 9 Inches in front of robot-center
        final float CAMERA_VERTICAL_DISPLACEMENT = 14.0f * robot.mmPerInch;   // eg: Camera is 14 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, robot.phoneYRotate, robot.phoneZRotate, robot.phoneXRotate));

        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

        // WARNING:
        // In this sample, we do not wait for PLAY to be pressed.  Target Tracking is started immediately when INIT is pressed.
        // This sequence is used to enable the new remote DS Camera Preview feature to be used with this sample.
        // CONSEQUENTLY do not put any driving commands in this loop.
        // To restore the normal opmode structure, just un-comment the following line:

        waitForStart();

        // Note: To use the remote camera preview:
        // AFTER you hit Init on the Driver Station, use the "options menu" to select "Camera Stream"
        // Tap the preview window to receive a fresh image.

//        //Red Facing bridge skystone side
//        encoderStrafe(0.3, 20,4);//right
//        encoderDrive(0.4,15,15,3);//forward

        //red foundation
//        encoderStrafe(-0.3,9,3);
//        encoderDrive(0.4,-20,-20,3);
//        robot.leftFoundationCRServo.setPower(1);
//        robot.rightFoundationCRServo.setPower(-1);
//        sleep(2000);
//        robot.leftFoundationCRServo.setPower(0);
//        robot.rightFoundationCRServo.setPower(0);
//        sleep(1000);
//        encoderDrive(0.4,25,25,3);
//        robot.leftFoundationCRServo.setPower(0);
//        robot.rightFoundationCRServo.setPower(0);
//        encoderDrive(0.4,-5,-5,3);
//        encoderStrafe(0.3, 40, 5);

//        //blue skystone
//        encoderStrafe(-0.3,9,3);
//        encoderDrive(0.4,-20,-20,3);
//        robot.dragCRServo.setPower(-1);
//        sleep(1500);
//        robot.dragCRServo.setPower(0);
//        encoderDrive(0.5, 10, 10, 2);

        encoderDrive(0.4, -4,-4,3);

        targetsSkyStone.activate();
        while(!isStopRequested()&& !robot.isAlignedWithSkystone) {
            // check all the trackable targets to see which one (if any) is visible.
            robot.targetVisible = false;
            if (((VuforiaTrackableDefaultListener) stoneTarget.getListener()).isVisible()) {
                telemetry.addData("Visible Target", stoneTarget.getName());
                robot.targetVisible = true;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) stoneTarget.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    robot.lastLocation = robotLocationTransform;
                }
                break;
            }

            // Provide feedback as to where the robot is located (if we know).
            if (robot.targetVisible) {
                // express position (translation) of robot in inches.
                VectorF translation = robot.lastLocation.getTranslation();
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        translation.get(0) / robot.mmPerInch, translation.get(1) / robot.mmPerInch, translation.get(2) / robot.mmPerInch);

                // express the rotation of the robot in degrees.
                Orientation rotation = Orientation.getOrientation(robot.lastLocation, EXTRINSIC, XYZ, DEGREES);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
                if (0 < (translation.get(1) / robot.mmPerInch)) {
                    robot.stoneCenter = true;
                }
                else if((translation.get(1) / robot.mmPerInch) < 0)
                {
                    robot.stoneLeft = true;
                }
                else
                    robot.stoneRight = true;
            } else {
                telemetry.addData("Visible Target", "none");
                telemetry.addData("Stone Left: ", robot.stoneLeft);
                telemetry.addData("Stone Center: ", robot.stoneCenter);
                telemetry.addData("Stone Right: ", robot.stoneRight);
            }
            telemetry.update();

        }

        // Disable Tracking when we are done;
        targetsSkyStone.deactivate(); //20 to 22 inches from stone
        if(robot.stoneLeft)
            encoderStrafe(-0.5, 20, 2);
        if(robot.stoneCenter)
            encoderDrive(robot.DRIVE_SPEED, 20, 20, 2);
        if(robot.stoneRight)
            encoderStrafe(0.5, 20, 2);
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
        robot.leftBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        robot.rightBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        robot.rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        int newLeftBackTarget;
        int newLeftFrontTarget;
        int newRightBackTarget;
        int newRightFrontTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftBackTarget = robot.leftBackMotor.getCurrentPosition() + (int)(leftInches * robot.COUNTS_PER_INCH);
            newLeftFrontTarget = robot.leftFrontMotor.getCurrentPosition() + (int)(leftInches * robot.COUNTS_PER_INCH);
            newRightBackTarget = robot.rightBackMotor.getCurrentPosition() + (int)(rightInches * robot.COUNTS_PER_INCH);
            newRightFrontTarget = robot.rightFrontMotor.getCurrentPosition() + (int)(rightInches * robot.COUNTS_PER_INCH);
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

    public void encoderStrafe(double speed, double distance, double timeoutS) {

        //Speed decides direction. + is right. - is left.

        int newLeftBackTarget;
        int newLeftFrontTarget;
        int newRightBackTarget;
        int newRightFrontTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            if(speed > 0)
            {
                robot.leftBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.rightBackMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.rightFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            }
            else if(speed < 0)
            {
                robot.leftBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.leftFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.rightBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.rightFrontMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            }

            newLeftBackTarget = robot.leftBackMotor.getCurrentPosition() + (int)(distance * robot.COUNTS_PER_INCH);
            newLeftFrontTarget = robot.leftFrontMotor.getCurrentPosition() + (int)(distance * robot.COUNTS_PER_INCH);
            newRightBackTarget = robot.rightBackMotor.getCurrentPosition() + (int)(distance * robot.COUNTS_PER_INCH);
            newRightFrontTarget = robot.rightFrontMotor.getCurrentPosition() + (int)(distance * robot.COUNTS_PER_INCH);
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
