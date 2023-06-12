package com.cookandroid.api;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private static final int LOGO_DISPLAY_TIME = 2000; // 로고를 표시할 시간 (밀리초 단위)

    private TextView status1;
    private EditText searchEditText;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);



        logoImageView = findViewById(R.id.logo_image);

        // 로고 표시
        logoImageView.setVisibility(View.VISIBLE);

        // 일정 시간 후에 로고를 숨기고 앱 화면을 표시
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logoImageView.setVisibility(View.GONE);
                setContentView(R.layout.activity_main); // activity_main으로 전환
                initializeViews();
            }
        }, LOGO_DISPLAY_TIME);
    }

    private void initializeViews() {
        // activity_main의 뷰들을 초기화
        status1 = findViewById(R.id.result);
        searchEditText = findViewById(R.id.edit);

        // 나머지 초기화 작업 등을 수행
    }

    public void mOnClick(View v) {
        String searchLocation = searchEditText.getText().toString();
        searchEVInformation(searchLocation);
    }

    private void searchEVInformation(String searchLocation) {
        String serviceKey = "pFOKiJqyG4VilQBGF9cIjtzn%2B2mGybFC2cSyuVBB7LkvJ7nxtcc07HBtpEXoZgyWh%2FFkbr11Yr6bOy3zK9rCiw%3D%3D";
        String apiUrl = "http://openapi.kepco.co.kr/service/EvInfoServiceV2/getEvSearchList?"
                + "&addr=" + searchLocation + "&pageNo=1&numOfRows=10&ServiceKey=" + serviceKey;

        new NetworkTask().execute(apiUrl);
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(params[0]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = conn.getInputStream();

                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = parserCreator.newPullParser();
                    parser.setInput(in, null);

                    int parserEvent = parser.getEventType();
                    while (parserEvent != XmlPullParser.END_DOCUMENT) {
                        switch (parserEvent) {
                            case XmlPullParser.START_TAG:
                                if (parser.getName().equals("addr")) {
                                    result.append("주소: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("chargeTp")) {
                                    result.append("충전기 타입: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("cpId")) {
                                    result.append("충전소 ID: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("cpNm")) {
                                    result.append("충전기 명칭: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("cpStat")) {
                                    result.append("충전기 상태 코드: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("cpTp")) {
                                    result.append("충전 방식: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("csId")) {
                                    result.append("충전소 ID: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("csNm")) {
                                    result.append("충전소 명칭: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("lat")) {
                                    result.append("위도: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("longi")) {
                                    result.append("경도: ").append(parser.nextText()).append("\n");
                                } else if (parser.getName().equals("statUpdateDatetime")) {
                                    result.append("충전기 상태 갱신 시각: ").append(parser.nextText()).append("\n");
                                }
                                break;

                            case XmlPullParser.END_TAG:
                                if (parser.getName().equals("item")) {
                                    result.append("\n");
                                }
                                break;
                        }
                        parserEvent = parser.next();
                    }
                } else {
                    result.append("에러 응답 코드: ").append(responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                result.append("에러가 발생했습니다.");
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            status1.setText(result);
        }
    }
}
