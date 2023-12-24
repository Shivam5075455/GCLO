package com.example.gclo.Adapters.PersonDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Models.PersondetailModel;
import com.example.gclo.R;

import java.util.ArrayList;
import java.util.List;

public class AllPersonDetailsAdapter extends RecyclerView.Adapter<AllPersonDetailsAdapter.viewHolder> {

    Context context;
    List<PersondetailModel> persondetailModels;


    public AllPersonDetailsAdapter(Context context, List<PersondetailModel> persondetailModels){

        this.context = context;
        this.persondetailModels = persondetailModels;
//        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public AllPersonDetailsAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.person_detail_layout,parent,false);


        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllPersonDetailsAdapter.viewHolder holder, int position) {

        PersondetailModel personDetails = this.persondetailModels.get(position);

        holder.tvpersonName.setText(personDetails.getName());
        holder.tvPDPersonId.setText(personDetails.getId());
        holder.tvpersonLong.setText(personDetails.getLongitude());
        holder.tvpersonLat.setText(personDetails.getLatitude());
        holder.tvPDPersonId.setText(personDetails.getId());
        holder.tvpersonName.setText(personDetails.getName());
        holder.tvPDPersonUsername.setText(personDetails.getUsername());
        holder.tvPDPersonEmail.setText(personDetails.getEmail());
        holder.tvPDPersonGender.setText(personDetails.getGender());
        holder.tvpersonLat.setText(personDetails.getLatitude());
        holder.tvpersonLong.setText(personDetails.getLongitude());
        holder.tvPDPost.setText(personDetails.getPost());

        holder.tvpersonIn.setText(personDetails.getZoneIn());
//        holder.tvpersonOut.setText(personDetails.getZoneOut());
        holder.tvpersonDistance.setText(personDetails.getDistance());
    }

    @Override
    public int getItemCount() {
        return persondetailModels.size();
    }

    public void updateData(List<PersondetailModel> persondetailModelList) {
        persondetailModelList.clear();
        persondetailModelList.addAll(persondetailModelList);
    }

    public class viewHolder extends RecyclerView.ViewHolder {
//
//    TextView tvpersonName, tvpersonId, tvpersonLat, tvpersonLong,tvpersonDistance,tvpersonIn,tvpersonOut;
//        public viewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvpersonName= itemView.findViewById(R.id.personName);
//            tvpersonId= itemView.findViewById(R.id.personId);
//            tvpersonLat= itemView.findViewById(R.id.personLat);
//            tvpersonLong= itemView.findViewById(R.id.personLong);
//            tvpersonDistance= itemView.findViewById(R.id.personDistance);
//            tvpersonIn= itemView.findViewById(R.id.personIn);
//            tvpersonOut= itemView.findViewById(R.id.personOut);
//
//        }


        TextView tvpersonName,tvPDPersonGender,tvPDPersonId, tvPDPersonUsername, tvpersonLat, tvpersonLong, tvpersonDistance,
                tvpersonIn, tvpersonOut, tvPDPersonEmail,tvPDPost;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            tvpersonName = itemView.findViewById(R.id.tvPDPersonName);
            tvPDPersonUsername = itemView.findViewById(R.id.tvPDPersonUsername);
            tvpersonLat = itemView.findViewById(R.id.tvPDPersonLat);
            tvpersonLong = itemView.findViewById(R.id.tvPDPersonLong);
            tvpersonDistance = itemView.findViewById(R.id.tvPDPersonDistance);
            tvpersonIn = itemView.findViewById(R.id.tvPDPersonIn);
//            tvpersonOut = itemView.findViewById(R.id.tvPDPersonOut);
            tvPDPersonEmail = itemView.findViewById(R.id.tvPDPersonEmail);
            tvPDPersonGender = itemView.findViewById(R.id.tvPDPersonGender);
            tvPDPersonId = itemView.findViewById(R.id.tvPDPersonId);
            tvPDPost = itemView.findViewById(R.id.tvPDPost);

        }

    }
}
