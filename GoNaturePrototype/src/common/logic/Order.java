package common.logic;

import java.io.Serializable;


/**
 * The Order class is an entity which contain all the required details about Order.
 * This class is Serializable in order to be able to use it with the ocsf framework.
 * @Author GalBitton
 * @version 1.0.0
 */
public class Order implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// TODO: maybe change timeOfVisit to Time variable
	private String parkName,telephoneNumber,email,timeOfVisit;
	private Integer orderNumber,numberOfVisitors;
	
	public Order() {}
	
	public Order(String parkName,String telephoneNumber, String email,
			String timeOfVisit,Integer orderNumber, Integer numberOfVisitors) {
		this.parkName=parkName;
		this.telephoneNumber=telephoneNumber;
		this.email=email;
		this.timeOfVisit=timeOfVisit;
		this.orderNumber=orderNumber;
		this.numberOfVisitors=numberOfVisitors;
	}
	
	public String getParkName() {return parkName;}
	public void setParkName(String parkName) {
		this.parkName=parkName;
	}
	
	public String getTelephoneNumber() {return telephoneNumber;}
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber=telephoneNumber;
	}
	
	public String getEmail() {return email;}
	public void setEmail(String email) {
		this.email=email;
	}
	
	//TODO: check maybe change this to TIME variable
	public String getTimeOfVisit() {return timeOfVisit;}
	public void setTimeOfVisit(String timeOfVisit) {
		this.timeOfVisit=timeOfVisit;
	}
	
	public Integer getOrderNumber() {return orderNumber;}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber=orderNumber;
	}
	
	public Integer getNumberOfVisitors() {return numberOfVisitors;}
	public void setNumberOfVisitors(Integer numberOfVisitors) {
		this.numberOfVisitors=numberOfVisitors;
	}
}
