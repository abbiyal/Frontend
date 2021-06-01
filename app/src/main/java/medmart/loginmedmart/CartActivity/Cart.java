package medmart.loginmedmart.CartActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
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

public class Cart extends AppCompatActivity implements PaymentResultListener {
    TextView itemCount, currentLocation, cartValue, amountText;
    EditText completeAddress;
    Button addAddress, proceedCheckout;
    RecyclerView cartRecyclerView;
    CartAdapter cartAdapter;
    LinearLayout emptyCartLayout;
    ConstraintLayout constraintLayout;
    NestedScrollView nestedScrollView;
    DecimalFormat decimalFormat = new DecimalFormat("##");

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
                GetOrderId(decimalFormat.format(CartService.GetInstance().getTotalValue() * 100));
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

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        /**
         * Add your logic here for a successful payment response
         */
    }

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("paymeterror", "Exception in onPaymentError", e);
        }
    }

    public void StartPayment(String orderId, Double amount) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_eutwk0rpzPmlAl");

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.app_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "MedMart");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderId);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", decimalFormat.format(amount * 100));//pass amount in currency subunits
            options.put("prefill.email", );
            options.put("prefill.contact", "7009964216");
            options.put("send_sms_hash", true);
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 3);
//            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("payment error", "Error in starting Razorpay Checkout", e);
        }
    }

    private void GetOrderId(String amount) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("amount", amount);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> getOrderIdCall = retrofitInterface.generateOrderId(jwt, params);
        final String[] orderID = {""};
        getOrderIdCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                HashMap<String, String> orderId = response.body();
                orderID[0] = orderId.get("orderid");
                StartPayment(orderID[0], CartService.GetInstance().getTotalValue());
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (completeAddress.getEditableText().toString().length() > 0) {
            addAddress.setVisibility(View.INVISIBLE);
            proceedCheckout.setVisibility(View.VISIBLE);
        } else {
            addAddress.setVisibility(View.VISIBLE);
            proceedCheckout.setVisibility(View.INVISIBLE);
        }

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
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        final String[] shopName = {""};
        final String[] shopAddress = {""};
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("shopid", String.valueOf(CartService.GetInstance().getShopId()));
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> shopDetailCall = retrofitInterface.findShopDetails(jwt, params);
        shopDetailCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                HashMap<String, String> details = response.body();
                if (details.get("response").contentEquals("Error")) {
                    Toast.makeText(getApplicationContext(), "Shop Details Error", Toast.LENGTH_SHORT);
                } else {
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
}