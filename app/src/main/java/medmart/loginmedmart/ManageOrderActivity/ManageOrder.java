package medmart.loginmedmart.ManageOrderActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import medmart.loginmedmart.HomeActivity.HelperClasses.CategoryAdapter;
import medmart.loginmedmart.ManageOrderActivity.HelperClasses.PastOrderAdapter;
import medmart.loginmedmart.ManageOrderActivity.HelperClasses.PastOrderCard;
import medmart.loginmedmart.R;

public class ManageOrder extends AppCompatActivity {
    RecyclerView pastOrderRV;
    PastOrderAdapter pastOrderAdapter;
    ArrayList<PastOrderCard> pastOrders;

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
        GenerateRandomData();
        pastOrderAdapter.SetContent(pastOrders);
    }

    private void GenerateRandomData() {
        for (int i = 0; i < 10; i++) {
            pastOrders.add(new PastOrderCard("100", 23.7, "Biyal Pharma",
                    "10 Mar 2021 at 11:30 pm", "Pending"));
        }
    }

    public void GoBack(View view) {
        finish();
    }
}