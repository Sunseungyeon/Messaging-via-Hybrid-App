package com.example.user.hw3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WebView browser;
    Context mContext;
    String num = "";
    String smsNum, smsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        browser = (WebView)findViewById(R.id.webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new JavaScriptInterface(), "Android");

        browser.loadUrl("file:///android_asset/webpage.html");
    }

    public class JavaScriptInterface {

        @JavascriptInterface
        public String phoneNumber(String str) { //input button's numbers make one string, return the string
            num += str;
            return num;
        }

        @JavascriptInterface
        public String cleartxt() { // phone number textarea cleared
            Toast.makeText(getApplicationContext(), "clear", Toast.LENGTH_SHORT).show();
            num="";
            Toast.makeText(getApplicationContext(), num, Toast.LENGTH_SHORT).show();
            return num;
        }

        @JavascriptInterface
        public void sendSMS(String smsNumber,String smsText){ // execused when submin button clicked

            if (smsNumber.length()>0 && smsText.length()>0){
                sendMessage(smsNumber, smsText);
            }else{
                Toast.makeText(getApplicationContext(), "모두 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
        }

        public void sendMessage(String smsNumber, String smsText){
            PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

            /**
             * SMS가 발송될때 실행
             * When the SMS massage has been sent
             */
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch(getResultCode()){
                        case Activity.RESULT_OK:
                            // 전송 성공
                            Toast.makeText(getApplicationContext(), "전송 완료", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            // 전송 실패
                            Toast.makeText(getApplicationContext(), "전송 실패", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            // 서비스 지역 아님
                            Toast.makeText(getApplicationContext(), "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            // 무선 꺼짐
                            Toast.makeText(getApplicationContext(), "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            // PDU 실패
                            Toast.makeText(getApplicationContext(), "PDU Null", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("SMS_SENT_ACTION"));

            SmsManager mSmsManager = SmsManager.getDefault();
            mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
        }
    }
}