package sns.me2day;


public class Me2PostData {
	public String body;
	public String tag;
	public int icon = 0;
	public String longitude;
	public String latitude;
	public String spotId;
	public String location;
	public String attachment;
	public boolean useGZip = true;
	public boolean isCloseComment = false;
	public String headerFileName = null;
	public int retrycount = 1;
	
	public String getUserAgent() {
		return "me2day/(Android OS)";
	}
}
