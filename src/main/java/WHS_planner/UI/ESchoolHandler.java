package WHS_planner.UI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

/**
 * Static utility class to handle eSchoolPlus data requests
 */
public class ESchoolHandler {

    //Constants
    private static String ESCHOOL_LOGIN_URL = "https://esp40hac.sungardk12saas.com/HAC4_001/Account/LogOn";
    private static String SCHEDULE_URL = "https://esp40hac.sungardk12saas.com/HAC4_001/Content/Student/Classes.aspx";
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

        //Save the cookies
        Map<String, String> loginCookies = loginForm.cookies();

        //Login with correct credentials
        Jsoup.connect(ESCHOOL_LOGIN_URL)
                .userAgent(USER_AGENT)
                .data("Database", "850") //850 is the "ID" for the Wayland PS Live "District"
                .data("LogOnDetails.UserName", username)
                .data("LogOnDetails.Password", password)
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
    private static JSONArray getCourses(String username, String password) throws LoginException, IOException{
        //Splits rawCourseTable into individual rows
        Elements rawCourses = getRawCourseTable(username, password).getElementsByTag("tr");

        //Removes the first row (table headers)
        Elements dataKeys = rawCourses.remove(0).getElementsByTag("td");

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
}
