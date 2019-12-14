package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 *                     TeleOP Controls
 *       _=====_                               _=====_
 *      / _____ \                             / _____ \
 *    /.-'_____'-.---------------------------.-'_____'-\
 *   /   |     |  '.        L O G I        .'  |     |   \
 *  / ___| /|\ |___ \                     / ___| (Y) |___ \
 * / |      |      | ;  __           _   ; |             | ;
 * | | <---   ---> | | |__|         |_|  | |(X)       (B)| |
 * | |___   |   ___| ; SELECT      START ; |___       ___| ;
 * |\    | \|/ |    /  _     ___      _   \    | (A) |    /|
 * | \   |_____|  .','" "', |___|  ,'" "', '.  |_____|  .' |
 * |  '-.______.-' /       \ANALOG/       \  '-._____.-'   |
 * |               |       |------|       |                |
 * |              /\       /      \       /\               |
 * |             /  '.___.'        '.___.'  \              |
 * |            /                            \             |
 *  \          /                              \           /
 *   \________/                                \_________/
 *                         Gamepad 1:
 *
 * gamepad1.dpad_up :
 * gamepad1.dpad_left :
 * gamepad1.dpad_right :
 * gamepad1.dpad_down :
 * gamepad1.a :
 * gamepad1.x :
 * gamepad1.b :
 * gamepad1.y :
 * gamepad1.left_trigger : Left Intake Motor set to OUTTAKE
 * gamepad1.left_bumper : Left Intake Motor set to INTAKE
 * gamepad1.right_trigger : Right Intake Motor set to OUTTAKE
 * gamepad1.right_bumper : Right Intake Motor se to INTAKE
 * gamepad1.left_stick_y : Robot drive Forward || Backward
 * gamepad1.left_stick_x : Robot strafe Left || Right
 * gamepad1.right_stick_y :
 * gamepad1.right_stick_x : Robot rotate Counter Clockwise || Clockwise
 * gamepad1.left_stick_button :
 * gamepad1.right_stick_button :
 * gamepad1.start :
 * gamepad1.back :
 *
 *                      TeleOP Controls
 *        _=====_                               _=====_
 *       / _____ \                             / _____ \
 *     /.-'_____'-.---------------------------.-'_____'-\
 *    /   |     |  '.        L O G I        .'  |     |   \
 *   / ___| /|\ |___ \                     / ___| (Y) |___ \
 *  / |      |      | ;  __           _   ; |             | ;
 *  | | <---   ---> | | |__|         |_|  | |(X)       (B)| |
 *  | |___   |   ___| ; SELECT      START ; |___       ___| ;
 *  |\    | \|/ |    /  _     ___      _   \    | (A) |    /|
 *  | \   |_____|  .','" "', |___|  ,'" "', '.  |_____|  .' |
 *  |  '-.______.-' /       \ANALOG/       \  '-._____.-'   |
 *  |               |       |------|       |                |
 *  |              /\       /      \       /\               |
 *  |             /  '.___.'        '.___.'  \              |
 *  |            /                            \             |
 *   \          /                              \           /
 *    \________/                                \_________/
 *                          Gamepad 2
 *
 * gamepad2.dpad_up :
 * gamepad2.dpad_left :
 * gamepad2.dpad_right :
 * gamepad2.dpad_down :
 * gamepad2.a :
 * gamepad2.x :
 * gamepad2.b :
 * gamepad2.y :
 * gamepad2.left_trigger :
 * gamepad2.left_bumper :
 * gamepad2.right_trigger :
 * gamepad2.right_bumper :
 * gamepad2.left_stick_y :
 * gamepad2.left_stick_x :
 * gamepad2.right_stick_y : Lift Arm Motor Counter Clockwise || Clockwise
 * gamepad2.right_stick_x :
 * gamepad2.left_stick_button :
 * gamepad2.right_stick_button :
 * gamepad2.start :
 * gamepad2.back :
 */

@TeleOp(name="TeleOP")

public class TeleOP extends OpMode
{
    Definitions robot = new Definitions();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init()
    {
        robot.robotHardwareMapInit(hardwareMap);
        robot.teleOpInit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start(){
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop()
    {
        //Robot Movement
        robot.leftBackMotor.setPower(Range.clip((gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
        robot.leftFrontMotor.setPower(Range.clip((gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
        robot.rightBackMotor.setPower(Range.clip((-gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
        robot.rightFrontMotor.setPower(Range.clip((-gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));

        if(gamepad1.left_bumper || gamepad1.right_bumper || (gamepad1.left_trigger != 0) || (gamepad1.right_trigger != 0))
        {
            if(gamepad1.left_bumper)
                robot.leftIntakeMotor.setPower(1);
            if(gamepad1.right_bumper)
                robot.rightIntakeMotor.setPower(1);
            if(gamepad1.left_trigger != 0)
                robot.leftIntakeMotor.setPower(-1);
            if(gamepad1.right_trigger != 0)
                robot.rightIntakeMotor.setPower(-1);
        }
        else
        {
            robot.leftIntakeMotor.setPower(0);
            robot.rightIntakeMotor.setPower(0);
        }

        if(gamepad1.y || gamepad1.a)
        {
            if (gamepad1.y) {
                robot.leftFoundationServo.setPower(1);
                robot.rightFoundationServo.setPower(1);
            }
            if (gamepad1.a) {
                robot.leftFoundationServo.setPower(-1);
                robot.rightFoundationServo.setPower(-1);
            }
        }
        else
        {
            robot.leftFoundationServo.setPower(0);
            robot.rightFoundationServo.setPower(0);
        }
//        if(gamepad2.left_stick_y == 0)
//        {
//            robot.liftArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//            if (robot.LIFTARMMOTORMINPOSITION <= robot.liftArmMotor.getCurrentPosition() && robot.liftArmMotor.getCurrentPosition() <= robot.LIFTARMMOTORMAXPOSITION) {
//                if (!gamepad2.y || !gamepad2.a) {
//                    robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORRESETPOSITION);
//                    robot.liftArmMotor.setPower(1);
//                }
//
//                if (gamepad2.y && !(robot.liftArmMotorLevelCount <= 5))
//                    robot.liftArmMotorLevelCount++;
//                if (gamepad2.a && !(robot.liftArmMotorLevelCount >= 0))
//                    robot.liftArmMotorLevelCount--;
//
//                switch (robot.liftArmMotorLevelCount) {
//                    default:
//                        robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORRESETPOSITION);
//                        robot.liftArmMotor.setPower(1);//fix these powers they may need to reverse in order for the motor to go down
//                        break;
//
//                    case (1):
//                        robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORLEVEL1POSITION);
//                        robot.liftArmMotor.setPower(1);
//                        break;
//
//                    case (2):
//                        robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORLEVEL2POSITION);
//                        robot.liftArmMotor.setPower(1);
//                        break;
//
//                    case (3):
//                        robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORLEVEL3POSITION);
//                        robot.liftArmMotor.setPower(1);
//                        break;
//
//                    case (4):
//                        robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORLEVEL4POSITION);
//                        robot.liftArmMotor.setPower(1);
//                        break;
//
//                    case (5):
//                        robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORLEVEL5POSITION);
//                        robot.liftArmMotor.setPower(1);
//                        break;
//                }
//            }
//            if (robot.LIFTARMMOTORMINPOSITION < robot.liftArmMotor.getCurrentPosition()) {
//                robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORRESETPOSITION);
//                robot.liftArmMotor.setPower(1);
//            }
//            if (robot.liftArmMotor.getCurrentPosition() < robot.LIFTARMMOTORMAXPOSITION) {
//                robot.liftArmMotor.setTargetPosition(robot.LIFTARMMOTORRESETPOSITION);
//                robot.liftArmMotor.setPower(-1);
//            }
//        }
//        else
//        {
//            robot.liftArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            robot.liftArmMotor.setPower(gamepad2.left_stick_y);
//        }

        robot.liftArmMotor.setPower(gamepad2.right_stick_y);

        telemetry.addData("liftArmLevelCount", robot.liftArmMotorLevelCount);
        telemetry.addData("leftBackMotor", robot.leftBackMotor.getCurrentPosition());
        telemetry.addData("leftFrontMotor", robot.leftFrontMotor.getCurrentPosition());
        telemetry.addData("rightBackMotor", robot.rightBackMotor.getCurrentPosition());
        telemetry.addData("rightFrontMotor", robot.rightFrontMotor.getCurrentPosition());
        telemetry.addData("liftArmMotor", robot.liftArmMotor.getCurrentPosition());
        telemetry.addData("pitchServo", robot.pitchServo.getPosition());
        telemetry.addData("rollServo", robot.rollServo.getPosition());
        telemetry.addData("gripServo", robot.gripServo.getPosition());
        telemetry.update();
    }




    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
