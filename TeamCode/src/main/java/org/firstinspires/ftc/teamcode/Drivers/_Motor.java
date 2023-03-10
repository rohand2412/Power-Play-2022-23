package org.firstinspires.ftc.teamcode.Drivers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Control.Robot;

public class _Motor {

    private final String _NAME;
    private final Type _TYPE;
    private final Usage _USAGE;
    private final double _WHEEL_DIAMETER_INCHES;
    private final double _COUNTS_PER_INCH;
    private final double _COUNTS_PER_DEGREE;
    private final boolean _HAS_ENCODER;
    private final DcMotor.RunMode _DEFAULT_RUNMODE;

    private final DcMotor _motor;
    private double _typicalSpeed;
    private double _speed;
    private boolean _isBusy;
    private RunLimiter _runLimiter;
    private double _startTime;
    private double _elapsedTime;

    private boolean _isProg;
    private double _progSpeed;
    private int _progCounts;
    private int _progStartCounts;

    public _Motor(String name, Type type, DcMotorSimple.Direction direction, DcMotor.ZeroPowerBehavior zeroPowerBehavior, boolean hasEncoder) {
        _NAME = name;
        _TYPE = type;
        _USAGE = Usage.Circular;
        _WHEEL_DIAMETER_INCHES = 0;
        _COUNTS_PER_INCH = 0;
        _COUNTS_PER_DEGREE = _TYPE._COUNTS/360.0;
        _HAS_ENCODER = hasEncoder;
        _DEFAULT_RUNMODE = _HAS_ENCODER ? DcMotor.RunMode.RUN_USING_ENCODER : DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        _motor = Robot.hardwareMap.dcMotor.get(_NAME);
        _config(direction, zeroPowerBehavior);
    }

    public _Motor(String name, Type type, DcMotorSimple.Direction direction, DcMotor.ZeroPowerBehavior zeroPowerBehavior, double wheelDiameterInches, boolean hasEncoder) {
        _NAME = name;
        _TYPE = type;
        _USAGE = Usage.Linear;
        _WHEEL_DIAMETER_INCHES = wheelDiameterInches;
        _COUNTS_PER_INCH = _TYPE._COUNTS/(_WHEEL_DIAMETER_INCHES * Math.PI);
        _COUNTS_PER_DEGREE = 0;
        _HAS_ENCODER = hasEncoder;
        _DEFAULT_RUNMODE = _HAS_ENCODER ? DcMotor.RunMode.RUN_USING_ENCODER : DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        _motor = Robot.hardwareMap.dcMotor.get(_NAME);
        _config(direction, zeroPowerBehavior);
    }

    public void setTypicalSpeed(double speed) {
        _typicalSpeed = speed;
    }

    public void update() {
        if (_isBusy) {
            switch (_runLimiter) {
                case Magnitude:
                    if (_isProg) {
                        double progress = Math.abs((_motor.getCurrentPosition() - _progStartCounts) / _progCounts);
                        double input = 20 * (progress - 0.85);
                        double speed = (1 / (Math.exp(input) + 1)) * _progSpeed;
                        _setSpeed(speed);
                    }

                    if (!_motor.isBusy()) {
                        stop();
                    }
                    break;
                case Time:
                    if (Robot.runtime.milliseconds() >= _startTime + _elapsedTime) {
                        stop();
                    }
                    break;
            }
        }
    }

    public void runSpeed(double speed) {
        if (!_isBusy) {
            _motor.setMode(_DEFAULT_RUNMODE);
            _setSpeed(speed);
        }
    }

    public void runSpeed() {
        runSpeed(_typicalSpeed);
    }

    public void runDistance(double speed, double distance) {
        if (!_isBusy && speed != 0 && _USAGE == Usage.Linear && _HAS_ENCODER) {
            _isBusy = true;
            _runLimiter = RunLimiter.Magnitude;
            _zeroSpeed();
            int sign = (speed < 0 ^ distance < 0 ? -1 : 1);
            _motor.setTargetPosition(_motor.getCurrentPosition() + (int) (sign * Math.abs(distance) * _COUNTS_PER_INCH));
            _motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            _setSpeed(sign * Math.abs(speed));
        }
        else if (_USAGE == Usage.Circular) {
            stop();
            while (true) {
                Robot.telemetry.addLine("[ERROR] _Motor.runDistance USED WITH USAGE MODE CIRCULAR");
                Robot.telemetry.update();
            }
        }
        else if (!_HAS_ENCODER) {
            stop();
            while (true) {
                Robot.telemetry.addLine("[ERROR] _Motor.runDistance USED WITHOUT ENCODER");
                Robot.telemetry.update();
            }
        }
    }

    public void runDistance(double distance) {
        runDistance(_typicalSpeed, distance);
    }

    public void runDistProgressive(double speed, double distance) {
        if (!_isBusy && speed != 0 && _USAGE == Usage.Linear && _HAS_ENCODER) {
            _isBusy = true;
            _isProg = true;
            _runLimiter = RunLimiter.Magnitude;
            _zeroSpeed();
            int sign = (speed < 0 ^ distance < 0 ? -1 : 1);
            _progStartCounts = _motor.getCurrentPosition();
            _progCounts = (int) (sign * Math.abs(distance) * _COUNTS_PER_INCH);
            _motor.setTargetPosition(_progStartCounts + _progCounts);
            _motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            _progSpeed = sign * Math.abs(speed);
        }
        else if (_USAGE == Usage.Circular) {
            stop();
            while (true) {
                Robot.telemetry.addLine("[ERROR] _Motor.runDistProgressive USED WITH USAGE MODE CIRCULAR");
                Robot.telemetry.update();
            }
        }
        else if (!_HAS_ENCODER) {
            stop();
            while (true) {
                Robot.telemetry.addLine("[ERROR] _Motor.runDistProgressive USED WITHOUT ENCODER");
                Robot.telemetry.update();
            }
        }
    }

    public void runTime(double speed, double milliseconds) {
        if (!_isBusy && speed != 0) {
            _isBusy = true;
            _runLimiter = RunLimiter.Time;
            _zeroSpeed();
            _startTime = Robot.runtime.milliseconds();
            _elapsedTime = milliseconds;
            _motor.setMode(_DEFAULT_RUNMODE);
            _setSpeed(speed);
        }
    }

    public void runTime(double milliseconds) {
        runTime(_typicalSpeed, milliseconds);
    }

    public void runDegrees(double speed, double degrees) {
        if (!_isBusy && speed != 0 && _USAGE == Usage.Circular && _HAS_ENCODER) {
            _isBusy = true;
            _runLimiter = RunLimiter.Magnitude;
            _zeroSpeed();
            int sign = (speed < 0 || degrees < 0 ? -1 : 1);

            _motor.setTargetPosition(_motor.getCurrentPosition() + (int) (sign * Math.abs(degrees) * _COUNTS_PER_DEGREE));
            _motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            _setSpeed(sign * Math.abs(speed));
        }
        else if (_USAGE == Usage.Linear) {
            stop();
            while (true) {
                Robot.telemetry.addLine("[ERROR] _Motor.runDegrees USED WITH USAGE MODE LINEAR");
                Robot.telemetry.update();
            }
        }
        else if (!_HAS_ENCODER) {
            stop();
            while (true) {
                Robot.telemetry.addLine("[ERROR] _Motor.runDegrees USED WITHOUT ENCODER");
                Robot.telemetry.update();
            }
        }
    }

    public void runDegrees(double degrees) {
        runDegrees(_typicalSpeed, degrees);
    }

    public void runRotations(double speed, double rotations) {
        runDegrees(speed, rotations * 360.0);
    }

    public void runRotations(double rotations) {
        runRotations(_typicalSpeed, rotations);
    }

    public void stop() {
        _isProg = false;
        _isBusy = false;
        _setSpeed(0);
        _motor.setMode(_DEFAULT_RUNMODE);
    }

    public void _zeroSpeed() {
        _setSpeed(0);
        _motor.setMode(_DEFAULT_RUNMODE);
    }

    public int getCounts() {
        return _motor.getCurrentPosition();
    }

    public int getTargetPosition() {
        return _motor.getTargetPosition();
    }

    public String getName() {
        return _NAME;
    }

    public double getWheelDiameterInches() {
        return _WHEEL_DIAMETER_INCHES;
    }

    public double getCountsPerInch() {
        return _COUNTS_PER_INCH;
    }

    public double getCountsPerDegree() {
        return _COUNTS_PER_DEGREE;
    }

    public double getTypicalSpeed() {
        return _typicalSpeed;
    }

    public double getSpeed() {
        return _speed;
    }

    public boolean isBusy() {
        return _isBusy;
    }

    private void _config(DcMotorSimple.Direction direction, DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        _isBusy = false;
        _motor.setDirection(direction);
        _motor.setZeroPowerBehavior(zeroPowerBehavior);
        _motor.setMode(_DEFAULT_RUNMODE);
        _zeroSpeed();
    }

    private void _setSpeed(double speed) {
        _speed = speed;
        _motor.setPower(Math.max(Math.min(_speed, 1), -1));
    }

    private void _setSpeed() {
        _setSpeed(_typicalSpeed);
    }

    public enum Type {
        GOBILDA_30_RPM(5281.1),
        GOBILDA_117_RPM(1425.1),
        GOBILDA_312_RPM(537.6),
        GOBILDA_435_RPM(383.6),
        REV_CORE_HEX_MOTOR(288);

        private final double _COUNTS;

        Type(double counts) { _COUNTS = counts; }

        public double getCounts() { return _COUNTS; }
    }

    private enum Usage {
        Linear,
        Circular
    }

    public enum RunLimiter {
        Magnitude,
        Time,
    }
}