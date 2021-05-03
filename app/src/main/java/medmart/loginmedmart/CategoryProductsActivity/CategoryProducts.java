package medmart.loginmedmart.CategoryProductsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import medmart.loginmedmart.CommonAdapter.SearchAdapter;
import medmart.loginmedmart.CommonAdapter.SearchCard;
import medmart.loginmedmart.R;
import medmart.loginmedmart.UtilityClasses.ProductCatalogue;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProducts extends AppCompatActivity {

    EditText searchText;
    TextView categoryName, resultString;
    ArrayList<SearchCard> completeListOfProducts;

    RecyclerView categoryRecycler;
    SearchAdapter categorySearchAdapter;

    public void GoBack(View view) {
        finish();
    }

    public void ClearSearch(View view) {
        searchText = findViewById(R.id.search_text);
        searchText.getText().clear();
        resultString.setText("Showing all products under " + categoryName.getText().toString());
        NotifyRecycler(completeListOfProducts);
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
        setContentView(R.layout.activity_category_products);

        categoryName = findViewById(R.id.category_name);
        searchText = findViewById(R.id.search_text);
        resultString = findViewById(R.id.result_text);

        Intent intent = getIntent();
        categoryName.setText(intent.getStringExtra("categoryname") + " Products");
        resultString.setText("Showing all products under " + intent.getStringExtra("categoryname"));
        SetOnEditorAction();
        categoryRecycler = findViewById(R.id.rv_search_result);
        categoryRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        categoryRecycler.setLayoutManager(linearLayoutManager);
        RequestForCompleteList();
    }

    private void RequestForCompleteList() {
        //todo get complete list and set completelistofproducts
        completeListOfProducts = GenerateSampleData();

        if (categorySearchAdapter == null) {
            categorySearchAdapter = new SearchAdapter(getApplicationContext());
            categoryRecycler.setAdapter(categorySearchAdapter);
        }

        NotifyRecycler(completeListOfProducts);
    }

    private void NotifyRecycler(ArrayList<SearchCard> lisOfProducts) {
        categorySearchAdapter.SetContent(lisOfProducts);
    }

    private void SetSearchUi(String query) {
        searchText.setText(query);
        searchText.setSelection(searchText.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        resultString.setText(resultString.getText().toString() + ">" + query);
    }

    private void CallSearch(String query) {
        // todo search results under category nd category name is in categoryName textview
        ArrayList<SearchCard> searchResults = new ArrayList<>();


        NotifyRecycler(searchResults);
    }

    private ArrayList<SearchCard> GenerateSampleData() {
        ArrayList<SearchCard> searchCards = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            searchCards.add(new SearchCard(R.drawable.crocin, getString(R.string.sample_medicine_name),
                    getString(R.string.sample_medicine_company), getString(R.string.sample_medicine_size), "sample"));
        }

        return searchCards;
    }

        private void SetOnEditorAction() {
        searchText.setImeActionLabel("Go", EditorInfo.IME_ACTION_DONE);
        searchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    String query = searchText.getText().toString();
                    SetSearchUi(query);
                    CallSearch(query);
                    return true;
                }

                return false;
            }
        });
    }
}