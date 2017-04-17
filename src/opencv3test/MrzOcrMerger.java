package opencv3test;

import java.util.ArrayList;
import java.util.List;

//字符单元兼并对象
public class MrzOcrMerger {
  //兼并的字符单元集合
  List<MrzOcrRect> areas;

  MrzOcrRect rctotal;

  private int htotal, wtotal;

  public MrzOcrMerger(){
      areas = new ArrayList<MrzOcrRect>();
      rctotal = new MrzOcrRect(); 
  }
  //获取平均高度
  public double getAvgHeight(){
      return (double)htotal / areas.size();
  }
  //获取平均宽度
  public double getAvgWidth(){
      return (double)wtotal / areas.size();
  }
  //获取兼并总区域宽度与高度的比例值
  public double getRatio(){
      return (double)rctotal.getWidth() / getAvgHeight();
  }
  //获取字符兼并内字符单元集合尾部元素，也可认为是获取可兼并连接点
  public MrzOcrRect getLastArea(){
      if(areas.size() > 0)
          return areas.get(areas.size() - 1);
      return null;
  }
  //兼并字符单元
  public void add(MrzOcrRect rect){
      areas.add(rect);
      htotal += rect.getHeight();
      wtotal += rect.getWidth();
      //在兼并时计算总区域大小
      if(areas.size() == 1){
          rctotal.setRect(rect);
      }else{
          rctotal.union(rect);
      }   
  }
}
