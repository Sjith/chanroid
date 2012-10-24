package com.kr.rulubla.VideoUploader.Uploader;

import java.util.ArrayList;

import android.graphics.Bitmap;

// 썸네일 추출 결과가 저장되는 객체
public class ThumbResult {

	ArrayList<String> imgarray;
	ArrayList<Bitmap> bitarray;
	
	String result;
	String imgpath;
	String imgname;
	
	// 실제로 동작을 수행하는 클래스에서 url Array를 세팅할 때 사용
	public void setImgArray(ArrayList<String> array) {
		this.imgarray = array;
	}
	
	// 세팅된 url 배열을 가져온다.
	public ArrayList<String> getImgArray() {
		return imgarray;
	}
	
	// 동작을 수행하는 클래스에서 비트맵 배열을 세팅한다.
	public void setBitmapArray(ArrayList<Bitmap> array) {
		this.bitarray = array;
	}
	
	// 세팅된 url 배열으로 생성된 비트맵 배열을 가져온다.
	public ArrayList<Bitmap> getBitmapArray() {
		return bitarray;
	}
	
	// 전송 실패시 에러 메시지를 설정한다.
	public void setResult(String result) {
		this.result = result;
	}
	
	// 전송 실패시 설정된 에러 메시지를 가져온다.
	public String getResult() {
		return result;
	}
	
	// 썸네일이 저장된 인터넷상의 경로를 세팅한다. (썸네일 업로드 시에 사용)
	public void setImgPath(String path) {
		this.imgpath = path;
	}
	
	// 썸네일 업로드 시 사용하기 위한 썸네일 저장경로를 가져온다.
	public String getImgPath() {
		return imgpath;
	}
	
	// 썸네일 업로드시 fileName 파라미터로 사용될 값을 세팅한다.
	public void setImgName(String name) {
		this.imgname = name;
	}

	// 썸네일 업로드시 fileName 파라미터로 사용될 값을 가져온다.
	public String getImgName() { 
		return imgname;
	}
	
}
