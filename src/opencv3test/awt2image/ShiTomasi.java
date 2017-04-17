package opencv3test.awt2image;

import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 
 * ShiTomasi 角点检测 图像特征点检测
 *
 * @description 
 *
 * @author Felix_Feng
 *
 * @date Apr 1, 2017
 *
 */
public class ShiTomasi {
	
	private static String filePath = "D:\\Temp\\data\\lena.jpg";
	
	
		/*"FAST" – FastFeatureDetector
		"STAR" – StarFeatureDetector
		"SIFT" – SIFT (nonfree module)
		"SURF" – SURF (nonfree module)
		"ORB" – ORB
		"MSER" – MSER
		"GFTT" – GoodFeaturesToTrackDetector
		"HARRIS" – GoodFeaturesToTrackDetector with Harris detector enabled
		"Dense" – DenseFeatureDetector
		"SimpleBlob" – SimpleBlobDetector
		图片中的特征大体可分为三种：点特征、线特征、块特征。*/
	
	public static void featureDetector(int detectorType){
		
		Mat source = Imgcodecs.imread(filePath,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat dst = new Mat();
		MatOfKeyPoint keyPoints = new MatOfKeyPoint();
		FeatureDetector detector = FeatureDetector.create(detectorType);
		detector.detect(source, keyPoints);
		Features2d.drawKeypoints(source, keyPoints, dst);
		Imgcodecs.imwrite("d:/Temp/image/book6/featureDetector"+detectorType+".jpg", dst);
	}
	
	public static Mat featureDetector2D(int Extractor,int Matcher){
		String object_filename = "D:\\Temp\\data\\stuff-b.jpg";
		String scene_filename = "D:\\Temp\\data\\stuff.jpg";
		Mat img_object = Imgcodecs.imread(object_filename,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat img_scene = Imgcodecs.imread(scene_filename,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		FeatureDetector detector = FeatureDetector.create(5); //4=SURF
		MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
		MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();
		//角点
		detector.detect(img_object, keypoints_object);
		detector.detect(img_scene, keypoints_scene);
		DescriptorExtractor extractor = DescriptorExtractor.create(Extractor);
		Mat desc_object = new Mat();
		Mat desc_scene = new Mat();
		//提取特征描述子
		extractor.compute(img_object, keypoints_object, desc_object);
		extractor.compute(img_scene, keypoints_scene, desc_scene);
		DescriptorMatcher matcher = DescriptorMatcher.create(Matcher);
		MatOfDMatch matches = new MatOfDMatch();
		//声明所要用到的matcher
		matcher.match(desc_object,desc_scene, matches);
		List<DMatch> matchesList = matches.toList();
		Double max_dist = 0.0;
		Double min_dist =100.0;
		//计算关键点之间的最大及最小距离
		for(int i=0;i<matchesList.size();i++){		
			double dist = (double) matchesList.get(i).distance;
			if(dist< min_dist)min_dist=dist;
			if(dist> max_dist)max_dist=dist;
		}
		System.out.println(max_dist + " " + min_dist);
		LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
		MatOfDMatch gm = new MatOfDMatch();
		//计算有配对成功的关键点 且距离小于3*min_dist
		for(int i=0;i<matchesList.size();i++){
			if(matchesList.get(i).distance<(3*min_dist)){
				good_matches.addLast(matchesList.get(i));
			}		
		}
		gm.fromList(good_matches);
		Mat img_matches = new Mat();
		Features2d.drawMatches(img_object, keypoints_object, img_scene, keypoints_scene,
				gm, img_matches, new Scalar(0,0,255), new Scalar(255,0,0), new MatOfByte(), 2);
		for(int k=0;k<good_matches.size();k++){
			System.out.println(k + "  "+ good_matches.get(k).queryIdx+ "   "+good_matches.get(k).trainIdx
					+ "   "+good_matches.get(k).distance);
		}
		Imgcodecs.imwrite("d:/Temp/image/book6/featureDetector2D.jpg", img_matches);
		return img_matches;
	}
	
	
	public static void shiTomasi(int maxCorners,double qualityLevel,double minDistance){		
		Mat source = Imgcodecs.imread(filePath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
		Mat dst = source.clone();
		Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2GRAY);  
		MatOfPoint corners = new MatOfPoint();
		Imgproc.goodFeaturesToTrack(dst, corners, maxCorners, 
				qualityLevel, minDistance);
		MatOfPoint2f Pts = new MatOfPoint2f();
		corners.convertTo(Pts, CvType.CV_32FC2);
		Point allPoint[] = Pts.toArray();
		for(Point px : allPoint){
			Imgproc.circle(source, px, 6, new Scalar(0,0,255));			
		}
		Imgcodecs.imwrite("d:/Temp/image/book6/shiTomasi.jpg", source);
		
		Size winSize = new Size(5,5);
		Size zeroZone = new Size(-1,-1);
		TermCriteria  term = new TermCriteria();
		term.type = TermCriteria.COUNT;
		term.maxCount = 1;
		MatOfPoint2f cor2f = new MatOfPoint2f();
		cor2f.fromList(corners.toList());
		Imgproc.cornerSubPix(dst, cor2f, winSize, zeroZone, term);
		cor2f.convertTo(Pts, CvType.CV_32FC2);
		allPoint = Pts.toArray();
		int count =1;
		for(Point px : allPoint){			
			System.out.println(count +"    "+px.x + "    "+px.y);
			Imgproc.circle(source, px, 6, new Scalar(0,255,255));	
			count++;
		}
		Imgcodecs.imwrite("d:/Temp/image/book6/shiTomasi2.jpg", source);
				
	}
	
	public static void harris(int blockSize,int ksize,double k){
		Mat source = Imgcodecs.imread(filePath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
		Mat dst = source.clone();
		Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2GRAY); 
		Imgproc.Canny(dst, dst, 200, 200);
		Imgproc.cornerHarris(dst, dst, blockSize, ksize, k,1);
		Core.normalize(dst, dst,0,255,Core.NORM_MINMAX,CvType.CV_32FC1,new Mat());
		Core.convertScaleAbs(dst, dst);
		int thresh = 190;
		for(int y=0;y<dst.height();y++){
			for(int x=0;x<dst.width();x++){
				dst.get(x, y);
				if(dst.get(x, y)[0]>thresh){
					Imgproc.circle(source, new Point(x,y) ,6 , new Scalar(0,0,255),1,8,0);
				}
				
			}
		}
		Imgcodecs.imwrite("d:/Temp/image/book6/harris.jpg", source);	
	}
	
	
	
	public static void main(String args[] ){
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		/*ShiTomasi.shiTomasi(26,0.02,15);
		ShiTomasi.harris(3, 3, 0.03);
		ShiTomasi.featureDetector(1);
		ShiTomasi.featureDetector(4);*/
		ShiTomasi.featureDetector2D(5,5);
		//Imgcodecs.imwrite("D:/444.jpg", imageToShow);
		//Mat source = Imgcodecs.imread("D:\\Temp\\image\\123.jpg");
		//ContourDetection.equalizeHist(source);
	}	
	
}
