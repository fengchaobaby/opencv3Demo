package opencv3test.awt2image;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 主要函数 Imgproc.distanceTransform 细化字体  找到 图形中心位置
 * 
 * 主要是求得每个像素点的值是该像素点到其最近的“零”像素点的距离。
 *
 * @description 
 *
 * @author Felix_Feng
 *
 * @date Mar 17, 2017
 *
 */
public class FindPalmCenter {
	
	public static void findPalmCenter(){

		Mat source = Imgcodecs.imread("D:/Temp/image/20160912223814977.jpg");
		Mat target = new Mat(source.size(),CvType.CV_8U);		
		
        Imgproc.cvtColor(source, target, Imgproc.COLOR_RGB2GRAY);  
		Imgproc.GaussianBlur(target, target, new Size(15,15),0,0);
		Mat threshold_output = new Mat(source.rows(),source.cols(),source.type());
		Imgproc.threshold(target, threshold_output, 100, 255, Imgproc.THRESH_OTSU);
		Imgcodecs.imwrite("D:/Temp/image/book2/findPalmCenter_threshold.jpg",threshold_output);	
		Mat destination1 = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat canny_mat = new Mat(source.rows(),source.cols(),source.type());
		Mat hierarchy = new Mat(source.rows(),source.cols(),CvType.CV_8UC1,new Scalar(0));
		Core.inRange(threshold_output, new Scalar(0,0,0), new Scalar(0,0,0), canny_mat);
		Imgproc.findContours(canny_mat, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.distanceTransform(threshold_output,destination1,Imgproc.CV_DIST_L2,3);
		double largest_area = 0;
		int largest_contour_index = 0;
		Rect bounding_rect = new Rect();
		for (int i = 0; i < contours.size(); i++)
		{
			double a = Imgproc.contourArea((Mat) contours.get(i), false); // Find the
			//找到最大轮廓
			if (a > largest_area) {
				largest_area = a;
				largest_contour_index = i; // Store the index of largest contour
				 // Find
			}
		}
		Mat tempMat = source.clone();
		Imgproc.drawContours(tempMat, contours, largest_contour_index, new Scalar(0, 250, 0),5);	
		Imgcodecs.imwrite("d:/Temp/image/book2/tempMat.jpg", tempMat);

		int temp=0;
		int R=0;
		int centerX =0;
		int centerY = 0;
		double d =0;
		for(int i=0;i<source.rows();i++){
			for(int y=0;y<source.cols();y++){
				d=Imgproc.pointPolygonTest(new MatOfPoint2f(contours.get(largest_contour_index).toArray()),
						new Point(y , i), true);  //判断点是否在图形内   note ： y and i reverse 
				
					temp = (int)destination1.get(i, y)[0];
					if(temp>R){
						R=temp;
						centerX=y;
						centerY=i;
					
				}						
			}
		}	
			
		System.out.println(centerX + " " + centerY + "  "+ R);
		/*RotatedRect r = null;
		for(int j=0;j<contours.size();j++){
			if(contours.get(i).toArray().length > 115){
				//拟合椭圆
				r=Imgproc.fitEllipse(new MatOfPoint2f(contours.get(i).toArray()));
				//绘制椭圆
				Imgproc.ellipse(source, r, new Scalar(0,0,255),  3, 8);
				Point ellipsePt = r.center;
				Imgproc.line(source,ellipsePt,ellipsePt,new Scalar(0,0,255),3);
			}
			
		}*/
		Imgproc.circle(source, new Point(centerX, centerY), R, new Scalar(255,0,0));
		Imgproc.line(source,new Point(centerX, centerY),new Point(centerX, centerY),new Scalar(255,0,0),3);
		Mat normalizeDst = new Mat();
		Core.normalize(destination1, normalizeDst,0,255,Core.NORM_MINMAX);
		Imgcodecs.imwrite("d:/Temp/image/book2/centerX.jpg", source);
		Imgcodecs.imwrite("d:/Temp/image/book2/normalizeDst.jpg", normalizeDst);
		Imgcodecs.imwrite("d:/Temp/image/book2/destination1.jpg", destination1);

}
	
	public static void main(String args[] ){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		FindPalmCenter.findPalmCenter();
		//Imgcodecs.imwrite("D:/444.jpg", imageToShow);
		//Mat source = Imgcodecs.imread("D:\\Temp\\image\\123.jpg");
		//ContourDetection.equalizeHist(source);
	}	
	
}
