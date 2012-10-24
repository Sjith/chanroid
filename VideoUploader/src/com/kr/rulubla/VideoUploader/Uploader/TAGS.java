package com.kr.rulubla.VideoUploader.Uploader;

public interface TAGS {

	public static final String TAG = "Uploader";

	public static final String boundary = "----------Ef1Ef1cH2Ef1ae0Ef1gL6ae0gL6gL6";
	public static final String HYPEN = "--";
	public static final String CRLF = "\r\n";
	
	// fileUploader.asp
		// params
	public static final String UPLOAD_VIDEO = "Upload";
		// elements
	public static final String UPLOAD_RESULT = "rtnValue";
	public static final String UPLOAD_CID = "cID";
	public static final String UPLOAD_MESSAGE = "message";
	
	// thumbCheck.asp
		// params
	public static final String IMAGES_CID = "cid";
		// elements
	public static final String IMAGES_RESULT = "extract";
	public static final String IMAGES_URL = "thumbURI";
	public static final String IMAGES_NAME = "thumbName";
	public static final String IMAGES_LIST = "thumbList";
	public static final String IMAGES = "thumbnail";
	
	// thumbUploader.asp
		// params
	public static final String IMGUPLOAD_CID = IMAGES_CID;
		// elements
	public static final String IMGUPLOAD_RESULT = UPLOAD_RESULT;
	public static final String IMGUPLOAD_NUM = "tid";
	public static final String IMGUPLOAD_URL = "url";
	public static final String IMGUPLOAD_PATH = "uImage";
	public static final String IMGUPLOAD_MESSAGE = UPLOAD_MESSAGE;
	
	// thumbSelect.asp
		// params
	public static final String THUMB_CID = IMAGES_CID;
	public static final String THUMB_SEQ = "seq";
		// elements
	public static final String THUMB_RESULT = "select";
	public static final String THUMB_MESSAGE = UPLOAD_MESSAGE;
	
	// transCheck.asp
		// params
		// elements
	public static final String TRANS_CID = "cid";
	public static final String TRANS_RESULT = "rtnValue";
	public static final String TRANS_PROGRESS = "percent";
	public static final String TRANS_LENGTH = "duration";
	public static final String TRANS_URL = "vodURL";
	public static final String TRANS_THUMB_URL = "thumbURL";
	public static final String TRANS_MESSAGE = "message";
	
	
}
