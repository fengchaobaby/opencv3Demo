package opencv3test.awt2image;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

public class HoughTest {
	
	
	/**
	 * 霍夫线变换检测
	 * @param source
	 * @param threshold
	 * @param minLineLength
	 * @param maxLineGap
	 * @param canny_threshold
	 * @param canny_threshold2
	 * @return
	 */
	public static Mat houghline(Mat source,int threshold,double minLineLength,
			double maxLineGap,double canny_threshold,double canny_threshold2) {

		Mat mRgba = new Mat();
		Mat thresholdImage = new Mat(source.size(), CvType.CV_8UC1);
		Imgproc.cvtColor(mRgba, thresholdImage, Imgproc.COLOR_GRAY2BGR, 0);
		Imgproc.Canny(source, thresholdImage, canny_threshold, canny_threshold2);
		Mat lines = new Mat();
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		source.copyTo(destination);
		Imgproc.HoughLinesP(thresholdImage, lines, 1, Math.PI / 180, threshold,minLineLength,maxLineGap);

		for (int x = 0; x < lines.cols(); x++) {
			double[] vec = lines.get(0, x);
			System.out.print(vec.length);
			double x1 = vec[0], y1 = vec[1],x2 = vec[2],y2 = vec[3];
			Point start = new Point(x1, y1);
			Point end = new Point(x2,y2);
			Imgproc.line(destination, start, end, new Scalar(255, 0, 0),3);			
		}
		Imgcodecs.imwrite("D:\\Temp\\image\\HoughLinesP_result.jpg", destination);
		
		return destination;
	}
	
	
	public static void main(String args[] ){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\Temp\\data\\building.jpg",Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat dst = new Mat();
		HoughTest.houghline(src, 103, 1, 10, 201,128);
		Imgcodecs.imwrite("D:\\Temp\\image\\user4_result.jpg", dst);	
	}
	
}
