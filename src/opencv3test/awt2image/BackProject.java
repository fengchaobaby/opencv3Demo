package opencv3test.awt2image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BackProject {
	
	/**
	 * 水平投影 根据列上的黑色像素点数量来绘制 精确定位
	 * @param src
	 * @return
	 */
	public static Mat HorizonProjection(Mat src){		
		Mat dst = new Mat(src.rows(),src.cols(),CvType.CV_8UC1,new Scalar(0,0,0));
		double[] result = {255,255,255};
		int count = 0;
	    for(int i = 0; i < src.cols(); i++){     	
	    	count = 0;
	        for(int j = 0; j < src.rows(); j++){ 
	        	double[] temp = src.get(j, i);
	           if(temp[0]==0){	        	  
	        	   count = count +1 ;
	           }           
	        }

	        for(int w=0;w<count; w++){
	        	dst.put(w ,i, result );
	        }
	    }
		return dst; 	    
	} 
	
	public static Mat matGO(Mat src){
		
		Mat dst = new Mat(src.rows(),src.cols(),CvType.CV_8UC1);
		for(int j=0;j<src.rows();j++){
			for(int i=0;i<src.cols();i++){
				double[] temp = src.get(j, i);
				temp[0]=255;
				temp[0]=255;
				temp[0]=255;
				dst.put(j,i,temp);
				
			}
		}
		return dst;
	}
	
	
	 double[] p_dst ;
	public static void VerticalProjection(Mat src){  	 
	    int i, j;  
  
	    for(j = 0; j < src.cols(); j++){  
	         
	        for(i = 0; i < src.rows(); i++){  
	         //   p = src.get(i, j)[0];

	        }  
	    }  
	}  
	  
/**
 * 反向投影
 * 
 * @description 
 *
 * @author Felix_Feng
 *
 * @date Mar 16, 2017
 *
 */
	public static void backProject(int threshold1){
		int bins = 30;
		//原图片
		Mat source2 = Imgcodecs.imread("D:\\Temp\\image\\20170330104057.jpg");
		Mat handle = new Mat();
		Imgproc.GaussianBlur(source2, source2, new Size(15,15),0,0);
		Imgcodecs.imwrite("D:/Temp/image/book4/BackProject_GaussianBlur.jpg",source2);
		//背景太白 3rd用100
		Imgproc.threshold(source2, source2, threshold1, 255, Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite("D:/Temp/image/book4/BackProject_threshold1.jpg",source2);	
		Imgproc.cvtColor(source2, handle, Imgproc.COLOR_RGB2HSV);
		MatOfInt histSize = new MatOfInt(bins,bins);
		MatOfFloat ranges = new MatOfFloat(0,179,0,255);
		Mat hist = new Mat();
		MatOfInt channels = new MatOfInt(0,1);
		Imgproc.calcHist(Arrays.asList(handle), channels, new Mat(), hist, histSize, ranges,false);
		Core.normalize(hist, hist,0,255,Core.NORM_MINMAX,-1,new Mat());
		Mat out = new Mat();
		Imgproc.calcBackProject(Arrays.asList(handle), channels, hist, out, ranges, 1);
		Imgcodecs.imwrite("D:/Temp/image/book4/BackProject.jpg",out);	
		
	}
	/**
	 * 直方图统计 按rgb颜色分成255份，统计每种颜色的个数
	 * @param source
	 * @return
	 */
	public static Mat getHistogram(Mat source){
		
		List<Mat> bgr_planes = new ArrayList<Mat>();
		List<Mat> bgr_plane = new ArrayList<Mat>();
		
		Core.split(source, bgr_planes);
		
		Mat hist = new Mat();
		MatOfInt histSize = new MatOfInt(256);
		MatOfFloat ranges = new MatOfFloat(0,256);
		int hist_w = 512;
		int hist_h = 500;
		long bin_w ;
		
		//Mat mask = new Mat(source.size(), CvType.CV_8UC1);
		//Imgproc.cvtColor(source, mask, Imgproc.COLOR_BGR2GRAY);
	      // Imgproc.threshold(mask, mask, 0, 255, Imgproc.THRESH_OTSU);
	       
		Mat histImage = new Mat(hist_h,hist_w,CvType.CV_8UC3);
		for(int j = 0 ; j<3; j++){
			bgr_plane.clear();
			bgr_plane.add(bgr_planes.get(j));
			Imgproc.calcHist(bgr_plane, new MatOfInt(0), new Mat(), hist, histSize, ranges);
			bin_w = Math.round((double)(hist_w / 256));
			//归一化
			Core.normalize(hist, hist,0,histImage.rows(),Core.NORM_MINMAX);
			for(int i = 1; i<256;i++){
				if(j==0){
					Imgproc.line(
							histImage,
							new Point(bin_w *(i-1),hist_h-Math.round(hist.get(i-1,0)[0])),
							new Point(bin_w *(i),hist_h-Math.round(hist.get(i, 0)[0])),
							new Scalar(255,0,0),
							2,8,0);
				}
				else if(j==1){
					Imgproc.line(
							histImage,
							new Point(bin_w *(i-1),hist_h-Math.round(hist.get(i-1,0)[0])),
							new Point(bin_w *(i),hist_h-Math.round(hist.get(i, 0)[0])),
							new Scalar(0,255,0),
							2,8,0);
				}
				else if(j==2){
					Imgproc.line(
							histImage,
							new Point(bin_w *(i-1),hist_h-Math.round(hist.get(i-1,0)[0])),
							new Point(bin_w *(i),hist_h-Math.round(hist.get(i, 0)[0])),
							new Scalar(0,0,255),
							2,8,0);
				}
			}			
		}
		
		Imgcodecs.imwrite("d:/Temp/image/book7/histImage.jpg", histImage);
		return ranges; 	
	}
	
	

	public static void main(String[] args){

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//BackProject.backProject(150);
		
		Mat source2 = Imgcodecs.imread("D:\\Temp\\image\\book8\\mat_cvtColor.jpg");
		Mat source = Imgcodecs.imread("D:\\Temp\\data\\lena.jpg");
		//BackProject.getHistogram(source);
		Mat dst = BackProject.HorizonProjection(source2);
		//Mat dst = BackProject.matGO(source2);
		Imgcodecs.imwrite("D:\\Temp\\image\\book7\\dst.jpg",dst);
	}
}
