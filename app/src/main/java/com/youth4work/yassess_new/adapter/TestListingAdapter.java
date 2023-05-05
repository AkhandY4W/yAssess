package com.youth4work.yassess_new.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.youth4work.yassess_new.R;
import com.youth4work.yassess_new.network.model.TestModel;
import com.youth4work.yassess_new.ui.AllWebView;
import com.youth4work.yassess_new.ui.PracticeTestActivity;
import com.youth4work.yassess_new.utils.PrepDialogsUtils2Kt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TestListingAdapter extends RecyclerView.Adapter<TestListingAdapter.ViewHolder> {

    List<TestModel.YAssessment> testModelList;
    Context mContxt;
    public TestListingAdapter(List<TestModel.YAssessment> mTestModelList, Context mContxt) {
        setHasStableIds(true);
        testModelList = mTestModelList;
        this.mContxt = mContxt;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_test_listing_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestListingAdapter.ViewHolder holder, int position) {

        holder.txtTestName.setText(testModelList.get(position).getTestName());
        holder.txtTestCreater.setText(testModelList.get(position).getCompanyName());
        Picasso.get().load(testModelList.get(position).getLogoUrl()).into(holder.imgTest);
        Date datefrom = null;
        Date dateTo = null;
        Date todayDate = getTOdayDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            dateTo = formatter.parse(testModelList.get(position).getValidityto());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            datefrom = formatter.parse(testModelList.get(position).getValidityfrom());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (testModelList.get(position).getCanGiveTestFlag() == 1 && datefrom.compareTo(todayDate) <= 0 && dateTo.compareTo(todayDate) >= 0) {
            holder.txtTestStatus.setText("Live");
            holder.txtTestStatus.setTextColor(Color.parseColor("#087ccd"));
        } else if (testModelList.get(position).getCanGiveTestFlag() == 3 || testModelList.get(position).getCanGiveTestFlag() == 2) {
            holder.txtTestStatus.setText("Archive");
        } else if (testModelList.get(position).getCanGiveTestFlag() == 1 && datefrom.compareTo(todayDate) > 0) {
            holder.txtTestStatus.setText("Upcoming");
            holder.txtTestStatus.setTextColor(Color.parseColor("#cd9908"));
            holder.btnTakeTest.setVisibility(View.GONE);
            holder.txtTestStartDate.setVisibility(View.VISIBLE);
            //Date date = Calendar.getInstance().getTime();
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter2.format(datefrom);
            holder.txtTestStartDate.setText(dateString);


        }
        if (testModelList.get(position).getCanGiveTestFlag() == 1) {
            holder.btnTakeTest.setText("Take Test");
        } else if (testModelList.get(position).getCanGiveTestFlag() == 2) {
            if (testModelList.get(position).getCanviewFlag() == 2) {
                holder.btnTakeTest.setText("View Result");
            } else {
                holder.btnTakeTest.setText("Attempted");
            }
        } else if (testModelList.get(position).getCanGiveTestFlag() == 3) {

            holder.btnTakeTest.setVisibility(View.GONE);
            holder.txtTestStartDate.setVisibility(View.VISIBLE);
            holder.txtTestStartDate.setText("Not Attempted");
        } else {
            holder.btnTakeTest.setVisibility(View.GONE);
        }
        holder.txtTestStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(holder.txtTestStartDate.getText().equals("Not Attempted")) {
                    PrepDialogsUtils2Kt.showOkMsg(mContxt,"Sorry,your invitation is expired !");
                }
            }
        });
        holder.btnTakeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnTakeTest.getText().equals("Take Test")) {
                    PracticeTestActivity.show(testModelList.get(position).getTestid(), mContxt,true);

                } else if(holder.btnTakeTest.getText().equals("View Result")){
                    AllWebView.LoadWebView(mContxt, "https://www.youth4work.com/yAssess/result?id=" + testModelList.get(position).getEntestid(), "Result",testModelList.get(position).getTestid());
                }
                else if(holder.btnTakeTest.getText().equals("Attempted")) {
                    PrepDialogsUtils2Kt.showOkMsg(mContxt,"Sorry,you have already attempted this test !");
                }

                               /* else {
                    AllWebView.LoadWebView(mContxt, "https://www.yassess.youth4work.com/yAssess/SubjectiveTest/12831", "Result",testModelList.get(position).getTestid());
                }*/
            }
        });
    }

    private Date getTOdayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String todayString = formatter.format(todayDate);
        try {
            todayDate = formatter.parse(todayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return todayDate;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return testModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgTest;
        TextView txtTestName, txtTestStatus, txtTestCreater, txtTestStartDate;
        Button btnTakeTest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTest = itemView.findViewById(R.id.img_test);
            txtTestName = itemView.findViewById(R.id.txt_test_name);
            txtTestStatus = itemView.findViewById(R.id.txt_test_status);
            txtTestCreater = itemView.findViewById(R.id.txt_test_creater);
            btnTakeTest = itemView.findViewById(R.id.btn_take_test);
            txtTestStartDate = itemView.findViewById(R.id.txt_test_start_date);

        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
