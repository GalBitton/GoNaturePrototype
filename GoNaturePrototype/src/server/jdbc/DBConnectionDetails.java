package server.jdbc;

public class DBConnectionDetails {

	private String name;
	private String ip;
	private String username;
	private String password;
	
	public DBConnectionDetails(String name, String ip, String username, String password) {
		this.name=name;
		this.ip=ip;
		this.username=username;
		this.password=password;
	}
	
	public DBConnectionDetails() {}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip=ip;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username=username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password=password;
	}
}
