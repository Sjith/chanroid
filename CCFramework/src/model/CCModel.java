package model;


public abstract class CCModel {
	private boolean isLoaded = false;

	public final boolean isLoaded() {
		return isLoaded;
	}

	public final void setLoaded(boolean flag) {
		isLoaded = flag;
	}
}
