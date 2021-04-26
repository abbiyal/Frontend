package medmart.loginmedmart.SearchActivity;

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

import java.util.ArrayList;

import medmart.loginmedmart.R;
import medmart.loginmedmart.SearchActivity.HelperClasses.SearchAdapter;
import medmart.loginmedmart.SearchActivity.HelperClasses.SearchCard;

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
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.teal_700));
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        searchText = findViewById(R.id.search_text);
        resultString = findViewById(R.id.result_text);

        SetSearchUi(query);


        searchText.setText(query);
//        CallSearch(query);
        SetOnEditorAction();

        searchRecycler = findViewById(R.id.rv_search_result);
        searchRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        searchRecycler.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchAdapter();
        searchRecycler.setAdapter(searchAdapter);

        NotifyRecycler(GenerateSampleData());

    }

    private void SetSearchUi(String query) {
        searchText.setText(query);
        resultString.setText("Showing all results for " + query);
    }

    private void CallSearch(String query) {
        // todo query to the backend
    }

    private void NotifyRecycler(ArrayList<SearchCard> searchResult) {
        searchAdapter.SetContent(searchResult);
    }

    private ArrayList<SearchCard> GenerateSampleData() {
        ArrayList<SearchCard> searchCards = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            searchCards.add(new SearchCard(R.drawable.crocin, getString(R.string.sample_medicine_name),
                    getString(R.string.sample_medicine_company), getString(R.string.sample_medicine_size)));
        }

        return searchCards;
    }

    private void SetOnEditorAction() {
        searchText.setImeActionLabel("Go", EditorInfo.IME_ACTION_NEXT);
        searchText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                System.out.println("on editor one");
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