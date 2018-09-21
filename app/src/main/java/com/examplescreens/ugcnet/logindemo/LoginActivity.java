package com.examplescreens.ugcnet.logindemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    class ROLE{
        int id, status, created_by, updated_by, tenant_id, code;
        String name, created_at, updated_at;
    }


    class TENANT {
        int id, status, created_by, updated_by, allow_same_domain;
        String name, website, logo, url, created_at, updated_at, attachment_id, company_email, contact_person;
    }


    class PROFILE {
        int id, status, created_by, updated_by, user_id, tenant_id, gender;
        String first_name, last_name, avatar, phone_no, created_at, updated_at, attachment_id, bio;
    }


    class USER_WITH_ROLE_TENANT_PROFILE {
        int id, status, active_tenant_id, active_role_id, is_verified;
        String username, email, created_by, updated_by, created_at, updated_at, password_updated_at, is_email_verified;

        ROLE role_in_user = new ROLE();
        TENANT tenant_in_user = new TENANT();
        PROFILE profile_in_user = new PROFILE();
    }


    JSONObject jsonObject;
    StringBuilder finalResultString = new StringBuilder();

    public class CloudConnectAsync2 extends AsyncTask<Context, Void, String> {

        Context context;

        @Override
        protected String doInBackground(Context... contexts) {
            context = contexts[0];

            OkHttpClient okHttpClient = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .url("http://159.65.7.75:9000/v1/auth")
                    .post(requestBody)
                    .build();

            Response response = null;

            try{
                response = okHttpClient.newCall(request).execute();
                return response.body().string();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;


/*
        StringBuilder result = new StringBuilder();
        URL url;
        HttpURLConnection httpURLConnection;
        try{
            url = new URL("https://api.getpostman.com/collections/");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while(data != -1){
                Log.i("MY_APP", String.valueOf(data));
                char dataCh = (char) data;

                result.append(dataCh);
                data = inputStreamReader.read();
            }

        }catch (Exception e){
            Log.i("MY_APP", "InExceptionResult = "+result);
            Log.i("MY_APP", "Exception:"+e.getMessage());
        }
        Log.i("MY_APP", "Result: "+result.toString());
        return result.toString();*/
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                jsonObject = new JSONObject(s);

                finalResultString.append("status: "+jsonObject.get("status")).append("\n");

                JSONObject userJsonObject = jsonObject.getJSONObject("user");
                USER_WITH_ROLE_TENANT_PROFILE user_with_role_tenant_profile = new USER_WITH_ROLE_TENANT_PROFILE();
                user_with_role_tenant_profile.active_role_id = userJsonObject.getInt("active_role_id");
                user_with_role_tenant_profile.active_tenant_id = userJsonObject.getInt("active_tenant_id");
                user_with_role_tenant_profile.created_at = userJsonObject.getString("created_at");
                user_with_role_tenant_profile.created_by = userJsonObject.getString("created_by");
                user_with_role_tenant_profile.email = userJsonObject.getString("email");
                user_with_role_tenant_profile.id = userJsonObject.getInt("id");
                user_with_role_tenant_profile.is_email_verified = userJsonObject.getString("is_email_verified");
                user_with_role_tenant_profile.is_verified = userJsonObject.getInt("is_verified");
                user_with_role_tenant_profile.password_updated_at = userJsonObject.getString("password_updated_at");




            } catch (JSONException e) {
                e.printStackTrace();
            }

            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Result")
                    .setMessage(String.valueOf(jsonObject.names()))
                    .setCancelable(false)
                    .setPositiveButton("ok", null)
                    .show();
            Log.i("MY_APP", "Result: " + s);
        }
    }


    static String email;
    static String password;

    TextView textView;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(10000);
                }catch (Exception e){
                    Log.i("MY_APP", e.getMessage());
                }

                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Sajid");
                    }
                });
            }
        }).start();
/*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://jsonplaceholder.typicode.com/posts");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                    for (String line; (line = reader.readLine()) != null;) {
                        Log.i("MY_APP", line);
                    }
                }catch (Exception e){
                    Log.i("MY_APP", "Ex: "+e.getMessage());
                }
            }
        }).start();*/



        textView = findViewById(R.id.textView);



        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();

                email = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();

                Log.i("MY_APP", "Email: "+email);
                Log.i("MY_APP", "Password: "+password);

                /*
                CloudConnectAsync cloudConnectAsync = new CloudConnectAsync();
                cloudConnectAsync.execute(getApplicationContext());*/

                if(!email.isEmpty() && !password.isEmpty()){

                    CloudConnectAsync2 cloudConnectAsync2 = new CloudConnectAsync2();
                    cloudConnectAsync2.execute(getApplicationContext());

                }else{
                    Toast.makeText(LoginActivity.this, "Email or password invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
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
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

