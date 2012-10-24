package kr.co.vipsapp;


import android.os.Handler;

import com.spoledge.aacplayer.AACPlayer;


class RadioPlayer  {

	private static RadioPlayer instance = new RadioPlayer();

	
	public  final int STREAMING_BUFFER_AUDIO = 1000;
	public  final int STREAMING_BUFFER_DECODE = 700;
	
	//public  final String STREAMING_MMS_URL = "mms://115.68.15.116/efm/";
//	public  final String STREAMING_WMA_URL = "http://www.befm.or.kr/";
	//public  final String BUSAN_EFM = "http://www.befm.or.kr";
	
    private int playFeathers = 0;
    private int tabFeathers = 0;
    
    public final int PLAY_OFF		= 0;
    public final int PLAY_RADIO 	= 1;
    public final int PLAY_NEWS 	= 2;
    public final int PLAY_AOD_EFM 	= 4;
    public final int PLAY_AOD_ICAN	= 8;
    public final int PLAY_AOD_WHAT	= 16;	
    
    public final int TAB_RADIO = 1;
    public final int TAB_NEWS = 2;
    public final int TAB_AOD = 4;
    public final int TAB_SETTING = 8;
	
	private Handler uiHandler;	
	   
    private AACPlayer aacPlayer;
	
    private boolean playerStarted;
        
    private int scriptFontSize = 20;
    
    private String autoLogin;
    
    private int viewDepth = 0;
    
    private boolean isPlaying = false;
    
    private boolean isLoading = false;
    
    /* tab 내에서 뷰의 깊이*/
    private int[] tabViewDepth = {0,0,0,0};
    private int tab=0;
    
    public final int NOTIFICATION_ID = 9148; 
    //private String streamURL;
	
	private RadioPlayer(){
		uiHandler = new Handler();
	}

	public static RadioPlayer getInstance() {
		return instance;
	}



	public Handler getUiHandler() {
		return uiHandler;
	}

	public AACPlayer getAacPlayer() {
		return aacPlayer;
	}

	public boolean isPlayerStarted() {
		return playerStarted;
	}

	public void setPlayerStarted(boolean playerStarted) {
		this.playerStarted = playerStarted;
	}

	public void setAacPlayer(AACPlayer aacPlayer) {
		if (this.aacPlayer != null) {
			this.aacPlayer.setPlayerCallback(null);
			this.aacPlayer.stop();
		}
		this.aacPlayer = aacPlayer;
		System.gc();
	}
	
	public void setPlayFeathers(int playFeathers) {
		this.playFeathers = playFeathers;
	}

	public int getPlayFeathers() {
		return playFeathers;
	}
	
	public void setTabFeathers(int tabFeathers) {
		this.tabFeathers = tabFeathers;
	}
	
	public int getTabFeathers() {
		return this.tabFeathers;
	}

	public int getScriptFontSize() {
		return scriptFontSize;
	}

	public void setScriptFontSize(int scriptFontSize) {
		this.scriptFontSize = scriptFontSize;
	}

	public String getAutoLogin() {
		return autoLogin;
	}

	public void setAutoLogin(String autoLogin) {
		this.autoLogin = autoLogin;
	}

	public int getViewDepth() {
		return viewDepth;
	}

	public void setViewDepth(int viewDepth) {
		this.viewDepth = viewDepth;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}

	public int getTabViewDepth() {
		return tabViewDepth[getTab()];
	}

	public void setTabViewDepth(int tabViewDepth) {
		this.tabViewDepth[getTab()] = tabViewDepth;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}


	
    
}
