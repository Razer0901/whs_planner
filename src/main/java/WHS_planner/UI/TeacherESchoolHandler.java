package WHS_planner.UI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by student on 9/11/17.
 */
public class TeacherESchoolHandler {

    //Constants
    private static String ESCHOOL_LOGIN_URL = "https://esp40.eschoolplus.powerschool.com/TAC/Account/LogOn";
    private static String SCHEDULE_URL = "https://esp40.eschoolplus.powerschool.com/TAC/";
    private static String USER_AGENT = "Mozilla/5.0"; //Prevents 403 Forbidden errors by mimicking a browser

    /**
     * Grabs the raw HTML table of courses
     * @param username Student's eSchoolPlus (HAC) login username
     * @param password Student's eSchoolPlus (HAC) login password
     * @return Raw HTML table of courses
     * @throws LoginException Thrown if login failed
     * @throws IOException General IO exceptions
     */
    private static Element getRawCourseTable(String username, String password) throws LoginException, IOException{
        //Logs the user in and grabs the entire schedule page (HTML)
//        Document rawPage = Jsoup.connect(SCHEDULE_URL)
//                .cookies(getLoginCookies(username, password))
//                .userAgent(USER_AGENT)
//                .get();

        File input = new File("/Users/student/IdeaProjects/whs_planner_current/src/main/resources/HomePage.htm");
        Document rawPage = Jsoup.parse(input, "UTF-8", "");

        //Extracts the course table (HTML)
        Element rawCourseTable = rawPage.getElementsByTag("tbody").get(0);

        //Checks if a course table exists (if not, then login failed)
        if(rawCourseTable == null){
            throw new LoginException();
        }else{
            return rawCourseTable;
        }
    }

    /**
     * User login (NOTE: does not check for login success)
     * @param username Student's eSchoolPlus (HAC) login username
     * @param password Student's eSchoolPlus (HAC) login password
     * @return Map of authentication cookies
     * @throws IOException General IO exceptions
     */
    private static Map<String, String> getLoginCookies(String username, String password) throws IOException{
        //Submits GET request for login page (for the cookies)
        Connection.Response loginForm = Jsoup.connect(ESCHOOL_LOGIN_URL)
                .timeout(2000)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .execute();

        //Save the cookies
        Map<String, String> loginCookies = loginForm.cookies();

        //Login with correct credentials
        Jsoup.connect(ESCHOOL_LOGIN_URL)
                .userAgent(USER_AGENT)
                .timeout(10000) //Time out at 10 secs
//                .data("Database", "850") //850 is the "ID" for the Wayland PS Live "District"
                .data("UserName", username)
                .data("Password", password)
                .data("tempUN","")
                .data("tempPW","")
                .data("btnLogin","")
                .cookies(loginCookies)
                .method(Connection.Method.POST)
                .execute();
        return loginCookies;
    }

    /**
     * Parses raw course table HTML into a JSONArray of courses (with relevant data)
     * [Course Keys: name, id, teacher, period, room, quarters, days]
     * @param username Student's eSchoolPlus (HAC) login username
     * @param password Student's eSchoolPlus (HAC) login password
     * @return JSONArray of student's courses (with relevant data)
     * @throws LoginException Thrown if login failed
     * @throws IOException General IO exceptions
     */
    public static JSONArray getCourses(String username, String password) throws LoginException, IOException{

        //Splits rawCourseTable into individual rows
        Elements rawCourses = getRawCourseTable(username, password).getElementsByTag("tr");

        //Remove the final row (Advisory)
        rawCourses.remove(rawCourses.size()-1);

        //Initializes JSONArray to store courses
        JSONArray courses = new JSONArray();

        String teacher = rawCourses.get(1).getElementsByClass("sg-my-classes-groupby-staff").get(0).text().split(": ")[1];

        for (int i = 3; i < rawCourses.size(); i++) {
            Elements courseRow = rawCourses.get(i).getElementsByTag("td");

            String periodNumber = courseRow.get(0).getElementsByClass("sg-period").get(0).text();

            Element courseCell = courseRow.get(1).getElementsByClass("sg-myclasses-link").get(0);

            String[] mouseOver = courseCell.attr("title").split("\n");

            String[] courseName = courseCell.text().split(" \\(");

            String rawRoomText = mouseOver[3].split(": ")[1];
            String rawQuartersText = mouseOver[2].split(": ")[1];

            String rawDaysText = mouseOver[1].split(": ")[1];

//            Finalize course JSONObject
            JSONObject course = new JSONObject();
            course.put("name",courseName[0]);
            course.put("id",courseName[1].substring(0,courseName[1].length()-1));
            course.put("teacher",teacher);
            course.put("period", periodNumber);
            course.put("room", rawRoomText.substring(0,rawRoomText.length()-1));
            course.put("quarters", rawQuartersText.substring(0,rawQuartersText.length()-1));
            course.put("days", rawDaysText.substring(0,rawDaysText.length()-1));

//            Add course to final list
            courses.add(course);
        }

        return courses;
    }

    /**
     * Does something to ignore bad SSL certificates (copied directly from Stack Overflow)
     */
    static {
        TrustManager[] trustAllCertificates = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null; // Not relevant.
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing. Just allow them all.
                    }
                }
        };

        HostnameVerifier trustAllHostnames = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // Just allow them all.
            }
        };

        try {
            System.setProperty("jsse.enableSNIExtension", "false");
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
        }
        catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(getCourses("",""));
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

