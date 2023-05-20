import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

public class CameraManager {
    private VideoCapture videoCapture;

    public CameraManager() {
        videoCapture = new VideoCapture();
    }

    public Mat capturePhoto() {
        Mat frame = new Mat();
        videoCapture.read(frame);
        return frame;
    }
}
