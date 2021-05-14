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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int[] categoryButtonId = {R.id.all, R.id.gel, R.id.tablet, R.id.spray, R.id.syrup, R.id.powder};
    private Long SHOP_ID;
    private TextView shopNameTV, shopAddressTV;
    private  String shopName, shopAddress;

    ArrayList<SearchCard> searchInventory;
    ArrayList<ArrayList<SearchCard>> categoryInventory = new ArrayList<ArrayList<SearchCard>>(6);

    RecyclerView inventoryRecycler;
    InventoryAdapter inventoryAdapter;

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

        Intent intent = getIntent();
        SHOP_ID = intent.getLongExtra("shopid", 100);
        shopNameTV = findViewById(R.id.shop_name);
        shopAddressTV = findViewById(R.id.shop_address);
        shopName = intent.getStringExtra("shopname");
        shopAddress = intent.getStringExtra("shopaddress");

        SetShopUi();

        inventoryRecycler = findViewById(R.id.inventoryrv);
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
        HandleCategoryClick(findViewById(R.id.all));
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
            // todo get cart
            categoryOnFocus = categoryButtons[0];
            categoryButtons[0].setBackgroundColor(getColor(R.color.black));
            categoryButtons[0].setTextColor(getColor(R.color.white));
            categoryOnFocus = categoryButtons[0];
            PopulateRecyclerView(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void PopulateRecyclerView(int category) {
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
        button_onfocus.setTextColor(getColor(R.color.black));
        button_onfocus.setBackgroundColor(getColor(R.color.lightWhite));
        button_newfocus.setBackgroundColor(getColor(R.color.black));
        button_newfocus.setTextColor(getColor(R.color.white));
        this.categoryOnFocus = button_newfocus;
    }
}