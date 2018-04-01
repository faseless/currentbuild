package com.max.explo.bibo;

import android.content.pm.PackageManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;
import android.net.Uri;
import android.Manifest;
import android.widget.EditText;


import com.google.android.gms.common.api.CommonStatusCodes;


public class MainActivity extends AppCompatActivity {

    private EditText mEditTexto;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;

    private boolean autoFocus = true;
    private boolean useFlash = false;
    private boolean callOnly = false;
    private boolean mailOnly = false;
    private Button cameraButton;
    private Button emailButton;
    private Button phoneButton;
    private Button card1;
    private Button card2;
    private Button card3;
    private Button card4;
    private Button card5;
    private String email = "";
    private String phone = "";


    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    private static final String CARD_KEY = "CARD_KEY";
    private boolean emailRead = false;
    private boolean phoneRead = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.cameraB);
        emailButton = findViewById(R.id.mailB);
        phoneButton = findViewById(R.id.phoneB);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash);

                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash);
                mailOnly = true;
                intent.putExtra(OcrCaptureActivity.MailOnly, true);
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(OcrCaptureActivity.UseFlash, useFlash);
                intent.putExtra(OcrCaptureActivity.MailOnly, true);
                callOnly = true;
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);

        card1.setOnClickListener(cardHandler);
        card2.setOnClickListener(cardHandler);
        card3.setOnClickListener(cardHandler);
        card4.setOnClickListener(cardHandler);
        card5.setOnClickListener(cardHandler);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Log.d(TAG, "Text read: " + text);
                    email = getEmail(text);
                    phone = getPhone(text);
                    if (callOnly) {
                        if (phoneRead) {
                            directCall();
                        }
                        callOnly = false;
                        phoneRead = false;
                        emailRead = false;
                    } else if (mailOnly) {
                        if (emailRead) {
                            prepMail();
                        }
                        mailOnly = false;
                        phoneRead = false;
                        emailRead = false;
                    } else {
                        if (emailRead) {
                            debugToast("Email", email);
                        }
                        if (phoneRead) {
                            debugToast("Phone", phone);
                        }
                        if (!emailRead && !phoneRead) {
                            debugToast("ERROR", "nothing found");
                        } else {
                            phoneRead = false;
                            emailRead = false;
                        }
                    }
                } else {
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void debugToast(String action, String text) {
        Toast.makeText(this, action + " is " + text, Toast.LENGTH_LONG).show();
    }

    public View.OnClickListener cardHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ContactActivity.class);

            switch (v.getId()) {
                case R.id.card1:
                    break;
                case R.id.card2:
                    break;
                case R.id.card3:
                    break;
                case R.id.card4:
                    break;
                case R.id.card5:
                    break;
            }

        }
    };


    private String getEmail(String text) {
        String[] ctext = text.split(" |\n");
        String cemail = "";
        for (String s : ctext) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '@') {
                    cemail = s;
                    emailRead = true;
                    return cemail;
                }
            }
        }
        return cemail;
    }

    private void phoneSaved(String action, String text) {
        String tphone = getPhone(text);
        if (tphone != "") {
            Toast.makeText(this, action + " is " + tphone, Toast.LENGTH_SHORT).show();
        }
        phoneRead = false;
    }


    private String getPhone(String text) {
        String[] ctext = text.split("\n");
        for (String s : ctext) {
            String cnumb = "";
            for (int i = 0; i < s.length(); i++) {
                if (!(s.charAt(i) < '0' || s.charAt(i) > '9')) {
                    cnumb += s.charAt(i);
                }
            }
            if (cnumb.length() == 10 || cnumb.length() == 11) {
                phoneRead = true;
                return cnumb;
            }
        }
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    protected void directCall() {
        final int REQUEST_PHONE_CALL = 1;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        //checks for permission before placing the call
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                //places call
                startActivity(callIntent); //note have to attempt to call twice on the time running on a phone
            }
        }
    }

    protected void prepMail(){
        setContentView(R.layout.email_layout);

        mEditTexto = findViewById(R.id.edit_text_to);
        mEditTexto.setText(email);
        mEditTextSubject = findViewById(R.id.edit_text_subject);
        mEditTextMessage = findViewById(R.id.edit_text_message);

        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();

            }
        });
    }

    private void sendMail() {
        String[] recipient = new String[]{mEditTexto.getText().toString()};
        //String[] recipients = recipientList.split(",");
        //example1@gmail.com, example2@gmail.com

        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose an email client"));
    }

}
