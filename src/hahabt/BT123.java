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
            
            
            InputStream is =sc.openInputStream();
            
            System.out.println("[Message From Client] >> " + getStringFromInputStream(is));
            
        } catch (IOException ex) {
            Logger.getLogger(BT123.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
