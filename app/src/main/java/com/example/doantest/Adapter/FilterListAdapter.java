package com.example.doantest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doantest.Interface.ClickItemListener;
import com.example.doantest.Model.FilterModel;
import com.example.doantest.PhotoLabFilterV2;
import com.example.doantest.R;

import java.util.ArrayList;

public class FilterListAdapter extends  RecyclerView.Adapter<FilterListAdapter.DraftImageHolder>{
    private ArrayList<FilterModel> list;
    ClickItemListener clickItemListener;
    Context context;

    public FilterListAdapter(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }

    public void setData(ArrayList<FilterModel>list){
        this.list = list;
        PhotoLabFilterV2 photoLabFilter = new PhotoLabFilterV2();
        for (int i = 0 ; i < list.size(); i++){
            switch (list.get(i).getPos()){
                case 0:
                    list.get(i).setnBitmap(list.get(i).getBitmap());
                    break;
                case 1:
                    list.get(i).setnBitmap(photoLabFilter.getStarLitFilter().processFilter(list.get(i).getBitmap()));
                    break;
                case 2:
                    list.get(i).setnBitmap(photoLabFilter.getBlueMessFilter().processFilter(list.get(i).getBitmap()));
                    break;
                case 3:
                    list.get(i).setnBitmap(photoLabFilter.getAweStruckVibeFilter().processFilter(list.get(i).getBitmap()));
                    break;
                case 4:
                    list.get(i).setnBitmap(photoLabFilter.getLimeStutterFilter().processFilter(list.get(i).getBitmap()));
                    break;
                case 5:
                    list.get(i).setnBitmap(photoLabFilter.getNightWhisperFilter().processFilter(list.get(i).getBitmap()));
                    break;
                case 6:
                    list.get(i).setnBitmap(photoLabFilter.getDarker().processFilter(list.get(i).getBitmap()));
                    break;
                case 7:
                    list.get(i).setnBitmap(photoLabFilter.getIncreaseContrast().processFilter(list.get(i).getBitmap()));
                    break;
                case 8:
                    list.get(i).setnBitmap(photoLabFilter.getBrighten().processFilter(list.get(i).getBitmap()));
                    break;
                case 9:
                    list.get(i).setnBitmap(photoLabFilter.getFade().processFilter(list.get(i).getBitmap()));
                    break;
                case 10:
                    list.get(i).setnBitmap(photoLabFilter.getPTL1().processFilter(list.get(i).getBitmap()));
                    break;
                case 11:
                    list.get(i).setnBitmap(photoLabFilter.getPTL2().processFilter(list.get(i).getBitmap()));
                    break;
                default: list.get(i).setnBitmap(list.get(i).getBitmap());
            }
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DraftImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_horizontal_layout, parent, false);
        context = parent.getContext();
        return new DraftImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DraftImageHolder holder, int position) {
        FilterModel item = list.get(position);
        if(item==null) return;
        holder.tvName.setText(item.getName());

        holder.imgReview.setImageBitmap(item.getnBitmap());
        holder.imgReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickItemListener.ClickItemFilter(item.getnBitmap(), item.getPos());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null) return  list.size();
        return 0;
    }

    public class DraftImageHolder extends RecyclerView.ViewHolder{
        private ImageView imgReview;
        private TextView tvName;
        public DraftImageHolder(@NonNull View itemView) {
            super(itemView);
            imgReview = itemView.findViewById(R.id.imgReview);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
