package com.taralite.e_presensi.object;

/**
 * Created by taralite on 5/29/16.
 */
public class DataObjectRuanganDetail {
    private String Ruangan, Ruangan_status;

    public DataObjectRuanganDetail(String text1, String text2) {
        Ruangan = text1;
        Ruangan_status = text2;
    }

    public String getRuangan() {
        return Ruangan;
    }
    public String getRuangan_status() {
        return Ruangan_status;
    }
}
