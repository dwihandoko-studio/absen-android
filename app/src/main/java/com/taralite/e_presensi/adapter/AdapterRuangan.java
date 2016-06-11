package com.taralite.e_presensi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taralite.e_presensi.R;
import com.taralite.e_presensi.menu.RuanganDetailActivity;
import com.taralite.e_presensi.object.DataObjectRuangan;

import java.util.ArrayList;

/**
 * Created by taralite on 6/10/16.
 */
public class AdapterRuangan extends RecyclerView.Adapter<AdapterRuangan.DataObjectRuanganHolder> {
    private static String LOG_TAG = "AdapterRuangan";
    private ArrayList<DataObjectRuangan> mDataset;
    private static MyClickListener myClickListener;
    Context context;

    public static class DataObjectRuanganHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Ruangan, LantaiRuangan, StatusRuangan;
        CardView layoutCardView;
        ImageView ImageStatusRuangan, Garis;

        public DataObjectRuanganHolder(View itemView) {
            super(itemView);
            Ruangan = (TextView) itemView.findViewById(R.id.cr_ruangan);
            LantaiRuangan = (TextView) itemView.findViewById(R.id.cr_lantai);
            StatusRuangan = (TextView) itemView.findViewById(R.id.cr_status);
            layoutCardView = (CardView) itemView.findViewById(R.id.cr_card_view);
            ImageStatusRuangan = (ImageView) itemView.findViewById(R.id.cr_imagestatus);
            Garis = (ImageView) itemView.findViewById(R.id.cr_garis);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
//        this.myClickListener = myClickListener;
    }

    public AdapterRuangan(Context applicationContext, ArrayList<DataObjectRuangan> myDataset) {
        context = applicationContext;
        mDataset = myDataset;
    }

    @Override
    public DataObjectRuanganHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ruangan, parent, false);
        DataObjectRuanganHolder dataObjectHolder = new DataObjectRuanganHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectRuanganHolder holder, final int position) {
        holder.Ruangan.setText(mDataset.get(position).getRuangan());
        holder.LantaiRuangan.setText("Lantai " + mDataset.get(position).getLantaiRuangan());
        holder.StatusRuangan.setText(mDataset.get(position).getStatusRuangan());
        if (mDataset.get(position).getStatusRuangan().equals("kosong")) {
            holder.ImageStatusRuangan.setImageResource(R.drawable.buled_merah);
        } else {
            holder.ImageStatusRuangan.setImageResource(R.drawable.buled_hijau);
        }

        holder.layoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pindah = new Intent(context, RuanganDetailActivity.class);
                pindah.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pindah.putExtra("id_ruangan", mDataset.get(position).getKodeRuangan());
                pindah.putExtra("ruangan", mDataset.get(position).getRuangan());
                pindah.putExtra("ruangan_latlng1", mDataset.get(position).getRuangan_latlng1());
                pindah.putExtra("ruangan_latlng2", mDataset.get(position).getRuangan_latlng2());
                pindah.putExtra("ruangan_latlng3", mDataset.get(position).getRuangan_latlng3());
                pindah.putExtra("ruangan_latlng4", mDataset.get(position).getRuangan_latlng4());

                context.startActivity(pindah);
            }
        });
    }

    public void addItem(DataObjectRuangan dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);

    }

    public void updateItem(DataObjectRuangan dataObj, int index) {
        mDataset.set(index, dataObj);
        notifyDataSetChanged();

    }

    public void changePosition(int positionAwal, int positionAkhir) {
        notifyItemMoved(positionAwal, positionAkhir);
    }


    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}