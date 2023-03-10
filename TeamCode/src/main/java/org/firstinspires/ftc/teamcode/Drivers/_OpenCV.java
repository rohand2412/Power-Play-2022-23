package org.firstinspires.ftc.teamcode.Drivers;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Control.Robot;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class _OpenCV {

    private final String _NAME;

    private final OpenCvWebcam _webcam;

    public _OpenCV(String name, int width, int height, OpenCvPipeline pipeline, OpenCvCameraRotation rotation) {
        _NAME = name;
        int cameraMonitorViewId = Robot.hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", Robot.hardwareMap.appContext.getPackageName());
        _webcam = OpenCvCameraFactory.getInstance().createWebcam(Robot.hardwareMap.get(WebcamName.class, _NAME), cameraMonitorViewId);
        _webcam.setPipeline(pipeline);
        _webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                _webcam.startStreaming(width, height, rotation);
            }

            @Override
            public void onError(int errorCode) {
                Robot.telemetry.addLine("[ERROR] OPENCV WEBCAM ERROR: " + errorCode);
                Robot.telemetry.update();
            }
        });
    }

    public _OpenCV(String name, int width, int height, OpenCvPipeline pipeline) {
        this(name, width, height, pipeline, OpenCvCameraRotation.UPRIGHT);
    }

    public void pauseLiveView() {
        /* OUT OF ORDER */
        // Viewport was not being paused
        _webcam.pauseViewport();
    }

    public void resumeLiveView() {
        /* OUT OF ORDER */
        // Useless since viewport isn't being paused
        _webcam.resumeViewport();
    }

    public void stopStreaming() {
        _webcam.stopStreaming();
    }

    public void closeCameraDevice() {
        _webcam.closeCameraDevice();
    }

    public String getName() {
        return _NAME;
    }
}