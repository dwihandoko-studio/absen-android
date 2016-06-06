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
import com.taralite.e_presensi.UtamaActivity;
import com.taralite.e_presensi.adapter.AdapterJadwal;
import com.taralite.e_presensi.connection.WebClientDevWrapper;
import com.taralite.e_presensi.object.DataObjectJadwal;

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
 * Created by taralite on 5/26/16.
 */
public class JadwalActivity  extends AppCompatActivity {
    public static String nim, mhs_nama, id_kelas, id_semester, id_akademik, nip, dosen_nama, typeUser, position;
    LinearLayout backscp;
    RecyclerView list_jadwal;
    RecyclerView.LayoutManager mLayoutManager;
    AdapterJadwal mAdapter;
    String dateNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        backscp = (LinearLayout) findViewById(R.id.backscp);
        list_jadwal = (RecyclerView) findViewById(R.id.list_jadwal);
        list_jadwal.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        list_jadwal.setLayoutManager(mLayoutManager);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        dateNow = dateFormat.format(today);


        //AMBIL INTENT YG DI KIRIM OLEH CLASS SEBELUMNYA
        Intent ambil = getIntent();
        typeUser = ambil.getStringExtra("typeUser");


        // Get data sche from server
        try {
            if (typeUser.equals("loginMhs")) {

                nim = ambil.getStringExtra("nim");
                mhs_nama = ambil.getStringExtra("mhs_nama");
                id_kelas = ambil.getStringExtra("id_kelas");
                id_semester = ambil.getStringExtra("id_semester");
                id_akademik = ambil.getStringExtra("id_akademik");

                SimpleDateFormat form = new SimpleDateFormat("yyyy-04");

                Calendar c = Calendar.getInstance();
                int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                int monthMinDays = c.getActualMinimum(Calendar.DAY_OF_MONTH);

                String formattedDate = form.format(c.getTime());

                String start = formattedDate + "-" + monthMinDays;
                String end = formattedDate + "-" + monthMaxDays;
                System.out.println(start + "  <<>>> " + end);

                new getJadwalAll().execute("ListJadwalAll", id_kelas, id_semester, id_akademik);
            } else {
                nip = ambil.getStringExtra("dosen_id");
                Toast.makeText(getApplicationContext(), "API Jadwal dosen belum ada", Toast.LENGTH_LONG).show();
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


    public class getJadwalAll extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result, JumlahMatkul;
        String[] KodeJadwal, KodeMatkul, Matkul, JadwalHari, JadwalMulai, JadwalSelesai, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4, Dosen;
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
                nameValuePairs.add(new BasicNameValuePair("id_kelas", params[1]));
                nameValuePairs.add(new BasicNameValuePair("id_semester", params[2]));
                nameValuePairs.add(new BasicNameValuePair("id_akademik", params[3]));

                System.out.println(params[1]);
                System.out.println(params[2]);
                System.out.println(params[3]);

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                        HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                code = httpResponse.getStatusLine().getStatusCode();
                System.out.println("CODEEEEEE " + code);
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
                    System.out.println("Hasil Login " + json);
                    jObj = new JSONObject(json); // Response di jadikan sebuah
                    result = jObj.getString("status");
                    System.out.println("Hasul resul; " + result);
                    if (result.trim().equals("200")) {

                        JSONArray DATA = jObj.getJSONArray("results");
                        System.out.println("JUMLAH DATANYA " + DATA.length());
                        KodeJadwal = new String[DATA.length()];
                        KodeMatkul = new String[DATA.length()];
                        Matkul = new String[DATA.length()];
                        JadwalHari = new String[DATA.length()];
                        JadwalMulai = new String[DATA.length()];
                        JadwalSelesai = new String[DATA.length()];
                        Ruangan = new String[DATA.length()];
                        Ruangan_latlng1 = new String[DATA.length()];
                        Ruangan_latlng2 = new String[DATA.length()];
                        Ruangan_latlng3 = new String[DATA.length()];
                        Ruangan_latlng4 = new String[DATA.length()];
                        Dosen = new String[DATA.length()];

                        arrayList = new ArrayList<DataObjectJadwal>();

                        for (int y = 0; y < DATA.length(); y++) {
                            JSONObject ar = DATA.getJSONObject(y);
                            JadwalHari[y] = ar.getString("nama_hari");
                            try {
                                JadwalHari[y] = ar.getString("nama_hari");
                                JSONArray DATA2 = ar.getJSONArray("list_jadwal");

                                JumlahMatkul = String.valueOf(DATA2.length());
                                for (int x = 0; x < DATA2.length(); x++) {
                                    JSONObject ar2 = DATA2.getJSONObject(x);
                                    KodeJadwal[x] = ar2.getJSONObject("general").getString("kode_jadwal");
                                    Ruangan[x] = ar2.getJSONObject("info_ruangan").getString("nama_ruangan");
                                    Matkul[x] = ar2.getJSONObject("info_matkul").getString("nama_mk");
                                    Dosen[x] = ar2.getJSONObject("info_dosen").getString("nama_dosen");
                                    JadwalMulai[x] = ar2.getJSONObject("general").getString("jam_mulai");
                                    JadwalSelesai[x] = ar2.getJSONObject("general").getString("jam_selesai");
                                    Ruangan_latlng1[x] = ar2.getJSONObject("info_ruangan").getString("latlong_a");
                                    Ruangan_latlng2[x] = ar2.getJSONObject("info_ruangan").getString("latlong_b");
                                    KodeMatkul[x] = ar2.getJSONObject("info_matkul").getString("id_mk");
                                }

                            } catch (Exception er) {
                                System.out.println("Errorrr karena "+er.getMessage());
                                JadwalHari[y] = null;
                                KodeJadwal[y] = null;
                                Ruangan[y] = null;
                                Matkul[y] = null;
                                Dosen[y] = null;
                                JadwalMulai[y] = null;
                                JadwalSelesai[y] = null;
                                Ruangan_latlng1[y] = null;
                                Ruangan_latlng2[y] = null;
                                KodeMatkul[y] = null;
                            }

                            DataObjectJadwal obj = new DataObjectJadwal(JumlahMatkul, Matkul[y], JadwalHari[y], JadwalMulai[y], JadwalSelesai[y], Ruangan[y], Ruangan_latlng1[y], Ruangan_latlng2[y], Ruangan_latlng3[y], Ruangan_latlng4[y], Dosen[y]);
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
                    mAdapter = new AdapterJadwal(getApplicationContext(), arrayList);
                    list_jadwal.setAdapter(mAdapter);


                } else {
                    Toast.makeText(getApplicationContext(), "Gagal Response code " + code, Toast.LENGTH_LONG).show();
                }
            } catch (Exception er) {

            }
        }
    }
}