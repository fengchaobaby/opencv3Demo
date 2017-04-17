package opencv3test;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs; 
public class SplitPic {

	/**
	 * 切分图像 取中心部分 长宽各切分20%
	 * @param src
	 * @return
	 */
	public static Mat splitImage(Mat src){
		
		int cols = src.cols();
		int rows = src.rows();
		//创建一个矩形区域  起点像素X坐标 起点像素Y坐标 长度  宽度  注意 长宽位置要互换
		Rect rect =new Rect((int)(rows*0.2), (int)(cols*0.2),  (int)(cols*0.6),(int)(rows*0.6));
		Mat image_roi = new Mat(src,rect);
		return image_roi;
	}
	
	/**
	 * 平均切分图像4等份
	 * @param args
	 */
    public static void main(String[] args ){
    	
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("D:\\22.jpg");
   
        int m = 2;  
        int n = 2;

        int height = image.rows();  
        int width  = image.cols(); 
        System.out.println("height:"+height+" width:"+width);
        int ceil_height = height/m;  
        int ceil_width  = width/n;
        System.out.println("ceil_height:"+ceil_height+" ceil_width:"+ceil_width);

        String filename = "D:/cc_sub";

        for(int i = 0; i<m; i++ ) { 
            for(int j = 0; j<n; j++){    
                int a = j*ceil_width;
                int b = i*ceil_height;
                System.out.println(a+","+b+","+ceil_width+","+ceil_height);
                Rect rect = new Rect(j*ceil_width,i*ceil_height,ceil_width,ceil_height);  
                Mat roi_img = new Mat(image,rect); 
                //Mat tmp_img = new Mat();

                //roi_img.copyTo(tmp_img);

                Imgcodecs.imwrite(filename+i+"_"+j+".png", roi_img);
            }  
        }
    }
}