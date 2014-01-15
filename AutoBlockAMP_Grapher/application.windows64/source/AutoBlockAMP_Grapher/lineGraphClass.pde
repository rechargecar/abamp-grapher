/*
 * ----------------------------------
 *  lineGraph Class for Processing 2.0
 * ----------------------------------
 *
 * this is a simple knob class. The following shows 
 * you how to use it in a minimalistic way.
 *
 * DEPENDENCIES:
 *   N/A
 *
 * Created:  April, 07 2012
 * Author:   Alejandro Dirgan
 * Version:  0.5
 *
 * License:  GPLv3
 *   (http://www.fsf.org/licensing/)
 *
 * Follow Us
 *    adirgan.blogspot.com
 *    twitter: @ydirgan
 *    https://www.facebook.com/groups/mmiiccrrooss/
 *    https://plus.google.com/b/111940495387297822358/
 *
 * DISCLAIMER **
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS," AND WE MAKE NO EXPRESS OR IMPLIED WARRANTIES WHATSOEVER 
 * WITH RESPECT TO ITS FUNCTIONALITY, OPERABILITY, OR USE, INCLUDING, WITHOUT LIMITATION, ANY IMPLIED 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR INFRINGEMENT. WE EXPRESSLY 
 * DISCLAIM ANY LIABILITY WHATSOEVER FOR ANY DIRECT, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR SPECIAL 
 * DAMAGES, INCLUDING, WITHOUT LIMITATION, LOST REVENUES, LOST PROFITS, LOSSES RESULTING FROM BUSINESS 
 * INTERRUPTION OR LOSS OF DATA, REGARDLESS OF THE FORM OF ACTION OR LEGAL THEORY UNDER WHICH THE LIABILITY 
 * MAY BE ASSERTED, EVEN IF ADVISED OF THE POSSIBILITY OR LIKELIHOOD OF SUCH DAMAGES.
 */
 
 
 class pointDef
{
  float x;
  String xs;
  float y;
  boolean maxIam;
  boolean minIam;
  
  /////////////////////////////////////////////////////////
  pointDef()
  {
  }
  /////////////////////////////////////////////////////////
  pointDef(float yy)
  {
    y=yy;
  }
  /////////////////////////////////////////////////////////
  pointDef(float yy, float xx)
  {
    x=xx;
    y=yy;
  }
  /////////////////////////////////////////////////////////
  pointDef(float yy, String xx)
  {
    xs=xx;
    y=yy;
  }
  /////////////////////////////////////////////////////////
  float getY()
  {
    return y;
  }
  /////////////////////////////////////////////////////////
  float getX()
  {
    return x;
  }
  /////////////////////////////////////////////////////////
  String getXs()
  {
    return xs;
  }
  /////////////////////////////////////////////////////////
  void setMax()
  {
    maxIam=true;
    minIam=false;
  }
  /////////////////////////////////////////////////////////
  void setMin()
  {
    maxIam=true;
    minIam=false;
  }
  
}

class linePoint
{
  
  ArrayList linePoint = new ArrayList();
  pointDef ppoint;
  
  int id;
  
  color[] colorPointArray = {#FF8D00,#339045,#E04B50,#DE1FCB,#BC9B52,#3EA587,#961BBC,#1B9DBC,#46413C,#1BBC36,#BCAF1B,#BC871B,#BC361B,#46413C,#C9E37C};
  
  public color pointColor=#383FBF;
  public int pointTransparency=150;
  public int pointRadius=4;
  public boolean visiblePoint=true;
  
  public color lineColor=#383FBF;
  public int lineTransparency=200;
  public int lineSize=3;
  public boolean visibleLine=true;
  
  public color colorUnderCurve=#339045;
  public int underCurveTransparency=50;
  public boolean visibleUnderCurve=true;  
  
  public String legend;
  
  boolean visible=true;
  
  boolean empty=true;
  
  boolean sType=false;

  linePoint(int i,String textID)
  {
    id = i;
    
    pointColor=colorPointArray[id];
    lineColor=colorPointArray[id];
    colorUnderCurve=colorPointArray[id];
    
    legend=textID;
  }  
  /////////////////////////////////////////////////////////
  linePoint(int i,String textID, int pr, int lw, int lt)
  {
    id = i;
    
    pointColor=colorPointArray[id];
    lineColor=colorPointArray[id];
    colorUnderCurve=colorPointArray[id];
    
    legend=textID;
    
    pointRadius=pr;
    lineSize=lw;
    underCurveTransparency=lt;
    
  }  

  /////////////////////////////////////////////////////////
  int addPoint(float yValue)
  {
    linePoint.add(new pointDef(yValue));
    empty=false;
    return linePoint.size();
  }
  /////////////////////////////////////////////////////////
  int addPoint(float yValue, float xValue)
  {
    if (sType && linePoint.size()>0) 
    {
      println("ERROR: the dataSet was initialized using Strings as X values");
      return -1;
    }
    sType=false;
    linePoint.add(new pointDef(yValue,xValue));
    return linePoint.size();
  }
  /////////////////////////////////////////////////////////
  int addPoint(float yValue, String xValue)
  {
    if (!sType && linePoint.size()>0) 
    {
      println("ERROR: the dataSet was initialized using Strings as X values");
      return -1;
    }
    sType=true;
    linePoint.add(new pointDef(yValue,xValue));
    return linePoint.size();
  }
  /////////////////////////////////////////////////////////
  int removePoint(int p)
  {
    if (p>linePoint.size()-1 || p<0) 
    {
      println("element "+str(p)+" of line linePoint not FOUND!");
      return -1;
    }
    linePoint.remove(p);
    
    if (linePoint.size()==0) empty=true;
    
    return linePoint.size();    
  }
  /////////////////////////////////////////////////////////
  float getYPoint(int p)
  {
    if (p>linePoint.size()-1 || p<0) 
    {
      println("element "+str(p)+" of line linePoint not FOUND!");
      return -1;
    }
    ppoint = (pointDef )linePoint.get(p);
    
    return ppoint.getY();    
    
  }
  /////////////////////////////////////////////////////////
  float getXPoint(int p)
  {
    if (p>linePoint.size()-1 || p<0) 
    {
      println("element "+str(p)+" of line linePoint not FOUND!");
      return -1;
    }
    ppoint = (pointDef )linePoint.get(p);
    
    return ppoint.getX();    
  }  
  /////////////////////////////////////////////////////////
  String getXsPoint(int p)
  {
    if (p>linePoint.size()-1 || p<0) 
    {
      println("element "+str(p)+" of line linePoint not FOUND!");
      return "-1";
    }
    ppoint = (pointDef )linePoint.get(p);
    
    return ppoint.getXs();    
  }  
  /////////////////////////////////////////////////////////
  int getSize()
  {
    return linePoint.size();    
  }
  /////////////////////////////////////////////////////////
  void hide()
  {
    visible=false;    
  }
  /////////////////////////////////////////////////////////
  void unHide()
  {
    visible=true;    
  }
  /////////////////////////////////////////////////////////
  boolean visible()
  {
    return visible;    
  }

}

class ADLinechart
{
  
  int lX, lY, lW, lH;
  int lvx, lvy;
  int xp=10,yp=0;
  int xOffset=10, yOffset=0;
  int viewPortW, viewPortH;
  
  ArrayList dataSetList;
  linePoint lpoint;
  
  color shadowColor=#8E8787;
  boolean shadow=true;
  
  int lBorderWidth=2;
  color lColorBorder=0;
  color lColorFill=#FFFFFF;
  
  int lInternalBorderWidth=2;
  color lInternalColorBorder=#E3E3E3;
  color lInternalColorFill=#FFFFFF;
  
  color ColorXLabel=#262A71; 
  int xLabelOffset=10;
  int xLabelSize=16;
  String xLabelText="X Label";
  boolean xLabelVisible=false;

  color ColorYLabel=#262A71; 
  int yLabelOffset=15;
  int yLabelSize=16;
  String yLabelText="Y Label";
  boolean yLabelVisible=false;

  color ColorTitle=#262A71; 
  int titleOffset=40;
  int titleSize=24;
  String titleText="Title of the GRAPH";
  boolean titleVisible=false;

  color ColorVerticalDiv=#E3E3E3; 
  int VerticalDivWidth=1;
  color ColorHorizontalDiv=#E3E3E3; 
  int HorizontalDivWidth=1;
  
  color ColorPoint=#2A9035;  
  
  int labelXX, labelXY;

  int lVlines = 10;
  int lHlines=4;
  float lVerticalSpace=0;
  float lHorizontalSpace=0;
  
  int lNsamples=20;
  
  float lMin=100000000, lMax=-1000000000;
  
  int maxViewport;
  

  color yNumbersColor=#262A71; 
  int yNumbersOffset=5;
  int yNumbersSize=8;
  boolean yNumbersVisible=true;
  
  color xNumbersColor=#262A71; 
  int xNumbersOffset=5;
  int xNumbersSize=8;
  float xNumbersAngle=PI/2;
  boolean xNumbersVisible=true;
  int xNumbersSpacing=20;
  
  float delta;
  
  int legendSize=10;
  int legendXoffset=20;
  int legendYoffset=20;  
  boolean legendVisible=false;
  
  int pRadius=4, lWidth=3, lTransp=50;
  
  boolean empty=true;
  
  boolean debug=false;
  boolean pressOnlyOnce=true;
  int deb=0;
  
  /////////////////////////////////////////////////////////
  ADLinechart(int x, int y, int w, int h, String textID)
  {
    lX=x; lY=y; lW=w; lH=h;

    if (!legendVisible) 
      xOffset=0;
    else
      xOffset=10;   
    
    if ((!legendVisible) || (!yLabelVisible)) xp=15;
    if ((legendVisible) || (yLabelVisible)) xp=30;   
    if ((!titleVisible) || (!xLabelVisible)) yp=15;
    if ((titleVisible) || (xLabelVisible)) yp=30;
    updateViewPoint();
    arrayInit(textID);  
        
    xNumbersSpacing=lNsamples/5;        
  }
  /////////////////////////////////////////////////////////
  void setParameters(boolean vTitle, String title, int tSize, int tOffset, boolean xlVisible, String xlText, int xlSize, int xlOffset, int xnSpacing, boolean ylVisible, String ylText, int ylSize, int ylOffset, boolean lgVisible, int lgSize, int xlgOffset, int ylgOffset, boolean xnVisible, boolean ynVisible, boolean sh, int gx, int gy, int xOf, int yOf, int xM, int yM, int w, int h, int samples, int pr, int lw, int lt)
  {
    titleOffset=tOffset;
    titleSize=tSize;
    titleText=title;
    titleVisible=vTitle;

    xLabelVisible=xlVisible;
    xLabelText=xlText;
    xLabelSize=xlSize;
    xLabelOffset=xlOffset;
    xNumbersSpacing=xnSpacing;

    yLabelVisible=ylVisible;
    yLabelText=ylText;
    yLabelSize=ylSize;
    yLabelOffset=ylOffset;
    
    legendSize=lgSize;
    legendXoffset=xlgOffset;
    legendYoffset=ylgOffset;  
    legendVisible=lgVisible;
    
    yNumbersVisible=ynVisible;
    xNumbersVisible=xnVisible;
    shadow=sh;
    
    xp=gx;
    yp=gy;

    xOffset=xOf;    
    yOffset=yOf;

    lVlines=yM;
    lHlines=xM;
    
    lW=w;
    lH=h;
    
    lNsamples=samples;
 
    pRadius=pr;
    lWidth=lw;
    lTransp=lt;

    for (int i=0; i<getDataSets(); i++)
    {
      lineGraph.setPointRadius(pRadius,i);
      lineGraph.setLineWidth(lWidth,i);
      lineGraph.setUnderCurveTransparecy((int )lTransp,i);
    }
    
    updateViewPoint(); 
    
    
  }  
  /////////////////////////////////////////////////////////
  void setColors(color tc, color xlc, color ylc, color xnc, color ync, color ba, color gc)
  {
      setTitleColor(tc);
      setXlabelColor(xlc);
      setYlabelColor(ylc);
      setXNumbersColor(xnc);
      setYNumbersColor(ync);
      setBackgroudColor(ba);
      setGridColor(gc);
  }
  /////////////////////////////////////////////////////////
  void updateCoord()
  {
    if (!legendVisible) 
     xOffset=0;
    else
     xOffset=10;   
    
    if ((!legendVisible) || (!yLabelVisible)) xp=15;
    if ((legendVisible) || (yLabelVisible)) xp=100;   
    if ((!titleVisible) || (!xLabelVisible)) yp=15;
    if ((titleVisible) || (xLabelVisible)) yp=60;
  }
  ////////////////////////////////////////////////////////////////////////
  void updateViewPoint()
  {
    lvx=lX+xp;
    lvy=lY+yp;

    viewPortW=lW-2*xp;
    viewPortH=lH-2*yp;
    
    lVerticalSpace=(float )viewPortW/lVlines;
    lHorizontalSpace=(float )viewPortH/lHlines;
    
    maxViewport=lvy+viewPortH;
    
    delta = (float )viewPortW/lNsamples;

    
  }
  /////////////////////////////////////////////////////////
  void showShadow()
  {
    shadow=true;
  }
  /////////////////////////////////////////////////////////
  void hideShadow()
  {
    shadow=false;
  }
  /////////////////////////////////////////////////////////
  color getTitleColor()
  {
    return ColorTitle;
  }
  /////////////////////////////////////////////////////////
  void setTitleColor(color c)
  {
    ColorTitle=c;
  }
  /////////////////////////////////////////////////////////
  color getXlabelColor()
  {
    return ColorXLabel;
  }
  /////////////////////////////////////////////////////////
  void setXlabelColor(color c)
  {
    ColorXLabel=c;
  }
  /////////////////////////////////////////////////////////
  color getYlabelColor()
  {
    return ColorYLabel;
  }
  /////////////////////////////////////////////////////////
  void setYlabelColor(color c)
  {
    ColorYLabel=c;
  }
  /////////////////////////////////////////////////////////
  void setXNumbersColor(color c)
  {
    xNumbersColor=c;
  }
  /////////////////////////////////////////////////////////
  color getXNumbersColor()
  {
    return xNumbersColor;
  }
  /////////////////////////////////////////////////////////
  void setYNumbersColor(color c)
  {
    yNumbersColor=c;
  }
  /////////////////////////////////////////////////////////
  color getYNumbersColor()
  {
    return yNumbersColor;
  }
  /////////////////////////////////////////////////////////
  void setBackgroudColor(color c)
  {
    lColorFill=lInternalColorFill=c;
  }
  /////////////////////////////////////////////////////////
  color getBackgroudColor()
  {
    return lColorFill;
  }
  /////////////////////////////////////////////////////////
  void setGridColor(color c)
  {
    ColorVerticalDiv=ColorHorizontalDiv=lInternalColorBorder=c;
  }
  /////////////////////////////////////////////////////////
  color getGridColor()
  {
    return ColorVerticalDiv;
  }
  
  /////////////////////////////////////////////////////////
  void setPointRadius(float r, int ds)
  {
    if (ds > (dataSetList.size()-1) || ds<0)
    {
      println("data set "+str(ds)+" not found!");
      return;
    }   
      
    linePoint currentDataSet=(linePoint )dataSetList.get(ds);

    currentDataSet.pointRadius=(int )r;
    
    pRadius=(int )r;
    
  }
  /////////////////////////////////////////////////////////
  void setLineWidth(float r, int ds)
  {
    if (ds > (dataSetList.size()-1) || ds<0)
    {
      println("data set "+str(ds)+" not found!");
      return;
    }   
      
    linePoint currentDataSet=(linePoint )dataSetList.get(ds);

    currentDataSet.lineSize=(int )r;
    
    lWidth=(int )r;
    
  }
  /////////////////////////////////////////////////////////
  void setUnderCurveTransparecy(int t, int ds)
  {
    if (ds > (dataSetList.size()-1) || ds<0)
    {
      println("data set "+str(ds)+" not found!");
      return;
    }   
      
    linePoint currentDataSet=(linePoint )dataSetList.get(ds);

    currentDataSet.underCurveTransparency=t;
    
    lTransp=t;
 
  }
   /////////////////////////////////////////////////////////
  void setXlabelFrecuency(int f)
  {
    xNumbersSpacing=f;
  }
  /////////////////////////////////////////////////////////
  void setYgridMarks(int m)
  {
    lHlines=m;
    lHorizontalSpace=(float )viewPortH/lHlines;
  }
  /////////////////////////////////////////////////////////
  void setXgridMarks(int m)
  {
    lVlines=m;
    lVerticalSpace=(float )viewPortW/lVlines;
  }
  /////////////////////////////////////////////////////////
  void setGraphWidth(int w)
  {
    lW=w;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  void setGraphHeigth(int h)
  {
    lH=h;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  void setXGridSize(int s)
  {
    xp=s;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  void setYGridSize(int s)
  {
    yp=s;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  void setXGridOffset(int s)
  {
    xOffset=s;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  void setYGridOffset(int s)
  {
    yOffset=s;
    updateViewPoint();
  }
  ////////////////////////////////////////////////////////////////////////
  float round2nDecimals(float number, float decimal) {
      return (float)(round((number*pow(10, decimal))))/pow(10, decimal);
  }   
  /////////////////////////////////////////////////////////
  void setTitle(String title)
  {
    titleText=title;
    titleVisible=true;
    
    updateCoord();
    updateViewPoint();
 
  }
  /////////////////////////////////////////////////////////
  void showTitle()
  {
    titleVisible=true;

    updateCoord();
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  void hideTitle()
  {
    titleVisible=false;

    updateCoord();
    updateViewPoint();

  }
  /////////////////////////////////////////////////////////
  void setYLabel(String yLabel)
  {
    yLabelText=yLabel;
    yLabelVisible=true;
    updateCoord();
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  void setXLabel(String xLabel)
  {
    xLabelText=xLabel;
    xLabelVisible=true;
    updateCoord();
    updateViewPoint();
   
  }
  /////////////////////////////////////////////////////////
  void showLegend()
  {
    legendVisible=true;
    xOffset=10;
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  void hideLegend()
  {
    legendVisible=false;
    xOffset=0;
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  void showYlabel()
  {
    yLabelVisible=true;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  void hideYlabel()
  {
    yLabelVisible=false;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  void showXlabel()
  {
    xLabelVisible=true;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  void hideXlabel()
  {
    xLabelVisible=false;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  void arrayInit(String textID)
  {
    dataSetList = new ArrayList();
    dataSetList.add(new linePoint(dataSetList.size(), textID, pRadius, lWidth, lTransp));
  }
  /////////////////////////////////////////////////////////
  int addDataSet(String textID)
  {
    dataSetList.add(new linePoint(dataSetList.size(),textID,pRadius,lWidth,lTransp));
    return dataSetList.size();
  }
  /////////////////////////////////////////////////////////
  int getDataSets()
  {
    return dataSetList.size();
  }
  /////////////////////////////////////////////////////////
  void setMinMax(float value)
  {
    if (value>lMax) lMax=value;
    if (value<lMin) lMin=value;
  }  
  /////////////////////////////////////////////////////////
  void setSamples(int samples)
  {
 
    linePoint currentDataSet;
    
    for (int ds=0; ds<dataSetList.size(); ds++)
    {
      
      currentDataSet=(linePoint )dataSetList.get(ds);

      while (currentDataSet.getSize() > lNsamples+1)
      {
        currentDataSet.removePoint(0);
      }
    
    }    
    lNsamples=samples;
    delta = (float )viewPortW/lNsamples;
    xNumbersSpacing=lNsamples/5;
    
  }
  /////////////////////////////////////////////////////////
  void setGrid(int vDiv, int hDiv)
  {
    lVlines = vDiv;
    lHlines=hDiv;
    lVerticalSpace=(float )viewPortW/lVlines;
    lHorizontalSpace=(float )viewPortH/lHlines;
  }
  /////////////////////////////////////////////////////////
  void drawFrame()
  {
    stroke(lColorBorder);
    strokeWeight(lBorderWidth);
    fill(lColorFill);
    
    rect(lX,lY,lW,lH);

    stroke(lInternalColorBorder);
    strokeWeight(lInternalBorderWidth);
    fill(lInternalColorFill);
    rect(lvx-xOffset,lvy-yOffset,lW-xp*2,lH-yp*2);
    
  }
///////////////////////////////////////////////////////  
  void deBounce(int n)
  {
    if (pressOnlyOnce) 
      return;
    else
      
    if (deb++ > n) 
    {
      deb=0;
      pressOnlyOnce=true;
    }
    
  }  
  
  /////////////////////////////////////////////////////////
  boolean mouseOver()
  {
    boolean result=false; 
    
    if (debug)
      if ((mouseX>=lX) && (mouseX<=lX+lW) && (mouseY>=lY) && (mouseY<=lY+lH))
      {
        if (mousePressed && mouseButton==LEFT && keyPressed)
        {
          if (keyCode==CONTROL)
          {
            lX=lX+(int )((float )(mouseX-pmouseX)*1);
            lY=lY+(int )((float )(mouseY-pmouseY)*1);
            updateViewPoint();
            println("Moving...");
          }
          if (keyCode==SHIFT && pressOnlyOnce) 
          {
            printGeometry();
            pressOnlyOnce=false;
          }
          deBounce(5);
          result=true; 
        }
      }  
    
    return result;
  }  
  /////////////////////////////////////////////////////////
  void drawXLabel()
  {
    if (!xLabelVisible) return;
    
    fill(ColorXLabel);    
    textAlign(CENTER);
    textSize(xLabelSize);
    if (shadow)
    {
      fill(shadowColor);
      text(xLabelText,lX+lW/2+1,lY+lH-xLabelOffset+1);
    }
    fill(ColorXLabel);    
    text(xLabelText,lX+lW/2,lY+lH-xLabelOffset);

  }
  /////////////////////////////////////////////////////////
  void setXLabelSize(int s)
  {
    xLabelSize=s;
  }
  /////////////////////////////////////////////////////////
  void setXLabelOffset(int s)
  {
    xLabelOffset=s;
  }
  /////////////////////////////////////////////////////////
  void drawYLabel()
  {
    if (!yLabelVisible) return;
    
    textAlign(CENTER);
    textSize(yLabelSize);

    pushMatrix();
    translate(lX+yLabelOffset+10,lY+(lH/2));
    rotate(-PI/2);

    if (shadow)
    {
      fill(shadowColor);   
      text(yLabelText,1,1);
    }
    fill(ColorYLabel);    
    text(yLabelText,0,0);
    popMatrix();
    
    
  }
  /////////////////////////////////////////////////////////
  void setYLabelSize(int s)
  {
    yLabelSize=s;
  }
  /////////////////////////////////////////////////////////
  void setYLabelOffset(int s)
  {
    yLabelOffset=s;
  }
   /////////////////////////////////////////////////////////
  void drawTitle()
  {
    if (!titleVisible) return;
    
    textAlign(CENTER);
    textSize(titleSize);
    if (shadow)
    {
      fill(shadowColor);   
      text(titleText,lX+(lW/2)+1,lY+titleOffset+1);
    }
    fill(ColorTitle);    
    text(titleText,lX+(lW/2),lY+titleOffset);
  }
  /////////////////////////////////////////////////////////
  void setTitleSize(int s)
  {
    titleSize=s;
  }
  /////////////////////////////////////////////////////////
  void setTitleOffset(int s)
  {
    titleOffset=s;
  }
  /////////////////////////////////////////////////////////  
  void drawYValues()
  {
    if (!yNumbersVisible || empty) return;
    
    float step=(float )(lMax-lMin)/lHlines;
    int xcoord=lvx-lBorderWidth-yNumbersOffset-xOffset;

    fill(yNumbersColor);    
    textAlign(RIGHT);
    textSize(yNumbersSize);
   
    for (int i=0; i<lHlines+1; i++)
    {
      text(str(round2nDecimals(lMax-i*step,1)),xcoord,lvy-yOffset+i*lHorizontalSpace);
    }    
  }  
  /////////////////////////////////////////////////////////
  void showYvalues()
  {
    yNumbersVisible=true;
  }  
  /////////////////////////////////////////////////////////
  void hideYvalues()
  {
    yNumbersVisible=false;
  }  
  /////////////////////////////////////////////////////////
  void drawXValues()
  {

    if (!xNumbersVisible) return;

    linePoint currentDataSet;
    int xNumbersyPos=lvy+viewPortH+xNumbersOffset;
    int x=lvx-xOffset;
    
    if (!xNumbersVisible) return;

    currentDataSet=(linePoint )dataSetList.get(0);
    textAlign(LEFT);
    fill(xNumbersColor);    
    textSize(xNumbersSize);
    for (int i=0; i<currentDataSet.getSize(); i++)
    {
      if ((i%xNumbersSpacing)==0)
      {
        pushMatrix();
        translate(x+(i*delta),xNumbersyPos-yOffset);
        rotate(PI/2);
        
        if (currentDataSet.sType)
          text(currentDataSet.getXsPoint(i),0,0);
        else   
          text(str(round2nDecimals(currentDataSet.getXPoint(i),1)),0,0);


        popMatrix();
      }
    }

  }  
  /////////////////////////////////////////////////////////
  void showXvalues()
  {
    xNumbersVisible=true;
  }  
  /////////////////////////////////////////////////////////
  void hideXvalues()
  {
    xNumbersVisible=false;
  }  
  /////////////////////////////////////////////////////////
  void drawLegend()
  {
    if (!legendVisible) return;
    
    int x=lvx+viewPortW+legendXoffset;
    int y=lvy+legendYoffset-yOffset;
    
    linePoint currentDataSet;
    for (int ds=0; ds<dataSetList.size(); ds++)
    {
      currentDataSet =(linePoint )dataSetList.get(ds);
      fill(currentDataSet.pointColor);
      textSize(legendSize);
      text(currentDataSet.legend,x,y+(ds*10));

    }
    
  }
  /////////////////////////////////////////////////////////
  void setLegendSize(int s)
  {
    legendSize=s;
  }
  /////////////////////////////////////////////////////////
  void setlegendXOffset(int s)
  {
    legendXoffset=s;
  }
  /////////////////////////////////////////////////////////
  void setlegendYOffset(int s)
  {
    legendYoffset=s;
  }
  
  /////////////////////////////////////////////////////////
  void drawVerticalDiv()
  {
    int x=lvx-xOffset;
    
    strokeWeight(VerticalDivWidth);
    stroke(ColorVerticalDiv);
    for (int i=1; i<lVlines; i++)
    {
      line(x+i*lVerticalSpace,lvy-yOffset+lInternalBorderWidth/2+1,x+i*lVerticalSpace,lvy-yOffset+viewPortH);
    }
    
  }
  /////////////////////////////////////////////////////////
  void drawHorizontalDiv()
  {
    int x1=lvx-xOffset+lInternalBorderWidth;
    int x2=lvx-xOffset+viewPortW-lInternalBorderWidth;
    
    strokeWeight(HorizontalDivWidth);
    stroke(ColorHorizontalDiv);
    for (int i=1; i<lHlines; i++)
    {
      line(x1,lvy-yOffset+i*lHorizontalSpace,x2,lvy-yOffset+i*lHorizontalSpace);
    }    
  }

  /////////////////////////////////////////////////////////
  void pushValue(float value, int dataSet)
  {
    linePoint currentDataSet;

    
    if (dataSet > (dataSetList.size()-1) || dataSet<0)
    {
      println("data set "+str(dataSet)+" not found!");
      return;
    }   
    
    empty=false;
    
    currentDataSet=(linePoint )dataSetList.get(dataSet);
    currentDataSet.addPoint(value); 
    
    setMinMax(value);

    if (currentDataSet.getSize() > lNsamples+1)
    {
      currentDataSet.removePoint(0);
    }

  }  
  /////////////////////////////////////////////////////////
  void pushValue(float yValue, float xValue, int dataSet)
  {
    linePoint currentDataSet;
    
    if (dataSet > (dataSetList.size()-1) || dataSet<0) return;
    
    empty = false;
    
    currentDataSet=(linePoint )dataSetList.get(dataSet);
    currentDataSet.addPoint(yValue,xValue); 
    
    setMinMax(yValue);

    if (currentDataSet.getSize() > lNsamples+1)
    {
      currentDataSet.removePoint(0);
    }

  }    
  /////////////////////////////////////////////////////////
  void pushValue(float yValue, String xValue, int dataSet)
  {
    linePoint currentDataSet;
    
    if (dataSet > (dataSetList.size()-1) || dataSet<0) return;
    
    empty = false;
    
    currentDataSet=(linePoint )dataSetList.get(dataSet);
    currentDataSet.addPoint(yValue,xValue); 
    
    setMinMax(yValue);

    if (currentDataSet.getSize() > lNsamples+1)
    {
      currentDataSet.removePoint(0);
    }

  }      
  /////////////////////////////////////////////////////////
  void fillUnderCurve(float x1,float y1, float x2, float y2, float x3, float y3, float x4, float y4,int ds)
  {
    
    linePoint currentDataSet =(linePoint )dataSetList.get(ds);
    
    if (!currentDataSet.visibleUnderCurve) return;
    
    noStroke();
    fill(currentDataSet.colorUnderCurve,currentDataSet.underCurveTransparency);
    
    beginShape(POLYGON);
      vertex(x1,y1-1);
      vertex(x2,y2-1);
      vertex(x3,y3);
      vertex(x4,y4);      
    endShape(CLOSE);
  }
  /////////////////////////////////////////////////////////
  int translatedValue(float value)
  {
    float rValue;
    
    rValue=constrain(viewPortH*(lMax-value)/(lMax-lMin) +lvy-yOffset,lvy-yOffset,lvy+viewPortH-yOffset);    
    
    return (int )rValue;
  }
   /////////////////////////////////////////////////////////
  void drawPoint(float v, float x,int ds)
  {
    linePoint currentDataSet =(linePoint )dataSetList.get(ds);
    
    if (!currentDataSet.visiblePoint) return;
    noStroke();
    fill(currentDataSet.pointColor,currentDataSet.pointTransparency);
    ellipse(x,translatedValue(v),currentDataSet.pointRadius,currentDataSet.pointRadius);    
    
  }
  /////////////////////////////////////////////////////////
  void drawPoint(float v1, float x1, float v2, float x2, int ds)
  {
    linePoint currentDataSet =(linePoint )dataSetList.get(ds);
    
     
    fillUnderCurve(x1,translatedValue(v1),x2,translatedValue(v2),x2,translatedValue(0),x1,translatedValue(0),ds);

    if (currentDataSet.visibleLine) 
    { 
      strokeWeight(currentDataSet.lineSize);
      stroke(currentDataSet.lineColor,currentDataSet.lineTransparency);
      if (currentDataSet.lineSize>0) line(x1,translatedValue(v1),x2,translatedValue(v2));    
    }
    if (currentDataSet.visiblePoint)
    {
      noStroke();
      fill(currentDataSet.pointColor,currentDataSet.pointTransparency);
      ellipse(x1,translatedValue(v1),currentDataSet.pointRadius,currentDataSet.pointRadius); 
      ellipse(x2,translatedValue(v2),currentDataSet.pointRadius,currentDataSet.pointRadius); 
    }
    

  }
  /////////////////////////////////////////////////////////
  void drawValues()
  {
    int x=lvx-xOffset;
    linePoint currentDataSet;
    drawYValues();
    drawXValues();
    
    for (int ds=0; ds<dataSetList.size(); ds++)
    {
      
      currentDataSet=(linePoint )dataSetList.get(ds);

      if (currentDataSet.visible())
        for (int i=0; i<currentDataSet.getSize(); i++)
        {
          float v2 = (float) currentDataSet.getYPoint(i);
      
          if (i>0)
          {
            float v1 = (float) currentDataSet.getYPoint(i-1);
            drawPoint(v1,x+((i-1)*delta),v2,x+(i*delta),ds);
          }  
          else 
            drawPoint(v2,x+(i*delta),ds);
        }

    }
    
    
  }
  /////////////////////////////////////////////////////////  
 
  void update()
  {
    
    updateViewPoint();
    drawFrame();
    drawVerticalDiv();
    drawHorizontalDiv();
    drawXLabel();
    drawYLabel();
    drawTitle();
    drawValues();
    drawLegend();
    mouseOver();
    
  }
  /////////////////////////////////////////////////////////
  void setBorderWidth(int bw)
  {
    lBorderWidth=bw;
    update();
  }
  
  /////////////////////////////////////////////////////////
  void exportParameters()
  {
    String[] formulas = {
    "lineGraph.pushValue(-(sin((i-10)*PI/56))*(1+cos(i*PI/56)),i,0);",  
    "lineGraph.pushValue((sin((i-10)*PI/56)),i,1);",     
    "lineGraph.pushValue((cos((i+3)*PI/56)),i,2);",     
    "lineGraph.pushValue((sin((i-1)*PI/56)),i,3);",     
    "lineGraph.pushValue((cos((i-2)*PI/56)),i,4);",     
    "lineGraph.pushValue((sin((i-3)*PI/56)),i,5);",     
    "lineGraph.pushValue((cos((i-4)*PI/56)),i,6);",     
    "lineGraph.pushValue((sin((i-5)*PI/56)),i,7);",     
    "lineGraph.pushValue((cos((i-6)*PI/56)),i,8);",     
    "lineGraph.pushValue((sin((i-7)*PI/56)),i,9);"};     
    
    linePoint currentDataSet;

    println("/**************************************");
    println("lineGraph class 2012"); 
    println("Author: Alejandro Dirgan");
    println("**************************************/");
    println("ADLinechart lineGraph;");
    println("int i;");   
    println("void setup()");
    println("{");
    println("  size("+str(lW+40)+","+str(lH+40)+");");
    println("  smooth();");

    println("  lineGraph = new ADLinechart(20,20,"+str(lW)+","+str(lH)+",\"Data Set 1\");");
    print("  lineGraph.setParameters("+titleVisible+",\""+titleText+"\","+titleSize+","+titleOffset);
    print(","+xLabelVisible+",\""+xLabelText+"\","+xLabelSize+","+xLabelOffset+","+xNumbersSpacing);
    print(","+yLabelVisible+",\""+yLabelText+"\","+yLabelSize+","+yLabelOffset);
    print(","+legendVisible+","+legendSize+","+legendXoffset+","+legendYoffset);
    print(","+xNumbersVisible+","+yNumbersVisible+","+shadow+","+xp+","+yp+","+xOffset+","+yOffset+","+lHlines+","+lVlines+","+lW+","+lH+","+lNsamples+","+pRadius+","+lWidth+","+lTransp);
    println(");");

    print("  lineGraph.setColors("+getTitleColor()+","+getXlabelColor()+","+getYlabelColor()+","+getXNumbersColor()+","+getYNumbersColor()+","+getBackgroudColor()+","+getGridColor());
    println(");");

    for (int ds=0; ds<dataSetList.size()-1; ds++)
      println("  lineGraph.addDataSet(\"Data Set "+(ds+2)+"\");");
  
    println("}");
    println("void draw()");
    println("{");
    
    for (int ds=0; ds<dataSetList.size(); ds++)
      println("  "+formulas[ds]);

    println("  i++;");
    println("  lineGraph.update();");
    println("}");


  }
  ///////////////////////////////////////////////////////  
  void printGeometry()
  {
    println("  lineGraph = new ADLinechart("+lX+","+lY+","+lW+","+lH+",\"Data Set 1\");");
  }
///////////////////////////////////////////////////////  
  void setDebugOn()
  {
    debug=true;
  }
///////////////////////////////////////////////////////  
  void setDebugOff()
  {
    debug=false;
  }  
///////////////////////////////////////////////////////  
  void setMin(float n)
  {
    lMin=n;
  }  
///////////////////////////////////////////////////////  
  void setMax(float n)
  {
    lMax=n;
  }  
  
}

