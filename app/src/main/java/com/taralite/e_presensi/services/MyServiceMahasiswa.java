package com.taralite.e_presensi.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.taralite.e_presensi.LoginActivity;
import com.taralite.e_presensi.connection.WebClientDevWrapper;
import com.taralite.e_presensi.location.GPSTracker;

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

public class MyServiceMahasiswa extends Service {

    public static Handler handler = new Handler();
    public static Runnable ping = null;
    String nim, mhs_nama, id_kelas, id_semester, id_akademik, typeUser;
    String[] status_send_location;

    public MyServiceMahasiswa() {}

    @Override
    public void onStart(Intent intent, int startid) {
        System.out.println("Masuk ke Service");
        try {
            typeUser = LoginActivity.typeUser;
            if (typeUser.equals("loginMhs")) {
                mhs_nama = LoginActivity.mhs_nama;
                id_kelas = LoginActivity.id_kelas;
                id_semester = LoginActivity.id_semester;
                id_akademik = LoginActivity.id_akademik;
                nim = LoginActivity.nim;

                new getJadwalNow().execute("ListJadwalToday", id_kelas, id_semester, id_akademik);

            }

        } catch (Exception er) {

        }
    }

    public class getJadwalNow extends AsyncTask<String, Void, String> {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result;
        String[] KodeJadwal, KodeMatkul, Matkul, JadwalHari, JadwalMulai, JadwalSelesai, KodeRuangan, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4, Dosen;
        int code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

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

                System.out.println("Param :" + params[1]);
                System.out.println("Param :" + params[2]);
                System.out.println("Param :" + params[3]);

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                code = httpResponse.getStatusLine().getStatusCode();

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
                    System.out.println("DATA " +json);
                    jObj = new JSONObject(json); // Response di jadikan sebuah
                    result = jObj.getString("status");

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
                        status_send_location = new String[DATA.length()];

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
                            status_send_location[y] = "belum";
                        }
                        System.out.println("JADWAL ADA " +DATA.length());

                    } else {
                        // GAGAL STATUS
                        System.out.println("GAGAL STATUS");
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

            try {
                if (result.equals("200")) {
                    if (KodeJadwal.length > 0) {
                        System.out.println("RUNNABLE :" +KodeJadwal.length);
                        /** Runable untuk melakukan pengulangan jika terdapat jadwal
                         * apakah jam skrg sudah masuk dalam jadwal atau belum */

                        ping = new Runnable() {
                            @Override
                            public void run() {
                                handler.postDelayed(ping, 60000); // di ulang setiap 60 second
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                    String currentDateandTime = dateFormat.format(new Date());
                                    Date date = dateFormat.parse(currentDateandTime);

                                    long fiveMenitBackSecond = date.getTime();

                                    for (int y = 0; y < JadwalMulai.length; y++) {
                                        Date mulai = dateFormat.parse(JadwalMulai[y]);
                                        long started = mulai.getTime();
                                        Date akhir = dateFormat.parse(JadwalSelesai[y]);
                                        long finished = akhir.getTime();

                                        System.out.println("JAM MULAI :" + started);
                                        System.out.println("JAM SELESAI :" + finished);
                                        System.out.println("JAM SEKARANG :" + fiveMenitBackSecond);

                                        //status_send_location untuk mengecek jika ada jadwal lebih dari 1,
                                        if (status_send_location[y].equals("belum")) {
                                            //Pengecekan apakah jam sekarang masuk diantara jam pada jadwal
                                            if (started < fiveMenitBackSecond && finished > fiveMenitBackSecond) {
                                                //Maka sudah masuk jadwal dan kirim current location
                                                GPSTracker gps = new GPSTracker(MyServiceMahasiswa.this);
                                                String longitude = String.valueOf(gps.getLongitude());
                                                String latitude = String.valueOf(gps.getLatitude());
                                                System.out.println("MASUK ABSEN");
                                                System.out.println("LONGITUDE :" + longitude);
                                                System.out.println("LATITUDE :" + latitude);

                                                new sendLocationPushNotif().execute("pushAbsensi", KodeJadwal[y], latitude, longitude, nim, String.valueOf(y));

                                            } else {
                                                // Maka belum masuk jadwal nya
                                                System.out.println("BELUM ABSEN");
                                                System.out.println("JADWAL MULAI :" + JadwalMulai[y]);
                                            }

                                        } else {
                                            System.out.println("SEND LOCATION SUDAH PERNAH");
                                        }
                                    }

                                } catch (Exception er) {
                                    System.out.println("Errro runnabele notif " + er.getMessage());
                                }

                            }
                        };
                        handler.post(ping);

                    } else {
                        // Tidak memiliki jadwal hari ini
                        System.out.println("TIDAK ADA JADWAL HARI INI");

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Gagal Response code " + code, Toast.LENGTH_LONG).show();

                }

            } catch (Exception er) {

            }
        }
    }

    public class sendLocationPushNotif extends AsyncTask<String, Void, String> {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "", result;
        String typeUser;
        String status_absen;

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try {
                typeUser = params[0];
                DefaultHttpClient httpClient = (DefaultHttpClient) WebClientDevWrapper.getNewHttpClient();
                HttpPost httpPost = new HttpPost(LoginActivity.webservice + params[0]); // Jenis respost
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
                nameValuePairs.add(new BasicNameValuePair("signature", "dd7298aa1a5d2220ba3b11d82db4feb9a3bc908e"));
                nameValuePairs.add(new BasicNameValuePair("id_jadwal", params[1]));
                nameValuePairs.add(new BasicNameValuePair("lat", params[2]));
                nameValuePairs.add(new BasicNameValuePair("lon", params[3]));
                nameValuePairs.add(new BasicNameValuePair("nim", params[4]));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                int code = httpResponse.getStatusLine().getStatusCode();

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
                        status_absen = jObj.getJSONObject("results").getString("status_absen");
                        if (status_absen.equals("ok")) {
                            // STOP PUSH CEK KE SERVER
                            status_send_location[Integer.valueOf(params[5])] = "sudah";
                        }

                    } else {
                        // GAGAL LOGIN
                        System.out.println("GAGAL ABSEN");
                    }

                } else {
                    // GAGAL REQUEST
                    System.out.println("GAGAL REQUEST ");

                }

            } catch (Exception e) {
                System.out.println("TANGKEP ERROR LOCATION " + e.getMessage());
                return null;
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(String hasil) {
            try {
                if (result.trim().equals("200")) {
                    if (status_absen.equals("ok")) {
                        // STOP PUSH CEK KE SERVER
                    }

                } else {

                }

            } catch (Exception er) {
                System.out.println("TANGKEP ERROR KEHADIRAN " + er.getMessage());
            }
        }

        @Override
        protected void onCancelled() {}
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
