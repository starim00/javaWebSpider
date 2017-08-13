package cn.edu.htc.webspider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class GetHtml {
	public String getHTML(String url) throws URISyntaxException, ClientProtocolException,IOException{
        CookieSpecProvider cookieSpecProvider = new CookieSpecProvider() {
            @Override
            public CookieSpec create(HttpContext httpContext) {
                return new BrowserCompatSpec(){
                    @Override
                    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {

                    }
                };
            }
        };
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create().register(CookieSpecs.BEST_MATCH,new BestMatchSpecFactory()).register(CookieSpecs.BROWSER_COMPATIBILITY,new BrowserCompatSpecFactory()).register("cookie",cookieSpecProvider).build();
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec("cookie").setSocketTimeout(5000).setConnectTimeout(5000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieSpecRegistry(r).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet =new HttpGet(url);
        httpGet.setConfig(requestConfig);
        String html = "获取失败";
        try {
        	CloseableHttpResponse reponse = httpClient.execute(httpGet);
        	html = EntityUtils.toString(reponse.getEntity());
            
        }catch (IOException e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        return html;
    }
	public void writeWebTxt(String html,String savePath) throws IOException{
		File htmlTxt = new File(savePath);
		FileWriter fWriter = new FileWriter(htmlTxt);
		BufferedWriter bWriter = new BufferedWriter(fWriter);
		System.out.println(html);
		bWriter.write(html);
		bWriter.close();
	}
	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException{
		GetHtml test = new GetHtml();
		String html = test.getHTML("https://rm93.com/thread.php?fid=53");
		String savePath = "C:\\Users\\starim\\eclipse-workspace\\javaWebSpider\\test.txt";
		if(html!="获取失败") {
			test.writeWebTxt(html, savePath);
		}
	}
}
