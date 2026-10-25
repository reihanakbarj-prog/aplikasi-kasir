package com.kasir.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private int id;
    private Timestamp tanggal;
    private long total, bayar, kembali;
    private String kasir;
    private List<CartItem> items = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Timestamp getTanggal() { return tanggal; }
    public void setTanggal(Timestamp tanggal) { this.tanggal = tanggal; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getBayar() { return bayar; }
    public void setBayar(long bayar) { this.bayar = bayar; }
    public long getKembali() { return kembali; }
    public void setKembali(long kembali) { this.kembali = kembali; }
    public String getKasir() { return kasir; }
    public void setKasir(String kasir) { this.kasir = kasir; }
    public List<CartItem> getItems() { return items; }
}
