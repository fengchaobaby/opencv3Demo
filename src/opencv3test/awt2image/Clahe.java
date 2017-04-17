package opencv3test.awt2image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

public class Clahe {
	
	/**
	 * 自适应直方图均衡CLAHE
	 * @param args
	 */
	public static void main(String args[] ){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\Temp\\data\\starry_night.jpg",Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat dst = new Mat();
		CLAHE clahe = Imgproc.createCLAHE(/*clipLimit*/ 5.9,/*tileGridSize*/new Size(7,7));
		clahe.apply(src, dst);
		Imgcodecs.imwrite("D:\\Temp\\image\\user4_result.jpg", dst);	
	}
	
}
