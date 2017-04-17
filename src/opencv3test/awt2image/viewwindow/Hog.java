package opencv3test.awt2image.viewwindow;

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
import org.opencv.videoio.VideoCapture;

/**
 * 
 * HOG行人检测
 *
 * @description 
 *
 * @author Felix_Feng
 *
 * @date Apr 1, 2017
 *
 */
public class Hog {
	public static Mat detect(Mat inputframe){
		final Mat mat = inputframe;
		final HOGDescriptor hog= new HOGDescriptor();
		final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
		hog.setSVMDetector(descriptors);
		final MatOfRect foundLocations = new MatOfRect();
		final MatOfDouble foundWeights = new MatOfDouble();
		final Size padding = new Size(32,32);
		final Size winStride = new Size(8,8);
		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Point fontPoint = new Point();
		int frames = 0;
		int frameWithPeople = 0;
		
		final Scalar rectColor = new Scalar(0,255,0);
		final Scalar fontColor = new Scalar(255,255,255);
		hog.detectMultiScale(mat, foundLocations, foundWeights,0.0,winStride,padding,1.05,2.0,false);
		if(foundLocations.rows()>0){
			frameWithPeople++;
			List<Double> weightList = foundWeights.toList();
			List<Rect> rectList = foundLocations.toList();
			int i=0;
			for(Rect rect : rectList){
				rectPoint1.x = rect.x;
				rectPoint1.y = rect.y;
				rectPoint2.x = rect.x+rect.width;
				rectPoint2.y = rect.y+rect.height;
				Imgproc.rectangle(mat, rectPoint1, rectPoint2, rectColor ,2);
				fontPoint.x = rect.x;
				fontPoint.y = rect.y -4 ;
				
			}
			
		}
		
		return mat;
		
		
	}
	 
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);				
		//Hog.detect();
		
		
	}
	
	public static Image  mat2Image(Mat frame)
	{
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
}
