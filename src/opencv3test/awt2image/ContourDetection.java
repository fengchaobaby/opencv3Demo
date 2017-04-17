package opencv3test.awt2image;

import java.util.ArrayList;
import java.util.List;

import opencv3test.SplitPic;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ContourDetection {

	public Mat findAndDrawContours(){
		Mat morphOutput = new Mat();
		
		Mat source = Imgcodecs.imread("D:\\Temp\\image\\2223434564.jpg"/*,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE*/);
		
		Mat dst = new Mat(source.rows(),source.cols(),source.type());
		Mat sobeldst = new Mat(source.rows(),source.cols(),source.type());
		Mat lapudst = new Mat(source.rows(),source.cols(),source.type());
		
		Mat mat_morp = new Mat(source.rows(),source.cols(),source.type());
		Size dsize = new Size(source.width() * 2  , source.height() * 2 ); 
		Mat mat_gray = new Mat(source.cols()  , source.rows() , source.type());
		
		Mat mat_cvtColor = new Mat(source.cols(), source.rows(), source.type());  
        Imgproc.cvtColor(source, mat_cvtColor, Imgproc.COLOR_BGRA2GRAY);  
        Imgcodecs.imwrite("d:/Temp/image/book8/mat_cvtColor.jpg", mat_cvtColor);
        Mat temp = SplitPic.splitImage(mat_cvtColor);
		Imgproc.resize(temp, mat_gray, dsize,4,4,0);	
		Mat srcClone = mat_gray.clone();		
		Imgproc.threshold(mat_gray, mat_morp, 120, 255, Imgproc.THRESH_BINARY);
		//Imgproc.threshold(mat_gray, mat_morp, 0, 255, Imgproc.THRESH_OTSU);
		Imgproc.GaussianBlur(mat_morp, mat_morp, new Size(3,3),15,0);
		/**
		 * sobel 0 1 就是对y轴做扫描,结果中都是横线,1 0就是对X轴做扫描 结果都是竖线
		 */
		Imgproc.Sobel(mat_morp, sobeldst,CvType.CV_32F, 0, 1, 3, 1, 38, Core.BORDER_DEFAULT);
		Imgproc.Laplacian(mat_morp, lapudst, -1);
		Imgcodecs.imwrite("d:/Temp/image/book8/mat_morp.jpg", mat_morp);
		Imgcodecs.imwrite("d:/Temp/image/book8/lapudst.jpg", lapudst);
		//水平投影
		Mat horizonProjection = BackProject.HorizonProjection(mat_morp);
		Imgcodecs.imwrite("d:/Temp/image/book8/HorizonProjection.jpg", horizonProjection);
		
		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20,20));
		Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12,12));
		Imgproc.morphologyEx(mat_morp, morphOutput, Imgproc.MORPH_CLOSE, dilateElement);		
		Imgproc.morphologyEx(morphOutput, morphOutput, Imgproc.MORPH_OPEN, erodeElement);
		Imgcodecs.imwrite("d:/Temp/image/book8/balls.jpg", morphOutput);
		Imgproc.Canny(morphOutput, dst, 20, 100);
		Imgcodecs.imwrite("d:/Temp/image/book8/Canny.jpg", dst);
		Imgcodecs.imwrite("d:/Temp/image/book8/sobeldst.jpg", sobeldst);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat(source.rows(),source.cols(),CvType.CV_8UC1,new Scalar(0));
		Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat drawing = Mat.zeros(dst.size(), CvType.CV_8UC3);
		//矩形的4个点
		Point[] allPoint = new Point[4];
		/**
		 * 最小矩形
		 */			
		Rect sonRect = new Rect();
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
			for(int i=0;i<contours.size();i++){
				//最小矩形 靠谱
				RotatedRect r = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
				//内轮廓 不怎么靠谱
				//Imgproc.drawContours(drawing, contours, i, new Scalar(0,255,0),2);
				r.points(allPoint);
				sonRect = r.boundingRect();
				//去掉面积小于10%的轮廓
				if(sonRect.area() < (source.rows()*source.cols()*0.10))continue;
				double he = ContourDetection.pointBbetweenDistance(allPoint[0],allPoint[1]);
				double wi = ContourDetection.pointBbetweenDistance(allPoint[0],allPoint[3]);
				//高宽比例
				double ratio = wi / he;
				
				if(ratio > 3 && ratio< 3.3){
					Imgproc.line(drawing,allPoint[0],allPoint[1],new Scalar(0,255,255),2); //宽
					Imgproc.line(drawing,allPoint[0],allPoint[3],new Scalar(255,0,255),2); //长
					for(int j=0;j<4;j++){								 
						Imgproc.line(drawing,allPoint[j],allPoint[(j+1)%4],new Scalar(0,0,255),1);
					}
					Rect rect = Imgproc.boundingRect(contours.get(i));
					System.out.println(rect.height + " " + rect.width);
					System.out.println(sonRect.height + " "+sonRect.width) ;
					System.out.println(source.cols() + " "+source.rows()) ;
					Mat roi_img  = new Mat(new Size(sonRect.height,sonRect.width),source.type());
					//必须要拷贝才可以					
					roi_img = new Mat(srcClone, sonRect);									
					Imgcodecs.imwrite("d:/Temp/image/book8/roi_img.jpg", roi_img);
				}				
			}
		}
		Imgcodecs.imwrite("d:/Temp/image/book8/lines.jpg", drawing);
		
		/**
		 * 凸包
		 */
		/*for (int w=0;w<contours.size();w++){
			
			//某个轮廓的点 MatOfPoint和MatOfInt要配合使用 MatOfInt中有凸点集合的索引，按顺序连接即可形成闭合图形
			MatOfPoint temp = contours.get(w);						
			MatOfInt hull = new MatOfInt();
			Imgproc.convexHull(temp, hull,false); //凸包	
			//起点索引(最大点)
			int index = (int)hull.get((int)hull.size().height-1,0)[0];	
			List<Integer> indexes = hull.toList();			
			//凸点
			Point pt,pt0 = new Point(temp.get(index, 0)[0],temp.get(index, 0)[1]);
			for(int z=0;z<indexes.size()-1 ;z++){
				//如果不减一会图形不会闭合
				index = (int)hull.get(z, 0)[0];//从最小开始
				pt = new Point(temp.get(index, 0)[0],temp.get(index, 0)[1]);
				Imgproc.line(srcClone, pt0, pt, new Scalar(0,255,0),1);
				pt0 = pt;
			}
		}
		Imgcodecs.imwrite("d:/Temp/image/book8/srcClone.jpg", srcClone);*/
		return drawing;
	}
	
	public static double pointBbetweenDistance(Point point1,Point point2){		
		return Math.sqrt(Math.pow(point1.x - point2.x,2) + Math.pow(point1.y - point2.y,2));		 			
	}
	
	public void inRange(){
		Mat source = Imgcodecs.imread("D:\\Temp\\image\\2223434564.jpg"/*,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE*/);
		Scalar min = new Scalar(95,123,0);
		Scalar max = new Scalar(133,197,219);	
		Mat inRange = new Mat(source.cols()  , source.rows() , source.type());
		Mat mat_cvtColor = new Mat(source.cols(), source.rows(), source.type());  
        Imgproc.cvtColor(source, mat_cvtColor, Imgproc.COLOR_BGR2HSV,3);  
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat(source.rows(),source.cols(),CvType.CV_8UC1,new Scalar(0));
		Core.inRange(mat_cvtColor, min, max, inRange);
		Imgcodecs.imwrite("d:/Temp/image/book8/mat_inRange_begin.jpg", inRange);
		
		Imgproc.findContours(inRange, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		/**
		 * 最小矩形
		 */
		Point[] allPoint = new Point[4];
		Rect sonRect;
		Mat drawing = Mat.zeros(source.size(), CvType.CV_8UC3);
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
			for(int i=0;i<contours.size();i++){
				RotatedRect r3 = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
				//内轮廓
				Imgproc.drawContours(drawing, contours, i, new Scalar(0,255,0),1);
				r3.points(allPoint);				
			}
		}
		//找到所有轮廓,拿第一个轮廓的左上角点和最后一个轮廓的右下角点创建一个矩形
		Point[] beginPoints = contours.get(0).toArray();
		Point beginpoint = beginPoints[0];
		Point[] endPoints = contours.get(contours.size()-1).toArray();
		Point endPoint =  endPoints[endPoints.length-1];
		sonRect = new Rect(beginpoint,endPoint);
		Mat drawing2 = new Mat(source,sonRect);
		Imgcodecs.imwrite("d:/Temp/image/book8/sonRect.jpg", drawing2);
	}
	
	public Mat findCarNumber(double threshold1){
		Mat morphOutput = new Mat();
		
		Mat source = Imgcodecs.imread("D:\\Temp\\image\\2223434564.jpg"/*,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE*/);
		
		Mat dst = new Mat(source.rows(),source.cols(),source.type());
		Mat mat_morp = new Mat(source.rows(),source.cols(),source.type());
		Size dsize = new Size(source.width() * 2  , source.height() * 2 ); 
		Mat mat_gray = new Mat(source.cols()  , source.rows() , source.type());
		Mat mat_cvtColor = new Mat(source.cols(), source.rows(), source.type());  
        Imgproc.cvtColor(source, mat_cvtColor, Imgproc.COLOR_BGRA2GRAY);  
        Imgcodecs.imwrite("d:/Temp/image/book8/mat_cvtColor.jpg", mat_cvtColor);
		Imgproc.resize(mat_cvtColor, mat_gray, dsize,4,4,0);	
		Mat srcClone = mat_gray.clone();
		Imgproc.threshold(mat_gray, mat_morp, 140, 255, Imgproc.THRESH_BINARY);
		//Imgproc.threshold(mat_gray, mat_morp, 0, 255, Imgproc.THRESH_OTSU);
		//Imgproc.GaussianBlur(mat_morp, mat_morp, new Size(3,3),15,0);
		Imgcodecs.imwrite("d:/Temp/image/book8/mat_morp.jpg", mat_morp);
		//Imgproc.medianBlur(mat_gray, mat_median, 5);		
		//Core.addWeighted(mat_gray, 2.1, mat_median, -1.1, 0, mat_median);
		
		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20,20));
		Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12,12));
		Imgproc.morphologyEx(mat_morp, morphOutput, Imgproc.MORPH_CLOSE, dilateElement);
		Imgcodecs.imwrite("d:/Temp/image/book8/balls.jpg", morphOutput);
		//Imgproc.morphologyEx(mat_median, morphOutput, Imgproc.MORPH_TOPHAT, erodeElement);
		Imgproc.Canny(morphOutput, dst, threshold1, 40);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat(source.rows(),source.cols(),CvType.CV_8UC1,new Scalar(0));
		Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat drawing = Mat.zeros(dst.size(), CvType.CV_8UC3);
		//矩形的4个点
		Point[] allPoint = new Point[4];
		/**
		 * 最小矩形
		 */
		for(int i=0;i<contours.size();i++){
			RotatedRect r = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
			//内轮廓
			//Imgproc.drawContours(drawing, contours, i, new Scalar(0,255,0),1);
			r.points(allPoint);
			for(int j=0;j<4;j++){
				Imgproc.line(drawing,allPoint[j],allPoint[(j+1)%4],new Scalar(0,0,255),1);
			}
		}
		Imgcodecs.imwrite("d:/Temp/image/book8/lines.jpg", drawing);
		/**
		 * 凸包
		 */
		for (int w=0;w<contours.size();w++){
			
			//某个轮廓的点 MatOfPoint和MatOfInt要配合使用 MatOfInt中有凸点集合的索引，按顺序连接即可形成闭合图形
			MatOfPoint temp = contours.get(w);						
			MatOfInt hull = new MatOfInt();
			Imgproc.convexHull(temp, hull,false); //凸包	
			//起点索引(最大点)
			int index = (int)hull.get((int)hull.size().height-1,0)[0];	
			List<Integer> indexes = hull.toList();			
			//凸点
			Point pt,pt0 = new Point(temp.get(index, 0)[0],temp.get(index, 0)[1]);
			for(int z=0;z<indexes.size()-1 ;z++){
				//如果不减一会图形不会闭合
				index = (int)hull.get(z, 0)[0];//从最小开始
				pt = new Point(temp.get(index, 0)[0],temp.get(index, 0)[1]);
				Imgproc.line(srcClone, pt0, pt, new Scalar(0,255,0),1);
				pt0 = pt;
			}
		}
		Imgcodecs.imwrite("d:/Temp/image/book8/srcClone.jpg", srcClone);
		return drawing;
	}
	
	/**
	 * 均衡化直方图
	 * @param source
	 */
	public static void equalizeHist(Mat source){
		
		List<Mat> bgr_planes = new ArrayList<Mat>();
		List<Mat> bgr_plane = new ArrayList<Mat>();
		
		Mat mat = new Mat(source.rows(),source.cols(),source.type());
		Mat mat2 = new Mat(source.rows(),source.cols(),source.type());
		Mat mat3 = new Mat(source.rows(),source.cols(),source.type());
		Imgproc.cvtColor(source, mat, Imgproc.COLOR_BGRA2GRAY);
		Mat dest = new Mat(source.rows(),source.cols(),CvType.CV_8UC3);
		Imgproc.equalizeHist(mat, dest);
		Imgcodecs.imwrite("d:/Temp/image/book2/equalizeHist2.jpg", dest);
		Imgproc.adaptiveThreshold(mat, mat2, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, 0);
		Imgcodecs.imwrite("d:/Temp/image/book2/adaptiveThreshold.jpg", mat2);
		
	}
	
	public static MatOfPoint convertIndexesToPoints(MatOfPoint contour, MatOfInt indexes) {
	    int[] arrIndex = indexes.toArray();
	    Point[] arrContour = contour.toArray();
	    Point[] arrPoints = new Point[arrIndex.length];

	    for (int i=0;i<arrIndex.length;i++) {
	        arrPoints[i] = arrContour[arrIndex[i]];
	    }

	    MatOfPoint hull = new MatOfPoint(); 
	    hull.fromArray(arrPoints);
	    return hull; 
	}
	
	public static void main(String args[] ){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		ContourDetection  originalFrame = new ContourDetection();
		Mat imageToShow = originalFrame.findAndDrawContours();
		
		//Mat source = Imgcodecs.imread("D:\\Temp\\image\\3987977534.jpg");
		//Mat temp = SplitPic.splitImage(source);
		//Imgcodecs.imwrite("D:\\Temp\\image\\book8\\splitImage.jpg", temp);
		//ContourDetection.equalizeHist(source);
		
		originalFrame.inRange();
		
	}
}
