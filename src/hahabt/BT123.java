/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/// REF: http://www.oracle.com/technetwork/articles/javame/index-156193.html
/// REF: http://www.oracle.com/technetwork/java/javame/tech/index-140411.html


package hahabt;

import java.io.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.*;
import javax.microedition.io.*;

/**
 *
 * @author 102098
 */
public class BT123 {
    private static final String URL_SCHEME_BTSPP = "btspp";
    private static final String URL_SCHEME_BTL2CAP = "btl2cap"; 
                
    private String serverURL = "";    
    private LocalDevice localDevice; // local Bluetooth Manager
    private DiscoveryAgent discoveryAgent; // discovery agent
    
    /* Private Method */
    private String getServerURL(String urlScheme, String uuid, boolean auth, boolean encrypt, String name) {
        //btspp: //hostname: [ CN | UUID ]; parameters
        //btl2cap: //hostname: [ PSM | UUID ]; parameters
        String URL = "";
        URL += urlScheme + "://localhost:" +  uuid + ";";
        
        if(auth) URL += "authenticate=true;";
        else URL += "authenticate=false;";
        
        if(encrypt) URL += "encrypt=true;";
        else URL += "encrypt=false;";
        
        URL += "name="+ name +";";
        
        return URL;
    }
    
    private static String getStringFromInputStream(InputStream is) {

	BufferedReader br = null;
	StringBuilder sb = new StringBuilder();

	String line;
	try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            } catch (IOException e) {
		e.printStackTrace();
            } finally {
		if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
			e.printStackTrace();
                    }
		}
            }

            return sb.toString();
    }
    
    private void threadRx(StreamConnection sc) {
        BT_SPP_SERVER_RX rx = new BT_SPP_SERVER_RX();
        rx.setSc(sc);
        rx.main(null);
    }
    
    private void threadTx(StreamConnection sc) {
        BT_SPP_SERVER_TX tx = new BT_SPP_SERVER_TX();
        tx.setSc(sc);
        tx.main(null);
    }
    
    
    /* Public Method */
    public void btInit() throws BluetoothStateException {
        System.out.println("[BT INIT]");
        localDevice = null;
        discoveryAgent = null;
        // Retrieve the local device to get to the Bluetooth Manager
        localDevice = LocalDevice.getLocalDevice();                   
        // Servers set the discoverable mode to GIAC
        localDevice.setDiscoverable(DiscoveryAgent.GIAC);
        /// DiscoveryAgent.GIAC specifies General Inquiry Access Code. 
        //// No limit is set on how long the device remains in the discoverable mode.
        /// DiscoveryAgent.LIAC specifies Limited Inquiry Access Code. 
        //// The device will be discoverable for only a limited period of time, typically one minute.
        ///// After the limited period, the device automatically reverts to undiscoverable mode
        
        //Clients retrieve the discovery agent
        discoveryAgent = localDevice.getDiscoveryAgent();
    }
    
    public void btConn() {
        String connURL = getServerURL(URL_SCHEME_BTSPP, "2d26618601fb47c28d9f10b8ec891363", false, false, "MyBt");

        try {            
            StreamConnectionNotifier scn = (StreamConnectionNotifier)Connector.open(connURL);// Create a server connection (a notifier)
            
            StreamConnection sc = scn.acceptAndOpen();// Accept a new client connection
                        
            // Thread: Server's TX
            threadTx(sc);
            // Thread: Server's RX
            threadRx(sc);
            // Thread: Keep Main Thread Run...
            for(;;);
            
        } catch (IOException ex) {
            Logger.getLogger(BT123.class.getName()).log(Level.SEVERE, null, ex);
        }    
    } 
}

class BT_SPP_SERVER_RX extends Thread {
    private static StreamConnection sc = null;
  
    public void run() {
        System.out.println("[THREAD] spp rx thread start up!!");
        
        try {
            if(getSc() != null) {
                final InputStream is = getSc().openInputStream();
                final byte[] buffer = new byte[1024];
                
                for(;;) {
                    final int readBytes = is.read(buffer);
                    final String receivedMessage = new String(buffer, 0, readBytes);
                
                    System.out.println("[SERVER] Rx: " + receivedMessage);
                }
            }
        
        } catch (IOException ex) {
            Logger.getLogger(BT_SPP_SERVER_RX.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }

    public static void main(String args[]) {
        (new BT_SPP_SERVER_RX()).start();
    }
    
    public void setSc(StreamConnection sc) {
         if(sc != null) {
            //System.out.println("[rx][set sc]");
            this.sc = sc;    
         }
    }
    
    private StreamConnection getSc() {
        if(this.sc != null) {
            //System.out.println("[rx][get sc]");
            return this.sc;
        } else {
            System.out.println("[rx][sc is null]");
            return null;
        }
    }
}

class BT_SPP_SERVER_TX extends Thread {
    private static StreamConnection sc = null;
            
    public void run()  {
        System.out.println("[THREAD] spp tx thread start up!!");
     
        String message = "Welcome!!";
 
        DataOutputStream os = null;
        try {
            os = getSc().openDataOutputStream();
            
            java.io.InputStream in = System.in;
            char c = 0;
            do {
                if(c != 0 && c != '\n') message += c;
                
                if(c == '\n' || message.equals("Welcome!!")) {
                    os.write(message.getBytes());
                    os.flush();
                    message = "";
                }
            } while((c = (char) in.read()) > 0);
            
        } catch (IOException ex) {
            Logger.getLogger(BT_SPP_SERVER_TX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void main(String args[]) {
        (new BT_SPP_SERVER_TX()).start();
    }
    
    public void setSc(StreamConnection sc) {
         if(sc != null) {
            this.sc = sc;    
         }
    }
    
    private StreamConnection getSc() {
        if(this.sc != null) {
            return this.sc;
        } else {
            System.out.println("[tx][sc is null]");
            return null;
        }
    }
}
