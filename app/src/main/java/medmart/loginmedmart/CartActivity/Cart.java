package medmart.loginmedmart.CartActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import medmart.loginmedmart.CartActivity.HelperClasses.CartAdapter;
import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.HomeActivity.HomePage;
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
    TextView itemCount, currentLocation, cartValue, amountText;
    EditText completeAddress;
    Button addAddress, proceedCheckout;
    RecyclerView cartRecyclerView;
    CartAdapter cartAdapter;
    LinearLayout emptyCartLayout;
    ConstraintLayout constraintLayout;
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_cart);
        Checkout.preload(getApplicationContext());
        itemCount = findViewById(R.id.item_count);
        amountText = findViewById(R.id.textView);
        cartValue = findViewById(R.id.cart_value);
        completeAddress = findViewById(R.id.complete_address);
        addAddress = findViewById(R.id.add_address);
        proceedCheckout = findViewById(R.id.proceed_checkout);
        emptyCartLayout = findViewById(R.id.empty_cart_layout);
        nestedScrollView = findViewById(R.id.cart_scrollview);
        constraintLayout = findViewById(R.id.mainlayout);

        proceedCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetOrderId();
                StartPayment();
            }
        });

        completeAddress.setImeActionLabel("Go", EditorInfo.IME_ACTION_NEXT);
        completeAddress.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (completeAddress.getEditableText().toString().length() > 0) {
                        addAddress.setVisibility(View.INVISIBLE);
                        proceedCheckout.setVisibility(View.VISIBLE);
                    } else {
                        addAddress.setVisibility(View.VISIBLE);
                        proceedCheckout.setVisibility(View.INVISIBLE);
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(addAddress.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        currentLocation = findViewById(R.id.currentlocation);
        cartRecyclerView = findViewById(R.id.cartitem_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(linearLayoutManager);
        cartRecyclerView.setHasFixedSize(false);

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeAddress.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(completeAddress, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void GetOrderId() {
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetCartData();
    }

    public void GoBack(View view) {
        finish();
    }

    public void ShopNow(View view) {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("class", "cart");
        startActivity(intent);
        finish();
    }

    public void OpenShop(View view) {
        // todo backend to get shop name and address using id from cart
        Intent intent = new Intent(getApplicationContext(), ShopInventory.class);
        intent.putExtra("shopid", CartService.GetInstance().getShopId());
        intent.putExtra("shopname", "");
        intent.putExtra("shopaddress", "");
        intent.putExtra("productname", "null");
        startActivity(intent);
    }


    public void SetUi() {
        if (CartService.GetInstance().getTotalItems() == 0) {
            itemCount.setText("Empty Cart");
            emptyCartLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
            addAddress.setVisibility(View.GONE);
            proceedCheckout.setVisibility(View.GONE);
            amountText.setVisibility(View.GONE);
            cartValue.setVisibility(View.GONE);
            constraintLayout.setBackgroundColor(getColor(R.color.white));
        } else {
            itemCount.setText(String.valueOf(CartService.GetInstance().getListOfItems().size()) + " Items in Cart");
            emptyCartLayout.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            addAddress.setVisibility(View.VISIBLE);
            amountText.setVisibility(View.VISIBLE);
            cartValue.setVisibility(View.VISIBLE);
            constraintLayout.setBackgroundColor(getColor(R.color.lightWhite));
            cartValue.setText(String.valueOf(CartService.GetInstance().getTotalValue()));
        }
    }

    private void GetCartData() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("customerId", email);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, Object>> cartCall = retrofitInterface.getUserCart(jwt, params);
        cartCall.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                try {
                    HashMap<String, Object> cart = response.body();
                    String cartId = (String) cart.get("cartId");
                    String shopIdString = (String) cart.get("shopId");
                    long shopId = Long.parseLong(shopIdString);
                    String totalValueString = (String) cart.get("totalItems");
                    int totalItems = Integer.parseInt(totalValueString);
                    Double totalValue = (Double) cart.get("totalValue");
                    ArrayList<CartItem> items = (ArrayList<CartItem>) cart.get("items");
                    CartService.GetInstance().setCartId(cartId);
                    CartService.GetInstance().setShopId(shopId);
                    CartService.GetInstance().setTotalValue(totalValue);
                    CartService.GetInstance().setTotalItems(totalItems);
                    HashMap<String, CartItem> listofItems = new HashMap<String, CartItem>();

                    for (int i = 0; i < items.size(); i++) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Map<String, String>>() {
                        }.getType();
                        Map<String, String> productMap = gson.fromJson(gson.toJson(items.get(i)), type);
                        String productId = productMap.get("productId");
                        Double price = Double.parseDouble(productMap.get("price"));
                        int quantity = (int) (Double.parseDouble(productMap.get("quantity")));
                        CartItem cartItem = new CartItem(quantity, price, productId);
                        listofItems.put(productId, cartItem);
                    }

                    CartService.GetInstance().setListOfItems(listofItems);
                    CartService.GetInstance().setCartLoaded(true);
                    currentLocation.setText(Utility.GetDataFromCache(getApplicationContext(), "useraddress", "Chandigarh"));
                    SetUi();
                    getProductsForCart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Conenction Error !! ", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getProductsForCart() {
        List<String> productsIds = new ArrayList<String>(CartService.GetInstance().getListOfItems().keySet());
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

    public void IntitatePayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("<YOUR_KEY_ID>");
    }
}