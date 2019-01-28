package cn.demomaster.huan.ndktest.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.demomaster.huan.ndktest.MainActivity;
import cn.demomaster.huan.ndktest.R;
import cn.demomaster.huan.ndktest.model.CommonApi;
import cn.demomaster.huan.ndktest.net.RetrofitInterface;
import cn.demomaster.huan.ndktest.util.AppStateUtil;
import cn.demomaster.huan.quickdeveloplibrary.base.BaseActivityParent;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ActionBarLayout;
import cn.demomaster.huan.quickdeveloplibrary.helper.SharedPreferencesHelper;
import cn.demomaster.huan.quickdeveloplibrary.http.HttpUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivityParent {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button btn_login;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    Login();
                    return true;
                }
                return false;
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        getActionBarLayout().setActionBarModel(ActionBarLayout.ACTIONBAR_TYPE.NO_ACTION_BAR_NO_STATUS);
        getActionBarLayout().getLeftView().setVisibility(View.GONE);
    }

    private void Login() {
        //Retrofit
        RetrofitInterface retrofitInterface = HttpUtils.getInstance().getRetrofit(RetrofitInterface.class, "http://www.demomaster.cn/");
        retrofitInterface.login(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CommonApi>() {
                    @Override
                    public void onNext(@NonNull CommonApi response) {
                        Log.i(TAG, "onNext: " + JSON.toJSONString(response));
                        try {
                            JSONObject jsonObject = JSON.parseObject(response.getData().toString());
                            String id = jsonObject.get("id").toString();
                            SharedPreferencesHelper.getInstance().putString("UserId", id);
                            String username = jsonObject.get("username").toString();
                            SharedPreferencesHelper.getInstance().putString("username", username);
                            String nickname = jsonObject.get("nickname").toString();
                            SharedPreferencesHelper.getInstance().putString("nickname", nickname);
                            String headerUrl = jsonObject.get("face").toString();
                            SharedPreferencesHelper.getInstance().putString("headerUrl", headerUrl);
                            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                            Date date = new Date();//获得系统时间
                            String nowDaDate = sdf.format(date);
                            SharedPreferencesHelper.getInstance().putString("dateTime", nowDaDate);
                            AppStateUtil.getInstance().setAppStateIsLogined(true);
                            finish();
                            startActivity(MainActivity.class);
                            //List doctors1 = JSON.parseArray(response.getData().toString(), DoctorModelApi.class);
                            //String token = jsonObject.get("token").toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onStart() {
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.i(TAG, "onError: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            // TODO: alert the user with a Snackbar/AlertDialog giving them the permission rationale
            // To use the Snackbar from the design support library, ensure that the activity extends
            // AppCompatActivity and uses the Theme.AppCompat theme.
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


}

