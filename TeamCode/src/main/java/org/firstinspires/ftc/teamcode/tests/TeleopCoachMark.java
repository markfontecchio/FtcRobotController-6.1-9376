package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
@Disabled
@TeleOp
public class TeleopCoachMark extends LinearOpMode {

    // sets variables for motors and servos
    private DcMotor driveFL, driveFR, driveBL, driveBR, takingInRingsMotor, ringShooterMotor1, ringShooterMotor2;
    Servo wobbleGoalServo, ringFeederServo, wobbleGoalReleaseServo;
    Orientation angles;
    double towerGoalRingMotorSpeed = -0.85, powerShotRingMotorSpeed = -0.7;
    double wobbleGoalVerticalPosition = 0.22, wobbleGoalReleasePosition = 0.5;
    double drivePower = 1;
    double wheelDiameter = 3.77953;
    double encoderTicksPerRotation = 537.6;
    double gearRatio = 2.0;
    double wheelCircumference = wheelDiameter * Math.PI;
    double encoderTicksPerInch = ((encoderTicksPerRotation / wheelCircumference) * gearRatio);

    // creates variables for drive inputs from controllers
    private double forwardBackward, leftRight, rotate;

    @Override
    public void runOpMode() {

        // function to do init routine
        initialize();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // runs until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // sets values of variables for gamepad1 (start+a) inputs for driving
            forwardBackward = -gamepad1.left_stick_y;
            leftRight = gamepad1.left_stick_x;
            rotate = gamepad1.right_trigger - gamepad1.left_trigger;

            // function to update telemetry
            addTelemetry();

           // driving in all directions and rotating
            driveFL.setPower(forwardBackward + leftRight + rotate);
            driveFR.setPower(forwardBackward - leftRight - rotate);
            driveBL.setPower(forwardBackward - leftRight + rotate);
            driveBR.setPower(forwardBackward + leftRight - rotate);

            // automate power shot for end game
            if (gamepad1.a) {
                ringShooterMotor1.setPower(powerShotRingMotorSpeed);
                ringShooterMotor2.setPower(powerShotRingMotorSpeed);
                strafeRight(12);
                ringFeederServo.setPosition(1);
                sleep(300);
                ringFeederServo.setPosition(0.6);
                strafeRight(6);
                ringFeederServo.setPosition(1);
                sleep(300);
                ringFeederServo.setPosition(0.6);
                strafeRight(6);
                ringFeederServo.setPosition(1);
                sleep(300);
                ringFeederServo.setPosition(0.6);
            }

            // Press A for ring intake motor
            if (gamepad2.a) {
                if (takingInRingsMotor.getPower() < 0){
                    takingInRingsMotor.setPower(1);
                }
                else {
                    takingInRingsMotor.setPower(-1);
                }
            }

            // Right stick for wobble goal servo
            if (gamepad2.b) {
                if (wobbleGoalServo.getPosition() < 0.3) {
                    wobbleGoalServo.setPosition(wobbleGoalReleasePosition);
                }
                else {
                    wobbleGoalServo.setPosition(wobbleGoalVerticalPosition);
                }
            }

            // Press right trigger for ring feeder servo
            if (gamepad2.right_trigger == 1) {
                if (ringShooterMotor1.getPower() <= powerShotRingMotorSpeed){
                    ringFeederServo.setPosition(1);
                    sleep(300);
                    ringFeederServo.setPosition(0.6);
                }
            }

            // Press left trigger to unlock and lock wobble goal release servo
            if (gamepad2.left_trigger == 1) {
                if (wobbleGoalReleaseServo.getPosition() == 0) {
                    wobbleGoalReleaseServo.setPosition(0.3);
                }
                else {
                    wobbleGoalReleaseServo.setPosition(0);
                }
            }

            // press Y for shooter motor for tower goal
            if (gamepad2.y) {
                if (ringShooterMotor1.getPower() < powerShotRingMotorSpeed) {
                    ringShooterMotor1.setPower(0);
                    ringShooterMotor2.setPower(0);

                } else {
                    ringShooterMotor1.setPower(towerGoalRingMotorSpeed);
                    ringShooterMotor2.setPower(towerGoalRingMotorSpeed);
                }
            }

            // press X for shooter motor for powershot
            if (gamepad2.x) {
                ringShooterMotor1.setPower(powerShotRingMotorSpeed);
                ringShooterMotor2.setPower(powerShotRingMotorSpeed);
            }

        }
    }

    private void strafeRight(int inches) {
        driveFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        driveFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        driveFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        driveFL.setTargetPosition((int)(inches * encoderTicksPerInch));
        driveFR.setTargetPosition(-(int)(inches * encoderTicksPerInch));
        driveBL.setTargetPosition(-(int)(inches * encoderTicksPerInch));
        driveBR.setTargetPosition((int)(inches * encoderTicksPerInch));

        driveFL.setPower(drivePower);
        driveFR.setPower(-drivePower);
        driveBL.setPower(-drivePower);
        driveBR.setPower(drivePower);

        while (driveFL.isBusy()) {
            idle();
        }

        driveFL.setPower(0);
        driveFR.setPower(0);
        driveBL.setPower(0);
        driveBR.setPower(0);

        driveFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        driveFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        driveBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        driveBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // initialization class
    private void initialize() {

        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        // maps drive motor variables to hardware configuration names
        driveFL = hardwareMap.get(DcMotor.class, "motorTestFL");
        driveFR = hardwareMap.get(DcMotor.class, "motorTestFR");
        driveBL = hardwareMap.get(DcMotor.class, "motorTestBL");
        driveBR = hardwareMap.get(DcMotor.class, "motorTestBR");

        // sets left motors to reverse direction so they're going the right way
        driveFL.setDirection(DcMotor.Direction.REVERSE);
        driveBL.setDirection(DcMotor.Direction.REVERSE);

        // sets drive motor zero power behavior to brake
        driveFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        driveFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        driveBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        driveBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // maps ring intake motor variable to hardware configuration name
        takingInRingsMotor = hardwareMap.get(DcMotor.class, "takingInRingsMotor");

        // maps ring shooter motor variables to hardware configuration names
        ringShooterMotor1 = hardwareMap.get(DcMotor.class, "ringShooterMotor1");
        ringShooterMotor2 = hardwareMap.get(DcMotor.class, "ringShooterMotor2");

        // maps ring feeder servo variable to hardware configuration name
        ringFeederServo = hardwareMap.get(Servo.class, "ringFeederServo");
        ringFeederServo.setPosition(0.6); // sets initial position of servo

        // maps wobble goal servo variable to hardware configuration name
        wobbleGoalServo = hardwareMap.get(Servo.class, "wobbleGoalServo");
        wobbleGoalServo.setPosition(wobbleGoalVerticalPosition); // sets initial position of servo

        wobbleGoalReleaseServo = hardwareMap.get(Servo.class, "wobbleGoalReleaseServo");
        wobbleGoalReleaseServo.setPosition(0);

        telemetry.addData("Status", "Initialization Complete");
        telemetry.update();
    }

    // class to add and update telemetry
    private void addTelemetry() {
        telemetry.addData("Ring shooter motor speed", ringShooterMotor1.getPower());
        telemetry.addData("Ring intake motor speed", takingInRingsMotor.getPower());

        telemetry.update();
    }
}