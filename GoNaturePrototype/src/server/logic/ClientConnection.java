package server.logic;

import javafx.beans.property.SimpleStringProperty;


/**
 * The ClientConnection class is an entity which contains the client information.
 * This class use SimpleStringProperty in order to be able to observe it smoothly in UI.
 * @Author GalBitton
 * @version 1.0.0
 */
public class ClientConnection {
	private SimpleStringProperty hostIp,hostName,status;
	
	public ClientConnection(String hostIp,String hostName, String status) {
		this.hostIp=new SimpleStringProperty(hostIp);
		this.hostName=new SimpleStringProperty(hostName);
		this.status=new SimpleStringProperty(status);
	}
	
	public String getHostName() {
		return hostName.get();
	}
	
	public void setHostName(String hostName) {
		this.hostName.set(hostName);
	}
	
	public String getHostIp() {
		return hostIp.get();
	}
	
	public void setHostIp(String hostIp) {
		this.hostIp.set(hostIp);
	}
	
	public String getStatus() {
		return status.get();
	}
	
	public void setStatus(String status) {
		this.status.set(status);
	}
	

}
