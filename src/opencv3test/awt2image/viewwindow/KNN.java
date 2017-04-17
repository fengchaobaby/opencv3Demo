package opencv3test.awt2image.viewwindow;

import opencv3test.Panel;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Mat;





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
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;



public class KNN {
	
	
	
	
	public  static Mat detect(Mat inputframe){
		BackgroundSubtractorKNN bg = Video.createBackgroundSubtractorKNN();
		Mat fgmask = new Mat();
		bg.apply(inputframe, fgmask,0.26);
		//Imgcodecs.imwrite("d:/Temp/image/book6/fgmask.jpg", fgmask);
		return fgmask;
	}
	
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat webcam_image = new Mat();
		Mat frame = new Mat();
		Mat back = new Mat();
		Mat fore = new Mat();
		
		JFrame frame1 = new JFrame("1");
		JFrame frame2 = new JFrame("2");
		VideoCapture capture = new VideoCapture(); 
		capture.open("D:\\Temp\\data\\tree.avi");  
		if(capture.isOpened()){
			
			frame1.setSize(webcam_image.width()  + 10,webcam_image.height() + 10);
			frame2.setSize(webcam_image.width()  + 40,webcam_image.height() + 60);
			Panel panel1 = new Panel();
			Panel panel2 = new Panel();
			while(true){
				capture.read(webcam_image);
				if(!webcam_image.empty()){
					Mat source = webcam_image.clone();
					webcam_image = detect(webcam_image);
					panel1.setimagewithMat(webcam_image);
					frame1.repaint();
					panel2.setimagewithMat(source);
					frame2.repaint();
				}else {
					capture.release();
					break;
				}
			}
		}
		
		
	}
	
	
}

