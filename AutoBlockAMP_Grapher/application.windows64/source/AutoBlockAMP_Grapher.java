import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import controlP5.*; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class AutoBlockAMP_Grapher extends PApplet {

 /*
 * Author:   RechargeCar Inc.
 * Version:  0.1
 *
 * License:  GPLv3
 *   (http://www.fsf.org/licensing/)
 *
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



ADLinechart lineGraph;
ADLinechart lineGraph2;

int Slope;

PImage img;  // Declare variable "a" of type PImage

int Intercept;
int InterceptCalibrate;

int CAPvalue;

boolean portselected = false;

boolean Loaded = false;
float newintercept;

int OUTPUTMODE = 0;

boolean CONNECTED = false;


SerialReader serialReader;
boolean portOpened = false;
String portName;
RCIPacket packet;
Serial serialPort;
float amperage;
float Ah;

final int SlipEnd = PApplet.parseInt(0xC0);
final int SlipEsc = PApplet.parseInt(0xDB);
final int SlipEscEnd = PApplet.parseInt(0xDC);
final int SlipEscEsc = PApplet.parseInt(0xDD);


ControlP5 controlP5;

Textfield CAL;

Button ZERO;

DropdownList d2;
String DDListname;

Button CON;

public void setup() {
  size(610, 550);
  smooth();
  controlP5 = new ControlP5(this);

  d2 = controlP5.addDropdownList("SerialList", 10, 40, 150, 200);
  customize2(d2);

  CON= controlP5.addButton("CONNECT", 0, 175, 15, 55, 24);
  CON.setColorBackground(0xffBCBCBC);
  CON.setColorActive(0xffBCBCBC);
  CON.setColorForeground(0xffBCBCBC);

  CAL = controlP5.addTextfield("calibrate", 335, 15, 100, 24);
  CAL.setCaptionLabel(""); 

  controlP5.addButton("ZERO", 0, 280, 15, 40, 24);
  controlP5.setColorBackground(0xffBCBCBC);
  controlP5.setColorLabel(0xff000000);
  controlP5.setColorValue(0xff000000);

  lineGraph = new ADLinechart(10, 300, 590, 235, "Data Set 1");
  lineGraph.setParameters(false, "Title of the GRAPH", 24, 40, false, "X Label", 16, 10, 20, true, "Amperage", 16, 25, false, 10, 10, 10, true, true, false, 49, 38, -31, 10, 4, 10, 590, 235, 100, 5, 2, 50);
  lineGraph.setColors(-14275983, -14275983, -14275983, -14275983, -14275983, -1, -1842205);
  lineGraph.setBorderWidth(1);
  lineGraph.setMinMax(5);
  lineGraph.setMinMax(-5);

  lineGraph2 = new ADLinechart(10, 50, 590, 235, "Data Set 2");
  lineGraph2.setParameters(false, "Title of the GRAPH", 24, 40, false, "X Label", 16, 10, 20, true, "Amp-Hours", 16, 25, false, 20, 10, 10, true, true, false, 49, 38, -31, 10, 4, 10, 590, 235, 100, 5, 2, 50);
  lineGraph2.setColors(-14275983, -14275983, -14275983, -14275983, -14275983, -1, -1842205);
  lineGraph2.setBorderWidth(1);
  lineGraph2.addDataSet("Data Set 2");
  lineGraph2.setMinMax(CAPvalue+1);

  img = loadImage("ABAtitle_white.png");  // Load the image into the program

  controlP5.getTooltip().setDelay(200);
  controlP5.getTooltip().register("calibrate", "To Calibrate AutoBlock AMP: Apply known current, enter value and press enter.");
  controlP5.getTooltip().register("ZERO", "Zeros AutoBlock AMP and restarts Ah counting");
}

public void draw() {

  background(255);

  image(img, 450, 5);


  if (CONNECTED == true) {

    if (serialReader.available()) {
      packet = serialReader.getPacket();   

      if (packet.packetType == 5) {   // this is Ah packet
        Ah = (packet.value);
        println(Ah);
        Ah = CAPvalue-(Ah/1000);   // need to change this line...

       // println(Ah);

        lineGraph2.pushValue(Ah, (millis()/1000), 1);
      }

      if (packet.packetType == 4) {   // this is Amp packet
        amperage = (packet.value);
        amperage = amperage/100;
        lineGraph.pushValue(amperage, (millis()/1000), 0);
      }

      // lineGraph.pushValue(amperage, (millis()/1000), 0);
      //   lineGraph2.pushValue(Ah, (millis()/1000), 1);
    }
  }

  lineGraph.update();
  lineGraph2.update();

  //  controlP5.draw();
}

public void serialEvent(Serial p) {
  serialReader.checkSerial();
}

public void openPortAndGo() {
  serialPort = new Serial(this, portName, 57600);
  serialPort.clear();
  serialPort.bufferUntil(PApplet.parseByte(SlipEnd));
  serialReader = new SerialReader(serialPort, "serial1", portName);
}

public void controlEvent(ControlEvent theControlEvent) {    

  if (theControlEvent.isGroup()) {

    if (theControlEvent.group().name().equals("SerialList")) {

      portselected = true;
      CON.setColorActive(0xFF00C90D);
      CON.setColorForeground(0xFF39E444);

      println(round(theControlEvent.group().value())); 
      //      d2.captionLabel().setFontSize(13);
      d2.captionLabel().style().marginTop = 8;
      println(Serial.list().length);

      portName = Serial.list()[(round(theControlEvent.group().value()))];

      d2.clear();

      for (int i=0;i<(Serial.list().length);i++) {
        d2.addItem(Serial.list()[i], i);
      }
    }
  }

  else {

    if (theControlEvent.controller().name() == "calibrate") {

      InterceptCalibrate = Intercept;  // original Zero value now stored.

      // zero again to get value when power is flowing.

      slipStart();                  
      slipSend(0x00);    //version
      slipSend(0x00);    //Payload Size MSB
      slipSend(0x00);    //Payload Size LSB
      slipSend(0x16);    //Packet type
      slipSend(0x00);     // checksum
      slipEnd();

      delay(1000);

      LoadfromAB();

      println("MinA" + theControlEvent.controller().stringValue());
      println("zero intercept is:" + InterceptCalibrate);
      println("powered intercept is:" + Intercept);

      float newslope = PApplet.parseFloat(theControlEvent.controller().stringValue());

      newslope = abs(round((newslope * 10000)/(InterceptCalibrate-Intercept)));

      println("new intercept is:" + newslope);
      println(packet.ConfigBuffer);

      //program new values to EEPROM

      slipStart();
      slipSend(0x00);    //version
      slipSend(0x00);    //Payload Size MSB
      slipSend(0x13);    //Payload Size LSB
      slipSend(0x15);    //Packet type

      slipSend(packet.ConfigBuffer[0]);    //Fuel Gauge High end
      slipSend(packet.ConfigBuffer[1]);    //Fuel Gauge High end          

      slipSend(packet.ConfigBuffer[2]);    //Fuel Gauge Low end          
      slipSend(packet.ConfigBuffer[3]);    //Fuel Gauge Low end          

      slipSend(packet.ConfigBuffer[4]);
      slipSend(packet.ConfigBuffer[5]); 

      slipSend(packet.ConfigBuffer[6]);    
      slipSend(packet.ConfigBuffer[7]);    

      slipSend(packet.ConfigBuffer[8]);   

      slipSend((round(newslope) >> 8));     // Slope MSB
      slipSend(round(newslope) & 0xff);            // SLOPE LSB

      slipSend((InterceptCalibrate >> 8));     // INT MSB
      slipSend(InterceptCalibrate & 0xff);            // INT LSB

      slipSend(OUTPUTMODE); 

      slipSend(0x00);     // checksum
      slipEnd();

      // end of calibibration
    }

    if (theControlEvent.controller().name().equals("ZERO")) {   //this updates range values

        slipStart();
      slipSend(0x00);    //version
      slipSend(0x00);    //Payload Size MSB
      slipSend(0x00);    //Payload Size LSB
      slipSend(0x16);    //Packet type
      slipSend(0x00);     // checksum
      slipEnd();

      LoadfromAB();
    }

    if (theControlEvent.controller().name().equals("CONNECT")) { 

      if (CONNECTED == false && portselected == true) {

        CONNECTED = true; 

        openPortAndGo();  
        println("First attempt to connect");
        CON.setColorBackground(0xffBCBCBC);
        delay(500);

      if (serialReader.available() == false) {   // check to see if we connected, if not, close and try again

          delay(500);
          serialPort.clear();
          serialPort.stop();
          openPortAndGo();
          println("2nd attempt");
        }
        
      if (serialReader.available() == false) {   // check to see if we connected, if not, close and try again

          delay(500);
          serialPort.clear();
          serialPort.stop();
          openPortAndGo();
          println("3rd attempt");
        }

        LoadfromAB();
        lineGraph2.setMinMax(CAPvalue);
      }

      else if (CONNECTED == true && portselected == true) {   // closing connection

        CONNECTED = false;
        CON.setColorActive(color(60));
        serialPort.clear();
        serialPort.stop();
        println("Disconnecting");
      }
    }
  }
}

public void slipStart() {
  serialPort.write(0xC0);
}

public void slipEnd() {
  serialPort.write(0xC0);
  println("");
}

public void slipSend (int dataByte) { 

  if ((dataByte != 0xC0) && (dataByte != 0xDB)) {
    serialPort.write(dataByte);
  }
  else if (dataByte == 0xC0) {
    serialPort.write(0xDB); //SlipEsc
    serialPort.write(0xDC); //SlipEscEnd
  }
  else { //must be 0xDB / SlipEsc
    serialPort.write(0xDB);//SlipEsc
    serialPort.write(0xDD);//SlipEscEsc
  }

  print(hex(dataByte, 2) +" ");
}

public void customize2(DropdownList ddl2) {

  ddl2.setBackgroundColor(color(190));
  ddl2.setItemHeight(23);
  ddl2.setBarHeight(24);
  ddl2.captionLabel().set("Select Serial Port");
  ddl2.captionLabel().style().marginTop = 8;
  ddl2.captionLabel().style().marginLeft = 3;  
  ddl2.valueLabel().style().marginTop = 0;

  for (int i=0;i<(Serial.list().length);i++) {
    ddl2.addItem(Serial.list()[i], i);
  }

  ddl2.setColorBackground(color(80));
  ddl2.setColorActive(color(255, 128));
  ddl2.setHeight(((Serial.list().length)+1)*23);
}

public void LoadfromAB() {

  Loaded = false;

  while (Loaded == false) {

    slipStart();
    slipSend(0x00);    //version
    slipSend(0x00);    //Payload Size MSB
    slipSend(0x00);    //Payload Size LSB
    slipSend(0x17);    //Packet type
    slipSend(0x00);     // checksum
    slipEnd();


    if (serialReader.available()) {
      packet = serialReader.getPacket(); 

      if (packet.packetType == 18) { 

        int TempFGH =  (packet.ConfigBuffer[0] << 8) + packet.ConfigBuffer[1];
        int TempFGL =  (packet.ConfigBuffer[2] << 8) + packet.ConfigBuffer[3];
        int LLvalue =  (packet.ConfigBuffer[4] << 8) + packet.ConfigBuffer[5];  
        CAPvalue =  (packet.ConfigBuffer[6] << 8) + packet.ConfigBuffer[7];  
        int tach = packet.ConfigBuffer[8];    
        Slope = (packet.ConfigBuffer[9] << 8) + packet.ConfigBuffer[10];
        Intercept = (packet.ConfigBuffer[11] << 8) + packet.ConfigBuffer[12];

        //        print("FGH is: "+TempFGH);
        //        println(", FGL is: "+TempFGL);
        //        println("LL is: "+LLvalue); 
        //        println("CAP is: "+CAPvalue);          
        //        println("tach is: "+tach); 
        //        println("Slope is: "+Slope); 
        //        println("Intercept is: "+Intercept);

        Loaded = true;
      }
    }
  }
} 

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
  public float getY()
  {
    return y;
  }
  /////////////////////////////////////////////////////////
  public float getX()
  {
    return x;
  }
  /////////////////////////////////////////////////////////
  public String getXs()
  {
    return xs;
  }
  /////////////////////////////////////////////////////////
  public void setMax()
  {
    maxIam=true;
    minIam=false;
  }
  /////////////////////////////////////////////////////////
  public void setMin()
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
  
  int[] colorPointArray = {0xffFF8D00,0xff339045,0xffE04B50,0xffDE1FCB,0xffBC9B52,0xff3EA587,0xff961BBC,0xff1B9DBC,0xff46413C,0xff1BBC36,0xffBCAF1B,0xffBC871B,0xffBC361B,0xff46413C,0xffC9E37C};
  
  public int pointColor=0xff383FBF;
  public int pointTransparency=150;
  public int pointRadius=4;
  public boolean visiblePoint=true;
  
  public int lineColor=0xff383FBF;
  public int lineTransparency=200;
  public int lineSize=3;
  public boolean visibleLine=true;
  
  public int colorUnderCurve=0xff339045;
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
  public int addPoint(float yValue)
  {
    linePoint.add(new pointDef(yValue));
    empty=false;
    return linePoint.size();
  }
  /////////////////////////////////////////////////////////
  public int addPoint(float yValue, float xValue)
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
  public int addPoint(float yValue, String xValue)
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
  public int removePoint(int p)
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
  public float getYPoint(int p)
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
  public float getXPoint(int p)
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
  public String getXsPoint(int p)
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
  public int getSize()
  {
    return linePoint.size();    
  }
  /////////////////////////////////////////////////////////
  public void hide()
  {
    visible=false;    
  }
  /////////////////////////////////////////////////////////
  public void unHide()
  {
    visible=true;    
  }
  /////////////////////////////////////////////////////////
  public boolean visible()
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
  
  int shadowColor=0xff8E8787;
  boolean shadow=true;
  
  int lBorderWidth=2;
  int lColorBorder=0;
  int lColorFill=0xffFFFFFF;
  
  int lInternalBorderWidth=2;
  int lInternalColorBorder=0xffE3E3E3;
  int lInternalColorFill=0xffFFFFFF;
  
  int ColorXLabel=0xff262A71; 
  int xLabelOffset=10;
  int xLabelSize=16;
  String xLabelText="X Label";
  boolean xLabelVisible=false;

  int ColorYLabel=0xff262A71; 
  int yLabelOffset=15;
  int yLabelSize=16;
  String yLabelText="Y Label";
  boolean yLabelVisible=false;

  int ColorTitle=0xff262A71; 
  int titleOffset=40;
  int titleSize=24;
  String titleText="Title of the GRAPH";
  boolean titleVisible=false;

  int ColorVerticalDiv=0xffE3E3E3; 
  int VerticalDivWidth=1;
  int ColorHorizontalDiv=0xffE3E3E3; 
  int HorizontalDivWidth=1;
  
  int ColorPoint=0xff2A9035;  
  
  int labelXX, labelXY;

  int lVlines = 10;
  int lHlines=4;
  float lVerticalSpace=0;
  float lHorizontalSpace=0;
  
  int lNsamples=20;
  
  float lMin=100000000, lMax=-1000000000;
  
  int maxViewport;
  

  int yNumbersColor=0xff262A71; 
  int yNumbersOffset=5;
  int yNumbersSize=8;
  boolean yNumbersVisible=true;
  
  int xNumbersColor=0xff262A71; 
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
  public void setParameters(boolean vTitle, String title, int tSize, int tOffset, boolean xlVisible, String xlText, int xlSize, int xlOffset, int xnSpacing, boolean ylVisible, String ylText, int ylSize, int ylOffset, boolean lgVisible, int lgSize, int xlgOffset, int ylgOffset, boolean xnVisible, boolean ynVisible, boolean sh, int gx, int gy, int xOf, int yOf, int xM, int yM, int w, int h, int samples, int pr, int lw, int lt)
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
  public void setColors(int tc, int xlc, int ylc, int xnc, int ync, int ba, int gc)
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
  public void updateCoord()
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
  public void updateViewPoint()
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
  public void showShadow()
  {
    shadow=true;
  }
  /////////////////////////////////////////////////////////
  public void hideShadow()
  {
    shadow=false;
  }
  /////////////////////////////////////////////////////////
  public int getTitleColor()
  {
    return ColorTitle;
  }
  /////////////////////////////////////////////////////////
  public void setTitleColor(int c)
  {
    ColorTitle=c;
  }
  /////////////////////////////////////////////////////////
  public int getXlabelColor()
  {
    return ColorXLabel;
  }
  /////////////////////////////////////////////////////////
  public void setXlabelColor(int c)
  {
    ColorXLabel=c;
  }
  /////////////////////////////////////////////////////////
  public int getYlabelColor()
  {
    return ColorYLabel;
  }
  /////////////////////////////////////////////////////////
  public void setYlabelColor(int c)
  {
    ColorYLabel=c;
  }
  /////////////////////////////////////////////////////////
  public void setXNumbersColor(int c)
  {
    xNumbersColor=c;
  }
  /////////////////////////////////////////////////////////
  public int getXNumbersColor()
  {
    return xNumbersColor;
  }
  /////////////////////////////////////////////////////////
  public void setYNumbersColor(int c)
  {
    yNumbersColor=c;
  }
  /////////////////////////////////////////////////////////
  public int getYNumbersColor()
  {
    return yNumbersColor;
  }
  /////////////////////////////////////////////////////////
  public void setBackgroudColor(int c)
  {
    lColorFill=lInternalColorFill=c;
  }
  /////////////////////////////////////////////////////////
  public int getBackgroudColor()
  {
    return lColorFill;
  }
  /////////////////////////////////////////////////////////
  public void setGridColor(int c)
  {
    ColorVerticalDiv=ColorHorizontalDiv=lInternalColorBorder=c;
  }
  /////////////////////////////////////////////////////////
  public int getGridColor()
  {
    return ColorVerticalDiv;
  }
  
  /////////////////////////////////////////////////////////
  public void setPointRadius(float r, int ds)
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
  public void setLineWidth(float r, int ds)
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
  public void setUnderCurveTransparecy(int t, int ds)
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
  public void setXlabelFrecuency(int f)
  {
    xNumbersSpacing=f;
  }
  /////////////////////////////////////////////////////////
  public void setYgridMarks(int m)
  {
    lHlines=m;
    lHorizontalSpace=(float )viewPortH/lHlines;
  }
  /////////////////////////////////////////////////////////
  public void setXgridMarks(int m)
  {
    lVlines=m;
    lVerticalSpace=(float )viewPortW/lVlines;
  }
  /////////////////////////////////////////////////////////
  public void setGraphWidth(int w)
  {
    lW=w;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  public void setGraphHeigth(int h)
  {
    lH=h;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  public void setXGridSize(int s)
  {
    xp=s;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  public void setYGridSize(int s)
  {
    yp=s;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  public void setXGridOffset(int s)
  {
    xOffset=s;
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  public void setYGridOffset(int s)
  {
    yOffset=s;
    updateViewPoint();
  }
  ////////////////////////////////////////////////////////////////////////
  public float round2nDecimals(float number, float decimal) {
      return (float)(round((number*pow(10, decimal))))/pow(10, decimal);
  }   
  /////////////////////////////////////////////////////////
  public void setTitle(String title)
  {
    titleText=title;
    titleVisible=true;
    
    updateCoord();
    updateViewPoint();
 
  }
  /////////////////////////////////////////////////////////
  public void showTitle()
  {
    titleVisible=true;

    updateCoord();
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  public void hideTitle()
  {
    titleVisible=false;

    updateCoord();
    updateViewPoint();

  }
  /////////////////////////////////////////////////////////
  public void setYLabel(String yLabel)
  {
    yLabelText=yLabel;
    yLabelVisible=true;
    updateCoord();
    updateViewPoint();
  }
  /////////////////////////////////////////////////////////
  public void setXLabel(String xLabel)
  {
    xLabelText=xLabel;
    xLabelVisible=true;
    updateCoord();
    updateViewPoint();
   
  }
  /////////////////////////////////////////////////////////
  public void showLegend()
  {
    legendVisible=true;
    xOffset=10;
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  public void hideLegend()
  {
    legendVisible=false;
    xOffset=0;
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  public void showYlabel()
  {
    yLabelVisible=true;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  public void hideYlabel()
  {
    yLabelVisible=false;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  public void showXlabel()
  {
    xLabelVisible=true;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  public void hideXlabel()
  {
    xLabelVisible=false;
    updateCoord();
    updateViewPoint();
    
  }
  /////////////////////////////////////////////////////////
  public void arrayInit(String textID)
  {
    dataSetList = new ArrayList();
    dataSetList.add(new linePoint(dataSetList.size(), textID, pRadius, lWidth, lTransp));
  }
  /////////////////////////////////////////////////////////
  public int addDataSet(String textID)
  {
    dataSetList.add(new linePoint(dataSetList.size(),textID,pRadius,lWidth,lTransp));
    return dataSetList.size();
  }
  /////////////////////////////////////////////////////////
  public int getDataSets()
  {
    return dataSetList.size();
  }
  /////////////////////////////////////////////////////////
  public void setMinMax(float value)
  {
    if (value>lMax) lMax=value;
    if (value<lMin) lMin=value;
  }  
  /////////////////////////////////////////////////////////
  public void setSamples(int samples)
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
  public void setGrid(int vDiv, int hDiv)
  {
    lVlines = vDiv;
    lHlines=hDiv;
    lVerticalSpace=(float )viewPortW/lVlines;
    lHorizontalSpace=(float )viewPortH/lHlines;
  }
  /////////////////////////////////////////////////////////
  public void drawFrame()
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
  public void deBounce(int n)
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
  public boolean mouseOver()
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
  public void drawXLabel()
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
  public void setXLabelSize(int s)
  {
    xLabelSize=s;
  }
  /////////////////////////////////////////////////////////
  public void setXLabelOffset(int s)
  {
    xLabelOffset=s;
  }
  /////////////////////////////////////////////////////////
  public void drawYLabel()
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
  public void setYLabelSize(int s)
  {
    yLabelSize=s;
  }
  /////////////////////////////////////////////////////////
  public void setYLabelOffset(int s)
  {
    yLabelOffset=s;
  }
   /////////////////////////////////////////////////////////
  public void drawTitle()
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
  public void setTitleSize(int s)
  {
    titleSize=s;
  }
  /////////////////////////////////////////////////////////
  public void setTitleOffset(int s)
  {
    titleOffset=s;
  }
  /////////////////////////////////////////////////////////  
  public void drawYValues()
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
  public void showYvalues()
  {
    yNumbersVisible=true;
  }  
  /////////////////////////////////////////////////////////
  public void hideYvalues()
  {
    yNumbersVisible=false;
  }  
  /////////////////////////////////////////////////////////
  public void drawXValues()
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
  public void showXvalues()
  {
    xNumbersVisible=true;
  }  
  /////////////////////////////////////////////////////////
  public void hideXvalues()
  {
    xNumbersVisible=false;
  }  
  /////////////////////////////////////////////////////////
  public void drawLegend()
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
  public void setLegendSize(int s)
  {
    legendSize=s;
  }
  /////////////////////////////////////////////////////////
  public void setlegendXOffset(int s)
  {
    legendXoffset=s;
  }
  /////////////////////////////////////////////////////////
  public void setlegendYOffset(int s)
  {
    legendYoffset=s;
  }
  
  /////////////////////////////////////////////////////////
  public void drawVerticalDiv()
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
  public void drawHorizontalDiv()
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
  public void pushValue(float value, int dataSet)
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
  public void pushValue(float yValue, float xValue, int dataSet)
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
  public void pushValue(float yValue, String xValue, int dataSet)
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
  public void fillUnderCurve(float x1,float y1, float x2, float y2, float x3, float y3, float x4, float y4,int ds)
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
  public int translatedValue(float value)
  {
    float rValue;
    
    rValue=constrain(viewPortH*(lMax-value)/(lMax-lMin) +lvy-yOffset,lvy-yOffset,lvy+viewPortH-yOffset);    
    
    return (int )rValue;
  }
   /////////////////////////////////////////////////////////
  public void drawPoint(float v, float x,int ds)
  {
    linePoint currentDataSet =(linePoint )dataSetList.get(ds);
    
    if (!currentDataSet.visiblePoint) return;
    noStroke();
    fill(currentDataSet.pointColor,currentDataSet.pointTransparency);
    ellipse(x,translatedValue(v),currentDataSet.pointRadius,currentDataSet.pointRadius);    
    
  }
  /////////////////////////////////////////////////////////
  public void drawPoint(float v1, float x1, float v2, float x2, int ds)
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
  public void drawValues()
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
 
  public void update()
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
  public void setBorderWidth(int bw)
  {
    lBorderWidth=bw;
    update();
  }
  
  /////////////////////////////////////////////////////////
  public void exportParameters()
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
  public void printGeometry()
  {
    println("  lineGraph = new ADLinechart("+lX+","+lY+","+lW+","+lH+",\"Data Set 1\");");
  }
///////////////////////////////////////////////////////  
  public void setDebugOn()
  {
    debug=true;
  }
///////////////////////////////////////////////////////  
  public void setDebugOff()
  {
    debug=false;
  }  
///////////////////////////////////////////////////////  
  public void setMin(float n)
  {
    lMin=n;
  }  
///////////////////////////////////////////////////////  
  public void setMax(float n)
  {
    lMax=n;
  }  
  
}

/*
 * ----------------------------------
 *  serialFunctions Class for Processing 2.0
 * ----------------------------------
 *
 *
 * DEPENDENCIES:
 *   N/A
 *
 * Created:  April, 23 2012
 * Author:   RechargeCar Inc.
 * Version:  0.1
 *
 * License:  GPLv3
 *   (http://www.fsf.org/licensing/)
 *
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
 



public class RCIPacket {
  public int packetType;
  public int value;
  public int ConfigBuffer[];
}

public class SerialReader {
  final int SlipEnd = 0xC0;
  final int SlipEsc = 0xDB;
  final int SlipEscEnd = 0xDC;
  final int SlipEscEsc = 0xDD;

  private String id; // Thread name/id, in case of multiple instances
  private String port; // Serial port name to open for the thread
  private boolean available; // Has a new packet been received and parsed?
  private boolean newData;
  private Serial myPort;
  private int readResult;
  private int[] packetBuffer;
  private int bufferPosition;
  private RCIPacket packet;

  // Constructor, probably want the serial port name passed in here
  public SerialReader(Serial tempSerial, String s, String portName) {
    id = s;
    readResult = 0;
    packetBuffer = new int[30];
    packet = new RCIPacket();
    bufferPosition = 0;
    newData = false;
    available = false;

    myPort = tempSerial;
    //    myPort.clear();
    //    myPort.bufferUntil(SlipEnd);
  }

  public void checkSerial() {
    //    if(myPort.available() > 0) readResult = slipRead(packetBuffer, bufferPosition, 20);
    int tempRead = slipRead(packetBuffer, bufferPosition, 20);
    if (tempRead > 2) {    
      newData = true;
    }
    bufferPosition += tempRead;
    if (newData) {
      newData = false;
      if (bufferPosition > 8) {

        if (packetBuffer[3] == 5) {

          packet.packetType = 5;
          
          packet.value =  ((packetBuffer[4] & 0xff) << 16) | ((packetBuffer[5] & 0xff) << 8)  | (packetBuffer[6] & 0xff);
          available = true;
          
        }

        if (packetBuffer[3] == 4) {

          packet.packetType = 4;
          packet.value = ((packetBuffer[4] & 0xff) << 24) | ((packetBuffer[5] & 0xff) << 16) | ((packetBuffer[6] & 0xff) << 8)  | (packetBuffer[7] & 0xff);      
          available = true;
          
        } 
        
        if (packetBuffer[3] == 0x18) {

          packet.packetType = 18;
          packet.ConfigBuffer = subset(packetBuffer, 4, 14); 
          available = true;
          
        }      
        bufferPosition = 0;
      }
    }
  }

  public boolean available() {
    return available;
  }

  public RCIPacket getPacket() {
    available = false;
    return packet;
  }  

  /// <summary>
  /// Overrides base SerialProvider.Read() method to provide SLIP framing.
  /// </summary>
  /// <param name="buffer"></param>
  /// <param name="offset"></param>
  /// <param name="size"></param>
  public int slipRead(int[] buffer, int offset, int size) {
    int bytesReceived = 0;  
    int failures = 0;  
    int b = -1;
    boolean readComplete = false;
  
    while ((!readComplete) && (myPort.available() > 0) && (bytesReceived <= size) && (failures < 3)) {
      if (b != -1) {
        buffer[offset + bytesReceived++] = PApplet.parseInt(b);
      }

      try {
        b = -1;
        b = myPort.read();
      } 
      catch (Exception ex) {
        println("SlipProvider.Read - An exception occured while trying to read from port. <" + ex + ">");
        failures++;
      }

      switch (b) {
        case -1:
        case SlipEnd:
          if (bytesReceived == 0) {
            b = -1;
            continue;
          }
          readComplete = true;
          newData = true;
          break;
        case SlipEsc:
          b = myPort.read();
          switch (b) {
            case SlipEscEnd:
              b = SlipEnd;
              break;
            case SlipEscEsc:
              b = SlipEsc;
              break;
            default:
              break;
          }
        break;
        default:
        break;
        }
    }

    //base.lastReadBytesReceived = bytesReceived;
    return bytesReceived;
  }

  /// <summary>
  /// Serial Write function to provide SLIP framing.
  /// </summary>
  /// <param name="buffer"></param>
  /// <param name="pSize"></param>
  public void slipWrite(int[] buffer, int offset, int pSize) {
    int[] framedBuffer = new int[pSize * 2 + 2];

    int pos = 0;
    framedBuffer[pos++] = SlipEnd;

    int i = 0;
    while (i < pSize) {
      switch (buffer[i + offset])
      {
        case SlipEnd:
          framedBuffer[pos++] = SlipEsc;
          framedBuffer[pos++] = SlipEscEnd;
          break;
  
        case SlipEsc:
          framedBuffer[pos++] = SlipEsc;
          framedBuffer[pos++] = SlipEscEsc;
          break;
  
        default:
          framedBuffer[pos++] = buffer[i + offset];
          break;
      }
      i++;
    }

    framedBuffer[pos++] = SlipEnd;

    myPort.write("Finish this function!");
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "AutoBlockAMP_Grapher" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
