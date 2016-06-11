package com.taralite.e_presensi.menu;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taralite on 5/29/16.
 */
public class RuanganDetailActivity extends AppCompatActivity {

    String id_ruangan, ruangan, ruangan_latlng1, ruangan_latlng2, ruangan_latlng3, ruangan_latlng4, status_ruangan;
    LinearLayout backscp;
    TextView textRuangan, textStatus;
    ImageView imageStatus;
    public GoogleMap mMap;
    Button lihatMahasiswa;
    JSONArray DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruangan_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        setUpMapIfNeeded();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Show rationale and request permission.
        }

        backscp = (LinearLayout) findViewById(R.id.backscp);
        textRuangan = (TextView) findViewById(R.id.rd_ruangan);
        textStatus = (TextView) findViewById(R.id.rd_text_status);
        imageStatus = (ImageView) findViewById(R.id.rd_status);
        lihatMahasiswa = (Button) findViewById(R.id.rd_lihat_mahasiswa);

        Intent ambil = getIntent();
        id_ruangan = ambil.getStringExtra("id_ruangan");
        ruangan = ambil.getStringExtra("ruangan");
        ruangan_latlng1 = ambil.getStringExtra("ruangan_latlng1");
        ruangan_latlng2 = ambil.getStringExtra("ruangan_latlng2");
        ruangan_latlng3 = ambil.getStringExtra("ruangan_latlng3");
        ruangan_latlng4 = ambil.getStringExtra("ruangan_latlng4");

        drawPolyGon(ruangan_latlng1, ruangan_latlng2, ruangan_latlng3, ruangan_latlng4);
        new getJadwalByRoom().execute("JadwalRuangan", id_ruangan);

        textRuangan.setText(ruangan);

        backscp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class getJadwalByRoom extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result;
        String KodeJadwal, KodeMatkul, Matkul, JadwalMulai, JadwalSelesai, KodeRuangan, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4, Dosen;
        int code, jumlah;
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
                nameValuePairs.add(new BasicNameValuePair("id_ruangan", params[1]));
                nameValuePairs.add(new BasicNameValuePair("hari", "today"));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                code = httpResponse.getStatusLine().getStatusCode();

                if (code == HttpStatus.SC_OK) {
                    is = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
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

                        DATA = jObj.getJSONObject("results").getJSONArray("listjadwal");

                        if (DATA.length() > 0) {
                            KodeJadwal = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("general").getString("kode_jadwal");
                            KodeMatkul = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_matkul").getString("id_mk");
                            Matkul = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_matkul").getString("nama_mk");
                            JadwalMulai = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("general").getString("jam_mulai");
                            JadwalSelesai = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("general").getString("jam_selesai");
                            KodeRuangan = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_ruangan").getString("id_ruangan");
                            Ruangan = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_ruangan").getString("nama_ruangan");
                            Ruangan_latlng1 = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_ruangan").getString("latlong_a");
                            Ruangan_latlng2 = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_ruangan").getString("latlong_b");
                            Ruangan_latlng3 = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_ruangan").getString("latlong_c");
                            Ruangan_latlng4 = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_ruangan").getString("latlong_d");
                            Dosen = jObj.getJSONObject("results").getJSONObject("listjadwal").getJSONObject("info_dosen").getString("nama_dosen");

                        } else {
                            // GAGAL DAPAT JADWAL
                            System.out.println("GAGAL DAPAT JADWAL");
                            System.out.println("LENGTH DATA :" +DATA.length());
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
                    if (DATA.length() > 0){
                        textStatus.setText("ada");
                        imageStatus.setImageResource(R.drawable.buled_hijau);
                        lihatMahasiswa.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent pindah = new Intent(RuanganDetailActivity.this, JadwalActivity.class);
                                pindah.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                pindah.putExtra("id_jadwal", KodeJadwal);
                                startActivity(pindah);
                            }
                        });

                    } else {
                        textStatus.setText("kosong");
                        imageStatus.setImageResource(R.drawable.buled_merah);
                        lihatMahasiswa.setEnabled(false);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Gagal dapat jadwal berdasarkan room " + code, Toast.LENGTH_LONG).show();
                }

            } catch (Exception er) {

            }
        }
    }

    private void drawPolyGon(String ruangan_latlng1, String ruangan_latlng2, String ruangan_latlng3, String ruangan_latlng4) {

        String point1[] = ruangan_latlng1.split(",");
        String point2[] = ruangan_latlng2.split(",");
        String point3[] = ruangan_latlng3.split(",");
        String point4[] = ruangan_latlng4.split(",");

        double lat1 = Double.valueOf(point1[0]);
        double long1 = Double.valueOf(point1[1]);
        double lat2 = Double.valueOf(point2[0]);
        double long2 = Double.valueOf(point2[1]);
        double lat3 = Double.valueOf(point3[0]);
        double long3 = Double.valueOf(point3[1]);
        double lat4 = Double.valueOf(point4[0]);
        double long4 = Double.valueOf(point4[1]);

        PolygonOptions rectOptions = new PolygonOptions().
                add(new LatLng(lat1, long1),
                        new LatLng(lat2, long2),
                        new LatLng(lat3, long3),
                        new LatLng(lat4, long4),
                        new LatLng(lat1, long1));
        rectOptions.strokeWidth(2);
        rectOptions.strokeColor(Color.RED);
        rectOptions.fillColor(Color.TRANSPARENT);
        Polygon polygon = mMap.addPolygon(rectOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat1, long2)).zoom(17).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.rd_maps)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

            }
        }
    }
}
