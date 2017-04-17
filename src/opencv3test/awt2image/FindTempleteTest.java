package opencv3test.awt2image;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

/**
 * 根据模板找到该模板在图片中的位置
 * 
 * @company 东航股份信息部物流产品部
 *
 * @description 
 *
 * @author Felix_Feng
 *
 * @date Mar 16, 2017
 *
 */
public class FindTempleteTest {
	
	public static void findTemplete(Mat src,Mat templete, int method){
		int result_cols = src.cols() - templete.cols() + 1 ;
		int result_rows =  src.rows() - templete.rows() + 1 ;
		Mat result = new Mat(result_rows,result_cols,CvType.CV_32FC1);
		Imgproc.matchTemplate(src, templete, result, method);		
		//规格化  0 255 表示图片像素的最大和最小
		Core.normalize(result, result,0,255,Core.NORM_MINMAX,-1,new Mat());
		Imgcodecs.imwrite("D:/Temp/image/book4/result2.jpg",result);	
		//寻找矩阵中最大值和最小值
		MinMaxLocResult mmx = Core.minMaxLoc(result);
		Point matchLoc;
		if(method == Imgproc.TM_SQDIFF || method == Imgproc.TM_CCOEFF_NORMED){		
			matchLoc = mmx.minLoc;
		}else {			
			matchLoc = mmx.maxLoc;
		}
		//在原图上标注
		Imgproc.rectangle(src, matchLoc, 
				new Point(matchLoc.x + templete.cols(), matchLoc.y + templete.rows()),
				new Scalar(0,255,0));
		
		Imgproc.rectangle(result, matchLoc, 
				new Point(matchLoc.x + templete.cols(), matchLoc.y + templete.rows()),
				new Scalar(0,255,0));
		
		Imgcodecs.imwrite("D:/Temp/image/book4/a.jpg",src);	
		
		
		Mat srcClone = src.clone();
		//获取ROI区域
		Mat clone = srcClone.submat((int)matchLoc.y , (int)(matchLoc.y+templete.rows())
				,(int)matchLoc.x , (int)(matchLoc.x + templete.cols()));
		
	}
	
	/**
	 * 对比两张图片的相似度
	 * @param H1
	 * @param H2
	 * @return
	 */
	public static double compareHist(Mat H1,Mat H2){
		Mat HSVsource = new Mat();
		H1.convertTo(HSVsource, CvType.CV_32F);
		double result0 = Imgproc.compareHist(HSVsource, HSVsource, Imgproc.CV_COMP_BHATTACHARYYA);
		System.out.println(result0);
		return result0;		
	}
	
	/**
	 * 图片清晰度 使用sobel算子进行梯度计算 ，也可以使用Laplacian
	 * 
	 * @return
	 */
	public static double clarity(){  
	    Mat imageSource = Imgcodecs.imread("D:\\Temp\\image\\20141105090756507.jpg");  
	    Mat imageGrey = new Mat();  	  
	    Imgproc.cvtColor(imageSource, imageGrey, Imgproc.COLOR_RGB2GRAY);  
	    Mat imageSobel = new Mat();  
	    Imgproc.Sobel(imageGrey, imageSobel, CvType.CV_16U, 1, 1);    
	    //图像的平均灰度  
	    double meanValue = (Core.mean(imageSobel).val)[0];  
		return meanValue;  
	 
	}  
	
	
	
	public static void main(String[] args){

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat im = Imgcodecs.imread("D:\\Temp\\image\\20141105090756507.jpg");
		Mat templete = Imgcodecs.imread("D:\\Temp\\image\\20141105090827051.jpg");
		//FindTempleteTest.findTemplete(im, templete, Imgproc.TM_SQDIFF);	
		
		//FindTempleteTest.compareHist(im,im);
		FindTempleteTest.clarity();
	}
}
