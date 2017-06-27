package com.uestc.magicwo.livecampus.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liji.circleimageview.CircleImageView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.SimpleResponse;
import com.uestc.magicwo.livecampus.net.Urls;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Magicwo on 2017/6/27.
 */


public class CertificateActivity extends AppBaseActivity {

    @BindView(R.id.assort)
    TextView assort;
    @BindView(R.id.head_image_view)
    CircleImageView headImageView;
    @BindView(R.id.head_layout)
    LinearLayout headLayout;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.upload_button)
    Button uploadButton;
    @BindView(R.id.title)
    TextView title;
    public static final int REQUEST_CODE_SELECT = 100;
    private static final long ONE_KB = 1024;
    private static final long ONE_MB = 1024 * 1024;
    @BindView(R.id.select_pic_button)
    Button selectPicButton;

    private ProgressDialog mProgressDialog;
    ArrayList<File> imageFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_certificate);
        ButterKnife.bind(this);
        title.setText("认证");
        init();

    }

    private void init() {
        Glide.with(this).load(BaseApplication.headUrl).into(headImageView);
        selectPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("-->", "从手机相册选择");
                Intent intent = new Intent(CertificateActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFiles != null && imageFiles.size() >= 1)
                    upLoad(imageFiles);
                else {
                    showToast("请先选择照片！");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                Log.d("---------", "data=" + data.toString());
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageFiles.clear();
                for (ImageItem imageItem : images) {
                    imageFiles.add(new File(imageItem.path));
                }
                Glide.with(this).load(imageFiles.get(0)).into(imageView);
//                upLoad(imageFiles);
            }
        }

    }

    /**
     * 上传图片
     *
     * @param imageFiles
     */
    private void upLoad(ArrayList<File> imageFiles) {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false); // 进度条是否不明确
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("正在上传图片");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // 进度条为长方形
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO 设置进度条取消事件
            }
        });
        //TODO 补充onError事件
        mProgressDialog.show();

        OkGo.post(Urls.CERTIFICATION)
                .tag(this)
                .isMultipart(true)//FIXME isMultipart参数有待商榷
                .params("uid", BaseApplication.userId)
                .addFileParams("img", imageFiles)
                .execute(new JsonCallback<BaseResponse<SimpleResponse>>() {

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.upProgress(currentSize, totalSize, progress, networkSpeed);
                        int displayProgress = (int) (progress * 100);
                        mProgressDialog.setProgress(displayProgress);

                        mProgressDialog.setProgressNumberFormat(refreshSpeed(networkSpeed));
                        if (displayProgress >= 100) {
                            mProgressDialog.dismiss();
                            beforeUpload();
                        }
                    }

                    @Override
                    public void onSuccess(BaseResponse<SimpleResponse> simpleResponseBaseResponse, Call call, Response response) {
                        Log.d("----------", "图片上传完毕");
                        showToast("照片上传成功");
                        CertificateActivity.this.finish();

                    }
                });


    }

    private String refreshSpeed(long networkSpeed) {
        StringBuffer speed = new StringBuffer(10);
        if (networkSpeed > ONE_MB) {
            speed.append(String.format("%.2f", 1.0 * networkSpeed / ONE_MB) + "M");
        } else if (networkSpeed > ONE_KB) {
            speed.append(String.format("%.2f", 1.0 * networkSpeed / ONE_KB) + "K");
        } else {
            speed.append(String.valueOf(networkSpeed) + "B");
        }
        speed.append("/s");
        return speed.toString();
    }

    private void beforeUpload() {
    }

    private void uploadSuccess() {

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
