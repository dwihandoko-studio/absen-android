package com.taralite.e_presensi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.taralite.e_presensi.connection.WebClientDevWrapper;
import com.taralite.e_presensi.services.MyServiceMahasiswa;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String webservice = "http://192.168.1.100/presensi/apiv2/service/";
    public static String typeUser, id, nim, mhs_nama, id_kelas, email, id_semester, id_akademik, tahun_masuk, tanggal_lahir, no_hp, id_prodi, prodi, id_jurusan, jurusan, imei, nim_email;
    public static String id_dosen, nip, dosen_nama, jabatan;
    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    Spinner spinner1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.nim_email);
        spinner1 = (Spinner) findViewById(R.id.spinner1);

        List<String> list = new ArrayList<String>();
        list.add("Mahasiswa");
        list.add("Dosen");

        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = mngr.getDeviceId();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeUser = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        nim_email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            System.out.println("TYPE USER "+typeUser);
            if (typeUser.equals("Mahasiswa")) {
                UserLoginTask mAuthTask = new UserLoginTask();
                mAuthTask.execute("loginMhs", imei, nim_email);

            } else {
                UserLoginTask mAuthTask = new UserLoginTask();
                mAuthTask.execute("loginDosen", imei, nim_email);

            }
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
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result, mhs_id, mhs_name, kelas_id, dosen_id, dosenNama;


        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try {
                typeUser = params[0];
                DefaultHttpClient httpClient = (DefaultHttpClient) WebClientDevWrapper.getNewHttpClient();
                HttpPost httpPost = new HttpPost(webservice + params[0]); // Jenis respost
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);

                if(params[0].equals("loginMhs")){
                    nameValuePairs.add(new BasicNameValuePair("ImeiNumber", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("nim", params[2]));
                    nameValuePairs.add(new BasicNameValuePair("signature", "dd7298aa1a5d2220ba3b11d82db4feb9a3bc908e"));

                } else {
                    nameValuePairs.add(new BasicNameValuePair("ImeiNumber", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("email", params[2]));
                    nameValuePairs.add(new BasicNameValuePair("signature", "dd7298aa1a5d2220ba3b11d82db4feb9a3bc908e"));
                }

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                int code = httpResponse.getStatusLine().getStatusCode();

                if (code == HttpStatus.SC_OK) {
                    is = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close(); // tutup koneksi stlah medapatkan respone
                    json = sb.toString(); // Respon di jadikan sebuah string
                    System.out.println("HASIL :" +json);
                    jObj = new JSONObject(json); // Response di jadikan sebuah
                    result = jObj.getString("status");

                    if (result.trim().equals("200")) {
                        if (params[0].equals("loginMhs")) {

                            id = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("id");
                            nim = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("nim");
                            mhs_nama = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("nama_mhs");
                            id_kelas = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("id_kelas");
                            id_semester = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("id_semester");
                            id_akademik = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("id_akademik");
                            tahun_masuk = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("tahun_masuk");
                            tanggal_lahir = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("tanggal_lahir");
                            email = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("email");
                            no_hp = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("no_hp");
                            id_prodi = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("id_prodi");
                            prodi = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("prodi");
                            id_jurusan = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("id_jurusan");
                            jurusan = jObj.getJSONObject("results").getJSONObject("mahasiswa_profile").getString("jurusan");

                        } else {
                            id_dosen = jObj.getJSONObject("results").getJSONObject("dosen_profile").getString("id");
                            nip = jObj.getJSONObject("results").getJSONObject("dosen_profile").getString("nip");
                            dosen_nama = jObj.getJSONObject("results").getJSONObject("dosen_profile").getString("nama_dosen");
                            jabatan = jObj.getJSONObject("results").getJSONObject("dosen_profile").getString("jabatan");
                            tanggal_lahir = jObj.getJSONObject("results").getJSONObject("dosen_profile").getString("tanggal_lahir");
                            no_hp = jObj.getJSONObject("results").getJSONObject("dosen_profile").getString("no_hp");
                            email = jObj.getJSONObject("results").getJSONObject("dosen_profile").getString("email");
                        }

                    } else {
                        // GAGAL LOGIN
                        System.out.println("GAGAL LOGIN");
                    }

                } else {
                    // GAGAL REQUEST
                    System.out.println("GAGAL REQUEST ");
                }


            } catch (Exception e) {
                System.out.println("Errrorr " + e.getMessage());
                return null;

            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(String hasil) {
            mAuthTask = null;
            showProgress(false);
            try {

                if (result.trim().equals("200")) {

                    Intent pindah = new Intent(LoginActivity.this, UtamaActivity.class);
                    pindah.putExtra("typeUser", typeUser);

                    if (typeUser.equals("loginMhs")) {
                        pindah.putExtra("id", id);
                        pindah.putExtra("nim", nim);
                        pindah.putExtra("mhs_nama", mhs_nama);
                        pindah.putExtra("id_kelas", id_kelas);
                        pindah.putExtra("id_semester", id_semester);
                        pindah.putExtra("id_akademik", id_akademik);

                        /* Service mahasiswa untuk mengecek ada jadwal hari ini atau tidak
                         lalu di cek jam jadwalnya jika sudah memasuki jam pada jadwal
                         maka akan mengirimkan location, dgn catatan location di hp sudah enable*/

                        try {
                            Intent start = new Intent(LoginActivity.this, MyServiceMahasiswa.class);
                            start.putExtra("typeUser", typeUser);
                            start.putExtra("nim", nim);
                            start.putExtra("mhs_nama", mhs_nama);
                            start.putExtra("id_kelas", id_kelas);
                            start.putExtra("id_semester", id_semester);
                            start.putExtra("id_akademik", id_akademik);
                            startService(start);

                        } catch (Exception er) {

                        }

                    } else {
                        pindah.putExtra("nip", nip);
                        pindah.putExtra("dosen_nama", dosen_nama);

                    }
                    startActivity(pindah);

                } else {

                }

            } catch (Exception er) {
                System.out.println("Errorrr " + er.getMessage());

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);

        }
    }
}
