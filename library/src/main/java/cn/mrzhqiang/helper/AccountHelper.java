package cn.mrzhqiang.helper;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 简单的账户辅助工具
 */
public final class AccountHelper {

  private static final String CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  /** 创建传入长度的字符串密码 */
  public static String createPassword(@IntRange(from = 1) int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
    }
    return builder.toString();
  }

  public static String[] autoUsernames(@NonNull String prefix, @IntRange(from = 0) int startIndex,
      @IntRange(from = 2) int count) {
    String[] usernames = new String[count];
    int length = prefix.length();
    StringBuilder prefixBuilder = new StringBuilder(prefix);
    for (int number = 6 - length; number > 0; number--) {
      prefixBuilder.append("a");
    }
    prefix = prefixBuilder.toString();
    for (int i = 0; i < count; i++) {
      usernames[i] = prefix + (startIndex + i);
    }
    return usernames;
  }

  public static void main(String[] args) {
    System.out.println(Arrays.toString(autoUsernames("", 1, 10)));
  }
}
