package org.firstinspires.ftc.teamcode.Auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Control.Robot;
import org.firstinspires.ftc.teamcode.Control._Autonomous;
import org.firstinspires.ftc.teamcode.Drivers._Drivetrain;

import java.util.concurrent.TimeUnit;

@Autonomous(group="Auton", preselectTeleOp = "FinalTeleOp")
public class RedLeft extends _Autonomous {

    private State _state;
    private boolean _justEntered;
    private int _parkingSpot;

    @Override
    public void init() {
        Robot.setup(hardwareMap, telemetry, Robot.SetupType.AutonomousPart1);
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        Robot.setup(hardwareMap, telemetry, Robot.SetupType.AutonomousPart2);

        _state = State.FORWARD_TO_LOW_POLE;
        _justEntered = true;
    }

    @Override
    public void loop() {
        Robot.update();

        switch (_state) {
            case FORWARD_TO_LOW_POLE:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getDrivetrain().runDistance(0.3, 5, _Drivetrain.Movements.forward);
                }
                else if (!Robot.getDrivetrain().isBusy()) {
                    _state = State.RAISE_SLIDE;
                    _justEntered = true;
                }
                break;
            case RAISE_SLIDE:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getLinearslide().runTime(-.2,450);
                }
                else if (!Robot.getLinearslide().isBusy()) {
                    _state = State.Turn_Claw;
                    _justEntered = true;
                }
                break;
            case Turn_Claw:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getClaw6().setPosition(0.3);
                }
                else if(!Robot.getClaw6().isBusy()){
                    _state = State.OPEN_CLAW;
                    _justEntered=true;
                }
                break;
            case OPEN_CLAW:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getClaw().setPosition(Robot.CLAW_OPEN);
                }
                else if(!Robot.getClaw().isBusy()){
                    _state = State.Move_Left;
                    _justEntered=true;
                }
                break;
            case Move_Left:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getDrivetrain().runDistance(0.2, 14.5, _Drivetrain.Movements.left);
                }
                else if(!Robot.getDrivetrain().isBusy()){
                    _state = State.Move_Forward;
                    _justEntered=true;
                }
                break;
            case Move_Forward:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getDrivetrain().runDistance(0.2, 18, _Drivetrain.Movements.forward);
                }
                else if(!Robot.getDrivetrain().isBusy()){
                    _state = State.LOWER;
                    _justEntered=true;
                }
                break;
//            case Sense:
//                if (_justEntered) {
//                    _justEntered = false;
//                    double t1 = Robot.runtime.now(TimeUnit.MILLISECONDS);
//
//                    while(Robot.runtime.now(TimeUnit.MILLISECONDS)-t1 < 3000) {
//                        if (Robot.getColor().getHue() < 10 && Robot.getColor().getHue() > 350) {//red
//                            _parkingSpot = 1;
//                        } else if (Robot.getColor().getHue() > 110 && Robot.getColor().getHue() < 140) {//green
//                            _parkingSpot = 2;
//                        } else if (Robot.getColor().getHue() < 255 && Robot.getColor().getHue() > 215) {//blue
//                            _parkingSpot = 3;
//                        } else {
//                            _parkingSpot = 4;
//                        }
//                    }
//
//                    double t2 = Robot.runtime.now(TimeUnit.MILLISECONDS);
//
//                    while(Robot.runtime.now(TimeUnit.MILLISECONDS)-t2 < 1000) {
//                        Robot.telemetry.update();
//                        Robot.telemetry.addLine(String.valueOf(_parkingSpot));
//                        Robot.telemetry.update();
//                    }
//
//                    Robot.getDrivetrain().runDistance(0.2, 5, _Drivetrain.Movements.backward);
//
//                }
//                else if(!Robot.getDrivetrain().isBusy()){
//                    _state = State.LOWER;
//                    _justEntered=true;
//                }
//                break;
            case LOWER:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getLinearslide().runTime(.2,420);
                }
                else if (!Robot.getLinearslide().isBusy()) {
                    _state = State.Close_claw;
                    _justEntered = true;
                }
                break;

            case Close_claw:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getClaw().setPosition(Robot.CLAW_CLOSED);
                    double t2 = Robot.runtime.now(TimeUnit.MILLISECONDS);
                    while(Robot.runtime.now(TimeUnit.MILLISECONDS)-t2 < 1000) {
                        Robot.telemetry.update();
                        Robot.telemetry.addLine(String.valueOf(_parkingSpot));
                        Robot.telemetry.update();
                    }
                }
                else if (!Robot.getClaw().isBusy()) {
                    _state = State.AngleClaw;
                    _justEntered = true;
                }
                break;
            case AngleClaw:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getClaw6().setPosition(0);
                    double t2 = Robot.runtime.now(TimeUnit.MILLISECONDS);
                    while(Robot.runtime.now(TimeUnit.MILLISECONDS)-t2 < 1000) {
                        Robot.telemetry.update();
                        Robot.telemetry.addLine(String.valueOf(_parkingSpot));
                        Robot.telemetry.update();
                    }
                }
                else if(!Robot.getClaw6().isBusy()){
                    _state = State.move_forward1;
                    _justEntered=true;
                }
                break;
            case move_forward1:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getDrivetrain().runDistance(0.5, 36, _Drivetrain.Movements.forward);
                }
                else if (!Robot.getDrivetrain().isBusy()) {
                    _state = State.Turn_Right;
                    _justEntered = true;
                }
                break;
            case Turn_Right:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getDrivetrain().runDistance(0.5, 6.75, _Drivetrain.Movements.cw);
                }
                else if (!Robot.getDrivetrain().isBusy()) {
                    _state = State.Lift_linearslide;
                    _justEntered = true;
                }
                break;
            case Lift_linearslide:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getLinearslide().runTime(-.2,3500);
                }
                else if (!Robot.getLinearslide().isBusy()) {
                    _state = State.FORWARD;
                    _justEntered = true;
                }
                break;
            case FORWARD:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getDrivetrain().runDistance(0.3, 15.75, _Drivetrain.Movements.forward);
                }
                else if (!Robot.getDrivetrain().isBusy()) {
                    _state = State.RAISE_SLIDE;
                    _justEntered = true;
                }
                break;
            case move_forward2:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getDrivetrain().runDistance(0.5, 3, _Drivetrain.Movements.forward);
                }
                else if (!Robot.getDrivetrain().isBusy()) {
                    _state = State.Lowclaw;
                    _justEntered = true;
                }
            case Lowclaw:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getClaw6().setPosition(0.3);
                }
                else if(!Robot.getClaw6().isBusy()){
                    _state = State.Open_Claw1;
                    _justEntered=true;
                }
                break;
            case Open_Claw1:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getClaw().setPosition(Robot.CLAW_OPEN);
                }
                else if(!Robot.getClaw().isBusy()){
                    _state = State.UpClaw;
                    _justEntered=true;
                }
                break;
            case UpClaw:
                if(_justEntered){
                    _justEntered=false;
                    Robot.getClaw6().setPosition(0);
                }
                else if(!Robot.getClaw6().isBusy()){
                    _state = State.Lowlinslide;
                    _justEntered=true;
                }
                break;
            case Lowlinslide:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getLinearslide().runTime(.2,1016);
                }
                else if (!Robot.getLinearslide().isBusy()) {
                    _state = State.Turn_left;
                    _justEntered = true;
                }
                break;
            case Turn_left:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getDrivetrain().runDistance(0.5, 5, _Drivetrain.Movements.cw);
                }
                else if (!Robot.getDrivetrain().isBusy()) {
                    _state = State.Move_back;
                    _justEntered = true;
                }
                break;
            case Move_back:
                if (_justEntered) {
                    _justEntered = false;
                    Robot.getDrivetrain().runDistance(0.5, 36, _Drivetrain.Movements.backward);
                }
                else if (!Robot.getDrivetrain().isBusy()) {
                    _state = State.park;
                    _justEntered = true;
                }
                break;
            case park:
                if (_justEntered) {
                    _justEntered = false;
                    if (_parkingSpot==1) {//red
                        Robot.getDrivetrain().runDistance(0.5, 7, _Drivetrain.Movements.left);

                    } else if (_parkingSpot == 2) {//green
                        Robot.getDrivetrain().runDistance(0.5, 1, _Drivetrain.Movements.forward);
                    } else {
                        Robot.getDrivetrain().runDistance(0.5, 7, _Drivetrain.Movements.right);
                    }
                }
//                if (_justEntered){
//                    _justEntered=false;
//                    if (parkingSpot.equals("red")){
//                        Robot.getDrivetrain().runDistance(0.5, 10, _Drivetrain.Movements.left);
//                    }
//                    else if(parkingSpot.equals("green")){
//                        Robot.getDrivetrain().runDistance(0.5, 10, _Drivetrain.Movements.right);
//                    }
//                    else {
//                        Robot.getDrivetrain().runDistance(0.5, 1, _Drivetrain.Movements.forward);
//                    }
//                }
                break;
        }
    }

    private enum State {
        FORWARD_TO_LOW_POLE,
        RAISE_SLIDE,
        Turn_Claw,
        OPEN_CLAW,
        Move_Left,
        Move_Forward,
        Sense,
        LOWER,
        Close_claw,
        FORWARD,
        AngleClaw,
        move_forward1,
        Turn_Right,
        Lift_linearslide,
        move_forward2,
        Lowclaw,
        Open_Claw1,
        UpClaw,
        Lowlinslide,
        Turn_left,
        Move_back,
        park
    }
}
