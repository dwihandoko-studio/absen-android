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
import android.widget.TextView;
import android.widget.Toast;

import com.taralite.e_presensi.LoginActivity;
import com.taralite.e_presensi.R;
import com.taralite.e_presensi.adapter.AdapterJadwalHarian;
import com.taralite.e_presensi.connection.WebClientDevWrapper;
import com.taralite.e_presensi.object.DataObjectJadwalHarian;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by taralite on 5/28/16.
 */
public class JadwalHarianActivity extends AppCompatActivity {
    String nim, mhs_nama, id_kelas, id_semester, id_akademik, nip, typeUser, position, day;
    LinearLayout backscp;
    private RecyclerView list_jadwal_harian;
    private AdapterJadwalHarian mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String dateNow;
    TextView titleJadwal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_harian);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        backscp = (LinearLayout) findViewById(R.id.backscp);
        titleJadwal = (TextView) findViewById(R.id.titleJadwal);
        list_jadwal_harian = (RecyclerView) findViewById(R.id.list_jadwal_harian);
        list_jadwal_harian.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        list_jadwal_harian.setLayoutManager(mLayoutManager);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        dateNow = dateFormat.format(today);

        //AMBIL INTENT YG DI KIRIM OLEH CLASS SEBELUMNYA
        Intent ambil = getIntent();
        typeUser = ambil.getStringExtra("typeUser");

        // Get data sche from server
        try {
            if (typeUser.equals("loginMhs")) {

                day = ambil.getStringExtra("day");
                nim = ambil.getStringExtra("nim");
                mhs_nama = ambil.getStringExtra("mhs_nama");
                id_kelas = ambil.getStringExtra("id_kelas");
                id_semester = ambil.getStringExtra("id_semester");
                id_akademik = ambil.getStringExtra("id_akademik");
                position = ambil.getStringExtra("position");

                titleJadwal.setText("Jadwal " + day);
                new getJadwalToday().execute("ListJadwalToday", id_kelas, id_semester, id_akademik, position);

            } else {
                nip = ambil.getStringExtra("nip");
                position = ambil.getStringExtra("position");

                titleJadwal.setText("Jadwal " + day);
                new getJadwalToday().execute("ListJadwalForDosenToday", nip, position);

            }

        } catch (Exception er) {

        }

        backscp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    public class getJadwalToday extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result;
        String[] KodeJadwal, KodeMatkul, Matkul, JadwalMulai, JadwalSelesai, KodeRuangan, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4, Dosen;
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
                if (params[0].equals("ListJadwalToday")){
                    nameValuePairs.add(new BasicNameValuePair("signature", "dd7298aa1a5d2220ba3b11d82db4feb9a3bc908e"));
                    nameValuePairs.add(new BasicNameValuePair("id_kelas", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("id_semester", params[2]));
                    nameValuePairs.add(new BasicNameValuePair("id_akademik", params[3]));
                    nameValuePairs.add(new BasicNameValuePair("hari", params[4]));

                } else {
                    nameValuePairs.add(new BasicNameValuePair("signature", "dd7298aa1a5d2220ba3b11d82db4feb9a3bc908e"));
                    nameValuePairs.add(new BasicNameValuePair("id_dosen", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("hari", params[2]));
                }

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

                        JSONArray DATA = jObj.getJSONObject("results").getJSONArray("listjadwal");
                        KodeJadwal = new String[DATA.length()];
                        KodeMatkul = new String[DATA.length()];
                        Matkul = new String[DATA.length()];
                        JadwalMulai = new String[DATA.length()];
                        JadwalSelesai = new String[DATA.length()];
                        KodeRuangan = new String[DATA.length()];
                        Ruangan = new String[DATA.length()];
                        Ruangan_latlng1 = new String[DATA.length()];
                        Ruangan_latlng2 = new String[DATA.length()];
                        Ruangan_latlng3 = new String[DATA.length()];
                        Ruangan_latlng4 = new String[DATA.length()];
                        Dosen = new String[DATA.length()];


                        arrayList = new ArrayList<DataObjectJadwalHarian>();

                        for (int y = 0; y < DATA.length(); y++) {
                            JSONObject ar = DATA.getJSONObject(y);
                            KodeJadwal[y] = ar.getJSONObject("general").getString("kode_jadwal");
                            KodeMatkul[y] = ar.getJSONObject("info_matkul").getString("id_mk");
                            Matkul[y] = ar.getJSONObject("info_matkul").getString("nama_mk");
                            JadwalMulai[y] = ar.getJSONObject("general").getString("jam_mulai");
                            JadwalSelesai[y] = ar.getJSONObject("general").getString("jam_selesai");
                            KodeRuangan[y] = ar.getJSONObject("info_ruangan").getString("id_ruangan");
                            Ruangan[y] = ar.getJSONObject("info_ruangan").getString("nama_ruangan");
                            Ruangan_latlng1[y] = ar.getJSONObject("info_ruangan").getString("latlong_a");
                            Ruangan_latlng2[y] = ar.getJSONObject("info_ruangan").getString("latlong_b");
                            Ruangan_latlng3[y] = ar.getJSONObject("info_ruangan").getString("latlong_c");
                            Ruangan_latlng4[y] = ar.getJSONObject("info_ruangan").getString("latlong_d");
                            Dosen[y] = ar.getJSONObject("info_dosen").getString("nama_dosen");

                            DataObjectJadwalHarian obj1 = new DataObjectJadwalHarian(KodeMatkul[y], Matkul[y],  JadwalMulai[y] + "-" + JadwalSelesai[y], KodeRuangan[y], Ruangan[y], Ruangan_latlng1[y], Ruangan_latlng2[y], Ruangan_latlng3[y], Ruangan_latlng4[y], Dosen[y]);
                            arrayList.add(y, obj1);
                        }


                    } else {
                        // GAGAL LOGIN
                        System.out.println("GAGAL RESPON");
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
                    if(arrayList.size()>0){
                        mAdapter = new AdapterJadwalHarian(getApplicationContext(), arrayList);
                        list_jadwal_harian.setAdapter(mAdapter);

                    } else {
                        Toast.makeText(getApplicationContext(), "Jadwal anda hari ini kosong ", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Gagal Response code " + code, Toast.LENGTH_LONG).show();
                }
            } catch (Exception er) {

            }
        }


    }


}

