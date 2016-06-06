package com.taralite.e_presensi;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.taralite.e_presensi.adapter.AdapterJadwalHarian;
import com.taralite.e_presensi.connection.WebClientDevWrapper;
import com.taralite.e_presensi.menu.JadwalActivity;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taralite on 5/26/16.
 */
public class UtamaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView list_jadwal_harian;
    private AdapterJadwalHarian mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static String nim, mhs_nama, id_kelas, id_semester, id_akademik, nip, dosen_nama, typeUser;
    TextView namaUserLogin, typeUserLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utama);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list_jadwal_harian = (RecyclerView) findViewById(R.id.list_jadwal_harian);
        list_jadwal_harian.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        list_jadwal_harian.setLayoutManager(mLayoutManager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        namaUserLogin = (TextView) v.findViewById(R.id.namaUserLogin);
        typeUserLogin = (TextView) v.findViewById(R.id.jenisUserLogin);

        Intent ambil = getIntent();
        typeUser = ambil.getStringExtra("typeUser");

        if (typeUser.equals("loginMhs")) {
            nim = ambil.getStringExtra("nim");
            mhs_nama = ambil.getStringExtra("mhs_nama");
            id_kelas = ambil.getStringExtra("id_kelas");
            id_semester = ambil.getStringExtra("id_semester");
            id_akademik = ambil.getStringExtra("id_akademik");
            namaUserLogin.setText("" + mhs_nama);
            typeUserLogin.setText("Mahasiswa");

            new getScheToday().execute("ListJadwalToday", id_kelas, id_semester, id_akademik);

        } else {
            nip = ambil.getStringExtra("dosen_id");
            dosen_nama = ambil.getStringExtra("nama_dosen");
            namaUserLogin.setText("" + dosen_nama);
            typeUserLogin.setText("Dosen");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.utama, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_akun) {

        } else if (id == R.id.nav_ruangan) {

        } else if (id == R.id.nav_jadwal) {

            if (typeUser.equals("loginMhs")) {

                Intent pindah = new Intent(UtamaActivity.this, JadwalActivity.class);
                pindah.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pindah.putExtra("typeUser", typeUser);
                pindah.putExtra("nim", nim);
                pindah.putExtra("mhs_nama", mhs_nama);
                pindah.putExtra("id_kelas", id_kelas);
                pindah.putExtra("id_semester", id_semester);
                pindah.putExtra("id_akademik", id_akademik);
                startActivity(pindah);

            } else {

            }

        } else if (id == R.id.nav_kompensasi) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class getScheToday extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result;
        String[] KodeJadwal, KodeMatkul, Matkul, JadwalHari, JadwalMulai, JadwalSelesai, KodeRuangan, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4, Dosen;
        int code;
        ArrayList ArrayList;
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
                System.out.println("LINKNYA " + LoginActivity.webservice + params[0]);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
                nameValuePairs.add(new BasicNameValuePair("signature", "dd7298aa1a5d2220ba3b11d82db4feb9a3bc908e"));
                nameValuePairs.add(new BasicNameValuePair("id_kelas", params[1]));
                nameValuePairs.add(new BasicNameValuePair("id_semester", params[2]));
                nameValuePairs.add(new BasicNameValuePair("id_akademik", params[3]));
                nameValuePairs.add(new BasicNameValuePair("hari", "today"));

                System.out.println("PARAMSS " + params[0]);
                System.out.println("PARAMSS " + params[1]);
                System.out.println("PARAMSS " + params[2]);
                System.out.println("PARAMSS " + params[3]);

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
                    System.out.println("Hasil TODAY " + json);
                    jObj = new JSONObject(json); // Response di jadikan sebuah
                    result = jObj.getString("status");
                    System.out.println("Hasul resultnya : " + result);
                    if (result.trim().equals("200")) {

                        JSONArray DATA = jObj.getJSONObject("results").getJSONArray("listjadwal");
                        KodeJadwal = new String[DATA.length()];
                        KodeMatkul = new String[DATA.length()];
                        Matkul = new String[DATA.length()];
                        JadwalHari = new String[DATA.length()];
                        JadwalMulai = new String[DATA.length()];
                        JadwalSelesai = new String[DATA.length()];
                        KodeRuangan = new String[DATA.length()];
                        Ruangan = new String[DATA.length()];
                        Ruangan_latlng1 = new String[DATA.length()];
                        Ruangan_latlng2 = new String[DATA.length()];
                        Ruangan_latlng3 = new String[DATA.length()];
                        Ruangan_latlng4 = new String[DATA.length()];
                        Dosen = new String[DATA.length()];

                        ArrayList = new ArrayList<DataObjectJadwalHarian>();

                        for (int y = 0; y < DATA.length(); y++) {
                            JSONObject ar = DATA.getJSONObject(y);
                            KodeJadwal[y] = ar.getJSONObject("general").getString("kode_jadwal");
                            JadwalHari[y] = ar.getJSONObject("general").getString("hari");
                            JadwalMulai[y] = ar.getJSONObject("general").getString("jam_mulai");
                            JadwalSelesai[y] = ar.getJSONObject("general").getString("jam_selesai");
                            KodeMatkul[y] = ar.getJSONObject("info_matkul").getString("id_mk");
                            Matkul[y] = ar.getJSONObject("info_matkul").getString("nama_mk");
                            KodeRuangan[y] = ar.getJSONObject("info_ruangan").getString("id_ruangan");
                            Ruangan[y] = ar.getJSONObject("info_ruangan").getString("nama_ruangan");
                            Ruangan_latlng1[y] = ar.getJSONObject("info_ruangan").getString("latlong_a");
                            Ruangan_latlng2[y] = ar.getJSONObject("info_ruangan").getString("latlong_b");
                            Ruangan_latlng3[y] = ar.getJSONObject("info_ruangan").getString("latlong_c");
                            Ruangan_latlng4[y] = ar.getJSONObject("info_ruangan").getString("latlong_d");
                            Dosen[y] = ar.getJSONObject("info_dosen").getString("nama_dosen");


                            DataObjectJadwalHarian obj1 = new DataObjectJadwalHarian(KodeMatkul[y], Matkul[y], JadwalMulai[y] + "-" + JadwalSelesai[y], KodeRuangan[y], Ruangan[y], Ruangan_latlng1[y], Ruangan_latlng2[y], Ruangan_latlng3[y], Ruangan_latlng4[y], Dosen[y]);
                            ArrayList.add(y, obj1);
                        }


                    } else {
                        // GAGAL LOGIN
                        System.out.println("GAGAL DAPAT DATA");
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
                    mAdapter = new AdapterJadwalHarian(getApplicationContext(), ArrayList);
                    list_jadwal_harian.setAdapter(mAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Gagal Response code " + code, Toast.LENGTH_LONG).show();
                }
            } catch (Exception er) {

            }
        }


    }


}
