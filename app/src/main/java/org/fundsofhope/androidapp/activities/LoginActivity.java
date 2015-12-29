package org.fundsofhope.androidapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.fundsofhope.androidapp.R;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class        LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _userName;
    EditText _passWord;
    int user_flag;

    Button _loginButton;
    Button _signupButton;
    ProgressDialog progressDialog;
    String email;
    String password;
    JSONObject jobj=null;
    RadioButton user;
    RadioButton ngo;
    SharedPreferences.Editor editor;
    String URL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (pref.getInt("flag", -1) != 1) {
            setContentView(R.layout.activity_login);
           // FacebookSdk.sdkInitialize(getApplicationContext());
           // callbackManager = CallbackManager.Factory.create();
            //LoginButton loginButton = (LoginButton) view.findViewById(R.id.usersettings_fragment_login_button);
            //loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //});

            editor = pref.edit();
            editor.putInt("flag", 0);
            _userName = (EditText) findViewById(R.id.input_user);
            _passWord = (EditText) findViewById(R.id.input_password);

            _loginButton = (Button) findViewById(R.id.btn_login);
            _signupButton = (Button) findViewById(R.id.btn_signup);
            user=(RadioButton)findViewById(R.id.user);
            ngo=(RadioButton)findViewById(R.id.ngo);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ngo.setEnabled(false);
                    user_flag = 1;
                }
            });
            ngo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setEnabled(false);
                    user_flag = 2;
                }
            });

            _loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = _userName.getText().toString();
                    password = _passWord.getText().toString();
                    SharedPreferences mpref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = mpref.edit();
                    editor.putInt("user",user_flag);
                    editor.commit();
                    new LoginTask().execute("");
                }
            });

            _signupButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            });
        }
        else {
            setContentView(R.layout.activity_back);

            Button back = (Button) findViewById(R.id.back);
            TextView txt=(TextView)findViewById(R.id.link_login);
            SharedPreferences mpref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String ema=mpref.getString("email","");
            txt.setText("Hi "+ema);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //sdemail = pref.getString("email", "");
                    //password = pref.getString("pass", "");
                    //token=pref.getString("token","");
                    //Log.i(TAG,"email"+email+""+password);

                    Intent inte=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(inte);
                    finish();
                    //new LoginTask().execute("");
                }
            });
        }
    }



    private InputStream is = null;
    private String page_output = "";

    private class LoginTask extends AsyncTask<String, Integer, JSONObject> {


        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            // progressDialog.dismiss();


        }


        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub


//            Log.i(TAG,uname.getText().toString()+"ksjdvnslkdvxnwadlk");
//			Log.i(TAG,pass.getText().toString()+"fsdxcjvnskjdn");
            try{
                Log.i(TAG,"entered try()");

                //Toast.makeText(getApplicationContext(), "Please wait,connecting to server",Toast.LENGTH_LONG).show();
                Log.i(TAG,"entered toast()");
                Log.i(TAG,email);
                Log.i(TAG,password);
                if(user_flag==1)
                URL="http://fundsofhope.herokuapp.com/user/login";
                else if(user_flag==2)
                    URL="http://fundsofhope.herokuapp.com/ngo/login";
                HttpClient Client=new DefaultHttpClient();
                Log.i(TAG,"created client");
//			try{
//				String Response="";
                HttpGet httpget=new HttpGet(URL);
                Log.i(TAG, "hhtp get");
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                Log.i(TAG, "in response handler");

                List<NameValuePair> data = new ArrayList<NameValuePair>();
                if(user_flag==1)data.add(new BasicNameValuePair("username", email));
                else if(user_flag==2)data.add(new BasicNameValuePair("ngoid", email));
                data.add(new BasicNameValuePair("password", password));

                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(data));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.i(TAG,"executed");
                is = httpEntity.getContent();
                Log.i(TAG, "in strict mode");

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    page_output = sb.toString();
                    jobj=new JSONObject(page_output);
                    Log.i("LOG", "page_output --> " + page_output);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                Log.i(TAG,"request executed");

            } catch(UnsupportedEncodingException e){
            } catch (IOException e) {
            }
            Log.i(TAG, "returning response");
            return jobj;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(JSONObject result){
            Log.i(TAG, "Entered on post execute");
            progressDialog.dismiss();


            //Toast.makeText(LoginActivity.this,"length="+result.length()+result, Toast.LENGTH_LONG).show();


            try {
                if(result.getBoolean("login")) {
                    SharedPreferences.Editor editor;

                    SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = pref.edit();

                    String token=result.getString("token");

                    String message=result.getString("message");
                    editor.putInt("flag", 1);
                    editor.putInt("user",user_flag);


                    editor.putString("token", token);
                    editor.putString("email",email);
                    editor.putString("pass",password);
                    editor.commit();

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    System.out.println("token is"+token);
                    Log.i(TAG, "Entered if");
                    //onLoginSuccess();
                    successlog();
                }
                else if (!result.getBoolean("login")) {
                    onLoginFailed();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Can't Connect", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        protected void successlog(){


            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String password = _passWord.getText().toString();

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passWord.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passWord.setError(null);
        }

        return valid;
    }
}
