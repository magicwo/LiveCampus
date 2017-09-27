package com.uestc.magicwo.livecampus.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lzy.okgo.OkGo;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.appbase.AppBaseActivity;
import com.uestc.magicwo.livecampus.event.MessageEvent;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.SimpleResponse;
import com.uestc.magicwo.livecampus.net.Urls;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Magicwo on 2017/9/27.
 * 编辑资料
 */

public class EditActivity extends AppBaseActivity {
    @BindView(R.id.head_layout)
    LinearLayout headLayout;
    @BindView(R.id.save_button)
    Button saveButton;
    @BindView(R.id.editText)
    EditText editText;

    private int type;
    private String content;
    private String param;


    public static void runActivity(Context context, int type, String content) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("content", content);
        context.startActivity(intent);

    }

    @Override
    protected void initVariables() {
        super.initVariables();
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        content = intent.getStringExtra("content");

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.activity_edit_layout);
        ButterKnife.bind(this);
        saveButton.setVisibility(View.VISIBLE);
        if (content != null) {
            editText.setText(content);
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText().toString() == null || editText.getText().toString().equals("")) {
                    showToast("不能为空");
                    return;
                }
                update(type);
            }
        });

    }

    private void update(int type) {
        switch (type) {
            case 1:
                param = "username";
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("userID", BaseApplication.userId);
        params.put(param, editText.getText().toString());
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.UPDATEINFO).tag(this).upJson(jsonObject).execute(new JsonCallback<BaseResponse<SimpleResponse>>() {
            @Override
            public void onSuccess(BaseResponse baseResponse, Call call, Response response) {
                showToast("保存成功");
                EventBus.getDefault().post(new MessageEvent(editText.getText().toString()));
                EditActivity.this.finish();
            }
        });
    }

    @Override
    protected void loadData() {
        super.loadData();
    }

}
