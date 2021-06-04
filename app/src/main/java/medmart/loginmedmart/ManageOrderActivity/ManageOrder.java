package medmart.loginmedmart.ManageOrderActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryAdapter;
import medmart.loginmedmart.ManageOrderActivity.HelperClasses.PastOrderAdapter;
import medmart.loginmedmart.ManageOrderActivity.HelperClasses.PastOrderCard;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import medmart.loginmedmart.UtilityClasses.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageOrder extends AppCompatActivity {
    RecyclerView pastOrderRV;
    PastOrderAdapter pastOrderAdapter;
    ArrayList<PastOrderCard> pastOrders;
    String shopName, shopAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_manage_order);

        pastOrderRV = findViewById(R.id.rv_past_orders);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        pastOrderRV.setHasFixedSize(true);
        pastOrderRV.setLayoutManager(linearLayoutManager);
        pastOrderAdapter = new PastOrderAdapter(this);
        pastOrderRV.setAdapter(pastOrderAdapter);
        pastOrders = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetPastOrders();
    }

    private void GetPastOrders() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", Utility.GetDataFromCache(this, "email", "no user"));
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<List<PastOrderCard>> getPastOrders = retrofitInterface.GetPastOrders(jwt, params);
        getPastOrders.enqueue(new Callback<List<PastOrderCard>>() {
            @Override
            public void onResponse(Call<List<PastOrderCard>> call, Response<List<PastOrderCard>> response) {
                 pastOrders = new ArrayList<>(response.body());
                 pastOrderAdapter.SetContent(pastOrders);
                 progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<PastOrderCard>> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void GoBack(View view) {
        finish();
    }
}