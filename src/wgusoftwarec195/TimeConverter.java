/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Dan
 */
public final class TimeConverter {
    
    //Convert to utc time to be stored in database
    public static Timestamp ConvertToUtc(LocalDateTime time){   
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = time.atZone(zoneId);
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        time = utc.toLocalDateTime();
        //Create Timestamp values from Instants to update database
        Timestamp appointTime = Timestamp.valueOf(time);
        return appointTime;
    }
    
    //convert utc times from the database to the system's timezone to be displayed
    public static String ConvertToLocal(LocalDateTime time){
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zdt = time.atZone(zoneId);
        ZonedDateTime lzt = zdt.withZoneSameInstant(ZoneId.systemDefault());
        String appointTime = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy").format(lzt);
        return appointTime;
    }

}
