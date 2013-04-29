package model;


public abstract class CCModel {
	
	public final boolean isY(String flag) {
		return "Y".equals(flag);
	}
	
	public final String isY(boolean flag) {
		return flag ? "Y" : "N";
	}
	
}
