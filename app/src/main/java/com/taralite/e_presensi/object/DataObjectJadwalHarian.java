package com.taralite.e_presensi.object;

/**
 * Created by taralite on 5/26/16.
 */
public class DataObjectJadwalHarian {
    private String KodeMatkul, Matkul, Waktu, KodeRuangan, Ruangan, Ruangan_latlng1, Ruangan_latlng2, Ruangan_latlng3, Ruangan_latlng4,
             Dosen;

    public DataObjectJadwalHarian(String text1, String text2, String text3, String text4, String text5, String text6,
                                  String text7, String text8, String text9, String text10) {

        KodeMatkul = text1;
        Matkul = text2;
        Waktu = text3;
        KodeRuangan = text4;
        Ruangan = text5;
        Ruangan_latlng1 = text6;
        Ruangan_latlng2 = text7;
        Ruangan_latlng3 = text8;
        Ruangan_latlng4 = text9;
        Dosen = text10;
    }

    public String getKodeMatkul() {
        return KodeMatkul;
    }
    public String getMatkul() {
        return Matkul;
    }
    public String getWaktu() {
        return Waktu;
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
    public String getDosen() {
        return Dosen;
    }

}
