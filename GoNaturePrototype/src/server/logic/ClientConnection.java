package server.logic;

import javafx.beans.property.SimpleStringProperty;

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
