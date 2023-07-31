package com.smart.sparc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.android.volley.Response;

public class UserDataActivity extends AppCompatActivity {

    private List<Role> roleList;
    private ListView listView;
    private RoleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        listView = findViewById(R.id.listView);
        roleList = new ArrayList<>();
        adapter = new RoleAdapter(this, roleList);
        listView.setAdapter(adapter);

        fetchRolesFromApi();
    }

        private void fetchRolesFromApi () {
            String url = "http://164.164.122.69:8081/AndroidTest/api/auth/getAllRole";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parseJsonResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UserDataActivity.this, "Error fetching data from API", Toast.LENGTH_SHORT).show();
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }

        private void parseJsonResponse (JSONObject response){
            try {
                JSONArray data = response.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject roleObject = data.getJSONObject(i);

                    int roleId = roleObject.getInt("roleId");
                    String roleName = roleObject.getString("roleName");

                    Role role = new Role();
                    role.setRoleId(roleId);
                    role.setRoleName(roleName);

                    roleList.add(role);
                }

                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}