package kr.co.drdesign.golffinder;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;

public class SearchScreenGCInfo extends SearchGCInfo
{
	public static final String QUERY = "category:��ũ��������";
	public static final String CATEGORY = "\"��ũ��������\"";
	public static ArrayList<Map<String,String>> itemList = new ArrayList<Map<String,String>>();

	public SearchScreenGCInfo(Context context)
	{
		super(context, QUERY, CATEGORY);
	}
}