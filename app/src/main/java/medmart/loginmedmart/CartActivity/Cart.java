package medmart.loginmedmart.CartActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import medmart.loginmedmart.CartActivity.HelperClasses.CartAdapter;
import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.HomeActivity.HomePage;
import medmart.loginmedmart.ManageOrderActivity.ManageOrder;
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
    String orderId;
    String receiptId;
    List<String> productIdList = new ArrayList<>();
    List<String> quantityList = new ArrayList<>();
    List<String> priceList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();

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
                VerifyQuantity();
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

    private void VerifyQuantity() {
        long shopId = CartService.GetInstance().getShopId();
        HashMap<String, CartItem> listOfItems = CartService.GetInstance().getListOfItems();
        List<String> shopIdList = new ArrayList<>();
        List<String> productIdList = new ArrayList<>();
        List<String> quantityList = new ArrayList<>();

        shopIdList.add(String.valueOf(shopId));

        for (Map.Entry mapElement : listOfItems.entrySet()) {
            CartItem cartItem = ((CartItem) mapElement.getValue());

            productIdList.add(cartItem.getProductId());
            quantityList.add(String.valueOf(cartItem.getQuantity()));
        }

        HashMap<String, List<String>> params = new HashMap<>();
        params.put("shopId", shopIdList);
        params.put("productIds", productIdList);
        params.put("quantity", quantityList);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, List<String>>> quantityCheck = retrofitInterface.VerifiyAndDecreaseQuantity(jwt, params);
        quantityCheck.enqueue(new Callback<HashMap<String, List<String>>>() {
            @Override
            public void onResponse(Call<HashMap<String, List<String>>> call, Response<HashMap<String, List<String>>> response) {
                List<String> verifyResponse = response.body().get("response");

                if (verifyResponse.size() == 0) {
                    // todo success
                    GetOrderMetaData(decimalFormat.format(CartService.GetInstance().getTotalValue() * 100));
                } else if (verifyResponse.size() == 1) {
                    // todo multiple failure
                    Toast.makeText(getApplicationContext(), "multiple not in stock", Toast.LENGTH_SHORT).show();
                } else {
                    // todo 1 item fail
                    Toast.makeText(getApplicationContext(), "1 not in stock", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, List<String>>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        /**
         * Add your logic here for a successful payment response
         */
        try {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
            String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("shopid", String.valueOf(CartService.GetInstance().getShopId()));
            RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
            Call<HashMap<String, String>> shopDetailCall = retrofitInterface.findShopDetails(jwt, params);
            shopDetailCall.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    HashMap<String, String> details = response.body();
                    if (details.size() == 1) {
                        Toast.makeText(getApplicationContext(), "Shop Details Error", Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, List<String>> params = new HashMap<>();
                        params.put("shopId", new ArrayList<String>() {{
                            add(String.valueOf(CartService.GetInstance().getShopId()));
                        }});

                        params.put("shopName", new ArrayList<String>() {{
                            add(details.get("shopName"));
                        }});

                        params.put("shopAddress", new ArrayList<String>() {{
                            add(details.get("shopAddress"));
                        }});

                        params.put("receiptId", new ArrayList<String>() {{
                            add(receiptId);
                        }});

                        params.put("orderId", new ArrayList<String>() {{
                            add(orderId);
                        }});

                        String dateTime = "";
                        LocalDate now = LocalDate.now();
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm a");
                        String currentTime = df.format(LocalTime.now());

                        dateTime += now.getDayOfMonth() + " " + now.getMonth().toString().substring(0, 3) + " " + now.getYear();
                        dateTime += " at " + currentTime;

                        String finalDateTime = dateTime;
                        params.put("dateTime", new ArrayList<String>() {{
                            add(finalDateTime);
                        }});

                        params.put("amount", new ArrayList<String>() {{
                            add(decimalFormat.format(CartService.GetInstance().getTotalValue() * 100));
                        }});

                        params.put("deliveryAddress", new ArrayList<String>() {{
                            add(completeAddress.getEditableText().toString() + " " +
                                    Utility.GetDataFromCache(getApplicationContext(), "useraddress", "chandigarh"));
                        }});

                        params.put("userId", new ArrayList<String>() {{
                            add(Utility.GetDataFromCache(getApplicationContext(), "email", "xyz@gmail.com"));
                        }});

                        params.put("productIds", new ArrayList<String>(productIdList));
                        params.put("nameIds", new ArrayList<String>(nameList));
                        params.put("priceIds", new ArrayList<String>(priceList));
                        params.put("quantityIds", new ArrayList<String>(quantityList));
                        System.out.println("god dont do this to us " + productIdList.size());
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
                        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
                        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                        Call<HashMap<String, String>> saveOrder = retrofitInterface.AddOrder(jwt, params);
                        saveOrder.enqueue(new Callback<HashMap<String, String>>() {
                            @Override
                            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                                if (response.body().get("response").contentEquals("success")) {
                                    Toast.makeText(getApplicationContext(), "success for order save", Toast.LENGTH_SHORT).show();
                                    ClearBackendCart();
                                }
                            }

                            @Override
                            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "success for order save", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ClearBackendCart() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", email);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> emptyCart = retrofitInterface.emptyCart(jwt, params);
        emptyCart.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.body().get("response").contentEquals("success")) {
                    Toast.makeText(getApplicationContext(), "cart Empty !!!", Toast.LENGTH_SHORT).show();
                    CartService.GetInstance().ClearCart();
                    new AlertDialog.Builder(Cart.this)
                            .setTitle("Success")
                            .setMessage("Thank you for ordering with us!")
                            .setCancelable(false)
                            .setPositiveButton("Home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                    intent.putExtra("class", "cart");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Order History", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), ManageOrder.class);
                                    startActivity(intent);
                                }
                            })
                            .create().show();
                    SetUi();
                } else
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */
        try {
            new AlertDialog.Builder(Cart.this)
                    .setTitle("Failed")
                    .setMessage("Please try again!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            SharedPreferences sharedPreferences = getApplication().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
                            String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("orderId", receiptId);
                            RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                            Call<HashMap<String, String>> deleteOrder = retrofitInterface.DeleteOrder(jwt, params);
                            deleteOrder.enqueue(new Callback<HashMap<String, String>>() {
                                @Override
                                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                                    Toast.makeText(getApplicationContext(), "order deletd", Toast.LENGTH_SHORT).show();
                                    IncreaseQuantityBackend();
                                }

                                @Override
                                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "order deletion Error", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Log.e("paymeterror", "Exception in onPaymentError", e);
//        }
    }

    private void IncreaseQuantityBackend() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("productIds", productIdList);
        params.put("quantityIds", quantityList);
        params.put("shopId", new ArrayList<String>() {{
            add(String.valueOf(CartService.GetInstance().getShopId()));
        }});

        System.out.println("please god no" + productIdList.size());
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> onFailure = retrofitInterface.IncreaseQuantityOnFailure(jwt, params);
        onFailure.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                Toast.makeText(getApplicationContext(), "success in quantity increase", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "error in quantity increase " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void StartPayment(String orderId, String reciptId) {
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
            options.put("description", "Reference No. #" + reciptId);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderId);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", decimalFormat.format(CartService.GetInstance().getTotalValue() * 100));//pass amount in currency subunits
            options.put("prefill.email", Utility.GetDataFromCache(this, "email", "Please enter"));
            options.put("prefill.contact", Utility.GetDataFromCache(this, "phone", "Please enter"));
            options.put("send_sms_hash", true);
            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("payment error", "Error in starting Razorpay Checkout", e);
        }
    }

    private void GetOrderMetaData(String amount) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("amount", amount);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> getOrderIdCall = retrofitInterface.generateOrderId(jwt, params);
        getOrderIdCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                HashMap<String, String> responseMetaData = response.body();
                orderId = responseMetaData.get("orderid");
                receiptId = responseMetaData.get("receiptid");
                StartPayment(orderId, receiptId);
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
                if (details.size() == 1) {
                    Toast.makeText(getApplicationContext(), "Shop Details Error", Toast.LENGTH_SHORT).show();
                } else {
                    shopName[0] = details.get("shopName");
                    shopAddress[0] = details.get("shopAddress");
                    Intent intent = new Intent(getApplicationContext(), ShopInventory.class);
                    intent.putExtra("shopid", CartService.GetInstance().getShopId());
                    intent.putExtra("shopname", shopName[0]);
                    intent.putExtra("shopaddress", shopAddress[0]);
                    intent.putExtra("productname", "null");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
    }


    public void SetUi() {
        if (completeAddress.getEditableText().toString().length() > 0) {
            addAddress.setVisibility(View.INVISIBLE);
            proceedCheckout.setVisibility(View.VISIBLE);
        } else {
            addAddress.setVisibility(View.VISIBLE);
            proceedCheckout.setVisibility(View.INVISIBLE);
        }

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
                    long shopId = -9;

                    if (!shopIdString.contentEquals("null"))
                        shopId = Long.parseLong(shopIdString);

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
        productIdList.clear();
        quantityList.clear();
        priceList.clear();
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

                    for (int i = 0; i < productList.size(); i++) {
                        productIdList.add(productList.get(i).getProductId());
                        nameList.add(productList.get(i).getProductName());
                        CartItem cartItem = CartService.GetInstance().getListOfItems().get(productList.get(i).getProductId());
                        quantityList.add(String.valueOf(cartItem.getQuantity()));
                        priceList.add(String.valueOf(cartItem.getPrice()));
                    }

                    System.out.println("please god no here as well " + productIdList.size());
                }
            }

            @Override
            public void onFailure(Call<List<ProductCatalogue>> call, Throwable t) {

            }
        });

    }
}