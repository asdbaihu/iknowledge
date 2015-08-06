package cn.xdf.learn.read.tpo.fentixing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.bingo.annotation.ExcelColumn;
import com.bingo.annotation.ExcelId;
import com.bingo.io.DefaultExcelEntityWriter;
import com.bingo.io.EntityWriter;

/**
 * @author sunxingyang<br>
 */
public class FenFirstPageProcessor implements PageProcessor {
	@ExcelId(auto = true)
	private Integer id;
	@ExcelColumn(name="类型")
	private String category;
	private List<FenSecondPageProcessor> fspList= new ArrayList<FenSecondPageProcessor>(); 
	public List<FenSecondPageProcessor> getFspList() {
		return fspList;
	}

	public void setFspList(List<FenSecondPageProcessor> fspList) {
		this.fspList = fspList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	private Site site = Site.me().setSleepTime(1000).setRetryTimes(100)
			.setTimeOut(15000);
    static int index = 1;
	public void process(Page page) {
		List<String> titles = page.getHtml().$("li","data-title").regex("TPO.*").all();
		List<String> urls = page.getHtml().$("li","data-url").regex("/reading/.*").all();
		for (int i =0; i<1 ;i++) {
			FenSecondPageProcessor fsp = new FenSecondPageProcessor();
			Spider.create(fsp).addUrl("http://toefl.kaomanfen.com"+urls.get(i)).run();
			fsp.setTitle(titles.get(i));
			System.out.println("--------"+fsp.getTitle());
			System.out.println("--------"+fsp.getNeiRong());
			fspList.add(fsp);
		}
		
	}

	public Site getSite() {
		return site;

	}

	public static void main(String[] args) {
		List<FenFirstPageProcessor> ffpList = new ArrayList<FenFirstPageProcessor>();
		FenFirstPageProcessor ffp1 = new FenFirstPageProcessor();
		FenFirstPageProcessor ffp2 = new FenFirstPageProcessor();
		long startTime = System.currentTimeMillis();
			Spider.create(ffp1).addUrl("http://toefl.kaomanfen.com/read/special1").run();
			ffpList.add(ffp1);
			Spider.create(ffp2).addUrl("http://toefl.kaomanfen.com/read/special2?t=special2&s=1#slide1").run();
			ffpList.add(ffp2);
		System.out.println("爬取用时：" + (System.currentTimeMillis() - startTime)
				/ 1000 / 60 + "分");

		for (FenFirstPageProcessor fenFirstPageProcessor : ffpList) {
			List<FenSecondPageProcessor> fspl = fenFirstPageProcessor.getFspList();
			for (FenSecondPageProcessor fenSecondPageProcessor : fspl) {
				List<FenThirdPageProcessor> fsp = fenSecondPageProcessor.getFtppList();
				for (FenThirdPageProcessor fenThirdPageProcessor : fsp) {
					List<FenFourthPageProcessor> ffp = fenThirdPageProcessor.getFfpList();
					for (FenFourthPageProcessor fenFourthPageProcessor : ffp) {
						String ss = fenFourthPageProcessor.getAna();
						System.out.println("--------"+ss);
					}
				}
			}
		}
		
		/*EntityWriter writer = new DefaultExcelEntityWriter("E://test//TPO分题型.xlsx");
		Map<Object, Object> params = new HashMap<Object, Object>();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(FenFirstPageProcessor.class);
		classes.add(FenSecondPageProcessor.class);
		classes.add(FenThirdPageProcessor.class);
		classes.add(FenFourthPageProcessor.class);
		classes.add(Item.class);
		params.put(DefaultExcelEntityWriter.SHEET_WIDTH_HEADS, classes);
		params.put(DefaultExcelEntityWriter.SHEET_ORDER, classes);
		params.put(DefaultExcelEntityWriter.WRITE_EXCEL_OBJECT, true);
		writer.setParamters(params);
		writer.write(ffpList, FenFirstPageProcessor.class);*/
	}

}
