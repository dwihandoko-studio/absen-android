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
import com.taralite.e_presensi.object.DataObjectJadwalHarian;

import java.util.ArrayList;

/**
 * Created by taralite on 5/26/16.
 */
public class AdapterJadwalHarian extends RecyclerView.Adapter<AdapterJadwalHarian.DataObjectHolder> {
    private static String LOG_TAG = "AdapterJadwalHarian";
    private ArrayList<DataObjectJadwalHarian> mDataset;
    private static MyClickListener myClickListener;
    Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Matkul, Ruangan, Waktu, Dosen;
        ImageView Garis;
        CardView CardView;

        public DataObjectHolder(View itemView) {
            super(itemView);
            Matkul = (TextView) itemView.findViewById(R.id.cjh_matkul);
            Ruangan = (TextView) itemView.findViewById(R.id.cjh_ruangan);
            Waktu = (TextView) itemView.findViewById(R.id.cjh_waktu);
            Dosen = (TextView) itemView.findViewById(R.id.cjh_dosen);
            Garis = (ImageView) itemView.findViewById(R.id.cjh_garis);
            CardView = (CardView) itemView.findViewById(R.id.cjh_card_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // myClickListener.onItemClick(getAdapterPosition(), v);

        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        // this.myClickListener = myClickListener;
    }

    public AdapterJadwalHarian(Context applicationContext, ArrayList<DataObjectJadwalHarian> myDataset) {
        context = applicationContext;
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_jadwal_harian, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.Matkul.setText(mDataset.get(position).getMatkul());
        holder.Ruangan.setText(mDataset.get(position).getRuangan());
        holder.Waktu.setText(mDataset.get(position).getWaktu());
        holder.Dosen.setText(mDataset.get(position).getDosen());
        holder.CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                Intent pindah = new Intent(context, RuanganDetailActivity.class);
                pindah.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                pindah.putExtra("id_ruangan", mDataset.get(position).getKodeRuangan());
                pindah.putExtra("ruangan", mDataset.get(position).getRuangan());
                pindah.putExtra("ruangan_latlng1", mDataset.get(position).getRuangan_latlng1());
                pindah.putExtra("ruangan_latlng2", mDataset.get(position).getRuangan_latlng2());
                pindah.putExtra("ruangan_latlng3", mDataset.get(position).getRuangan_latlng3());
                pindah.putExtra("ruangan_latlng4", mDataset.get(position).getRuangan_latlng4());
                context.startActivity(pindah);
            } catch (Exception er) {

            }
            }
        });
    }

    public void addItem(DataObjectJadwalHarian dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);

    }

    public void updateItem(DataObjectJadwalHarian dataObj, int index) {
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