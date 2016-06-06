package com.taralite.e_presensi.menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import com.taralite.e_presensi.LoginActivity;
import com.taralite.e_presensi.R;

/**
 * Created by taralite on 5/29/16.
 */
public class RuanganDetailActivity extends AppCompatActivity {

    String id_ruangan, ruangan, ruangan_latlng1, ruangan_latlng2, ruangan_latlng3, ruangan_latlng4;
    LinearLayout backscp;
    TextView textRuangan, textStatus;
    ImageView imageStatus;
    public GoogleMap mMap;
    Button lihatMahasiswa;

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

        textRuangan.setText(ruangan);
        if (LoginActivity.typeUser.equals("loginMhs")) {
            lihatMahasiswa.setEnabled(true);
        } else {
            lihatMahasiswa.setEnabled(true);
        }

        backscp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        rectOptions.strokeWidth(4);
        rectOptions.strokeColor(Color.RED);
        rectOptions.fillColor(Color.TRANSPARENT);
        Polygon polygon = mMap.addPolygon(rectOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat1, long2)).zoom(18).build();

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
