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

/**
 *
 * @author Dan
 */
public class TimeConverter {
    
    public static Timestamp ConvertToUtc(LocalDateTime time){
        //Convert to a ZonedDate Time in UTC
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdtStart = time.atZone(zoneId);
        System.out.println("Local Start Time: " + zdtStart);
        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("Zoned Start time: " + utcStart);
        time = utcStart.toLocalDateTime();
        System.out.println("Zoned time with zone stripped:" + time);
        //Create Timestamp values from Instants to update database
        Timestamp appointStartTs = Timestamp.valueOf(time); //this value can be inserted into database
        System.out.println("Timestamp to be inserted: " +appointStartTs);
        
        return appointStartTs;
    }
}
