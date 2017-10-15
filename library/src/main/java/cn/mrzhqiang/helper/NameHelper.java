package cn.mrzhqiang.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import java.util.Locale;

/**
 * 对用户名字的相关处理
 */
public final class NameHelper {

  /** 图标文字颜色常量 */
  private static final int FG_COLOR = 0xFFFAFAFA;

  /**
   * 通过名字，生成随机颜色。
   * 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>
   */
  public static int getColorForName(String name) {
    // 如果名字不存在，或是个空串，那么返回默认颜色
    if (name == null || name.isEmpty()) {
      return 0xFF202020;
    }
    int colors[] = {
        0xFFe91e63, 0xFF9c27b0, 0xFF673ab7, 0xFF3f51b5, 0xFF5677fc, 0xFF03a9f4, 0xFF00bcd4,
        0xFF009688, 0xFFff5722, 0xFF795548, 0xFF607d8b
    };
    // 计算名字的hashCode，位与0xFFFFFFFF——相当于取得最后的8位
    // 再根据颜色数组长度取模，得到近乎随机的下标位置，返回对应的颜色
    return colors[(int) ((name.hashCode() & 0xffffffffL) % colors.length)];
  }

  /**
   * 获取名字的第一个字符
   * 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>
   */
  public static String getFirstLetter(String name) {
    for (Character c : name.toCharArray()) {
      // 字母或数字？
      if (Character.isLetterOrDigit(c)) {
        return c.toString();
      }
    }
    return "Z";// from Zhang
  }

  /**
   * 通过名字和指定尺寸，获取带名字首字母的图标
   * 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>
   */
  public static Bitmap get(String name, int size) {
    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    final String trimmedName = name == null ? "" : name.trim();
    drawTile(canvas, trimmedName, 0, 0, size, size);
    return bitmap;
  }

  /** 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a> */
  private static boolean drawTile(Canvas canvas, String name, int left, int top, int right,
      int bottom) {
    if (name != null) {
      final String letter = getFirstLetter(name);
      final int color = getColorForName(name);
      drawTile(canvas, letter, color, left, top, right, bottom);
      return true;
    }
    return false;
  }

  /** 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a> */
  private static boolean drawTile(Canvas canvas, String letter, int tileColor, int left, int top,
      int right, int bottom) {
    letter = letter.toUpperCase(Locale.getDefault());
    Paint tilePaint = new Paint(), textPaint = new Paint();
    tilePaint.setColor(tileColor);
    textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    textPaint.setColor(FG_COLOR);
    textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
    textPaint.setTextSize((float) ((right - left) * 0.8));
    Rect rect = new Rect();

    canvas.drawRect(new Rect(left, top, right, bottom), tilePaint);
    textPaint.getTextBounds(letter, 0, 1, rect);
    float width = textPaint.measureText(letter);
    canvas.drawText(letter, (right + left) / 2 - width / 2, (top + bottom) / 2 + rect.height() / 2,
        textPaint);
    return true;
  }

  private NameHelper() {
  }

}
