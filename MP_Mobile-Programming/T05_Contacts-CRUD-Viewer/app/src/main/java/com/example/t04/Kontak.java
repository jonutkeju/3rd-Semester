package com.example.t04;

public class Kontak {
    private long id;
    private String nama;
    private String noHp;
    private String noHp2;
    private String alamat;
    private String pekerjaan;

    public Kontak(long id, String nama, String noHp, String noHp2, String alamat, String pekerjaan) {
        this.id = id;
        this.nama = nama;
        this.noHp = noHp;
        this.noHp2 = noHp2;
        this.alamat = alamat;
        this.pekerjaan = pekerjaan;
    }

    public Kontak(String nama, String noHp, String noHp2, String alamat, String pekerjaan) {
        this.id = -1;
        this.nama = nama;
        this.noHp = noHp;
        this.noHp2 = noHp2;
        this.alamat = alamat;
        this.pekerjaan = pekerjaan;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getNoHp2() {
        return noHp2;
    }

    public void setNoHp2(String noHp2) {
        this.noHp2 = noHp2;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }
}
