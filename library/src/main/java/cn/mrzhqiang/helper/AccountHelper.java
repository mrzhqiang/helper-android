package cn.mrzhqiang.helper;

import android.support.annotation.IntRange;
import java.security.SecureRandom;

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

}
