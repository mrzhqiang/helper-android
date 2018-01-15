package cn.mrzhqiang.helper;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 与时间、日期相关的辅助工具。
 * <p>
 * 通常用于即时聊天、论坛发帖等需要时间戳的场景。
 *
 * @author mrzhqiang
 */
public final class TimeHelper {

  /** 中文时间，要适配系统区域设置的话，推荐用{@link Date#toLocaleString()} */
  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E", Locale.getDefault());
  /** 默认：2018-01-15 17:36 */
  private static final SimpleDateFormat DATE_NORMAL =
      new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
  /** 1970年以前：1969-12 */
  private static final SimpleDateFormat DATE_NORMAL0 =
      new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
  /** 1970年-去年：2016-12-31 */
  private static final SimpleDateFormat DATE_NORMAL1 =
      new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
  /** 同年：01-01 */
  private static final SimpleDateFormat DATE_THIS_YEAR =
      new SimpleDateFormat("MM月dd日", Locale.getDefault());
  /** 同月：10-01 00:00 */
  private static final SimpleDateFormat DATE_THIS_MONTH =
      new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
  /** 同周：12:00 星期二 */
  private static final SimpleDateFormat DATE_WEEK_DAY =
      new SimpleDateFormat("HH:mm E", Locale.getDefault());
  /** 同日：22:18 */
  private static final SimpleDateFormat DATE_TODAY =
      new SimpleDateFormat("HH:mm", Locale.getDefault());
  /** 使用日期作为目录名 */
  private static final SimpleDateFormat DATE_YMD =
      new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
  /** 使用时间作为文件名 */
  private static final SimpleDateFormat DATE_HMS =
      new SimpleDateFormat("HHmmssSSS", Locale.getDefault());

  /** 判断是否为今年。 */
  @CheckResult public static boolean thisYear(@NonNull Date date) {
    // 时间是一个瞬时值，不适合全局持有
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date);
    return cal1.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
  }

  /** 判断是否为今天。 */
  @CheckResult public static boolean today(@NonNull Date data) {
    return sameDay(data, new Date());
  }

  /** 比较两个日期是否为同一天。 */
  @CheckResult public static boolean sameDay(@NonNull Date a, @NonNull Date b) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(a);
    cal2.setTime(b);
    // 比较年份，如果相同，再去比较一年中的第几天是否相同
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }

  /** 中文显示这个时间戳，从 年/月/周/天/时/分 逐一比较，分层级显示。 */
  @CheckResult @NonNull public static synchronized String showTime(@NonNull Date timestamp) {
    // 超出“现在”，或早于1970年（包括），返回：年-月
    if (timestamp.getTime() > System.currentTimeMillis() || timestamp.getTime() <= 0) {
      return DATE_NORMAL0.format(timestamp);
    }

    // 检测时间距离：刚刚、1分钟前、N分钟前（不超过1小时）...
    String interval = lastTime(timestamp.getTime(), false);
    if (interval != null) {
      return interval;
    }

    // 开始检测日历
    Calendar date = Calendar.getInstance();
    date.setTime(timestamp);
    Calendar now = Calendar.getInstance();
    // 1970年（不包括）至今年——年-月-日
    if (date.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
      return DATE_NORMAL1.format(timestamp);
    }
    // 同年昨天（不希望对元旦节的“昨天”进行判定）
    int day = now.get(Calendar.DAY_OF_YEAR) - date.get(Calendar.DAY_OF_YEAR);
    int yesterday = 1;
    if (day == yesterday) {
      return "昨天 " + DATE_TODAY.format(timestamp);
    }
    // 同年前天（同上）
    int yesterdayAndYesterday = 2;
    if (day == yesterdayAndYesterday) {
      return "前天 " + DATE_TODAY.format(timestamp);
    }
    // 同年同周——时:分 星期几
    if (date.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)) {
      return DATE_WEEK_DAY.format(timestamp);
    }
    // 同年不同月——几月几日
    if (date.get(Calendar.MONTH) != now.get(Calendar.MONTH)) {
      return DATE_THIS_YEAR.format(timestamp);
    }
    // 同月不同日——几月几日
    if (date.get(Calendar.DAY_OF_MONTH) != now.get(Calendar.DAY_OF_MONTH)) {
      return DATE_THIS_YEAR.format(timestamp);
    }
    // 同日不同时——时:分
    if (date.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
      return DATE_TODAY.format(timestamp);
    }
    // 其他未考虑周全的情况，弥补一下
    return DATE_NORMAL.format(timestamp);
  }

  /** 1分钟=60秒 */
  private static final long ONE_MINUTE = 60;
  /** 2分钟 */
  private static final long TWO_MINUTE = 2 * ONE_MINUTE;
  /** 1小时=60分钟 */
  private static final long ONE_HOURS = 60 * ONE_MINUTE;
  /** 2小时 */
  private static final long TWO_HOURS = 2 * ONE_HOURS;
  /** 1天=24小时 */
  private static final long ONE_DAY = 24 * ONE_HOURS;
  /** 2天 */
  private static final long TWO_DAY = 2 * ONE_DAY;

  /**
   * 显示距离这个时间戳的间隔。
   *
   * @param time 某种时间戳
   * @return 格式化后的时间间隔字符串，不会返回<code>null</code>
   */
  @CheckResult public static String lastTime(long time) {
    return lastTime(time, true);
  }

  /**
   * 显示距离这个时间戳的间隔。
   *
   * @param time 某种时间戳
   * @param checkHours 是否达到一小时级别的间隔，传入<code>true</code>将永不返回<code>null</code>
   * @return 格式化后的时间间隔字符串，如果是<code>null</code>说明不在范围内，需要自己再一次格式化时间
   */
  private static String lastTime(long time, boolean checkHours) {
    // 取得距离现在的间隔毫秒
    long interval = (System.currentTimeMillis() - time);
    // 如果时间戳超前，则不进行后面的逻辑
    if (interval < 0) {
      return null;
    }
    // 将毫秒转换为秒
    long seconds = TimeUnit.MILLISECONDS.toSeconds(interval);
    if (seconds < ONE_MINUTE) {
      return "刚刚";
    } else if (seconds < TWO_MINUTE) {
      return "1 分钟前";
    } else if (seconds < ONE_HOURS) {
      long minute = Math.round(seconds / 60.0);
      return String.format(Locale.getDefault(), "%d 分钟前", minute);
    }

    if (!checkHours) {
      return null;
    }

    // 小时级别的判断
    if (seconds < TWO_HOURS) {
      return "1 小时前";
    } else if (seconds < ONE_DAY) {
      long day = Math.round(seconds / (60.0 * 60.0));
      return String.format(Locale.getDefault(), "%d 小时前", day);
    } else if (seconds < TWO_DAY) {
      return "1 天前";
    } else {
      return String.format(Locale.getDefault(), "%d 天前",
          Math.round(seconds / (60.0 * 60.0 * 24.0)));
    }
  }

  private TimeHelper() {
    throw new AssertionError("no instance");
  }

  public static void main(String[] args) {
    Calendar calendar = Calendar.getInstance();
    System.out.println("今年：" + thisYear(calendar.getTime()));
    System.out.println("今天：" + today(calendar.getTime()));
    System.out.println("相同的一天：" + sameDay(calendar.getTime(), new Date()));
    System.out.println("显示：" + showTime(calendar.getTime()));
    System.out.println("检查：" + lastTime(calendar.getTimeInMillis()));
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    System.out.println("相同的一天：" + sameDay(calendar.getTime(), new Date()));
    System.out.println("显示：" + showTime(calendar.getTime()));
    System.out.println("检查：" + lastTime(calendar.getTimeInMillis()));
    calendar.set(Calendar.YEAR, 2016);
    System.out.println("今年：" + thisYear(calendar.getTime()));
    System.out.println("今天：" + today(calendar.getTime()));
    System.out.println("显示：" + showTime(calendar.getTime()));
    System.out.println("检查：" + lastTime(calendar.getTimeInMillis()));
  }
}
