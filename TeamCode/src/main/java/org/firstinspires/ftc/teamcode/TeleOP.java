package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
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
 *                         Gamepad 1
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
        robot.rightFrontMotor.setPower(Range.clip((-gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
        robot.leftFrontMotor.setPower(Range.clip((gamepad1.left_stick_y - (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
        robot.rightBackMotor.setPower(Range.clip((-gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));
        robot.leftBackMotor.setPower(Range.clip((gamepad1.left_stick_y + (gamepad1.left_stick_x) - gamepad1.right_stick_x), -1, 1));

        //Intake
        if(gamepad1.dpad_up || gamepad1.dpad_down)
        {
            if(gamepad1.dpad_down)
            {
                robot.leftIntake.setPower(1);
                robot.rightIntake.setPower(1);
            }
            else
            {
                robot.leftIntake.setPower(-1);
                robot.rightIntake.setPower(-1);
            }

        }
        else
        {
            robot.leftIntake.setPower(0);
            robot.rightIntake.setPower(0);
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
