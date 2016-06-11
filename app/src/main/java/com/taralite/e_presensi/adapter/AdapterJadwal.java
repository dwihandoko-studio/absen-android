package com.taralite.e_presensi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taralite.e_presensi.R;
import com.taralite.e_presensi.menu.JadwalActivity;
import com.taralite.e_presensi.menu.JadwalHarianActivity;
import com.taralite.e_presensi.object.DataObjectJadwal;

import java.util.ArrayList;

/**
 * Created by taralite on 5/26/16.
 */
public class AdapterJadwal extends RecyclerView.Adapter<AdapterJadwal.DataObjectScheHolder> {
    private static String LOG_TAG = "AdapterJadwal";
    private ArrayList<DataObjectJadwal> mDataset;
    private static MyClickListener myClickListener;
    Context context;

    public static class DataObjectScheHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView Hari, Jumlah, Waktu;
        ImageView GarisBawah;
        CardView CardView;

        public DataObjectScheHolder(View itemView) {
            super(itemView);

            Hari = (TextView) itemView.findViewById(R.id.cj_hari);
            Jumlah = (TextView) itemView.findViewById(R.id.cj_jumlah_matkul);
            Waktu = (TextView) itemView.findViewById(R.id.cj_waktu);
            GarisBawah = (ImageView) itemView.findViewById(R.id.cj_garis);
            CardView = (CardView) itemView.findViewById(R.id.cj_card_view);

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

    public AdapterJadwal(Context applicationContext, ArrayList<DataObjectJadwal> myDataset) {
        context = applicationContext;
        mDataset = myDataset;
    }

    @Override
    public DataObjectScheHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_jadwal, parent, false);
        DataObjectScheHolder dataObjectHolder = new DataObjectScheHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectScheHolder holder, final int position) {

        if (mDataset.get(position).getJumlahJadwal().equals("0")){
            holder.Hari.setText(mDataset.get(position).getJadwalHari());
            holder.Jumlah.setText("Tidak Ada Mata Kuliah");
            holder.Waktu.setText("Kosong");
        } else {
            holder.Hari.setText(mDataset.get(position).getJadwalHari());
            holder.Jumlah.setText(mDataset.get(position).getJumlahJadwal() + " Mata Kuliah");
            holder.Waktu.setText(mDataset.get(position).getJadwalMulai() + " - " + mDataset.get(position).getJadwalSelesai());
            holder.CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent pindah = new Intent(context, JadwalHarianActivity.class);
                pindah.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(JadwalActivity.typeUser.equals("loginMhs")){
                        pindah.putExtra("day", mDataset.get(position).getJadwalHari());
                        pindah.putExtra("typeUser", JadwalActivity.typeUser);
                        pindah.putExtra("nim", JadwalActivity.nim);
                        pindah.putExtra("mhs_nama", JadwalActivity.mhs_nama);
                        pindah.putExtra("id_kelas", JadwalActivity.id_kelas);
                        pindah.putExtra("id_semester", JadwalActivity.id_semester);
                        pindah.putExtra("id_akademik", JadwalActivity.id_akademik);
                        pindah.putExtra("position", String.valueOf(position + 1));

                    } else {
                        pindah.putExtra("day", mDataset.get(position).getJadwalHari());
                        pindah.putExtra("typeUser", JadwalActivity.typeUser);
                        pindah.putExtra("nip", JadwalActivity.nip);
                    }

                context.startActivity(pindah);

                }
            });
        }
    }

    public void addItem(DataObjectJadwal dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);

    }

    public void updateItem(DataObjectJadwal dataObj, int index) {
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
