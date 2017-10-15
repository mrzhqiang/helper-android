package cn.mrzhqiang.sample;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import cn.mrzhqiang.helper.AccountHelper;
import cn.mrzhqiang.helper.NameHelper;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

  private ImageView avatar;
  private TextView content;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    avatar = (ImageView) findViewById(R.id.avatar);

    // 推荐使用下面注释掉的方法来处理
    avatar.setImageBitmap(NameHelper.get("mrzhqiang", getPixel(56)));
    //new BitmapWorkerTask(avatar).execute("2222");

    content = (TextView) findViewById(R.id.content);
    // 创建随机密码
    content.setText(AccountHelper.createPassword(10));
  }

  public int getPixel(int dp) {
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    return ((int) (dp * metrics.density));
  }

  private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    BitmapWorkerTask(ImageView imageView) {
      imageViewReference = new WeakReference<>(imageView);
    }

    @Override protected Bitmap doInBackground(String... params) {
      return NameHelper.get(params[0], getPixel(56));
    }

    @Override protected void onPostExecute(Bitmap bitmap) {
      if (bitmap != null && !isCancelled()) {
        final ImageView imageView = imageViewReference.get();
        if (imageView != null) {
          imageView.setImageBitmap(bitmap);
          imageView.setBackgroundColor(0x00000000);
        }
      }
    }
  }
}
