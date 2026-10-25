package com.kasir.model;

public class CartItem {
    private int produkId;
    private String nama;
    private long harga;
    private int qty;

    public CartItem() {}
    public CartItem(int produkId, String nama, long harga, int qty) {
        this.produkId = produkId; this.nama = nama; this.harga = harga; this.qty = qty;
    }
    public int getProdukId() { return produkId; }
    public String getNama() { return nama; }
    public long getHarga() { return harga; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public long getSubtotal() { return harga * qty; }
}
