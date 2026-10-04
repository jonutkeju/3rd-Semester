package com.example.t04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class KontakAdapter extends ArrayAdapter<Kontak> {

    public interface OnActionClickListener {
        void onEditClick(Kontak kontak);
        void onDeleteClick(Kontak kontak);
        void onPesanClick(Kontak kontak);
    }

    private final OnActionClickListener actionClickListener;

    private static class ViewHolder {
        TextView tvNama;
        TextView tvNoHp;
        TextView tvNoHp2;
        TextView tvAlamat;
        TextView tvPekerjaan;
        Button btnEdit;
        Button btnHapus;
        Button btnPesan;
    }

    public KontakAdapter(Context context, int resource, List<Kontak> objects, OnActionClickListener listener) {
        super(context, resource, objects);
        this.actionClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Kontak dataKontak = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_kontak, parent, false);
            holder = new ViewHolder();
            holder.tvNama = convertView.findViewById(R.id.tvNama);
            holder.tvNoHp = convertView.findViewById(R.id.tvNoHp);
            holder.tvNoHp2 = convertView.findViewById(R.id.tvNoHp2);
            holder.tvAlamat = convertView.findViewById(R.id.tvAlamat);
            holder.tvPekerjaan = convertView.findViewById(R.id.tvPekerjaan);
            holder.btnEdit = convertView.findViewById(R.id.btnEdit);
            holder.btnHapus = convertView.findViewById(R.id.btnHapus);
            holder.btnPesan = convertView.findViewById(R.id.btnPesan);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (dataKontak != null) {
            holder.tvNama.setText(dataKontak.getNama());
            holder.tvNoHp.setText("HP 1: " + dataKontak.getNoHp());
            
            if (dataKontak.getNoHp2() != null && !dataKontak.getNoHp2().isEmpty()) {
                holder.tvNoHp2.setVisibility(View.VISIBLE);
                holder.tvNoHp2.setText("HP 2: " + dataKontak.getNoHp2());
            } else {
                holder.tvNoHp2.setVisibility(View.GONE);
            }

            if (dataKontak.getAlamat() != null && !dataKontak.getAlamat().isEmpty()) {
                holder.tvAlamat.setVisibility(View.VISIBLE);
                holder.tvAlamat.setText("Alamat: " + dataKontak.getAlamat());
            } else {
                holder.tvAlamat.setVisibility(View.GONE);
            }

            if (dataKontak.getPekerjaan() != null && !dataKontak.getPekerjaan().isEmpty()) {
                holder.tvPekerjaan.setVisibility(View.VISIBLE);
                holder.tvPekerjaan.setText("Pekerjaan: " + dataKontak.getPekerjaan());
            } else {
                holder.tvPekerjaan.setVisibility(View.GONE);
            }
            
            holder.btnEdit.setOnClickListener(v -> {
                if (actionClickListener != null) {
                    actionClickListener.onEditClick(dataKontak);
                }
            });

            holder.btnHapus.setOnClickListener(v -> {
                if (actionClickListener != null) {
                    actionClickListener.onDeleteClick(dataKontak);
                }
            });

            holder.btnPesan.setOnClickListener(v -> {
                if (actionClickListener != null) {
                    actionClickListener.onPesanClick(dataKontak);
                }
            });
        }

        return convertView;
    }
}
