package com.smart.sparc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class RoleAdapter extends ArrayAdapter<Role> {

    private Context context;
    private List<Role> roleList;

    public RoleAdapter(Context context, List<Role> roleList) {
        super(context, 0, roleList);
        this.context = context;
        this.roleList = roleList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_role, parent, false);
        }

        Role role = roleList.get(position);

        TextView roleIdTextView = convertView.findViewById(R.id.roleIdTextView);
        TextView roleNameTextView = convertView.findViewById(R.id.roleNameTextView);

        roleIdTextView.setText(String.valueOf(role.getRoleId()));
        roleNameTextView.setText(role.getRoleName());

        return convertView;
    }
}
