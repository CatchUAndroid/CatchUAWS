package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.ExplorePeople.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.uren.catchu.GeneralUtils.DataModelUtil.UserDataUtil;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.Interfaces.ReturnCallback;
import com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments.Models.Contact;
import com.uren.catchu.R;

import java.util.ArrayList;
import java.util.List;

public class InviteContactsAdapter extends RecyclerView.Adapter<InviteContactsAdapter.MyViewHolder> implements Filterable {

    View view;
    LayoutInflater layoutInflater;
    Context context;
    Activity activity;
    List<Contact> contactList = new ArrayList<>();
    List<Contact> orgContactList = new ArrayList<>();
    ItemClickListener itemClickListener;
    ReturnCallback returnCallback;

    public InviteContactsAdapter(Context context, List<Contact> contactList, ItemClickListener itemClickListener) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.contactList.addAll(contactList);
        this.orgContactList.addAll(contactList);
        activity = (Activity) context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public InviteContactsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = layoutInflater.inflate(R.layout.person_vert_list_item, viewGroup, false);
        final InviteContactsAdapter.MyViewHolder holder = new InviteContactsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InviteContactsAdapter.MyViewHolder myViewHolder, int position) {
        Contact contact = contactList.get(position);
        myViewHolder.setData(contact, position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView usernameTextView;
        TextView shortenTextView;
        TextView phoneNumTextView;
        ImageView profilePicImgView;
        CardView personRootCardView;
        Contact contact;
        Button statuDisplayBtn;

        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            profilePicImgView = view.findViewById(R.id.profilePicImgView);
            usernameTextView = view.findViewById(R.id.usernameTextView);
            nameTextView = view.findViewById(R.id.nameTextView);
            phoneNumTextView = view.findViewById(R.id.phoneNumTextView);
            statuDisplayBtn = view.findViewById(R.id.statuDisplayBtn);
            shortenTextView = view.findViewById(R.id.shortenTextView);
            personRootCardView = view.findViewById(R.id.personRootCardView);
            usernameTextView.setVisibility(View.GONE);
            phoneNumTextView.setVisibility(View.VISIBLE);

            statuDisplayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statuDisplayBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_click));
                    itemClickListener.onClick(contact, position);
                }
            });
        }

        public void setData(Contact contact, int position) {
            this.position = position;
            this.contact = contact;
            setPhoneNum();
            UserDataUtil.setName(contact.getName(), nameTextView);
            UserDataUtil.setProfilePicture(context, null, null, shortenTextView, profilePicImgView);
            UserDataUtil.updateInviteButton(context, statuDisplayBtn, false);
        }

        public void setPhoneNum() {
            if (contact != null && contact.getPhoneNumber() != null && !contact.getPhoneNumber().isEmpty())
                this.phoneNumTextView.setText(contact.getPhoneNumber());
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                contactList.clear();

                if (searchString.trim().isEmpty())
                    contactList.addAll(orgContactList);
                else {
                    List<Contact> tempContactList = new ArrayList<>();

                    for (Contact contact : orgContactList) {
                        if (contact.getName().toLowerCase().contains(searchString.toLowerCase()))
                            tempContactList.add(contact);
                        else if (contact.getPhoneNumber().contains(searchString))
                            tempContactList.add(contact);
                    }

                    contactList.addAll(tempContactList);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactList = (List<Contact>) filterResults.values;
                notifyDataSetChanged();

                if(contactList != null && contactList.size() > 0)
                    returnCallback.onReturn(contactList.size());
                else
                    returnCallback.onReturn(0);
            }
        };
    }

    public void updateAdapter(String searchText, ReturnCallback returnCallback) {
        this.returnCallback = returnCallback;
        getFilter().filter(searchText);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}