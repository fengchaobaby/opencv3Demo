package opencv3test;

//自定义的矩形对象
public class MrzOcrRect {

  int left, top, right, bottom;

  public MrzOcrRect(){
  }

  public MrzOcrRect(int left, int top, int right, int bottom){
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
  }

  public void setRect(int left, int top, int right, int bottom){
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;       
  }

  public void setRect(final MrzOcrRect rect){
      left = rect.left;
      top = rect.top;
      right = rect.right;
      bottom = rect.bottom;
  }
  //在对输入的矩形进行边缘修正后（正数为收缩，负数为扩展），计算两个矩形是否存在交集
  //rh为横向修正量，rv为纵向修正量
  public boolean isIntersect(final MrzOcrRect rect, int rh, int rv){
      if(left > rect.right - rh) return false;
      if(right < rect.left + rh) return false;
      if(top > rect.bottom - rv) return false;
      if(bottom < rect.top + rv) return false;
      return true;
  }
  //合并矩形
  public void union(final MrzOcrRect rect){
      if(left > rect.left) left = rect.left;
      if(top > rect.top) top = rect.top;              
      if(right < rect.right) right = rect.right;
      if(bottom < rect.bottom) bottom = rect.bottom;          
  }
  //重载的区域交集运算方法1
  public boolean isIntersect(final MrzOcrRect rect, int rh){
      return isIntersect(rect, rh, 0);
  }
  //重载的区域交集运算方法1
  public boolean isIntersect(final MrzOcrRect rect){
      //•设定横向修正值为3是为了忽略边缘重叠的情况•
      return isIntersect(rect, 3, 0);
  }
  //克隆矩形对象
  public MrzOcrRect clone(){
      return new MrzOcrRect(left, top, right, bottom);
  }

  public int getHeight(){
      return bottom - top;
  }

  public int getWidth(){
      return right - left;
  }
  //获取横向中心点
  public double getHCenter(){
      return (left + right) * 0.5;
  }
  //获取纵向中心点
  public double getVCenter(){
      return (top + bottom) * 0.5;
  }
}
