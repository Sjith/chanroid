package kr.co.vipsapp.util.vo;

import java.io.Serializable;

public class CommentVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String seq;
	private String date;
	private String content;
	private String id;
	private String name;

	
	
	public CommentVO(String seq, String id, String name, String content, String date){
		this.seq = seq;
		this.id = id;
		this.name = name;
		this.content = content;
		this.date = date;		
	}
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
