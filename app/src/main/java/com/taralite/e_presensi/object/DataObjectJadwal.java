package com.taralite.e_presensi.object;

/**
 * Created by taralite on 5/26/16.
 */
public class DataObjectJadwal {

    private String JumlahJadwal, Matkul, JadwalHari, JadwalMulai, JadwalSelesai, Ruangan, Ruangan_latlng1, Ruangan_latlng2,
            Ruangan_latlng3, Ruangan_latlng4, Dosen;

    public DataObjectJadwal(String text1, String text2, String text3, String text4, String text5, String text6,
                            String text7, String text8, String text9, String text10, String text11) {
        JumlahJadwal = text1;
        Matkul = text2;
        JadwalHari = text3;
        JadwalMulai = text4;
        JadwalSelesai = text5;
        Ruangan = text6;
        Ruangan_latlng1 = text7;
        Ruangan_latlng2 = text8;
        Ruangan_latlng3 = text9;
        Ruangan_latlng4 = text10;
        Dosen = text11;
    }

    public String getJumlahJadwal() {
        return JumlahJadwal;
    }
    public String getMatkul() {
        return Matkul;
    }
    public String getJadwalHari() {
        return JadwalHari;
    }
    public String getJadwalMulai() {
        return JadwalMulai;
    }
    public String getJadwalSelesai() {
        return JadwalSelesai;
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
