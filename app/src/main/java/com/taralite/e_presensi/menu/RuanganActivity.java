package com.taralite.e_presensi.menu;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.taralite.e_presensi.LoginActivity;
import com.taralite.e_presensi.R;
import com.taralite.e_presensi.adapter.AdapterRuangan;
import com.taralite.e_presensi.connection.WebClientDevWrapper;
import com.taralite.e_presensi.object.DataObjectRuangan;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by taralite on 6/10/16.
 */
public class RuanganActivity extends AppCompatActivity {
    RecyclerView list_ruangan;
    RecyclerView.LayoutManager mLayoutManager;
    AdapterRuangan mAdapter;
    LinearLayout backscp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruangan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        list_ruangan = (RecyclerView) findViewById(R.id.list_ruangan);
        backscp = (LinearLayout) findViewById(R.id.backscp);
        list_ruangan.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        list_ruangan.setLayoutManager(mLayoutManager);

        new getRuanganAll().execute("ListRuangan");

        backscp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class getRuanganAll extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result;
        String[] KodeRuangan, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4, LantaiRuangan, StatusRuangan;
        int code;
        ArrayList arrayList;
        Dialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pDialog = new Dialog(getApplicationContext());
                pDialog.setTitle("Connecting...");
                pDialog.show();

            } catch (Exception er) {
                System.out.println("Error di MyServiceMahasiswa data " + er.getMessage());
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try {
                DefaultHttpClient httpClient = (DefaultHttpClient) WebClientDevWrapper.getNewHttpClient();
                HttpPost httpPost = new HttpPost(LoginActivity.webservice + params[0]); // Jenis respost
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
                nameValuePairs.add(new BasicNameValuePair("signature", "dd7298aa1a5d2220ba3b11d82db4feb9a3bc908e"));
                nameValuePairs.add(new BasicNameValuePair("hari", "today"));

                System.out.println("PARAM" + params[0]);

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                code = httpResponse.getStatusLine().getStatusCode();

                if (code == HttpStatus.SC_OK) {
                    is = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader( new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    is.close(); // tutup koneksi stlah medapatkan respone
                    json = sb.toString(); // Respon di jadikan sebuah string
                    jObj = new JSONObject(json); // Response di jadikan sebuah
                    result = jObj.getString("status");

                    if (result.trim().equals("200")) {

                        JSONArray DATA = jObj.getJSONObject("results").getJSONArray("listdata");
                        KodeRuangan = new String[DATA.length()];
                        Ruangan = new String[DATA.length()];
                        Ruangan_latlng1 = new String[DATA.length()];
                        Ruangan_latlng2 = new String[DATA.length()];
                        Ruangan_latlng3 = new String[DATA.length()];
                        Ruangan_latlng4 = new String[DATA.length()];
                        StatusRuangan = new String[DATA.length()];
                        LantaiRuangan = new String[DATA.length()];

                        arrayList = new ArrayList<DataObjectRuangan>();

                        for (int y = 0; y < DATA.length(); y++) {
                            JSONObject ar = DATA.getJSONObject(y);
                            KodeRuangan[y] = ar.getString("kode");
                            Ruangan[y] = ar.getString("ruangan");
                            Ruangan_latlng1[y] = ar.getString("latlong_a");
                            Ruangan_latlng2[y] = ar.getString("latlong_b");
                            Ruangan_latlng3[y] = ar.getString("latlong_c");
                            Ruangan_latlng4[y] = ar.getString("latlong_d");
                            StatusRuangan[y] = ar.getString("status_ruangan");
                            LantaiRuangan[y] = ar.getString("lantai");

                            System.out.println("JUMLAH MATKULNYA " + Ruangan);

                            DataObjectRuangan obj = new DataObjectRuangan(KodeRuangan[y], Ruangan[y], Ruangan_latlng1[y], Ruangan_latlng2[y], Ruangan_latlng3[y], Ruangan_latlng4[y], LantaiRuangan[y], StatusRuangan[y]);
                            arrayList.add(y, obj);
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
            pDialog.dismiss();
            try {
                if (result.equals("200")) {
                    mAdapter = new AdapterRuangan(getApplicationContext(), arrayList);
                    list_ruangan.setAdapter(mAdapter);

                } else {
                    Toast.makeText(getApplicationContext(), "Gagal Response code " + code, Toast.LENGTH_LONG).show();
                }

            } catch (Exception er) {

            }
        }
    }

}