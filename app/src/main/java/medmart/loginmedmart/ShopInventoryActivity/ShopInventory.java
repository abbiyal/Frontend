package medmart.loginmedmart.ShopInventoryActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import medmart.loginmedmart.CartManagement.Cart;
import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CommonAdapter.SearchCard;
import medmart.loginmedmart.R;
import medmart.loginmedmart.ShopInventoryActivity.HelperClasses.InventoryAdapter;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopInventory extends AppCompatActivity {

    private enum Category {
        ALL(0),
        GEL(1),
        TABLET(2),
        SPRAY(3),
        SYRUP(4),
        POWDER(5);

        private final int value;

        Category(final int newValue) {
            value = newValue;
        }

        private static Map map = new HashMap<>();

        public int getValue() {
            return value;
        }

        static {
            for (Category category : Category.values()) {
                map.put(category.value, category);
            }
        }

        public static String valueOf(int pageType) {
            return map.get(pageType).toString();
        }
    }

    ;

    private Button[] categoryButtons = new Button[6];
    private Button categoryOnFocus;
    private int categoryOnFocusIndex;
    private int[] categoryButtonId = {R.id.all, R.id.gel, R.id.tablet, R.id.spray, R.id.syrup, R.id.powder};
    private Long SHOP_ID;
    private TextView shopNameTV, shopAddressTV, cartItemCount, cartValue;
    private  String shopName, shopAddress;
    Button viewCartButton;
    LinearLayout viewCart;

    ArrayList<SearchCard> searchInventory;
    ArrayList<ArrayList<SearchCard>> categoryInventory = new ArrayList<ArrayList<SearchCard>>(6);

    RecyclerView inventoryRecycler;
    InventoryAdapter inventoryAdapter;

    EditText search;
    ImageView searchIcon, clearSearchIcon;

    public ShopInventory() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_shop_inventory);
        search = findViewById(R.id.search_text);
        searchIcon = findViewById(R.id.search_icon);
        clearSearchIcon = findViewById(R.id.clear_icon);

        searchInventory = new ArrayList<>();
        SetOnEditorAction();

        Intent intent = getIntent();
        SHOP_ID = intent.getLongExtra("shopid", 100);
        shopNameTV = findViewById(R.id.shop_name);
        shopAddressTV = findViewById(R.id.shop_address);
        shopName = intent.getStringExtra("shopname");
        shopAddress = intent.getStringExtra("shopaddress");

        SetShopUi();

        inventoryRecycler = findViewById(R.id.inventoryrv);
        viewCart = findViewById(R.id.view_cart_layout);
        cartItemCount = findViewById(R.id.item_count);
        cartValue = findViewById(R.id.cart_value);
        viewCartButton = findViewById(R.id.view_cart);
        // todo set viewCartButton click listener
        CheckCartUi();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        inventoryRecycler.setHasFixedSize(true);
        inventoryRecycler.setLayoutManager(linearLayoutManager);

        for (int i = 0; i < categoryButtons.length; i++) {
            categoryButtons[i] = (Button) findViewById(categoryButtonId[i]);
            categoryInventory.add(new ArrayList<SearchCard>());
            categoryButtons[i].setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    HandleCategoryClick(v);
                }
            });
        }

        categoryOnFocus = categoryButtons[0];
        categoryButtons[0].setBackgroundColor(getColor(R.color.black));
        categoryButtons[0].setTextColor(getColor(R.color.white));
        categoryOnFocus = categoryButtons[0];
        categoryOnFocusIndex = 0;
        HandleCategoryClick(findViewById(R.id.all));
    }

    private void SetOnEditorAction() {
        search.setImeActionLabel("Go", EditorInfo.IME_ACTION_NEXT);
        search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    CallSearch(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void ClearSearch(View view) {
        searchIcon.setVisibility(View.VISIBLE);
        clearSearchIcon.setVisibility(View.GONE);
        inventoryAdapter.SetContent(categoryInventory.get(categoryOnFocusIndex));
        search.getText().clear();
    }

    public void CallSearch(View v) {
        String query = search.getText().toString();
        String category = Category.valueOf(categoryOnFocusIndex);
        searchIcon.setVisibility(View.GONE);
        clearSearchIcon.setVisibility(View.VISIBLE);
        // todo call search nd use serachInventory for filling nd call setcontent
    }

    public void CheckCartUi() {
        if (Cart.GetInstance().getTotalItems() > 0) {
            cartItemCount.setText(Cart.GetInstance().getTotalItems() + " items in cart");
            cartValue.setText("Rs. " + Cart.GetInstance().getTotalValue());
            ViewGroup.LayoutParams layoutParams= inventoryRecycler.getLayoutParams();
            layoutParams.height = (int) (350 * getResources().getDisplayMetrics().density);
            inventoryRecycler.setLayoutParams(layoutParams);
            viewCart.setVisibility(View.VISIBLE);
        } else {
            ViewGroup.LayoutParams layoutParams= inventoryRecycler.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            inventoryRecycler.setLayoutParams(layoutParams);
            viewCart.setVisibility(View.GONE);
        }
    }

    private void SetShopUi() {
        shopNameTV.setText(shopName);
        shopAddressTV.setText(shopAddress);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void HandleCategoryClick(View view) {
        switch (view.getId()) {
            case R.id.all:
                setFocus(categoryOnFocus, categoryButtons[Category.ALL.getValue()]);
                PopulateRecyclerView(Category.ALL.getValue());
                break;

            case R.id.gel:
                setFocus(categoryOnFocus, categoryButtons[Category.GEL.getValue()]);
                PopulateRecyclerView(Category.GEL.getValue());
                break;

            case R.id.tablet:
                setFocus(categoryOnFocus, categoryButtons[Category.TABLET.getValue()]);
                PopulateRecyclerView(Category.TABLET.getValue());
                break;

            case R.id.spray:
                setFocus(categoryOnFocus, categoryButtons[Category.SPRAY.getValue()]);
                PopulateRecyclerView(Category.SPRAY.getValue());

                break;

            case R.id.syrup:
                setFocus(categoryOnFocus, categoryButtons[Category.SYRUP.getValue()]);
                PopulateRecyclerView(Category.SYRUP.getValue());
                break;

            case R.id.powder:
                setFocus(categoryOnFocus, categoryButtons[Category.POWDER.getValue()]);
                PopulateRecyclerView(Category.POWDER.getValue());
                break;
        }
    }

    private ArrayList<SearchCard> GenerateSampleData() {
        ArrayList<SearchCard> searchCards = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            searchCards.add(new SearchCard(R.drawable.crocin, getString(R.string.sample_medicine_name),
                    getString(R.string.sample_medicine_company), getString(R.string.sample_medicine_size), "random"
                    , "Rs. 24"));

            if (i < 5) {
                searchCards.get(i).setType("GEL");
            } else {
                searchCards.get(i).setType("SYRUP");
            }
        }

        return searchCards;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        SetShopUi();

        if (categoryInventory.get(0).size() == 0) {
            GetCartData();
            categoryOnFocus = categoryButtons[0];
            categoryButtons[0].setBackgroundColor(getColor(R.color.black));
            categoryButtons[0].setTextColor(getColor(R.color.white));
            categoryOnFocus = categoryButtons[0];
            PopulateRecyclerView(0);
            CheckCartUi();
        }
    }

    private void GetCartData() {
        Toast.makeText(this, "Loading cart", Toast.LENGTH_SHORT).show();
        // todo get cart here and populate
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
                    Cart.GetInstance().setCartId(cartId);
                    Cart.GetInstance().setShopId(shopId);
                    Cart.GetInstance().setTotalValue(totalValue);
                    Cart.GetInstance().setTotalItems(totalItems);
                    HashMap<String, CartItem> listofItems = new HashMap<String, CartItem>();

                    for (int i = 0; i < items.size(); i++) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Map<String, String>>(){}.getType();
                        Map<String, String> productMap = gson.fromJson(gson.toJson(items.get(i)), type);
                        String productId = productMap.get("productId");
                        Double price = Double.parseDouble(productMap.get("price"));
                        int quantity = (int)(Double.parseDouble(productMap.get("quantity")));
                        CartItem cartItem = new CartItem(quantity,price,productId);
                        listofItems.put(productId, cartItem);
                    }

                    Cart.GetInstance().setListOfItems(listofItems);
                    System.out.println(Cart.GetInstance().getListOfItems().size());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Conenction Error !! ", Toast.LENGTH_LONG);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void PopulateRecyclerView(int category) {
        this.categoryOnFocusIndex = category;

        if (categoryInventory.get(category).size() == 0) {
            if (category == 0) {
                HashMap<String, String> params = new HashMap<>();
                params.put("shopid", String.valueOf(SHOP_ID));
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
                String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
                RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
                Call<List<HashMap<String,String>>> shopInventoryCall = retrofitInterface.findShopProducts(jwt,params);
                shopInventoryCall.enqueue(new Callback<List<HashMap<String, String>>>() {
                    @Override
                    public void onResponse(Call<List<HashMap<String, String>>> call, Response<List<HashMap<String, String>>> response) {
                        List<HashMap<String,String>> products = response.body();
                        if ( !products.isEmpty()) {
                            ArrayList<SearchCard> searchCards = new ArrayList<>();
                            for(int i=0;i<products.size();i++) {
                                HashMap<String, String> product = products.get(i);
                                String productId = product.get("id");
                                String companyName = product.get("companyName");
                                String Dosestrength = product.get("doseStrength");
                                String productName = product.get("productName");
                                String size = product.get("size");
                                String type = product.get("type");
                                String price = product.get("price");
                                SearchCard searchCard = new SearchCard(R.drawable.syrup3, productName, companyName, size, productId, price);
                                searchCard.setType(type);
                                searchCards.add(searchCard);
                            }

                            categoryInventory.set(category, searchCards);

                            if (inventoryAdapter == null) {
                                inventoryAdapter = new InventoryAdapter(getApplicationContext(), SHOP_ID);
                                inventoryRecycler.setAdapter(inventoryAdapter);
                            }

                            inventoryAdapter.SetContent(categoryInventory.get(category));
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No products Found",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<HashMap<String, String>>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                for (int i = 0; i < categoryInventory.get(0).size(); i++) {
                    if (categoryInventory.get(0).get(i).getType().contentEquals("GEL")) {
                        categoryInventory.get(Category.GEL.getValue()).add(categoryInventory.get(0).get(i));
                    } else if (categoryInventory.get(0).get(i).getType().contentEquals("TABLET")) {
                        categoryInventory.get(Category.TABLET.getValue()).add(categoryInventory.get(0).get(i));
                    } else if (categoryInventory.get(0).get(i).getType().contentEquals("SPRAY")) {
                        categoryInventory.get(Category.SPRAY.getValue()).add(categoryInventory.get(0).get(i));
                    } else if (categoryInventory.get(0).get(i).getType().contentEquals("SYRUP")) {
                        categoryInventory.get(Category.SYRUP.getValue()).add(categoryInventory.get(0).get(i));
                    } else if (categoryInventory.get(0).get(i).getType().contentEquals("POWDER")) {
                        categoryInventory.get(Category.POWDER.getValue()).add(categoryInventory.get(0).get(i));
                    }
                }
            }
        }

        if (inventoryAdapter == null) {
            inventoryAdapter = new InventoryAdapter(this, SHOP_ID);
            inventoryRecycler.setAdapter(inventoryAdapter);
        }

        inventoryAdapter.SetContent(categoryInventory.get(category));
    }

    public void Back(View view) {
        finish();
    }

    private void setFocus(Button button_onfocus, Button button_newfocus) {
        if (button_newfocus != categoryButtons[0]) {
            search.setText("Search for Medicines in category");
        } else {
            search.setText("Search for Medicines");
        }

        button_onfocus.setTextColor(getColor(R.color.black));
        button_onfocus.setBackgroundColor(getColor(R.color.lightWhite));
        button_newfocus.setBackgroundColor(getColor(R.color.black));
        button_newfocus.setTextColor(getColor(R.color.white));
        this.categoryOnFocus = button_newfocus;
    }
}