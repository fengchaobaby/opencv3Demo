package opencv3test.awt2image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OCR {


    private final String LANG_OPTION = "-l";


    private final String EOL = System.getProperty("line.separator");


    //private String tessPath = new File("tesseract").getAbsolutePath();


    private String tessPath="D:\\Program Files (x86)\\Tesseract-OCR";


    public String recognizeText(File imageFile, String imageFormat) throws Exception {


       File tempImage = ImageIOHelper.createImage(imageFile, imageFormat);


       File outputFile = new File(imageFile.getParentFile(), "output");


       StringBuffer strB = new StringBuffer();


       List cmd = new ArrayList();


          cmd.add(tessPath + "//tesseract");

      
           cmd.add("");  
            cmd.add(outputFile.getName());  
            cmd.add(LANG_OPTION);  
            cmd.add("chi_sim");
            cmd.add("eng");  
       ProcessBuilder pb = new ProcessBuilder();
       pb.directory(imageFile.getParentFile());
       cmd.set(1, tempImage.getName());
       pb.command(cmd);
       pb.redirectErrorStream(true);
       Process process = pb.start();
       //tesseract.exe 1.jpg 1 -l chi_sim
       int w = process.waitFor();
       // delete temp working files
       tempImage.delete();
       if (w == 0) {
           BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile
                  .getAbsolutePath()
                  + ".txt"), "UTF-8"));
           String str;
           while ((str = in.readLine()) != null) {
              strB.append(str).append(EOL);
           }
           in.close();
       } else {
           String msg;
           switch (w) {
           case 1:
              msg = "Errors accessing files. There may be spaces in your image's filename.";
              break;
           case 29:
              msg = "Cannot recognize the image or its selected region.";
              break;
           case 31:
              msg = "Unsupported image format.";
              break;
           default:
              msg = "Errors occurred.";
           }
           tempImage.delete();
           throw new RuntimeException(msg);
           
       }
       new File(outputFile.getAbsolutePath() + ".txt").delete();
       System.out.println(strB.toString());
       return strB.toString();
    }
    
    public static void main(String[] args) {  
        /*String path = "D:\\Temp\\image\\book\\3.jpg";       
        System.out.println("ORC Test Begin......");  
        try {       
            String valCode = new OCR().recognizeText(new File(path), "jpg");       
            System.out.println(valCode);       
        } catch (IOException e) {       
            e.printStackTrace();       
        } catch (Exception e) {    
            e.printStackTrace();    
        }         
        System.out.println("ORC Test End......");  */
    	
    	
    	StringBuffer strB = new StringBuffer();
    	System.out.println(strB.toString());
    }    
  


}