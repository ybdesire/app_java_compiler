package example.ybdesire.com.javacompiler;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //AdMob
        MobileAds.initialize(this, "ca-app-pub-8100413825150401/1420343090");
        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //Edit Text
        final AppCompatEditText editText = (AppCompatEditText) findViewById(R.id.text_input_code);

        //Buttons
        Button btn=findViewById(R.id.button_tab);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), "    ");
            }
        });
        btn=findViewById(R.id.button_println);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), "System.out.println(  )");
            }
        });
        btn=findViewById(R.id.button_quote);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), "\"");
            }
        });
        btn=findViewById(R.id.button_semi);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.getText().insert(editText.getSelectionStart(), ";");
            }
        });
        // compile
        btn=findViewById(R.id.button_compile);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try  {

                            //Your code goes here
                            String code = editText.getText().toString();
                            String url = "http://45.79.179.111/java-android/compile_android.php";
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost(url);
                            // Add your data
                            List<NameValuePair> nameValuePairs = new ArrayList< NameValuePair >(5);
                            nameValuePairs.add(new BasicNameValuePair("source", code));
                            nameValuePairs.add(new BasicNameValuePair("input", "0"));

                            try {
                                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                                Log.d("myapp", "works till here. 2");
                                try {
                                    HttpResponse response = httpclient.execute(httppost);
                                    String responseBody = EntityUtils.toString(response.getEntity());
                                    TextView txtOutput=findViewById(R.id.txt_output);//find output label by id
                                    setText(txtOutput,responseBody);
                                    Log.d("myapp", "response " + responseBody);
                                } catch (ClientProtocolException e) {
                                    e.printStackTrace();
                                    TextView txtOutput=findViewById(R.id.txt_output);//find output label by id
                                    setText(txtOutput,getString(R.string.err_network));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    TextView txtOutput=findViewById(R.id.txt_output);//find output label by id
                                    setText(txtOutput,getString(R.string.err_network));
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                TextView txtOutput=findViewById(R.id.txt_output);//find output label by id
                                setText(txtOutput,getString(R.string.err_network));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView txtOutput=findViewById(R.id.txt_output);//find output label by id

                            txtOutput.setText(getString(R.string.err_network));
                        }
                    }
                });

                thread.start();
                //disable button and modify color
                Button btnc=findViewById(R.id.button_compile);
                btnc.setClickable(false);
                btnc.setBackgroundColor(Color.GRAY);

                //timer for 5s delay and enable button
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        Button btncc=findViewById(R.id.button_compile);
                        btncc.setClickable(true);
                        btncc.setBackgroundResource(android.R.drawable.btn_default);
                    }
                }, 5000);


            }
        });

        //init
        String str = "public class Main//should be Main here\n{\n    public static void main(String[] arg)\n    {\n        System.out.println(\"hello world\");\n    }\n }";
        SpannableString ss = CodeEditText.setHighLight(str);
        editText.setText(ss);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int start, int count, int after) {
                //Log.d("onTextChanged", "onTextChanged,str_len="+cs.toString().length());
                //Log.d("onTextChanged", "start="+start);
                //Log.d("onTextChanged", "str="+cs.toString().substring(start,start+1));
                //Log.d("onTextChanged", "count="+count);
                //Log.d("onTextChanged", "after"+after);

                if(cs.toString().substring(start,start+1).equals(" "))
                {
                    //Log.d("onTextChanged", "get space");
                    /*
                    SpannableString ss = new SpannableString(cs.toString());
                    String textToSearch = "public";
                    Pattern pattern = Pattern.compile(textToSearch);
                    Matcher matcher = pattern.matcher(ss);
                    while (matcher.find()) {
                        ss.setSpan(new ForegroundColorSpan(Color.RED), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    edittext.setText(ss);
                    */
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //Log.d("DBG", "beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("DBG", "afterTextChanged");
                editText.removeTextChangedListener(this);
                String str = editText.getText().toString();

                int po = editText.getSelectionStart();//get cursor
                SpannableString ss = CodeEditText.setHighLight(str);
                editText.setText(ss);

                editText.setSelection(po);//set cursor
                editText.addTextChangedListener(this);

            }

        });


    }
}
