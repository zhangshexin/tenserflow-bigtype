package com.pilllll.testpicture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * Created by zhangshexin on 2018/10/29.
 */

public class TestPicture extends Activity implements View.OnClickListener {
String TAG=this.getClass().getName();
        // 模型相关配置
        private static final int INPUT_SIZE = 224;
        private static final int IMAGE_MEAN = 117;
        private static final float IMAGE_STD = 1;
        private static final String INPUT_NAME = "input";
        private static final String OUTPUT_NAME = "output";
        private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
        private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
        private int[] res = {R.drawable.people, R.drawable.fengjing, R.drawable.rili, R.drawable.keybord,R.drawable.cat,R.drawable.fuza,R.drawable.motuo,R.drawable.people2,R.drawable.xuni};

        private Executor executor;
        private Uri currentTakePhotoUri;

        private Button result;
        private ImageView ivPicture;
        private Classifier classifier;
    private int i=-1;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (!isTaskRoot()) {
                finish();
            }

            setContentView(R.layout.activity_testpicture);

//            findViewById(R.id.iv_choose_picture).setOnClickListener(this);
//            findViewById(R.id.iv_take_photo).setOnClickListener(this);

            ivPicture = findViewById(R.id.iv_picture);
            result = findViewById(R.id.result);
            result.setOnClickListener(this);

            // 避免耗时任务占用 CPU 时间片造成UI绘制卡顿，提升启动页面加载速度
            Looper.myQueue().addIdleHandler(idleHandler);

        }

        /**
         *  主线程消息队列空闲时（视图第一帧绘制完成时）处理耗时事件
         */
        MessageQueue.IdleHandler idleHandler = new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                // 初始化 Classifier
                if (classifier == null) {
                    // 创建 TensorFlowImageClassifier
                    classifier = TensorFlowImageClassifier.create(TestPicture.this.getAssets(),
                            MODEL_FILE, LABEL_FILE, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME);
                }

                // 初始化线程池
                executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        thread.setName("ThreadPool-ImageClassifier");
                        return thread;
                    }
                });
                // 请求权限
//                requestMultiplePermissions();
                // 返回 false 时只会回调一次
                return false;
            }
        };

        @Override
        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.iv_choose_picture :
//                    choosePicture();
//                    break;
//                case R.id.iv_take_photo :
//                    takePhoto();
//                    break;
//                default:break;
//            }
            i++;
            view.setClickable(false);
            ((Button)view).setText("等待两秒……");
            handleInputPhoto();
        }

        /**
         * 选择一张图片并裁剪获得一个小图

        private void choosePicture() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICTURE_REQUEST_CODE);
        }*/

        /**
         * 使用系统相机拍照

        private void takePhoto() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST_CODE);
            } else {
                openSystemCamera();
            }
        }*/

        /**
         * 打开系统相机

        private void openSystemCamera() {
            //调用系统相机
            Intent takePhotoIntent = new Intent();
            takePhotoIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
            if (takePhotoIntent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, "当前系统没有可用的相机应用", Toast.LENGTH_SHORT).show();
                return;
            }

            String fileName = "TF_" + System.currentTimeMillis() + ".jpg";
            File photoFile = new File(FileUtil.getPhotoCacheFolder(), fileName);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //通过FileProvider创建一个content类型的Uri
                currentTakePhotoUri = FileProvider.getUriForFile(this, "gdut.bsx.tensorflowtraining.fileprovider", photoFile);
                //对目标应用临时授权该 Uri 所代表的文件
                takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                currentTakePhotoUri = Uri.fromFile(photoFile);
            }

            //将拍照结果保存至 outputFile 的Uri中，不保留在相册中
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentTakePhotoUri);
            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE);
        }
 */
        /**
         * 处理图片
         */
        private void handleInputPhoto() {
            // 加载图片
            Glide.with(TestPicture.this).asBitmap().listener(new RequestListener<Bitmap>() {

                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    Log.d(TAG,"handleInputPhoto onLoadFailed");
                    Toast.makeText(TestPicture.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d(TAG,"handleInputPhoto onResourceReady");
                    startImageClassifier(resource);
                    return false;
                }
            }).load(res[i]).into(ivPicture);
            if(i>=8)
                i=-1;
        }

        /**
         * 开始图片识别匹配
         * @param bitmap
         */
        private void startImageClassifier(final Bitmap bitmap) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, Thread.currentThread().getName() + " startImageClassifier");
                        Bitmap croppedBitmap = getScaleBitmap(bitmap, INPUT_SIZE);

                        final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);
                        Log.i(TAG, "startImageClassifier results: " + results);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                result.setText(String.format("results: %s", results));
                                result.setClickable(true);
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "startImageClassifier getScaleBitmap " + e.getMessage());
                    }
                }
            });
        }

        /**
         * 请求相机和外部存储权限

        private void requestMultiplePermissions() {

            String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String cameraPermission = Manifest.permission.CAMERA;

            int hasStoragePermission = ActivityCompat.checkSelfPermission(this, storagePermission);
            int hasCameraPermission = ActivityCompat.checkSelfPermission(this, cameraPermission);

            List<String> permissions = new ArrayList<>();
            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(storagePermission);
            }

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(cameraPermission);
            }

            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                ActivityCompat.requestPermissions(this, params, PERMISSIONS_REQUEST);
            }
        } */

//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//
//            if (resultCode == RESULT_OK) {
//                if (requestCode == PICTURE_REQUEST_CODE) {
//                    // 处理选择的图片
//                    handleInputPhoto(data.getData());
//                } else if (requestCode == OPEN_SETTING_REQUEST_COED){
//                    requestMultiplePermissions();
//                } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
//                    // 如果拍照成功，加载图片并识别
//                    handleInputPhoto(currentTakePhotoUri);
//                }
//            }
//        }

        /**
         * 对图片进行缩放
         * @param bitmap
         * @param size
         * @return
         * @throws java.io.IOException
         */
        private static Bitmap getScaleBitmap(Bitmap bitmap, int size) throws IOException {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWidth = ((float) size) / width;
            float scaleHeight = ((float) size) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
    }

