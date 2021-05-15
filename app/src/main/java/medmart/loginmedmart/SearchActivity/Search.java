package medmart.loginmedmart.SearchActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import medmart.loginmedmart.R;
import medmart.loginmedmart.CommonAdapter.SearchAdapter;
import medmart.loginmedmart.CommonAdapter.SearchCard;
import medmart.loginmedmart.UtilityClasses.ProductCatalogue;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {
    EditText searchText;
    TextView resultString;

    RecyclerView searchRecycler;
    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.teal_700));
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        searchText = findViewById(R.id.search_text);
        resultString = findViewById(R.id.result_text);

        SetSearchUi(query);

        CallSearch(query);
        SetOnEditorAction();

        searchRecycler = findViewById(R.id.rv_search_result);
        searchRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        searchRecycler.setLayoutManager(linearLayoutManager);
    }

    private void SetSearchUi(String query) {
        searchText.setText(query);
        searchText.setSelection(searchText.getText().length());
        InputMethodManager imm =(InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        resultString.setText("Showing all results for " + query);
    }

    private void CallSearch(String query) {
        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", query);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        System.out.println(jwt);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<List<ProductCatalogue>> searchCall = retrofitInterface.getSearchResults(jwt, params);
        searchCall.enqueue(new Callback<List<ProductCatalogue>>() {
            @Override
            public void onResponse(Call<List<ProductCatalogue>> call, Response<List<ProductCatalogue>> response) {
                List<ProductCatalogue> results = response.body();
                ArrayList<SearchCard> searchResults = new ArrayList<>();
                for (int i = 0; i < results.size(); i++) {
                    String sizeString = "";
                    switch (results.get(i).getType()) {
                        case "GEL":
                            sizeString = results.get(i).getSize() + " " + getString(R.string.Gel_Size);
                            break;
                        case "POWDER":
                            sizeString = results.get(i).getSize() + " " + getString(R.string.Powder_Size);
                            break;
                        case "SYRUP":
                        case "SPRAY":
                            sizeString = results.get(i).getSize() + " " + getString(R.string.Syrup_Spray_Size);
                            break;
                        case "TABLET":
                            sizeString = results.get(i).getSize() + " " + getString(R.string.Tablet_Size);
                            break;
                        default:
                            sizeString = results.get(i).getSize() + " UNITS";
                    }
                    searchResults.add(new SearchCard(R.drawable.crocin,
                            results.get(i).getProductName(), results.get(i).getCompanyName(),
                            sizeString, results.get(i).getProductId()));
                }

                if (searchAdapter == null) {
                    searchAdapter = new SearchAdapter(getApplicationContext());
                    searchRecycler.setAdapter(searchAdapter);
                }

                NotifyRecycler(searchResults);
            }

            @Override
            public void onFailure(Call<List<ProductCatalogue>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "connection Error !!!", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void NotifyRecycler(ArrayList<SearchCard> searchResult) {
        searchAdapter.SetContent(searchResult);
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

    public void GoBack(View view) {
        finish();
    }

    public void ClearSearch(View view) {
        searchText = findViewById(R.id.search_text);
        searchText.getText().clear();
    }
}