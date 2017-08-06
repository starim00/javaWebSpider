package cn.edu.htc.webspider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSpider {
    public void myPrint(String baseUrl) {
        Map<String, Boolean> oldMap = new LinkedHashMap<String, Boolean>();
        String oldLinkHost = "";  //host
        Pattern p = Pattern.compile("(https?://)?[^/\\s]*");
        Matcher m = p.matcher(baseUrl);
        if (m.find()) {
            oldLinkHost = m.group();
        }
        oldMap.put(baseUrl, false);
        oldMap = crawlLinks(oldLinkHost, oldMap);
        for (Map.Entry<String, Boolean> mapping : oldMap.entrySet()) {
            System.out.println("获取链接" + mapping.getKey());

        }
    }
    private Map<String, Boolean> crawlLinks(String oldLinkHost,Map<String, Boolean> oldMap) {
        Map<String, Boolean> newMap = new LinkedHashMap<>();
        String oldLink = "";

        for (Map.Entry<String, Boolean> mapping : oldMap.entrySet()) {
            System.out.println("link:" + mapping.getKey() + "--------check:"
                    + mapping.getValue());
            if (!mapping.getValue()) {
                oldLink = mapping.getKey();
                try {
                    URL url = new URL(oldLink);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);

                    if (connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        String line = "";
                        Pattern pattern = Pattern.compile("read\\.php\\?tid=\\d*");
                        Matcher matcher = null;
                        while ((line = reader.readLine()) != null) {
                            matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                String newLink = matcher.group(); 
                                if (!newLink.startsWith("http")) {
                                    if (newLink.startsWith("/"))
                                        newLink = "https://rm93.com/" + newLink;
                                    else
                                        newLink = "https://rm93.com/" + "/" + newLink;
                                }
                                if(newLink.endsWith("/"))
                                    newLink = newLink.substring(0, newLink.length() - 1);
                                
                                if (!oldMap.containsKey(newLink) && !newMap.containsKey(newLink) && newLink.startsWith(oldLinkHost)) {
                                   
                                    newMap.put(newLink, false);
                                }
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                oldMap.replace(oldLink, false, true);
            }
        }
        if (!newMap.isEmpty()) {
            oldMap.putAll(newMap);
            oldMap.putAll(crawlLinks(oldLinkHost, oldMap));  //鐢变簬Map鐨勭壒鎬э紝涓嶄細瀵艰嚧鍑虹幇閲嶅鐨勯敭鍊煎
        }
        return oldMap;
    }
}
