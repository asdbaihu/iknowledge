package cn.xdf.learn.read.tpo.fentixing;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.bingo.annotation.ExcelColumn;
import com.bingo.annotation.ExcelJoinColumn;
import com.bingo.annotation.ExcelOneToMany;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import cn.xdf.learn.read.tpo.zhenti.ThirdPageProcessor;
import cn.xdf.learn.util.HtmlUtil;
import cn.xdf.learn.util.StreamTool;

/**
 * @author sunxingyang<br>
 */
public class FenSecondPageProcessor implements PageProcessor {
	@ExcelColumn(name="标题")
	private String title;
	@ExcelColumn(name="内容")
	private String neiRong;		//内容
	@ExcelOneToMany
	@ExcelJoinColumn(name = "文章id")
	List<FenThirdPageProcessor> ftppList = new ArrayList<FenThirdPageProcessor>();
	
	public String getNeiRong() {
		return neiRong;
	}

	public void setNeiRong(String neiRong) {
		this.neiRong = neiRong;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

	public List<FenThirdPageProcessor> getFtppList() {
		return ftppList;
	}

	public void setFtppList(List<FenThirdPageProcessor> ftppList) {
		this.ftppList = ftppList;
	}



	private Site site = Site.me().setSleepTime(1000).setRetryTimes(100).setUserAgent("Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")
			.setTimeOut(15000);

	public void process(Page page) {
		FenThirdPageProcessor ftp = new FenThirdPageProcessor();
		this.setNeiRong(HtmlUtil.getContent(page.getHtml().$("div.view-text-r").toString()));
		String form = page.getHtml().$("form#practiseForm").toString();
		String action = page.getHtml().$("form", "action").toString();
		try {
			Spider.create(ftp).addUrl(getAll(action, form)).run();
			ftppList.add(ftp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Site getSite() {
		return site;

	}


	public String getAll(String action, String form) throws Exception {
		
		String ac = "";
		URL url = new URL("http://toefl.kaomanfen.com" + action);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");// 提交模式
		conn.setDoOutput(true);// 是否输入参数
		conn.setReadTimeout(1000 * 60 * 10); //设置读取超时
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)"); //模拟ie浏览器
		conn.setRequestProperty("Accept-Language", "zh-cn");
		conn.setRequestProperty("Connection", "close"); //不进行持久化连接
		StringBuffer params = new StringBuffer();
		params.append(form);
		byte[] bypes = params.toString().getBytes();
		conn.getOutputStream().write(bypes);// 输入参数
		InputStream inStream = conn.getInputStream();
		ac = Html.create(new String(StreamTool.readInputStream(inStream),"utf-8")).$("form", "action").toString();
		if (ac.endsWith(",0.html")) {
			return "http://toefl.kaomanfen.com/" + ac;
		} else {
			return getAll(ac, form);
		}
	}
}
