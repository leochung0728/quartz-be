package com.leochung0728.quartz.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
    public static LocalDateTime covertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static long toTimeStamp(LocalDate date) {
        return date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
