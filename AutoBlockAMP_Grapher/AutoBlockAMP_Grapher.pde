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

import processing.serial.*;
SerialReader serialReader;
boolean portOpened = false;
String portName;
RCIPacket packet;
Serial serialPort;
float amperage;
float Ah;

final int SlipEnd = int(0xC0);
final int SlipEsc = int(0xDB);
final int SlipEscEnd = int(0xDC);
final int SlipEscEsc = int(0xDD);

import controlP5.*;
ControlP5 controlP5;

Textfield CAL;

Button ZERO;

DropdownList d2;
String DDListname;

Button CON;

void setup() {
  size(610, 550, P2D);
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
  lineGraph2.setParameters(false, "Title of the GRAPH", 24, 40, false, "X Label", 16, 10, 20, true, "Amp-Hours", 16, 25, false, 10, 10, 10, true, true, false, 49, 38, -31, 10, 4, 10, 590, 235, 100, 5, 2, 50);
  lineGraph2.setColors(-14275983, -14275983, -14275983, -14275983, -14275983, -1, -1842205);
  lineGraph2.setBorderWidth(1);
  lineGraph2.addDataSet("Data Set 2");
  lineGraph2.setMinMax(CAPvalue+1);
    
  img = loadImage("ABAtitle_white.png");  // Load the image into the program

controlP5.getTooltip().setDelay(200);
  controlP5.getTooltip().register("calibrate","To Calibrate AutoBlock AMP:\nApply known current, enter value and press enter.");
    controlP5.getTooltip().register("ZERO","Zeros AutoBlock AMP\nand restarts Ah counting");


}

void draw() {

  background(255);
  
  image(img, 450, 5);


  if (CONNECTED == true) {

    if (serialReader.available()) {
      packet = serialReader.getPacket();   

      if (packet.packetType == 5) {   // this is Ah packet
        Ah = (packet.value);
        // println(Ah);
        Ah = CAPvalue-(Ah/1000);
        
        println(Ah);
        
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
  
}

void serialEvent(Serial p) {
  serialReader.checkSerial();
}

void openPortAndGo() {
  serialPort = new Serial(this, portName, 115200);
  serialPort.clear();
  serialPort.bufferUntil(byte(SlipEnd));
  serialReader = new SerialReader(serialPort, "serial1", portName);

}

void controlEvent(ControlEvent theControlEvent) {

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

      float newslope = float(theControlEvent.controller().stringValue());

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

      slipSend((round(newslope) >> 8) & 0x03);     // Slope MSB
      slipSend(round(newslope) & 0xff);            // SLOPE LSB

      slipSend((InterceptCalibrate >> 8) & 0x03);     // INT MSB
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

    if (theControlEvent.controller().name().equals("CONNECT")) {   //this updates range values

        if (CONNECTED == false && portselected == true) {

          CONNECTED = true; 
          println("connected");
          openPortAndGo();
          LoadfromAB();
          lineGraph2.setMinMax(CAPvalue);
      }

      else if (CONNECTED == true && portselected == true) {

        CONNECTED = false;
        CON.setColorActive(color(60));
        serialPort.clear();
        serialPort.stop();
      }
    }
    
  }
}

void slipStart() {
  serialPort.write(0xC0);
}

void slipEnd() {
  serialPort.write(0xC0);
  println("");
}

void slipSend (int dataByte) { 

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

void customize2(DropdownList ddl2) {

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

void LoadfromAB() {

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
