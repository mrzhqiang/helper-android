package cn.mrzhqiang.helper;

import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 这个类是账户辅助工具。
 * <p>
 * 用来生成随机密码，或批量生成用户名。
 *
 * @author mrzhqiang
 */
public final class AccountHelper {

  /** 26个大小写字母，以及0-9数字的拼接 */
  private static final String CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  /** 生成随机数的实例 */
  private static final SecureRandom RANDOM = new SecureRandom();
  /** 正则表达式，判断字符串是否为大小写英文字母——至少有一个 */
  private static final Pattern PREFIX_PATTERN = Pattern.compile("[A-Za-z]+");

  /**
   * 创建随机密码。
   *
   * @param length 密码长度，最小为1，最大没有限制，但不建议太长导致线程无响应
   * @return 指定长度的随机密码
   */
  @CheckResult @AnyThread public static String createPassword(@IntRange(from = 1) int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
    }
    return builder.toString();
  }

  /**
   * 自动生成用户名数组。
   *
   * @param prefix 用户名前缀
   * @param startIndex 起始索引，不会将1转换为0001这样的格式
   * @param count 需要生成的用户名数量
   * @return 用户名数组
   */
  @CheckResult @WorkerThread public static String[] autoUsernames(@NonNull String prefix,
      @IntRange(from = 0) int startIndex, @IntRange(from = 2) int count) {
    return autoUsernames(prefix, 6, startIndex, count);
  }

  /**
   * 自动生成用户名数组。
   *
   * @param prefix 用户名前缀
   * @param minLength 用户名前缀的最小长度，因为有些用户名限制了最小长度
   * @param startIndex 起始索引，不会将1转换为0001这样的格式
   * @param count 需要生成的用户名数量
   * @return 用户名数组
   */
  @CheckResult @WorkerThread public static String[] autoUsernames(@NonNull String prefix,
      @IntRange(from = 0) int minLength, @IntRange(from = 0) int startIndex,
      @IntRange(from = 2) int count) {
    if (!PREFIX_PATTERN.matcher(prefix).matches()) {
      throw new IllegalArgumentException("Prefix illegal argument.");
    }
    String[] usernames = new String[count];
    // 如果前缀长度不足6位，将从中间补0，缺多少补多少
    StringBuilder builder = new StringBuilder(prefix);
    for (int i = 0; i < minLength - prefix.length(); i++) {
      builder.append("0");
    }
    prefix = builder.toString();
    for (int i = 0; i < count; i++) {
      usernames[i] = prefix + (startIndex + i);
    }
    return usernames;
  }

  public static void main(String[] args) {
    System.out.println(Arrays.toString(autoUsernames("a", 0, 100)));
  }
}
