package com.rokid.iot.portal.share;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Slf4j
public class DateHelper {

    /**
     * 判断日期是否和（today日期的差）相同
     *
     * @param date
     * @param dateNum today日期的差 ；1表示明天，0表示今天，-1表示昨天，-2表示前天，以此类推
     * @return
     * @author <a href="http://git.oschina.net/denger"> 羊登琼 </a>
     */
    public static boolean isToday4Num(final Date date, int dateNum) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        final Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, dateNum);
        return DateUtils.isSameDay(cal1, cal2);
    }


    public static String pareDateInDate(Date date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception e) {
            log.error("log format error; {}", date, e);
        }
        return null;
    }


    public static String pareTimeAndAdd1Secs(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, 1);
            return new SimpleDateFormat("HH:mm").format(calendar.getTime());
        } catch (Exception e) {
            log.error("log format error; {}", date, e);
        }
        return null;
    }

    public static String current() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        } catch (Exception e) {
            log.error("log format error;", e);
        }
        return null;
    }

    public static String pareDate(Date date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        } catch (Exception e) {
            log.error("log format error; {}", date, e);
        }
        return null;
    }

    public static Date pareDate(String dateAndTimeString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateAndTimeString);
        } catch (Exception e) {
            log.error("log format error; {}", dateAndTimeString, e);
        }
        return null;
    }

    public static Date pareDateNoTime(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (Exception e) {
            log.error("log format error; {}", dateString, e);
        }
        return null;
    }

    public static boolean dateIsInPast(String dateString) {

        try {
            Date time = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            calendar.add(Calendar.DAY_OF_MONTH, 1);


            Date now = new Date();
            return now.after(calendar.getTime());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean dateAndTimeIsInPast(String dateAndTimeString) {
        try {
            Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateAndTimeString);
            Date now = new Date();
            return now.after(time);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean dateExceedTwoYearsFromNow(String dateString) {
        try {
            Date time = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);

            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.YEAR, currentYear + 2);

            Date twoYearsFromNow = calendar.getTime();

            return time.after(twoYearsFromNow);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean dateAndTimeExceedTwoYearsFromNow(String dateAndTimeString) {
        try {
            Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateAndTimeString);

            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.YEAR, currentYear + 2);

            Date twoYearsFromNow = calendar.getTime();

            return time.after(twoYearsFromNow);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * MO|NI|AF|EV
     */
    private static final Set<String> LACKED_INFO_TIMES = Sets.newHashSet("MO", "NI", "AF", "EV");

    public static boolean utcTimeIsNotClear(String utc) {
        return utcTimeIsNotClear(utc, LACKED_INFO_TIMES);
    }

    private static boolean utcTimeIsNotClear(String utc, Set<String> specifiedSet) {
        Preconditions.checkArgument(StringUtils.isNotBlank(utc), "utc info is empty[" + JsonHelper.toJson(utc));
        for (String each : specifiedSet) {
            if (utc.contains(each)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws ParseException, InterruptedException {

        for (int i = 0; i < 100; i++) {
            Date time = new Date();
            System.out.println(time);
            System.out.println(DateHelper.pareTimeAndAdd1Secs(time));
            Thread.sleep(10000);
        }

    }


}
