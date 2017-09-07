package WHS_planner;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DayArrayGenerator {
    public static void main(String[] args) throws IOException{
        ArrayList<String[]> dates = new ArrayList<>(); //Store days with school [date,ID]
        ArrayList<String[]> days = new ArrayList<>(); //Store calendarData days [ID,id]
        ArrayList<String[]> calendarData = new ArrayList<>(); //Letterdata [letter,id]

        BufferedReader br = new BufferedReader(new FileReader("/Users/geoffreywang/IdeaProjects/WHS Planner/whs_planner/src/main/resources/2017.csv")); //Path to csv file
        String string = br.readLine();
        int counter = 0;
        while(string != null) {
            String[] strings = string.split(",[^-\\s]");
            String letterDay = strings[0];
            if(letterDay.length() == 3) {
                letterDay = letterDay.substring(1,2);
                if (letterDay.equals("A") ||
                        letterDay.equals("B") ||
                        letterDay.equals("C") ||
                        letterDay.equals("D") ||
                        letterDay.equals("E") ||
                        letterDay.equals("F") ||
                        letterDay.equals("G") ||
                        letterDay.equals("H")) {
                    String date = strings[2].substring(0,strings[2].length()-1);
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd MMMM, yyyy", Locale.US);
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/d", Locale.US);
                    Date dateObj = null;
                    try {
                        dateObj = inputDateFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    date = outputDateFormat.format(dateObj);
                    System.out.println(date);


                    dates.add(new String[]{date,counter+""});
                    days.add(new String[]{counter+"",counter+""});
                    calendarData.add(new String[]{letterDay,counter+""});
                    counter++;
                }
            }
            string = br.readLine();
        }
        br.close();
        System.out.println(dates);
        System.out.println(days);
        System.out.println(calendarData);

        String output = "";
        output += "{\"@dates\":[";
        for (int i = 0; i < dates.size(); i++) {
            String[] temp = dates.get(i);
            output += "\"dates"+temp[1]+": "+temp[0]+"\"";
            if(i != dates.size()-1){
                output+= ",";
            }
        }
        output += "],\"@days\":[";
        for (int i = 0; i < days.size(); i++) {
            String[] temp = days.get(i);
            output += "\"days"+temp[1]+": "+temp[0]+"\"";
            if(i != days.size()-1){
                output+= ",";
            }
        }
        output += "],\"@calendarData\":[";
        for (int i = 0; i < 365; i++) {
            if(i < calendarData.size()) {
                String[] temp = calendarData.get(i);
                output += "\"calendarData"+temp[1]+": "+temp[0]+"\"";
            }else{
                output += "\"calendarData"+i+": There is No School Today\"";
            }
            if(i != 365-1){
                output+= ",";
            }
        }
        output += "]}";
        System.out.println(output);
    }


}
