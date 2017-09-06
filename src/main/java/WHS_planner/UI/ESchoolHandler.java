package WHS_planner.UI;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class ESchoolHandler {
    private String ESCHOOL_LOGIN_URL = "https://esp40hac.sungardk12saas.com/HAC4_001/Account/LogOn";
    private String USER_AGENT = "Mozilla/5.0";

    public ESchoolHandler(){
//        try {
//            // Default doc grab
//            Document loginPage = Jsoup.connect(ESCHOOL_LOGIN_URL)
//                    .userAgent(USER_AGENT)
//                    .get();
//        }catch (IOException exception){
//            exception.printStackTrace();
//        }
        try {
            Document doc = Jsoup.connect("https://esp40hac.sungardk12saas.com/HAC4_001/Content/Student/Classes.aspx")
                    .cookies(login("",""))
                    .userAgent(USER_AGENT)
                    .get();
            System.out.println(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> login(String username, String password){
        Map<String, String> loginCookies = null;
        Connection.Response loginForm = null;
        try {
            loginForm = Jsoup.connect(ESCHOOL_LOGIN_URL)
                    .timeout(2000)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
            loginCookies = loginForm.cookies();
            Connection.Response login = Jsoup.connect(ESCHOOL_LOGIN_URL)
                    .userAgent(USER_AGENT)
                    .data("Database", "850")
                    .data("LogOnDetails.UserName", username)
                    .data("LogOnDetails.Password", password)
                    .cookies(loginCookies)
                    .method(Connection.Method.POST)
                    .execute();
            loginCookies = login.cookies();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loginCookies;
    }

    public static void main(String[] args) {
        new ESchoolHandler();
    }
}
