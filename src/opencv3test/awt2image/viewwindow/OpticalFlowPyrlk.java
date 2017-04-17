package opencv3test.awt2image.viewwindow;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Mat;

import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

/**
 * 光流检测 有异常
 *
 * @description 
 *
 * @author Felix_Feng
 *
 * @date Apr 6, 2017
 *
 */
public class OpticalFlowPyrlk {

	public Mat lucasKanade(Mat webcam_image) throws InterruptedException{
	
		MatOfPoint2f mMOP2fptsThis = new MatOfPoint2f();
		MatOfPoint2f mMOP2fptsPrev = new MatOfPoint2f();
		MatOfPoint2f mMOP2fptsSafe = new MatOfPoint2f();
		MatOfFloat mMOFerr = new MatOfFloat();
		MatOfByte mMOBStatus = new MatOfByte();
		Mat matOpflowThis = new Mat();
		Mat matOpflowPrev = new Mat();
		List<Point> pts,corners,cornersThis,cornersPrev;
		List<Byte> byteStatus;
		int x,y,iLineThickness =3;
		Point pt ,pt1,pt2;
		Scalar colorOpticalFolw =new Scalar(0,255,0);
		Mat mRgba = null;
		//Mat webcam_image = new Mat();
		//Mat frame2 = new Mat();
		//capture.open(0);
		//capture.read(webcam_image);
		
		//if(capture.isOpened()){
			//while(true){
				//Thread.sleep(2000);
				//capture.read(webcam_image);
				//if(!webcam_image.empty()){
					mRgba = webcam_image.clone();
					int qualityLevel = 40;
					MatOfPoint MOPcorners = new MatOfPoint();
					if(mMOP2fptsPrev.rows()==0){
						Imgproc.cvtColor(mRgba, matOpflowThis, Imgproc.COLOR_RGB2GRAY);
						matOpflowThis.copyTo(matOpflowPrev);
						Imgproc.goodFeaturesToTrack(matOpflowPrev, MOPcorners, 
								qualityLevel, 0.05, 20);
						mMOP2fptsPrev.fromArray(MOPcorners.toArray());
						mMOP2fptsPrev.copyTo(mMOP2fptsSafe);
					}else {
						matOpflowThis.copyTo(matOpflowPrev);
						Imgproc.cvtColor(mRgba, matOpflowThis, Imgproc.COLOR_RGB2GRAY);
						Imgproc.goodFeaturesToTrack(matOpflowThis, MOPcorners, 
								qualityLevel, 0.05, 20);
						mMOP2fptsThis.fromArray(MOPcorners.toArray());
						mMOP2fptsSafe.copyTo(mMOP2fptsPrev);
						mMOP2fptsThis.copyTo(mMOP2fptsSafe);
					}
					Video.calcOpticalFlowPyrLK(matOpflowPrev, matOpflowThis, mMOP2fptsPrev, mMOP2fptsThis, mMOBStatus, mMOFerr);
					cornersPrev = mMOP2fptsPrev.toList();
					cornersThis = mMOP2fptsThis.toList();
					byteStatus = mMOBStatus.toList();
					y = byteStatus.size() - 1;
					for(x = 0;x<y;x++){
						pt = cornersThis.get(x);
						pt2 = cornersPrev.get(x);
						Imgproc.circle(mRgba, pt, 5, colorOpticalFolw,iLineThickness -1 );
						Imgproc.line(mRgba, pt,pt2,colorOpticalFolw,iLineThickness);
					}
				//}
				
			//}
		//}
		 Imgcodecs.imwrite("d:/Temp/image/book6/mRgba.jpg", mRgba);
		return mRgba;
		
	}
	
	
}
