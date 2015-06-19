package kr.ac.korea.embedded.lightapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 사용자 고유 아이디를 서버로 할당받고, 사용되는 서비스들을 초기화함
 */
public class MainActivity extends Activity implements View.OnClickListener, Response.ErrorListener, Response.Listener<String>, Runnable {
    SharedPreferences pfSetting;
    RequestQueue rq;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pfSetting = getSharedPreferences("setting", MODE_PRIVATE);
        rq = Volley.newRequestQueue(this);

        int userId = pfSetting.getInt("userId", 0);

        if (userId != 0) {
            chkUserId(userId);
            return;
        }

        setContentView(R.layout.setting);

        findViewById(R.id.start).setOnClickListener(this);
    }

    private void chkUserId(int id) {
        String url = StaticValue.checkUserUrl(id);
        StringRequest req = new StringRequest(Request.Method.GET, url, this, this);
        rq.add(req);
    }

    private void createUserId() {
        String url = StaticValue.createUserUrl();
        StringRequest req = new StringRequest(Request.Method.POST, url, this, this);
        rq.add(req);
    }

    @Override
    public void run() {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        handler.post(this);
    }

    @Override
    public void onClick(View v) {
        //Only R.id.start
        EditText txt = (EditText)findViewById(R.id.userId);

        if (txt.getText().length() == 0) {
            createUserId();
        } else {
            try {
                int userId = Integer.parseInt(txt.getText().toString());
                chkUserId(userId);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "입력값이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.v("Internet-Error", error.toString());
        Toast.makeText(this, "인터넷에 연결할 수 없어 정보를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();

        if (pfSetting.contains("userId")) init();
    }

    @Override
    public void onResponse(String response) {
        JSONObject res, body;

        try {
            res = new JSONObject(response);
            body = res.getJSONObject("result");
            if (body.has("id")) {
                int id = body.getInt("id");
                pfSetting.edit().putInt("userId", id).apply();
                init();
            } else {
                Toast.makeText(this, "존재하지 않는 환자 아이디거나 새로운 아이디 발급에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "서버에 문제가 발생했습니다. 관리자에게 문의 바랍니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}