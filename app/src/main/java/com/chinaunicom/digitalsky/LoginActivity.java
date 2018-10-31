package com.chinaunicom.digitalsky;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private ImageView iv_background;       //背景
    private ImageView riv_profile_photo;   //头像
    private EditText et_account;        //用户名editText
    private EditText et_password;       //密码editText
    private Button btn_login;           //登录button
    private CheckBox cb_psd_visible;    //密码是否可见checkBox
    private TextView tv_forget_password;//忘记密码？
    private TextView tv_register;       //注册新密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        //设置背景
        iv_background = this.findViewById(R.id.background);
        iv_background.setImageBitmap(ReadBitmapUtils.readBitmap(this, R.drawable.background));
        iv_background.setScaleType(ImageView.ScaleType.FIT_XY);     //填满屏幕

        //设置头像.数据库中有用户头像数据后设置相应图片
        riv_profile_photo = this.findViewById(R.id.profile_photo);
        riv_profile_photo.setImageBitmap(ReadBitmapUtils.readBitmap(this, R.drawable.defaultpic));

        et_account = this.findViewById(R.id.et_account);
        et_password = this.findViewById(R.id.et_password);
        btn_login = this.findViewById(R.id.btn_login);
        cb_psd_visible = this.findViewById(R.id.cb_password_visible);
        tv_forget_password = this.findViewById(R.id.tv_forget_password);
        tv_register = this.findViewById(R.id.tv_register);

        /**
         * 登录button
         * 验证用户名、密码是否为空
         * 验证用户名、密码格式
         * 验证用户名是否被注册
         * 登录，跳转至内容页
         */
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = et_account.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    et_account.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    et_password.requestFocus();
                    return;
                } else {
                    Toast.makeText(LoginActivity.this, "登录", Toast.LENGTH_LONG).show();
                    //                    Intent intent = new Intent(LoginActivity.this, DeviceActivity.class);
                    //                    startActivity(intent);
                }
            }
        });
        /**
         * 密码是否可见icon
         */
        cb_psd_visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    cb_psd_visible.setBackgroundResource(R.drawable.icon_see_psd);
                } else {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    cb_psd_visible.setBackgroundResource(R.drawable.icon_not_see_psd);
                }
                et_password.setSelection(et_password.getText().toString().length());
            }
        });

        /**
         * 忘记密码？链接
         * 点击跳转至忘记密码页面
         */
        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPsdActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 注册新用户链接
         * 点击跳转至注册页面
         */
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
