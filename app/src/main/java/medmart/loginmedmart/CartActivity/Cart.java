package medmart.loginmedmart.CartActivity;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import medmart.loginmedmart.CartActivity.HelperClasses.CartAdapter;
import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.R;
import medmart.loginmedmart.ShopInventoryActivity.ShopInventory;
import medmart.loginmedmart.UtilityClasses.ProductCatalogue;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import medmart.loginmedmart.UtilityClasses.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    TextView itemCount, currentLocation;
    RecyclerView cartRecyclerView;
    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_cart);

        itemCount = findViewById(R.id.item_count);
        currentLocation = findViewById(R.id.currentlocation);
        cartRecyclerView = findViewById(R.id.cartitem_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
        cartRecyclerView.setHasFixedSize(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductsForCart();
    }

    public void GoBack(View view) {
        finish();
    }

    public void OpenShop(View view) {
        // todo backend to get shop name and address using id from cart
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        final String[] shopName = {""};
        final String[] shopAddress = {""};
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("shopid",String.valueOf(CartService.GetInstance().getShopId()));
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String,String>> shopDetailCall = retrofitInterface.findShopDetails(jwt,params);
        shopDetailCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                HashMap<String,String> details = response.body();
                if(details.get("response").contentEquals("Error")){
                    Toast.makeText(getApplicationContext(),"Shop Details Error",Toast.LENGTH_SHORT);
                }
                else{
                    shopName[0] = details.get("shopName");
                    shopAddress[0] = details.get("shopAddress");
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
        Intent intent = new Intent(getApplicationContext(), ShopInventory.class);
        intent.putExtra("shopid", CartService.GetInstance().getShopId());
        intent.putExtra("shopname", shopName[0]);
        intent.putExtra("shopaddress", shopAddress[0]);
        intent.putExtra("productname", "null");
        startActivity(intent);
    }

    private void SetUi() {
        currentLocation.setText(Utility.GetDataFromCache(this, "useraddress", "Chandigarh"));
    }

    public void getProductsForCart() {
        List<String> productsIds = new ArrayList<String> (CartService.GetInstance().getListOfItems().keySet());
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<List<ProductCatalogue>> productsCall = retrofitInterface.findPrdocutsById(jwt, productsIds);
        productsCall.enqueue(new Callback<List<ProductCatalogue>>() {
            @Override
            public void onResponse(Call<List<ProductCatalogue>> call, Response<List<ProductCatalogue>> response) {
                List<ProductCatalogue> productList = response.body();

                if (productList.size() != 0) {

                    if (cartAdapter == null) {
                        cartAdapter = new CartAdapter(Cart.this);
                        cartRecyclerView.setAdapter(cartAdapter);
                    }

                    cartAdapter.SetContent(new ArrayList<ProductCatalogue>(productList));
                }
            }

            @Override
            public void onFailure(Call<List<ProductCatalogue>> call, Throwable t) {

            }
        });

    }
    public void IntitatePayment(){
        Checkout checkout = new Checkout();
        checkout.setKeyID("<YOUR_KEY_ID>");
    }
}