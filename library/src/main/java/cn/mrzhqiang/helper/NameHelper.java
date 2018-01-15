package cn.mrzhqiang.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import java.util.Locale;

/**
 * 这个类是名字辅助工具。
 * <p>
 * 用来提取字符串首字母，以及通过字符串和指定大小，得到包含首字母的头像。
 *
 * @author mrzhqiang
 */
public final class NameHelper {

  /** 前景颜色常量。 */
  private static final int FG_COLOR = 0xFFFAFAFA;

  /** 预定义背景颜色常量数组。 */
  private static final int[] COLORS = {
      0xFFe91e63, 0xFF9c27b0, 0xFF673ab7, 0xFF3f51b5, 0xFF5677fc, 0xFF03a9f4, 0xFF00bcd4,
      0xFF009688, 0xFFff5722, 0xFF795548, 0xFF607d8b
  };

  /**
   * 通过名字和大小，返回一个圆形头像。
   *
   * @param name 名字，字符串类型，不能为null
   * @param size 大小，整型，最小是10
   * @return 圆形头像，中间是名字首字母，背景颜色随机
   */
  @CheckResult @WorkerThread public static Bitmap circleBitmapOf(@NonNull String name,
      @IntRange(from = 10) int size) {
    return circleTransform(bitmapOf(name, size));
  }

  /**
   * 这个方法参考了<a href="https://github.com/wasabeef/picasso-transformations">picasso-transformations</a>
   * 的<code>CropCircleTransformation</code>。
   *
   * @param source 需要转换的{@link Bitmap}，注意：这个参数会调用{@link Bitmap#recycle()}来回收
   * @return 全新的，裁剪为圆形的位图
   */
  @CheckResult @WorkerThread public static Bitmap circleTransform(@NonNull Bitmap source) {
    int size = Math.min(source.getWidth(), source.getHeight());
    int width = (source.getWidth() - size) / 2;
    int height = (source.getHeight() - size) / 2;

    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    BitmapShader shader =
        new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
    if (width != 0 || height != 0) {
      // source isn't square, move viewport to center
      Matrix matrix = new Matrix();
      matrix.setTranslate(-width, -height);
      shader.setLocalMatrix(matrix);
    }
    paint.setShader(shader);
    paint.setAntiAlias(true);

    float r = size / 2f;
    canvas.drawCircle(r, r, r, paint);

    source.recycle();

    return bitmap;
  }

  /**
   * 获取名字的第一个字符（仅限于字母或数字）。
   * <p>
   * 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>。
   *
   * @param name 一个名字或其他字符串类型的值
   * @return 传入字符串的首字母，如果传入一个空串，将使用默认字符{@code m}
   */
  @CheckResult @AnyThread public static String getFirstLetter(@NonNull String name) {
    for (Character c : name.toCharArray()) {
      // 字母或数字？
      if (Character.isLetterOrDigit(c)) {
        return c.toString();
      }
    }
    // from mrzhqiang
    return "m";
  }

  /**
   * 通过名字和指定尺寸，获取带名字首字母的图标。
   * <p>
   * 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>。
   *
   * @param name 名字或其他字符串。
   * @param size 生成的{@link Bitmap}尺寸值，最小是10，最大没有上限，但不建议超过屏幕一半的大小。
   * @return 参见 {@link Bitmap}。
   */
  @CheckResult @WorkerThread public static Bitmap bitmapOf(@NonNull String name,
      @IntRange(from = 10) int size) {
    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    final String trimmedName = name.trim();
    drawTile(canvas, trimmedName, size, size);
    return bitmap;
  }

  /** 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>。 */
  private static void drawTile(Canvas canvas, String name, int right, int bottom) {
    final String letter = getFirstLetter(name);
    final int color = getColorForName(name);
    drawTile(canvas, letter, color, right, bottom);
  }

  /** 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>。 */
  private static int getColorForName(@NonNull String name) {
    // 如果名字是个空串，那么返回默认颜色
    if (name.isEmpty()) {
      return 0xFF202020;
    }
    // 计算名字的hashCode，位与0xFFFFFFFF——相当于取得最后的8位
    // 然后根据数组长度取模，得到随机的下标位置，返回相对唯一的颜色值
    return COLORS[(int) ((name.hashCode() & 0xffffffffL) % COLORS.length)];
  }

  /** 这个方法来自<a "href"=https://github.com/siacs/Conversations>Conversations</a>。 */
  private static void drawTile(Canvas canvas, String letter, int tileColor, int right, int bottom) {
    letter = letter.toUpperCase(Locale.getDefault());
    Paint tilePaint = new Paint(), textPaint = new Paint();
    tilePaint.setColor(tileColor);
    textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    textPaint.setColor(FG_COLOR);
    textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
    textPaint.setTextSize((float) ((right) * 0.8));
    Rect rect = new Rect();

    canvas.drawRect(new Rect(0, 0, right, bottom), tilePaint);
    textPaint.getTextBounds(letter, 0, 1, rect);
    float width = textPaint.measureText(letter);
    canvas.drawText(letter, (right) / 2 - width / 2, (bottom) / 2 + rect.height() / 2, textPaint);
  }

  private NameHelper() {
  }
}
