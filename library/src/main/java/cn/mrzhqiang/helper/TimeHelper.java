package cn.mrzhqiang.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 时间的格式化显示
 * <p>
 * Created by mrZQ on 2017/4/10.
 */
public final class TimeHelper {

  // 通用的时间格式，要适配的话，推荐用Date.toLocaleString()
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E", Locale.getDefault());
  // 默认的时间格式
  public static final SimpleDateFormat DATE_NORMAL = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
  // 2016-12-31
  public static final SimpleDateFormat DATE_NORMAL1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
  // 01-01
  public static final SimpleDateFormat DATE_THIS_YEAR = new SimpleDateFormat("MM-dd", Locale.getDefault());
  // 10-01 00:00
  public static final SimpleDateFormat DATE_THIS_MONTH = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
  // 12:00 星期二
  public static final SimpleDateFormat DATE_WEEK_DAY = new SimpleDateFormat("HH:mm E", Locale.getDefault());
  // 22:18
  public static final SimpleDateFormat DATE_TODAY = new SimpleDateFormat("HH:mm", Locale.getDefault());
  // 推荐目录名
  public static final SimpleDateFormat DATE_YMD = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
  // 推荐文件名
  public static final SimpleDateFormat DATE_HMS = new SimpleDateFormat("HHmmssSSS", Locale.getDefault());

  /**
   * 判断是否为今年
   */
  public static boolean isThisYear(long time) {
    // 时间是一个瞬时值，不适合全局持有
    GregorianCalendar now = new GregorianCalendar();
    int year = now.get(Calendar.YEAR);

    now.setTimeInMillis(time);
    return year == now.get(Calendar.YEAR);
  }

  /**
   * 判断是否为今天
   */
  public static boolean isToday(long time) {
    GregorianCalendar now = new GregorianCalendar();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH);
    int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);

    now.setTimeInMillis(time);
    return year == now.get(Calendar.YEAR)
        && month == now.get(Calendar.MONTH)
        && dayOfMonth == now.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 判断是否为现在(1分钟以内)
   */
  public static boolean isNow(long time) {
    GregorianCalendar now = new GregorianCalendar();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH);
    int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int min = now.get(Calendar.MINUTE);

    now.setTimeInMillis(time);
    return year == now.get(Calendar.YEAR)
        && month == now.get(Calendar.MONTH)
        && dayOfMonth == now.get(Calendar.DAY_OF_MONTH)
        && hour == now.get(Calendar.HOUR_OF_DAY)
        && min == now.get(Calendar.MINUTE);
  }

  public static String showTime(long time) {
    if (time <= 0) {
      return "";
    }

    GregorianCalendar now = new GregorianCalendar();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH);
    int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int min = now.get(Calendar.MINUTE);

    now.setTimeInMillis(time);
    // 不是今年：2016-12-31
    if (year != now.get(Calendar.YEAR)) {
      return DATE_NORMAL1.format(now.getTime());
    }
    // 同年异月：01-01
    if (month != now.get(Calendar.MONTH)) {
      return DATE_THIS_YEAR.format(now.getTime());
    }
    // 同月异日
    if (dayOfMonth != now.get(Calendar.DAY_OF_MONTH)) {
      int interval = dayOfMonth - now.get(Calendar.DAY_OF_MONTH);
      if (interval >= 7) {
        // 大于7天：10-01 00:00
        return DATE_THIS_MONTH.format(now.getTime());
      } else {
        // 7天以内：10-12 星期四
        return DATE_WEEK_DAY.format(now.getTime());
      }
    }
    // 今天：00:00
    if (hour == now.get(Calendar.HOUR_OF_DAY)
        && min == now.get(Calendar.MINUTE)) {
      return "刚刚";// 时分一样
    }
    return DATE_TODAY.format(new Date(time));
  }

  private TimeHelper() {
    throw new AssertionError("no instance");
  }

  public static void main(String[] args) {
    System.out.println(TimeHelper.showTime(new GregorianCalendar(2017, 8, 2).getTimeInMillis()));
  }
}
