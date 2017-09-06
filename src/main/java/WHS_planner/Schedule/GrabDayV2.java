package WHS_planner.Schedule;

import WHS_planner.Main;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GrabDayV2 {

    private String error;

    private List<String> cookies;


    private String user, password;

    private HttpsURLConnection connection;

    private final String USER_AGENT = "Mozilla/5.0";

    String url = "https://ipass.wayland.k12.ma.us/school/ipass/syslogin.html";

    public GrabDayV2(String user, String password) {
        this.user = user;
        this.password = password;

    }


    public boolean testConn() throws Exception
    {

        String url = "https://ipass.wayland.k12.ma.us/school/ipass/syslogin.html";


        CookieHandler.setDefault(new CookieManager());
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


        Grabber grab = new Grabber();

        String page = grab.getPageContent(url);
        String params = grab.getForm(page, user, password);

        grab.send(url, params);

        connection.disconnect();

        return !error.contains("Invalid");
    }

    public void getSchedule() throws Exception{
        CookieHandler.setDefault(new CookieManager());
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        Grabber g = new Grabber();

        String html = g.getPageContent(url);
        String params = g.getForm(html, user, password);
        g.send(url, params);

        String s1 = g.getPageContent("https://ipass.wayland.k12.ma.us/school/ipass/samschedule.html");
        Document d = Jsoup.parse(s1);
        Element e = d.getElementsByTag("a").get(1);
        String anchor = e.toString();

        anchor = anchor.substring(anchor.indexOf("sam"), anchor.indexOf("')"));
        String fin = g.getPageContent("https://ipass.wayland.k12.ma.us/school/ipass/"+anchor);


        File f = new File(Main.SAVE_FOLDER + File.separator +"output.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(fin);
        bw.close();

        connection.disconnect();

    }


    private class Grabber
    {
        private Grabber()
        {

        }

        private String getPageContent(String url) throws Exception
        {
            URL ipass = new URL(url);
            connection = (HttpsURLConnection) ipass.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("User-Agent", USER_AGENT);

            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");

            if (cookies != null)
            {
                for (String cookie : cookies)
                {
                    //System.out.println(cookie);
                    connection.setRequestProperty("Cookie", cookie.split(";", 2)[1]);
                }
            }

            int resp = connection.getResponseCode();

            //System.out.println("\nSending GET request to " + url);
            //System.out.println("Response code is: "+resp);

            InputStreamReader ir = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(ir);

            String inLine;
            StringBuilder response = new StringBuilder();

            while((inLine = br.readLine()) != null)
            {
                response.append(inLine);
                response.append("\n");
            }
            br.close();
            ir.close();

            return response.toString();
        }



        private String getForm(String html, String user, String pass) throws Exception
        {
            //System.out.println("Getting form data...");

            Document doc = Jsoup.parse(html);

            Element loginform = doc.getElementById("login");
            Elements inputelements = loginform.getElementsByTag("input");

            ArrayList<String> params = new ArrayList<>();

            for(Element el : inputelements)
            {
                String key = el.attr("name");
                String val = el.attr("value");

                if(key.equals("userid"))
                {
                    val = user;
                }
                else if(key.equals("password"))
                {
                    val = pass;
                }

                params.add(key + "=" + URLEncoder.encode(val, "UTF-8"));
            }

            StringBuffer buffer = new StringBuffer();

            for(String param : params)
            {
                if(buffer.length() == 0)
                {
                    buffer.append(param);
                }
                else
                {
                    buffer.append("&" + param);
                }
            }

            return buffer.toString();
        }


        private void send(String url, String params) throws Exception
        {
            //System.out.println("Attempting to send data");

            URL obj = new URL(url);

            connection = (HttpsURLConnection) obj.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");

            if(cookies != null) {
                for (String cookie : cookies) {
                    connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }
            }


            connection.setDoOutput(true);
            connection.setDoInput(true);

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

            System.out.println(params);
            dos.writeBytes(params);
            dos.flush();
            dos.close();


            int response = connection.getResponseCode();

            //System.out.println("\nSending POST request to "+url);
           // System.out.println("Parameters : " + params); //Unsafe
           // System.out.println("Response code:" + response);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuffer stringbuffer = new StringBuffer();

            while((input = br.readLine()) != null)
            {
                stringbuffer.append(input);
            }


            setCookies(connection.getHeaderFields().get("Set-Cookie"));
            error = stringbuffer.toString();
            br.close();
        }

        private List<String> getCookies()
        {
            return cookies;
        }

        private void setCookies(List<String> cs)
        {
            cookies = cs;
        }
    }
}
