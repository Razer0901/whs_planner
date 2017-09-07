package WHS_planner;

import java.io.*;

public class DayArrayGenerator {
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader("2017.csv")); //Path to csv file
        String string = br.readLine();
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
                    System.out.println(letterDay + " - " + strings[2]);
                }
            }
            string = br.readLine();
        }

        br.close();
    }
}
