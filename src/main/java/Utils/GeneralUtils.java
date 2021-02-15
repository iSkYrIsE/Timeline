package Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GeneralUtils {
    static public String getFirstLastName(String input) {
        if(input!=null) {
            String[] parts = input.split(" ");
            if(parts.length>1) {
                return parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase() + " " + parts[parts.length-1].substring(0, 1).toUpperCase() + parts[parts.length-1].substring(1).toLowerCase();
            } else {
                return input;
            }
        } else {
            return "";
        }
    }

    static public String generateTimeStr(LocalDateTime startDate, LocalDateTime endDate) {
        if(endDate!=null && startDate!=null) {
            if(startDate.getMonthValue() == endDate.getMonthValue() && startDate.getDayOfMonth() == endDate.getDayOfMonth() && startDate.getYear() == endDate.getYear()) {
                return startDate.format(DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.ENGLISH))  + " - " + endDate.format(DateTimeFormatter.ofPattern("HH:mm '◦' dd MMM").withLocale(Locale.ENGLISH));
            } else if(startDate.getYear() == endDate.getYear()) {
                return  startDate.format(DateTimeFormatter.ofPattern("HH:mm d MMM").withLocale(Locale.ENGLISH)) + " - " + endDate.format(DateTimeFormatter.ofPattern("HH:mm dd MMM").withLocale(Locale.ENGLISH));
            } else {
                return startDate.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yy").withLocale(Locale.ENGLISH)) + " - " + endDate.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yy").withLocale(Locale.ENGLISH));
            }
        } else {
            if(startDate!=null) {
                return startDate.format(DateTimeFormatter.ofPattern("HH:mm '◦' d MMM").withLocale(Locale.ENGLISH));
            } else{
                return "---";
            }
        }
    }

}
