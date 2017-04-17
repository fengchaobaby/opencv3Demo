package opencv3test.awt2image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.photo.Photo;

public class Denoising {
	
	public static void main(String[] args){

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat im = Imgcodecs.imread("D:\\Temp\\image\\book\\2.jpg");
		Photo.fastNlMeansDenoising(im, im);
		Imgcodecs.imwrite("D:\\Temp\\image\\user3_result.jpg", im);			
	}
}
