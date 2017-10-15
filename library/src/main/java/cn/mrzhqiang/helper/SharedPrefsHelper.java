package cn.mrzhqiang.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 共享首选项的辅助工具
 */
public final class SharedPrefsHelper {
  private static final String TAG = "SharedPrefsHelper";

  private final SharedPreferences msp;

  public SharedPrefsHelper(@NonNull Context context, @Nullable String name) {
    if (name == null || name.length() == 0) {
      name = TAG;
    }
    this.msp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
  }

  /** 通过Base64编码保存 */
  @CheckResult public boolean saveEncode(@NonNull Object object, @NonNull String key)
      throws IOException {
    if (!(object instanceof Serializable)) {
      throw new IOException(object.getClass().getSimpleName() + " must be implement Serializable.");
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(object);
    String encode = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
    return msp.edit().putString(key, encode).commit();
  }

  /** 以明文形式保存 */
  @CheckResult public boolean save(@NonNull Object object, @NonNull String key) throws IOException {
    if (!(object instanceof Serializable)) {
      throw new IOException(object.getClass().getSimpleName() + " must be implement Serializable.");
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(object);
    String encode = new String(baos.toByteArray());
    return msp.edit().putString(key, encode).commit();
  }

  @CheckResult public SharedPreferences.Editor editor() {
    return msp.edit();
  }

  @CheckResult public SharedPreferences msp() {
    return msp;
  }

  /** 取得Base64解码后的对象 */
  @CheckResult public <T> T takeDecode(@NonNull Class<T> clazz, @Nullable String key)
      throws IOException {
    String value = msp.getString(key, null);
    if (value == null) {
      throw new IOException("This key " + key + " get value is null.");
    }
    byte[] buffer = Base64.decode(value, Base64.DEFAULT);
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object object = null;
    try {
      object = ois.readObject();
    } catch (ClassNotFoundException e) {
      throw new IOException("This key " + key + " can not read " + clazz.getSimpleName());
    } finally {
      ois.close();
      bais.close();
    }
    if (object == null) {
      throw new IOException("This key " + key + " can read, but it is null.");
    }
    return clazz.cast(object);
  }

  /** 直接取得对象 */
  @CheckResult public <T> T take(@NonNull Class<T> clazz, @Nullable String key) throws IOException {
    String value = msp.getString(key, null);
    if (value == null) {
      throw new IOException("This key " + key + " get value is null.");
    }
    byte[] buffer = value.getBytes();
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object object = null;
    try {
      object = ois.readObject();
    } catch (ClassNotFoundException e) {
      throw new IOException("This key " + key + " can not read " + clazz.getSimpleName());
    } finally {
      ois.close();
      bais.close();
    }
    if (object == null) {
      throw new IOException("This key " + key + " can read, but it is null.");
    }
    return clazz.cast(object);
  }

  /** 移除所有Key */
  @CheckResult public boolean remove(@NonNull String... keys) {
    SharedPreferences.Editor editor = msp.edit();
    for (String key : keys) {
      editor.remove(key);
    }
    return editor.commit();
  }
}
