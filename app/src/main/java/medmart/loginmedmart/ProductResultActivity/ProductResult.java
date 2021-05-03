package medmart.loginmedmart.ProductResultActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import medmart.loginmedmart.CommonAdapter.ShopAdapter;
import medmart.loginmedmart.CommonAdapter.ShopCard;
import medmart.loginmedmart.MapActivity.PlacesSearch;
import medmart.loginmedmart.R;
import medmart.loginmedmart.SearchActivity.Search;
import medmart.loginmedmart.UtilityClasses.Utility;
import okhttp3.internal.Util;

public class ProductResult extends AppCompatActivity {
    RecyclerView shopRecycler;
    ShopAdapter shopAdapter;
    EditText search;
    private LatLng mDefaultLocation = new LatLng(30.767, 76.7774);
    private String mDefaultLocationName = "Chandigarh";
    TextView currentAddress;
    TextView resultString;
    private String productId;
    private String medicineName;

    public void Back(View view) {
        finish();
    }

    public void ChangeAddress(View view) {
        Intent intent = getIntent();
        intent.setClass(getApplicationContext(), PlacesSearch.class);
        intent.putExtra("class", "productresult");
        startActivity(intent);
    }

    public void CallSearch(View view) {
        String query = search.getText().toString();
        Intent intent = new Intent(this, Search.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        search.getText().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_product_result);
        search = findViewById(R.id.search_text);
        resultString = findViewById(R.id.result_text);
        currentAddress = findViewById(R.id.current_address);
        currentAddress.setText(Utility.GetDataFromCache(this, "useraddress", mDefaultLocationName));
        Intent intent = getIntent();
        medicineName = intent.getStringExtra("medicinename");
        productId = intent.getStringExtra("productid");

        SetOnEditorAction();
        shopRecycler = findViewById(R.id.shop_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        shopRecycler.setHasFixedSize(true);
        shopRecycler.setLayoutManager(linearLayoutManager);
        // todo reuest nd call result string
        RequestBackend();

    }

    private void RequestBackend() {

        ArrayList<ShopCard> shopCards = new ArrayList<>();
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));
        shopCards.add(new ShopCard(R.drawable.biyal_shop__1_, "Biyal Pharmaceuticals", "2.3Km"));

        if (shopAdapter == null) {
            shopAdapter = new ShopAdapter();
            shopRecycler.setAdapter(shopAdapter);
        }

        NotifyShopRecycler(shopCards);
        resultString.setText(shopAdapter.getItemCount() + " store delivering " + medicineName);
    }

    private void NotifyShopRecycler(ArrayList<ShopCard> shopCards) {
        shopAdapter.SetContent(shopCards);
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
}