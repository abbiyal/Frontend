package medmart.loginmedmart.ManageOrderActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import medmart.loginmedmart.ManageOrderActivity.HelperClasses.OrderDetailAdapter;
import medmart.loginmedmart.ManageOrderActivity.HelperClasses.OrderDetailCard;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import medmart.loginmedmart.UtilityClasses.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetail extends AppCompatActivity {
    TextView orderIdTV, shopNameTV, shopAddressTV, phoneNo, dateTimeTV, status, totalValueTV, deliveryAddressTV;
    RecyclerView orderRecyclerView;
    OrderDetailAdapter orderDetailAdapter;
    ArrayList<OrderDetailCard> orderDetailCards;
    String shopName, shopAddress, orderId, deliveryAddress, dateTime, totalValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_order_detail);

        orderIdTV = findViewById(R.id.order_id);
        shopNameTV = findViewById(R.id.shop_name);
        shopAddressTV = findViewById(R.id.shop_address);
        phoneNo = findViewById(R.id.phone_number);
        dateTimeTV = findViewById(R.id.datetime);
        status = findViewById(R.id.status);
        totalValueTV = findViewById(R.id.total_value);
        deliveryAddressTV = findViewById(R.id.delivery_address);
        orderRecyclerView = findViewById(R.id.rv_past_orders);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        orderRecyclerView.setHasFixedSize(false);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        orderDetailAdapter = new OrderDetailAdapter(this);
        orderRecyclerView.setAdapter(orderDetailAdapter);
        orderDetailCards = new ArrayList<>();

        Intent intent = getIntent();
        shopName = intent.getStringExtra("shopName");
        shopAddress = intent.getStringExtra("shopAddress");
        orderId = intent.getStringExtra("orderId");
        deliveryAddress = intent.getStringExtra("deliveryAddress");
        dateTime = intent.getStringExtra("dateTime");
        totalValue = intent.getStringExtra("totalValue");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetUi();
        GetOrderDetail();
    }

    private void SetUi() {
        orderIdTV.setText(orderId);
        shopNameTV.setText(shopName);
        shopAddressTV.setText(shopAddress);
        deliveryAddressTV.setText(deliveryAddress);
        phoneNo.setText(Utility.GetDataFromCache(this, "phone", "9887654143"));
        dateTimeTV.setText(dateTime);
    }

    private void GetOrderDetail() {
        System.out.println("here or not in order detail");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("orderId", orderId);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<List<OrderDetailCard>> orderDetail = retrofitInterface.GetOrderDetail(jwt, params);
        orderDetail.enqueue(new Callback<List<OrderDetailCard>>() {
            @Override
            public void onResponse(Call<List<OrderDetailCard>> call, Response<List<OrderDetailCard>> response) {
                orderDetailCards = new ArrayList<>(response.body());
                orderDetailAdapter.SetContent(orderDetailCards);
                totalValueTV.setText(totalValue);
            }

            @Override
            public void onFailure(Call<List<OrderDetailCard>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getMessage());
            }
        });
    }

    public void GoBack(View view) {
        finish();
    }
}