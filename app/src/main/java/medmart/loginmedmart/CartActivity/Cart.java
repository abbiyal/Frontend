package medmart.loginmedmart.CartActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.R;
import medmart.loginmedmart.ShopInventoryActivity.ShopInventory;
import medmart.loginmedmart.UtilityClasses.Utility;

public class Cart extends AppCompatActivity {
    TextView itemCount, currentLocation;

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
    }

    public void GoBack(View view) {
        finish();
    }

    public void OpenShop(View view) {
        // todo backend to get shop name and address using id from cart
        Intent intent = new Intent(getApplicationContext(), ShopInventory.class);
        intent.putExtra("shopid", CartService.GetInstance().getShopId());
        intent.putExtra("shopname", "");
        intent.putExtra("shopaddress", "");
        startActivity(intent);
    }

    private void SetUi() {
        currentLocation.setText(Utility.GetDataFromCache(this, "useraddress", "Chandigarh"));
    }

    public void RemoveItem(int position) {

    }
}