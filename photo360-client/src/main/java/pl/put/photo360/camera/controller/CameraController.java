package pl.put.photo360.camera.controller;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraController {

    private VideoCapture capture;
    private boolean cameraActive;

    public CameraController() {
        this.capture = new VideoCapture();
        this.cameraActive = false;
    }

    public synchronized Mat startCamera() {
        if (!this.cameraActive) {
            this.capture.open(0);
            if (this.capture.isOpened()) {
                this.cameraActive = true;
            } else {
                // Obsługa błędu otwarcia kamery
            }
        }

        Mat frame = new Mat();
        if (this.cameraActive) {
            this.capture.read(frame);
            if (frame.empty()) {
                // Obsługa pustego ramki
            }
        }

        return frame;
    }

    public synchronized void stopCamera() {
        if (this.cameraActive && this.capture.isOpened()) {
            this.capture.release();
            this.cameraActive = false;
        }
    }
}
