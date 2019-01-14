package com.pilllll.testpicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.fengjing);
//        bitmap.getPixels();
        ImageView res=findViewById(R.id.imgResource);
        res.setImageBitmap(bitmap);


        Bitmap curPic = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        int[] oldPiex=new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(oldPiex,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());

        //对列进行操作
        for(int i=0;i<oldPiex.length;i+=bitmap.getWidth()){
            //一次取一行
            for (int j=i;j<i+bitmap.getWidth();j++){
               if(j%2==0)//这里的模值为动态可调的
                oldPiex[j]= Color.argb(1, 0, 0, 0);
            }

            int line=i/bitmap.getWidth();//行号

//            if(line%8==0){
//                for (int j=i;j<i+bitmap.getWidth();j++){
//                    oldPiex[j]= Color.argb(1, 0, 0, 0);
//                }
//            }
        }
        //对行进行操作

        curPic.setPixels(oldPiex, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        ImageView imageView=findViewById(R.id.img);
        imageView.setImageBitmap(curPic);
    }


    public static Bitmap handleImageEffect(Bitmap bitmap,float rotate,float saturation,float scale){
        // 创建副本，用于将处理过的图片展示出来而不影响原图，Android系统也不允许直接修改原图
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();

        // 修改色调,即色彩矩阵围绕某种颜色分量旋转
        ColorMatrix rotateMatrix = new ColorMatrix();
        // 0,1,2分别代表像素点颜色矩阵中的Red，Green,Blue分量
        rotateMatrix.setRotate(0,rotate);
        rotateMatrix.setRotate(1,rotate);
        rotateMatrix.setRotate(2,rotate);

        // 修改饱和度
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        // 修改亮度，即某种颜色分量的缩放
        ColorMatrix scaleMatrix = new ColorMatrix();
        // 分别代表三个颜色分量的亮度
        scaleMatrix.setScale(scale,scale,scale,1);

        //将三种效果结合
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(rotateMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(scaleMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        canvas.drawBitmap(bitmap,0,0,paint);
        return bmp;
    }
}
