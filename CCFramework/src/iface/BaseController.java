package iface;


public interface BaseController extends BaseModelCallback {
	public abstract void registerModelCallback();
	public abstract void unregisterModelCallback();
}
