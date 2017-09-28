package WHS_planner.UI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Static utility class to handle eSchoolPlus data requests
 */
public class ESchoolHandler {
    //Constants
    private static String ESCHOOL_LOGIN_URL = "https://hac40.eschoolplus.powerschool.com/HAC4_001/Account/LogOn";
    private static String SCHEDULE_URL = "https://hac40.eschoolplus.powerschool.com/HAC4_001/Content/Student/Classes.aspx";
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
        Document rawPage = Jsoup.connect(SCHEDULE_URL)
                .cookies(getLoginCookies(username, password))
                .userAgent(USER_AGENT)
                .get();

        //Extracts the course table (HTML)
        Element rawCourseTable = rawPage.getElementById("plnMain_dgSchedule");

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

        int stringIndex = loginForm.body().indexOf("Wayland PS Live");
        String schoolCode = loginForm.body().substring(stringIndex - 5,stringIndex-2);

        //Save the cookies
        Map<String, String> loginCookies = loginForm.cookies();

        //Login with correct credentials
        Connection.Response login = Jsoup.connect(ESCHOOL_LOGIN_URL)
                .userAgent(USER_AGENT)
                .timeout(10000) //Time out at 10 secs
                .data("Database", schoolCode) //850 is the "ID" for the Wayland PS Live "District"
                .data("LogOnDetails.UserName", username)
                .data("LogOnDetails.Password", password)
                .cookies(loginCookies)
                .method(Connection.Method.POST)
                .execute();

        loginCookies.putAll(login.cookies());
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

        //Remove the first row (table headers)
        Elements dataKeys = rawCourses.remove(0).getElementsByTag("td");
        //Remove the final row (Advisory)
        rawCourses.remove(rawCourses.size()-1);

        //Initializes JSONArray to store courses
        JSONArray courses = new JSONArray();

        //Parse data for each row
        for(Element row: rawCourses){

            //Parse course data into a JSONObject (using table headers as keys)
            JSONObject rawCourse = new JSONObject(); //Initializes temp JSONObject to store course data
            Elements rowData = row.getElementsByTag("td"); //Splits row cells
            for (int i = 0; i < dataKeys.size(); i++) {
                rawCourse.put(dataKeys.get(i).text(),rowData.get(i).text());
            }
            //Finalize course JSONObject (rename keys for future use)
            JSONObject course = new JSONObject();
            course.put("name",rawCourse.get("Description"));
            course.put("id",rawCourse.get("Course"));
            course.put("teacher",rawCourse.get("Teacher"));
            course.put("period", rawCourse.get("Periods"));
            course.put("room", rawCourse.get("Room"));
            course.put("quarters", rawCourse.get("Marking Periods"));
            course.put("days", rawCourse.get("Days"));

            //Add course to final list
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
}
