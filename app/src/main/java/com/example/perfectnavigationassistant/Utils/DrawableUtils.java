package com.example.perfectnavigationassistant.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class DrawableUtils {
    public static Bitmap SetRoundCornerBitmap(Bitmap bitmap, float roundPx) {
        int width = bitmap.getWidth();
        int heigh = bitmap.getHeight();
        // 创建输出bitmap对象
        Bitmap outmap = Bitmap.createBitmap(width, heigh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, heigh);
        final RectF rectf = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectf, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return outmap;
    }

}
