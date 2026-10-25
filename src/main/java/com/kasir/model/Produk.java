package com.kasir.model;

public class Produk {
    private int id;
    private String kode;
    private String nama;
    private long harga;
    private int stok;
    private String kategori;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
}
