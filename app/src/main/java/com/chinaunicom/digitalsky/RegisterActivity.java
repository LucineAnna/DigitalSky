package com.chinaunicom.digitalsky;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mob.MobSDK;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity {
    private ImageView iv_background;        //背景图片
    private EditText et_mobile;              //手机号editText
    private EditText et_password;           //密码editText
    private EditText et_verification_code;   //验证码editText
    private ImageButton btn_back;           //返回button
    private Button btn_register;            //注册button
    private Button btn_get_verification_code; //获取验证码button
    private CheckBox cb_psd_visible;        //密码是否可见icon

    private String phoneNumber;
    private String password;
    private String verificationCode;

    private String CodeText;
    private int time = 60;       //验证码倒计时
    EventHandler eventHandler;

    //数据库
    private ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        contentResolver = RegisterActivity.this.getContentResolver();
        init();

        MobSDK.init(this, ContentData.AppKey, ContentData.AppSECRET);
        eventHandler = new EventHandler(){
            /**
             * 在操作之后被触发
             * @param event
             * @param result SMSSDK.RESULT_COMPLETE表示操作成功，为SMSSDK.RESULT_ERROR表示操作失败
             * @param data   事件操作的结果
             */
            public void afterEvent(int event, int result, Object data){
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * 使用Handler来分发Message对象到主线程中，处理事件
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0x00:
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    if (result == SMSSDK.RESULT_COMPLETE) { //回调  当返回的结果是complete
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) { //获取验证码
                            Toast.makeText(RegisterActivity.this, "发送验证码成功", Toast.LENGTH_SHORT).show();
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { //提交验证码
//                            Toast.makeText(RegisterActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
                        }
                    } else { //进行操作出错，通过下面的信息区分析错误原因
                        try {
                            Throwable throwable = (Throwable) data;
                            throwable.printStackTrace();
                            JSONObject object = new JSONObject(throwable.getMessage());
                            String des = object.optString("detail");//错误描述
                            int status = object.optInt("status");//错误代码
                            //错误代码：  http://wiki.mob.com/android-api-%E9%94%99%E8%AF%AF%E7%A0%81%E5%8F%82%E8%80%83/
                            if (status > 0 && !TextUtils.isEmpty(des)) {
                                Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 0x01:
                    btn_get_verification_code.setClickable(false);
                    btn_get_verification_code.setText("重新发送(" + msg.arg1 + ")");
                    break;
                case 0x02:
                    btn_get_verification_code.setText("获取验证码");
                    btn_get_verification_code.setClickable(true);
                    break;
            }
        }
    };

    private void init(){
        //设置背景
        iv_background = this.findViewById(R.id.background);
        iv_background.setImageBitmap(ReadBitmapUtils.readBitmap(this, R.drawable.background));
        iv_background.setScaleType(ImageView.ScaleType.FIT_XY);     //填满屏幕

        et_mobile = this.findViewById(R.id.et_mobile);
        et_password = this.findViewById(R.id.et_password);
        et_verification_code = this.findViewById(R.id.et_verification_code);
        btn_back = this.findViewById(R.id.btn_back);
        btn_get_verification_code = this.findViewById(R.id.btn_get_verification_code);
        btn_register = this.findViewById(R.id.btn_register);
        cb_psd_visible = this.findViewById(R.id.cb_password_visible);

        /**
         * 返回Button
         * 返回上一页面
         */
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });

        /**
         * 密码是否可见
         */
        cb_psd_visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    cb_psd_visible.setBackgroundResource(R.drawable.icon_see_psd);
                }else {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    cb_psd_visible.setBackgroundResource(R.drawable.icon_not_see_psd);
                }
                et_password.setSelection(et_password.getText().toString().length());
            }
        });

        /**
         * 获取验证码Button
         */
        btn_get_verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = et_mobile.getText().toString().trim();
                /**
                 * 与数据库用户手机号进行匹配
                 */
                if (phoneNumber.equals("18605919710")){
                    Toast.makeText(RegisterActivity.this, "该手机号已被注册", Toast.LENGTH_LONG).show();
                    return;
                }else if (!IsMobileNoUtils.isMobileNO(phoneNumber)) {
                    Toast.makeText(RegisterActivity.this, "手机号不正确", Toast.LENGTH_LONG).show();
                    return;
                }
                SMSSDK.getVerificationCode("86", phoneNumber);
                btn_get_verification_code.setClickable(false);

                //开启线程去更新button的text
                new Thread() {
                    @Override
                    public void run() {
                        time = 60;
                        for (int i = 0; i < time; i++) {
                            Message message = handler.obtainMessage(0x01);
                            message.arg1 = time - i;
                            handler.sendMessage(message);
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(0x02);
                    }
                }.start();
            }
        });

        /**
         * 注册Button
         * 判断phoneNumber、password合法性
         * 判断验证码正确性
         * 提交phoneNumber、password至数据库
         */
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = et_mobile.getText().toString().trim();
                password = et_password.getText().toString().trim();
                verificationCode = et_verification_code.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
                    et_mobile.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                    et_password.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(RegisterActivity.this, "验证码不能为空", Toast.LENGTH_LONG).show();
                    et_verification_code.requestFocus();
                    return;
                }else {
                    if (IsPsdPatternUtils.isPsdPattern(password)){
                        if (null != verificationCode && verificationCode.length() == 4) {
                            SMSSDK.submitVerificationCode("86", phoneNumber, verificationCode);
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                            /**
                             * 将用户数据 phoneNumber、password写入数据库
                             */

                            /**
                             * 跳转至登录页面
                             */
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "验证码不正确", Toast.LENGTH_LONG).show();
                            et_verification_code.requestFocus();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this, "密码格式不正确", Toast.LENGTH_LONG).show();
                        et_password.requestFocus();
                        return;
                    }
                }
            }
        });
    }
    private class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }
        /**
         *Uri.parse("content://sms/inbox")表示对收到的短信的一个监听的uri.
         */
        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            StringBuilder sb = new StringBuilder();
            Cursor cursor = getContentResolver().query(
                    Uri.parse("content://sms/inbox"), null, null, null, null);
            //这里不要使用while循环.我们只需要获取当前发送过来的短信数据就可以了.
            cursor.moveToNext();
            sb.append("body=" + cursor.getString(cursor.getColumnIndex("body")));  //获取短信内容的实体数据.
            Pattern pattern = Pattern.compile("[^0-9]");  //正则表达式.
            Matcher matcher = pattern.matcher(sb.toString());
            CodeText = matcher.replaceAll("");
            et_verification_code.setText(CodeText);  //将输入验证码的控件内容进行改变.
            cursor.close();  //关闭游标指针.
            super.onChange(selfChange);
        }
    }
}
