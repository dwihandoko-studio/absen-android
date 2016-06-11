package com.taralite.e_presensi.object;

/**
 * Created by taralite on 6/10/16.
 */
public class DataObjectRuangan {
    private String KodeRuangan, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4,
            LantaiRuangan, StatusRuangan;

    public DataObjectRuangan(String text1, String text2, String text3, String text4, String text5, String text6,
                             String text7, String text8) {
        KodeRuangan = text1;
        Ruangan = text2;
        Ruangan_latlng1 = text3;
        Ruangan_latlng2 = text4;
        Ruangan_latlng3 = text5;
        Ruangan_latlng4 = text6;
        LantaiRuangan = text7;
        StatusRuangan = text8;
    }

    public String getKodeRuangan() {
        return KodeRuangan;
    }
    public String getRuangan() {
        return Ruangan;
    }
    public String getRuangan_latlng1() {
        return Ruangan_latlng1;
    }
    public String getRuangan_latlng2() {
        return Ruangan_latlng2;
    }
    public String getRuangan_latlng3() {
        return Ruangan_latlng3;
    }
    public String getRuangan_latlng4() {
        return Ruangan_latlng4;
    }
    public String getLantaiRuangan() {
        return LantaiRuangan;
    }
    public String getStatusRuangan() {
        return StatusRuangan;
    }
}
