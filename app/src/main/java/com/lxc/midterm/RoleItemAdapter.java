package com.lxc.midterm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lxc.midterm.domain.Person;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 12194 on 2017/11/18.
 */

public class RoleItemAdapter extends RecyclerView.Adapter<RoleItemAdapter.ViewHolder>
                            implements View.OnClickListener,View.OnLongClickListener{

    private List<Person> mItemsList;
    private Context context;

    private onItemClickListener mOnItemClickListener = null;
    private onItemLongClickListener mOnItemLongClickListener = null;

    //定义item点击监听器接口
    public static interface onItemClickListener {
        void onItemClick(View view, int position);
    }
    public static interface onItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(onItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        CircleImageView roleHead;
        TextView roleName;
        TextView roleSex;
        TextView roleAge;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            roleHead = view.findViewById(R.id.role_head);
            roleName = view.findViewById(R.id.role_name);
            roleSex = view.findViewById(R.id.role_gender);
            roleAge = view.findViewById(R.id.role_age);
        }
    }

    //把context也传入，加载头像的时候要用到
    public RoleItemAdapter(List<Person> list, Context context) {
        mItemsList = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bg, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person item = mItemsList.get(position);
        Glide.with(context).load(item.getHead_url()).into(holder.roleHead);
        holder.roleName.setText(item.getName());
        holder.roleSex.setText(item.getSex());
        holder.roleAge.setText(item.getPerson_date());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(v, (int)v.getTag());
        }
        return true;
    }

}
