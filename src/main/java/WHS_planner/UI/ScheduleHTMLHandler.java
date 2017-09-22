package WHS_planner.UI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class ScheduleHTMLHandler {

    public static String STUDENT_URL = "http://hac40.eschoolplus.powerschool.com/HAC4_001/Classes/Schedule";
    public static String TEACHER_URL = "https://esp40.eschoolplus.powerschool.com/TAC/Account/LogOn";

    /**
     * Grabs the raw HTML table of courses
     * @param file Raw HTML of the schedule page
     * @return Raw HTML table of courses
     * @throws LoginException Thrown if login failed
     * @throws IOException General IO exceptions
     */
    private static Element getRawCourseTableTeacher(File file) throws LoginException, IOException {
        if(!file.getName().contains(".htm")){
            throw new LoginException();
        }
        if(file.isDirectory()){
            throw new LoginException();
        }

        Document rawPage = Jsoup.parse(file, "UTF-8", "");

        //Extracts the course table (HTML)
        Elements rawCourseTable = rawPage.getElementsByTag("tbody");

        //Checks if a course table exists (if not, then login failed)
        if(rawCourseTable.size() == 0){
            throw new LoginException();
        }else{
            return rawCourseTable.get(0);
        }
    }

    /**
     * Parses raw course table HTML into a JSONArray of courses (with relevant data)
     * [Course Keys: name, id, teacher, period, room, quarters, days]
     * @param file Raw HTML of the schedule page
     * @return JSONArray of student's courses (with relevant data)
     * @throws LoginException Thrown if login failed
     * @throws IOException General IO exceptions
     */
    public static JSONArray getCoursesTeacher(File file) throws LoginException, IOException{

        //Splits rawCourseTable into individual rows
        Elements rawCourses = getRawCourseTableTeacher(file).getElementsByTag("tr");

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
     * Grabs the raw HTML table of courses
     * @param file Raw HTML of the schedule page
     * @return Raw HTML table of courses
     * @throws LoginException Thrown if login failed
     * @throws IOException General IO exceptions
     */
    private static Element getRawCourseTableStudent(File file) throws LoginException, IOException{

        Document rawPage = Jsoup.parse(file, "UTF-8", "");

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
     * Parses raw course table HTML into a JSONArray of courses (with relevant data)
     * [Course Keys: name, id, teacher, period, room, quarters, days]
     * @param file Raw HTML of the schedule page
     * @return JSONArray of student's courses (with relevant data)
     * @throws LoginException Thrown if login failed
     * @throws IOException General IO exceptions
     */
    public static JSONArray getCoursesStudent(File file) throws LoginException, IOException{
        //Splits rawCourseTable into individual rows
        Elements rawCourses = getRawCourseTableStudent(file).getElementsByTag("tr");

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
}
