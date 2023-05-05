package com.youth4work.yassess_new.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.network.model.response.SampleQuestionModel;

import java.util.List;

public class PractiseTestAdapter extends RecyclerView.Adapter<PractiseTestAdapter.ViewHolder> {

    SampleQuestionModel sampleQuestionModel;
    Context context;
    private String optionsPrefix2[] = {"A. ", "B. ", "C. ", "D. ", "E. ", "F. "};
    private static PractiseTestAdapter.OnItemClickListener listener;
    int row_index=-1;
    String colorString;
    public PractiseTestAdapter(SampleQuestionModel sampleQuestion, Context mContext) {
        sampleQuestionModel = sampleQuestion;
        this.context = mContext;
    }
    public void setOnItemClickListener(PractiseTestAdapter.OnItemClickListener listener) {
        PractiseTestAdapter.listener = listener;
    }
    @NonNull
    @Override
    public PractiseTestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new PractiseTestAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_option, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PractiseTestAdapter.ViewHolder holder, int position) {
        List<SampleQuestionModel.sampleOptions> options=sampleQuestionModel.getSampleOptions();
        if (options.get(position).getOption().isEmpty()) {
            holder.txtOption.setVisibility(View.GONE);
        } else {
          holder.txtOption.setText(Html.fromHtml("<strong>" + optionsPrefix2[position] + "</strong>     " + options.get(position ).getOption()));
           holder.txtOption.setTextColor(Color.parseColor("#000000"));
        }
        if (!options.get(position).getOptionImgUrl().isEmpty()) {
            holder.imgAnsImage.setVisibility(View.VISIBLE);
            Picasso.get().load(options.get(position).getOptionImgUrl()).into(holder.imgAnsImage);
        }

       /*holder.lytOptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sampleQuestionModel.getCorrect().equals(options.get(position).getOption())) {
                    row_index = position;
                    colorString="#64D739" ;
                    notifyDataSetChanged();
                }
                else {
                    row_index = position;
                    colorString="#f65454" ;
                    notifyDataSetChanged();
                }

            }
        });*/
        if(row_index==position){
            holder.lytOptionContainer.setBackgroundColor(Color.parseColor(colorString));
            holder.txtOption.setTextColor(Color.parseColor("#ffffff"));
        }
        else
        {
            holder.lytOptionContainer.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.txtOption.setTextColor(Color.parseColor("#000000"));
        }
    }

    public void updateList(int row_index,String colorString) {
        this.row_index=row_index;
        this.colorString=colorString;
        notifyDataSetChanged();

    }
    @Override
    public int getItemCount() {
        return sampleQuestionModel.getSampleOptions().size();
    }
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);


    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOption;
        ImageView imgAnsImage;
        LinearLayout lytOptionContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lytOptionContainer = itemView.findViewById(R.id.option_container);
            txtOption = itemView.findViewById(R.id.txt_option);
            imgAnsImage = itemView.findViewById(R.id.ansImage);
            itemView.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(itemView, getLayoutPosition());

            });
       }
    }

}
