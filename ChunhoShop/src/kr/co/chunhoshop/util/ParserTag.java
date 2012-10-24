package kr.co.chunhoshop.util;

public interface ParserTag {

	// 페이스북 인증 코드
	public static final String FACEBOOK_APP_ID = "251026131605033";
	public static final int FACEBOOK_AUTH_CODE = 32665;

	public static final String TWITTER_CONSUMER_KEY = "kaJFLoHFQkqlh5h4yHLFpA";
	public static final String TWITTER_CONSUMER_SECRET = "o0FRhVJoK9Z4VKNGz8sTU5FM4TCIuw0AfNCIjpqhBk";
	public static final String TWITTER_CALLBACK_URL = "http://www.chunhoshop.com/";
	public static final String MOVE_TWITTER_LOGIN = "com.android.twittercon.TWITTER_LOGIN";
	public static final int TWITTER_LOGIN_CODE = 10;

	public static boolean D = true;

	public String PAGE = "http://www.chunhoshop.com";
	public String CHARACTER_SET = "UTF-8";
	public String ITEMLIST = "item_list";
	public String ITEM = "item";

	// product item
	public String PNAME = "p_name";
	public String PNUM = "p_num";
	public String PPRICE = "p_price";
	public String PIMG = "p_img";
	public String PPOINT = "p_point";
	public String PCONTENT = "p_content";
	public String PCOUNT = "p_count";
	public String PGIFT = "p_gift";
	public String PNO = "p_no";
	public String PNOLIST = "p_number_list";
	public String PMAKER = "p_maker";
	public String PVIDEO = "p_movie_url";
	public String PVIDEOLENGTH = "p_movie_second";

	//
	public String PEVENT = "p_event_flag";
	public String PMONTH = "p_month_flag";
	public String PMONTHURL = "p_month_url";
	public String PNEW = "p_new_flag";
	public String PHEALTH = "p_health_flag";

	public String PTXT1 = "p_view_txt_1";
	public String PTXT2 = "p_view_txt_2";
	public String PTXT3 = "p_view_txt_3";
	public String PTXT4 = "p_view_txt_4";
	public String PTXT5 = "p_view_txt_5";

	public String PVIEWIMG1 = "p_view_img_1";
	public String PVIEWIMG2 = "p_view_img_2";
	public String PVIEWIMG3 = "p_view_img_3";
	public String PVIEWIMG4 = "p_view_img_4";
	public String PVIEWIMG5 = "p_view_img_5";

	public String PCARDFLAG = "p_card_flag";
	public String PCARDNAME = "p_card_name";
	public String PCARDCONTENT = "p_card_content";

	public String POPTIONAREA = "p_option_area";
	public String POPTION = "p_option";
	public String POPTIONNAME = "p_option_name";
	public String POPTIONVALUE = "p_option_value";
	public String POPTIONMINCNT = "p_option_min_cnt";
	public String POPTIONTYPE = "p_option_type";
	// 2011-08-25 added tags : product item

	public String POPFLAG = "popup_flag";
	public String POPURL = "popup_imgurl";

	// comment item
	public String CNUM = "c_idx";
	public String CNAME = "c_name";
	public String CSUBJECT = "c_subject";
	public String CCONTENT = "c_content";
	public String CDATE = "c_date";
	public String CIMG = "c_files";

	// notice item
	public String NNUM = "n_num";
	public String NCATEGORY = "n_category";
	public String NWRITER = "n_writer";
	public String NSUBJECT = "n_subject";
	public String NDATE = "n_regdate";
	public String NHIT = "n_hit";
	public String NCONTENT = "n_contents";

	// bonus(?) item
	public String GNUM = "g_num";
	public String GNAME = "g_name";
	public String GIMG = "g_img";

	// auth item
	public String ANAME = "authname";
	public String ASTATE = "authstate";
	public String AAUTO = "authauto";
	public String ATEL = "authhtel";
	public String AGENT = "agentname";
	public String AGENTPIC = "agentimg";

	// zone category item
	public String ITEMCONTENT1 = "item_content1";
	public String ITEMCONTENT2 = "item_content2";
	public String ITEMCONTENT3 = "item_content3";
	public String ITEMCONTENT4 = "item_content4";
	public String ITEMCONTENT5 = "item_content5";
	public String ITEMCONTENT6 = "item_content6";

	// search category item
	public String CATEGORY1 = "item_category1";
	public String CATEGORY2 = "item_category2";
	public String CATEGORY3 = "item_category3";
	public String CATEGORY4 = "item_category4";
	public String CATEGORY5 = "item_category5";
	public String CATEGORY6 = "item_category6";

	public String CCONTENT1 = "item_content1";
	public String CCONTENT2 = "item_content2";
	public String CCONTENT3 = "item_content3";
	public String CCONTENT4 = "item_content4";
	public String CCONTENT5 = "item_content5";
	public String CCONTENT6 = "item_content6";

	public String CATEGORYNAME = "index_name";
	public String CATEGORYURL = "index_url";
	public String CATEGORYNUM = "index_num";

	// event item
	public String ENUM = "e_num";
	public String ETITLE = "e_title";
	public String EIMG = "e_img";
	public String ECONTENT = "e_content";
	public String ESDATE = "s_date";
	public String EEDATE = "e_date";
	public String ECASE1 = "e_case1";
	public String ECASE2 = "e_case2";
	public String EURL = "return_url";

	// order list item
	public String ONUM = "o_num";
	public String OPRICE = "o_total_price";
	public String OSTATE = "o_state";
	public String OSUBITEM = "sub_item";
	public String ODATE = "o_date";
	public String OOPTION = "o_option";

	public String[] ORDERITEMS = { ONUM, OPRICE, OSTATE, ODATE, OOPTION };

	public String[] CATEGORYS = { CATEGORY1, CATEGORY2, CATEGORY3, CATEGORY4,
			CATEGORY5, CATEGORY6 };

	public String[] CCONTENTS = { CCONTENT1, CCONTENT2, CCONTENT3, CCONTENT4,
			CCONTENT5, CCONTENT6 };

	public String[] CITEMS = { CATEGORYNAME, CATEGORYURL, CATEGORYNUM };

	// faq item
	public String FAQNUM = "b_num";
	public String FAQTITLE = "b_title";
	public String FAQCONTENT = "b_content";

	// xml tag array.
	public String TAGS[] = { PAGE, PCONTENT, PCOUNT, PEVENT, PMONTH, PNEW,
			PVIDEO, PMONTHURL, PCARDFLAG, PCARDNAME, PCARDCONTENT, PMAKER, POPTIONNAME,
			PHEALTH, PIMG, PNAME, PNO, PNOLIST, PNUM, PPOINT, PPRICE, AGENT, AGENTPIC,
			PVIDEOLENGTH, ANAME, ASTATE, AAUTO, ATEL, CCONTENT, CDATE, CIMG,
			CNAME, CNUM, CSUBJECT, NNUM, NCATEGORY, NSUBJECT, NDATE, NHIT,
			NCONTENT, FAQNUM, FAQTITLE, FAQCONTENT, GNUM, GNAME, GIMG, ENUM,
			ETITLE, EIMG, ECONTENT, ESDATE, EEDATE, ECASE1, ECASE2, EURL,
			POPFLAG, POPURL };

	public String[] CONTENT = { CATEGORYNAME, CATEGORYURL, CATEGORYNUM };

	public String OPTIONS[] = { POPTIONAREA, POPTION, POPTIONNAME,
			POPTIONVALUE, POPTIONMINCNT, POPTIONTYPE };

	public String PTXTS[] = { PTXT1, PTXT2, PTXT3, PTXT4, PTXT5 };

	public String PIMGS[] = { PVIEWIMG1, PVIEWIMG2, PVIEWIMG3, PVIEWIMG4,
			PVIEWIMG5 };

	// xml pages
	public String ALLPRODUCT = "/xml/m_product.asp";
	public String BESTPRODUCT = "/xml/m_product_best.asp";
	public String EVENTLIST = "/xml/m_product_event.asp";
	public String NEWPRODUCT = "/xml/m_list_category.asp?mode=main";
	public String NOTICELIST = "/xml/m_product_notice.asp";

	public String EVENTDESC = "/xml/m_product_m_event_veiw.asp?e_num=";
	public String EVENTBOARD = "/xml/m_product_m_event.asp";
	public String EVENTINPUT = "/xml/m_event_input.asp?";
	public String ORDERPAGE = "/xml/m_order_input.asp?";

	public String NOTICEITEM = "/xml/m_product_notice_view.asp?n_num=";
	public String BONUSLIST = "/xml/m_product_gift.asp?g_category=";
	public String CATEGORY = "/xml/m_product_search.asp?p_name=";
	public String CATEGORYPAGE = "/xml/m_list_category.asp?mode=category";
	public String COMMENTDESC = "/xml/m_product_comment_view.asp?c_idx=";
	public String COMMENTLIST = "/xml/m_product_comment.asp?p_num=";
	public String PNAMESEARCH = "/xml/m_product_search.asp?p_name=";
	public String PNUMSEARCH = "/xml/m_product_detail.asp?p_num=";

	public String POPPAGE = "/xml/m_popup.asp";
	public String FAQPAGE = "/xml/m_product_faq.asp";
	public String LOGINPAGE = "/xml/m_auth.asp?";
	public String ZONECATEGORY = "/xml/m_list_category.asp?mode=chunhozone";
	public String SAMPLESEND = "/xml/m_product_sample_input.asp?";
	public String ORDERLIST = "/xml/m_orders.asp?";
}
