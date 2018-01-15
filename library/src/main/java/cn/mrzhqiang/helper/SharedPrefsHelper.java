package cn.mrzhqiang.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 这个类是共享首选项辅助工具。
 * <p>
 * 用来便捷操作{@link SharedPreferences}的存取对象，不依赖任何第三方框架，仅使用Android SDK或JDK中的API完成。
 *
 * @author mrzhqiang
 */
public final class SharedPrefsHelper {
  private static final String TAG = "SharedPrefsHelper";

  private final SharedPreferences msp;

  /**
   * 共享首选项辅助工具的唯一构造方法。
   *
   * @param context 上下文，需要用来取得共享首选项实例
   * @param name 通过名字参数创建相应的共享首选项文件
   */
  public SharedPrefsHelper(@NonNull Context context, @Nullable String name) {
    if (name == null || name.isEmpty()) {
      name = TAG;
    }
    this.msp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
  }

  /**
   * 通过Base64编码保存一个可序列化对象到共享首选项中。
   *
   * @param object 需要保存可序列化对象
   * @param key 保存此对象时，对应的KEY
   * @return <code>true</code>表示保存成功，<code>false</code>表示保存失败
   * @throws IOException 如果没有实现{@link Serializable}接口将抛出IO异常
   */
  @WorkerThread @CheckResult public boolean putEncode(@NonNull String key, @NonNull Object object)
      throws IOException {
    if (key.isEmpty()) {
      throw new IllegalArgumentException("Key is empty.");
    }
    return msp.edit().putString(key, stringOf(object, true)).commit();
  }

  /**
   * 以明文形式保存一个可序列化对象到共享首选项中。
   *
   * @param object 需要保存可序列化对象
   * @param key 保存此对象时，对应的KEY
   * @return <code>true</code>表示保存成功，<code>false</code>表示保存失败
   * @throws IOException 如果没有实现{@link Serializable}接口将抛出IO异常
   */
  @WorkerThread @CheckResult public boolean put(@NonNull String key, @NonNull Object object)
      throws IOException {
    if (key.isEmpty()) {
      throw new IllegalArgumentException("Key is empty.");
    }
    return msp.edit().putString(key, stringOf(object, false)).commit();
  }

  /** 将可序列化的对象转换为字符串 */
  @NonNull private String stringOf(@NonNull Object obj, boolean isEncode) throws IOException {
    if (!(obj instanceof Serializable)) {
      throw new IOException(obj.getClass().getSimpleName() + " must be implement Serializable.");
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    oos.flush();
    byte[] bytes = baos.toByteArray();
    oos.close();
    baos.close();
    return new String(isEncode ? Base64.encode(bytes, Base64.DEFAULT) : bytes);
  }

  /**
   * 取得Base64解码后的对象。
   *
   * @param clazz 需要取得的对应类型
   * @param key 需要取得的KEY
   * @return 对应类型的对象，有可能为null，所以使用返回值时，要注意空指针异常
   * @throws IOException 可能无法找到对应KEY的序列化字符串或对象无法创建
   */
  @WorkerThread @Nullable @CheckResult public <T> T takeDecode(@NonNull String key,
      @NonNull Class<T> clazz) throws IOException {
    if (key.isEmpty()) {
      throw new IllegalArgumentException("Key is empty.");
    }
    String value = msp.getString(key, null);
    if (value == null) {
      throw new IOException("This key " + key + " get null.");
    }
    return clazz.cast(objectOf(value, true));
  }

  /**
   * 直接取得对象。
   *
   * @param clazz 需要取得的对应类型
   * @param key 需要取得的KEY
   * @return 对应类型的对象，有可能为null，所以使用返回值时，要注意空指针异常
   * @throws IOException 可能无法找到对应KEY的序列化字符串或对象无法创建
   */
  @WorkerThread @Nullable @CheckResult public <T> T take(@NonNull String key,
      @NonNull Class<T> clazz) throws IOException {
    if (key.isEmpty()) {
      throw new IllegalArgumentException("Key is empty.");
    }
    String value = msp.getString(key, null);
    if (value == null) {
      throw new IOException("This key " + key + " get value is null.");
    }
    return clazz.cast(objectOf(value, false));
  }

  /** 将字符串转换为对象。 */
  @NonNull private Object objectOf(@NonNull String value, boolean isDecode) throws IOException {
    byte[] buffer = isDecode ? Base64.decode(value, Base64.DEFAULT) : value.getBytes();
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object object;
    try {
      object = ois.readObject();
    } catch (ClassNotFoundException e) {
      throw new IOException(e.getMessage());
    } finally {
      ois.close();
      bais.close();
    }
    if (object == null) {
      throw new IOException("Null read object.");
    }
    return object;
  }

  /**
   * 移除对应Key存储的对象。
   *
   * @param keys 任意个对应的KEY
   * @return <code>true</code>移除成功；<code>false</code>移除失败
   */
  @CheckResult public boolean remove(@NonNull String... keys) {
    SharedPreferences.Editor editor = msp.edit();
    for (String key : keys) {
      editor.remove(key);
    }
    return editor.commit();
  }
}
