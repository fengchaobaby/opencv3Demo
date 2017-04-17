package opencv3test.awt2image;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CheckImgFindBlueBall {
	
	public static void find(){
		
		Mat source = Imgcodecs.imread("D:/Temp/image/87254620.jpg");
		Mat hsv_img = new Mat();
		Mat result = new Mat();
		Mat circles = new Mat();
		Scalar hsv_min = new Scalar(100,90,90,0);
		Scalar hsv_max = new Scalar(140,255,255,0);
		Mat thredsholded = new Mat();
		Imgproc.cvtColor(source, hsv_img, Imgproc.COLOR_BGR2HSV);
		Core.inRange(hsv_img, hsv_min, hsv_max, thredsholded);
		Imgproc.GaussianBlur(thredsholded, thredsholded, new Size(9,9), 0,0);
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(55,55));
		Imgproc.dilate(thredsholded, thredsholded, element);
		//找圆
		Imgproc.HoughCircles(thredsholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2,
				thredsholded.height()/4 , 500 , 50 , 0 , 0);
		int rows = circles.rows();
		int elemSize = (int)circles.elemSize();
		float[] data2 = new float[rows*elemSize/4];
		if(data2.length>0){
			circles.get(0, 0,data2);
			for(int i=0;i<data2.length;i=i+3){
				Point center = new Point(data2[i],data2[i+1]);
				Imgproc.ellipse(source, center, new Size((double)data2[i+2],(double)data2[i+2]), 0, 0, 360, 
						new Scalar(255,0,255), 4, 8, 0);
			}
		}
		Imgcodecs.imwrite("D:/Temp/image/book4/source.jpg",source);	
		Imgcodecs.imwrite("D:/Temp/image/book4/hsv_img.jpg",hsv_img);	
		Imgcodecs.imwrite("D:/Temp/image/book4/thredsholded.jpg",thredsholded);	
		
	}
	
	public static void main(String[] args){

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CheckImgFindBlueBall.find();		
	}
}
