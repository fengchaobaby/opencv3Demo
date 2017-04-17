package opencv3test.awt2image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 
 * 颜色检测
 * @description 
 *
 * @author Felix_Feng
 *
 * @date Mar 16, 2017
 *
 */
public class ColorBlobDetector {

	private Scalar mBlobColorHsv = new Scalar(255);
	private Scalar mLowerBound = new Scalar(0);
	private Scalar mUpperBound = new Scalar(0);
	private double mMinContourArea = 0.1;
	private Scalar mColorRadius = new Scalar(25,50,50,0);
	private Mat mSpectrum = new Mat();
	private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
	Mat mPyrDownMat = new Mat();
	Mat mHsvMat = new Mat();
	Mat mMask = new Mat();
	Mat mDilatedMask = new Mat();
	Mat mHierarchy = new Mat();
	
	public void ColorBlobDetector(){
		
		Mat source = Imgcodecs.imread("D:\\Temp\\data\\aero3.jpg");
		Mat sourceHsv = new Mat();
		Imgproc.cvtColor(source, sourceHsv, Imgproc.COLOR_RGB2HSV_FULL);
		mBlobColorHsv = Core.sumElems(sourceHsv);
		int pointCount = source.rows() * source.cols();
		for(int i =0; i< mBlobColorHsv.val.length ; i++){
			mBlobColorHsv.val[i] /= pointCount;
		}	
		setHsvColor(mBlobColorHsv);
		process(source);
		List<MatOfPoint> contours = mContours;
		Imgproc.drawContours(sourceHsv, contours, -1, new Scalar(255,255,0));
		Imgcodecs.imwrite("D:/Temp/image/book4/source.jpg",source);	
		
	}
	
	public void setHsvColor(Scalar hsvColor){
		double minH = (hsvColor.val[0]>= mColorRadius.val[0])? hsvColor.val[0]-mColorRadius.val[0]:0;
		double maxH = (hsvColor.val[0]+ mColorRadius.val[0]<=255)? hsvColor.val[0]+mColorRadius.val[0]:255;
		mLowerBound.val[0]=minH;
		mUpperBound.val[0]=maxH;
		mLowerBound.val[1]=hsvColor.val[1]-mColorRadius.val[1];
		mUpperBound.val[1]=hsvColor.val[1]+mColorRadius.val[1];
		mLowerBound.val[2]=hsvColor.val[2]-mColorRadius.val[2];
		mUpperBound.val[2]=hsvColor.val[2]+mColorRadius.val[2];
		mLowerBound.val[3]=0;
		mUpperBound.val[3]=255;
		
		Mat spectrumHsv = new Mat(1,(int)(maxH-minH),CvType.CV_8UC3);
		for(int j=0;j<maxH-minH;j++){			
			byte[] tmp = {(byte)(minH+j),(byte)255,(byte)255};
			spectrumHsv.put(0,j,tmp);
		}
		Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL,4);
	}
	
	
	public void process(Mat rgbaImage){
		Imgproc.pyrDown(rgbaImage,mPyrDownMat);
		Imgproc.pyrDown(mPyrDownMat,mPyrDownMat);
		Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
		Core.inRange(mHsvMat , mLowerBound, mUpperBound, mMask);
		Imgproc.dilate(mMask, mDilatedMask, new Mat());
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(mDilatedMask,contours,mHierarchy,Imgproc.RETR_EXTERNAL,
				Imgproc.CHAIN_APPROX_SIMPLE);
		double maxArea = 0;
		Iterator<MatOfPoint> each = contours.iterator();
		while(each.hasNext()){
			MatOfPoint wrapper = each.next();
			double area = Imgproc.contourArea(wrapper);
			if(area>maxArea){
				maxArea = area;
			}
			
		}
		mContours.clear();
		each = contours.iterator();
		while(each.hasNext()){
			MatOfPoint contour = each.next();
			if(Imgproc.contourArea(contour)>mMinContourArea*maxArea){
				//矩阵元素乘法
				Core.multiply(contour, new Scalar(4,4,5), contour);
				mContours.add(contour);
				
			}
			
		}
		
		
	}
	
	/**
	 * 颜色查找 RGB方式
	 * @return
	 */
	public void findColor(int B,int G ,int R){
		
		Mat source2 = Imgcodecs.imread("D:/Temp/image/RGB.jpg");
		Imgproc.GaussianBlur(source2, source2, new Size(3,3),0,0);
		Mat hsvImg= new Mat();
		//利用HSV颜色模式来查找
		//Imgproc.cvtColor(source2, hsvImg, Imgproc.COLOR_BGR2HSV);
		Imgcodecs.imwrite("D:/Temp/image/book4/findColor_source2.jpg",source2);	
		Mat threshoded = new Mat();
		//根据图形的像素来查找,中间2个参数代表了查找像素的上限和下限
		Core.inRange(source2, new Scalar(254, 0, 0), new Scalar(254,254,254), threshoded);
		Imgcodecs.imwrite("D:/Temp/image/book4/findColor.jpg",threshoded);	
		
	}
	
	
	public void getImgPoint(){
		
		Mat kernel = Imgcodecs.imread("D:/Temp/image/RGB.jpg");
		for (int i = 0; i < kernel.rows(); i++) {
			for (int j = 0; j < 100; j++) {
				double tmp[] = kernel.get(i, j);
				//get出来就是像素的RGB
				System.out.println("--R----- G------B--");
				System.out.println(tmp[0] + " " + tmp[1] + " " + tmp[2]);
			}
		}		
	}
	
	public static void main(String[] args){

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ColorBlobDetector cl = new ColorBlobDetector();
		//cl.ColorBlobDetector();
		//cl.findColor(0, 0, 0);
		cl.getImgPoint();
	}

	
	
}
