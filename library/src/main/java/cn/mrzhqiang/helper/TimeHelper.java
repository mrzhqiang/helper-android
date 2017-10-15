package cn.mrzhqiang.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 与时间、日期相关的辅助工具
 * <p>
 * Created by mrZQ on 2017/4/10.
 */
public final class TimeHelper {

  // 通用的时间格式，要适配的话，推荐用Date.toLocaleString()
  public static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E", Locale.getDefault());
  // 默认的时间格式
  public static final SimpleDateFormat DATE_NORMAL =
      new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
  // 早于1970年的时间格式
  public static final SimpleDateFormat DATE_NORMAL0 =
      new SimpleDateFormat("yyyy-MM", Locale.getDefault());
  // 2016-12-31
  public static final SimpleDateFormat DATE_NORMAL1 =
      new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
  // 01-01
  public static final SimpleDateFormat DATE_THIS_YEAR =
      new SimpleDateFormat("MM-dd", Locale.getDefault());
  // 10-01 00:00
  public static final SimpleDateFormat DATE_THIS_MONTH =
      new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
  // 12:00 星期二
  public static final SimpleDateFormat DATE_WEEK_DAY =
      new SimpleDateFormat("HH:mm E", Locale.getDefault());
  // 22:18
  public static final SimpleDateFormat DATE_TODAY =
      new SimpleDateFormat("HH:mm", Locale.getDefault());
  // 使用日期作为目录名
  public static final SimpleDateFormat DATE_YMD =
      new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
  // 使用时间作为文件名
  public static final SimpleDateFormat DATE_HMS =
      new SimpleDateFormat("HHmmssSSS", Locale.getDefault());

  /** 判断是否为今年 */
  public static boolean thisYear(Date date) {
    // 时间是一个瞬时值，不适合全局持有
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date);
    return cal1.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
  }

  /** 判断是否为今天 */
  public static boolean today(Date data) {
    return sameDay(data, new Date());
  }

  /** 比较两个日期是否为同一天 */
  public static boolean sameDay(Date a, Date b) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(a);
    cal2.setTime(b);
    // 比较年份，如果相同，再去比较一年中的第几天是否相同
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }

  /** 显示这个时间戳，从年/月/日/时/分逐一比较，拆开来显示 */
  public static String showTime(Date timestamp) {
    // 超过此时此刻的时间戳，返回空串
    if (timestamp.getTime() > System.currentTimeMillis()) {
      return "";
    }
    // 时间戳早于1970年，显示全部时间
    if (timestamp.getTime() <= 0) {
      return DATE_NORMAL0.format(timestamp);
    }

    Calendar now = Calendar.getInstance();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH);
    int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int min = now.get(Calendar.MINUTE);

    now.setTime(timestamp);
    // 早于今年：2016-12-31
    if (year != now.get(Calendar.YEAR)) {
      return DATE_NORMAL1.format(timestamp);
    }
    // 早于当月：01-01
    if (month != now.get(Calendar.MONTH)) {
      return DATE_THIS_YEAR.format(timestamp);
    }
    // 早于当天
    if (dayOfMonth != now.get(Calendar.DAY_OF_MONTH)) {
      int interval = dayOfMonth - now.get(Calendar.DAY_OF_MONTH);
      if (interval > 6) {
        // 6天以前：10-01 00:00
        return DATE_THIS_MONTH.format(timestamp);
      } else if (interval > 2) {
        // 2天以前：10-12 星期X
        return DATE_WEEK_DAY.format(timestamp);
      } else if (interval == 2) {
        return String.format(Locale.getDefault(), "前天 %s", DATE_TODAY.format(timestamp));
      } else {
        return String.format(Locale.getDefault(), "昨天 %s", DATE_TODAY.format(timestamp));
      }
    }
    // 同一分钟：刚刚
    if (hour == now.get(Calendar.HOUR_OF_DAY) && min == now.get(Calendar.MINUTE)) {
      return "刚刚";
    }
    // 当天：00:00 -- 23:59
    return DATE_TODAY.format(timestamp);
  }

  /**
   * 显示距离这个时间戳的间隔
   *
   * @param active true表示比较活跃，将5分钟内都视为刚刚；false则不做特殊处理
   * @param time 最近的一次时间戳
   * @return 格式化后的时间间隔字符串
   */
  public static String lastSeen(boolean active, long time) {
    long interval = (System.currentTimeMillis() - time) / 1000;
    active = active && interval <= 300;
    if (active || interval < 60) {
      return "刚刚查看过";
    } else if (interval < 60 * 2) {
      return "1 分钟前查看过";
    } else if (interval < 60 * 60) {
      return String.format(Locale.getDefault(), "%d 分钟前查看过", Math.round(interval / 60.0));
    } else if (interval < 60 * 60 * 2) {
      return "1 小时前查看过";
    } else if (interval < 60 * 60 * 24) {
      return String.format(Locale.getDefault(), "%d 小时前查看过", Math.round(interval / (60.0 * 60.0)));
    } else if (interval < 60 * 60 * 48) {
      return "1 天前查看过";
    } else {
      return String.format(Locale.getDefault(), "%d 天前查看过",
          Math.round(interval / (60.0 * 60.0 * 24.0)));
    }
  }

  private TimeHelper() {
    throw new AssertionError("no instance");
  }

  public static void main(String[] args) {
    Calendar calendar = Calendar.getInstance();
    System.out.println("今年：" + thisYear(calendar.getTime()));
    System.out.println("今天：" + today(calendar.getTime()));
    System.out.println("相同的一天："+sameDay(calendar.getTime(), new Date()));
    System.out.println("显示："+showTime(calendar.getTime()));
    System.out.println("检查：" + lastSeen(true, calendar.getTimeInMillis()));
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    System.out.println("相同的一天："+sameDay(calendar.getTime(), new Date()));
    System.out.println("显示："+showTime(calendar.getTime()));
    System.out.println("检查：" + lastSeen(true, calendar.getTimeInMillis()));
    calendar.set(Calendar.YEAR, 2016);
    System.out.println("今年：" + thisYear(calendar.getTime()));
    System.out.println("今天：" + today(calendar.getTime()));
    System.out.println("显示："+showTime(calendar.getTime()));
    System.out.println("检查：" + lastSeen(true, calendar.getTimeInMillis()));
  }
}
