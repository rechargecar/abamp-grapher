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
 

import processing.serial.*;

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
        buffer[offset + bytesReceived++] = int(b);
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
  void slipWrite(int[] buffer, int offset, int pSize) {
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

