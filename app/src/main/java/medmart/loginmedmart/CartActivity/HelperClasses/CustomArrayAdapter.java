package medmart.loginmedmart.CartActivity.HelperClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;

import medmart.loginmedmart.CartActivity.Cart;
import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.R;
import medmart.loginmedmart.ShopInventoryActivity.ShopInventory;
import medmart.loginmedmart.UtilityClasses.ProductCatalogue;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CustomArrayAdapter extends ArrayAdapter {
    private ProductCatalogue[] cartItems;
    private Context context;
    private Dialog pickQuantity;
    private int[] quantityIds = {R.id.remove_item, R.id.quantity1, R.id.quantity2, R.id.quantity3, R.id.quantity4
            , R.id.quantity5, R.id.quantity6, R.id.quantity7, R.id.quantity8, R.id.quantity9, R.id.quantity10};
    private TextView[] quantities = new TextView[11];
    private ImageView cancel_dialog;

    public void SetContent(ProductCatalogue[] cartItems) {
        this.cartItems = cartItems;
    }

    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull ProductCatalogue[] cartItems) {
        super(context, resource, cartItems);
        this.cartItems = cartItems;
        this.context = context;

        pickQuantity = new Dialog(context);
        pickQuantity.setContentView(R.layout.quantity_dialogbox);
        pickQuantity.setCancelable(false);


        for (int i = 0; i < 11; i++) {
            quantities[i] = pickQuantity.findViewById(quantityIds[i]);
        }

        cancel_dialog = pickQuantity.findViewById(R.id.cancel_dialog);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null)
            row = LayoutInflater.from(context).inflate(R.layout.cart_card_view, parent, false);
        ImageView medicineImage = row.findViewById(R.id.medicine_image);
        TextView medicineName = row.findViewById(R.id.medicine_name);
        TextView medicineSize = row.findViewById(R.id.medicine_size);
        TextView medicinePrice = row.findViewById(R.id.medicine_price);
        Button quantity = row.findViewById(R.id.quantity);
        ImageView deleteItem = row.findViewById(R.id.delete_icon);
        medicineName.setText(cartItems[position].getProductName());
        medicineSize.setText(cartItems[position].getSize());
        medicinePrice.setText("Rs. " + String.valueOf(CartService.GetInstance()
                .getListOfItems().get(cartItems[position].getProductId()).getPrice()));
        View finalRow = row;

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveItem(position);
            }
        });

        quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickQuantity(position, finalRow);
            }
        });

        return finalRow;
    }

    private void SetQuantity(int position, View holder, String quantity) {
        ProductCatalogue product = cartItems[position];
        Boolean isProductPresent = CartService.GetInstance().getListOfItems().containsKey(product.getProductId());

        SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", email);
        params.put("shopId", String.valueOf(CartService.GetInstance().getShopId()));
        params.put("productId", product.getProductId());
        params.put("quantity", quantity);

        if (!isProductPresent) {
            params.put("price", String.valueOf(String.valueOf(CartService.GetInstance()
                    .getListOfItems().get(cartItems[position].getProductId()).getPrice())));
            params.put("productName", product.getProductName());

            RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
            Call<HashMap<String, String>> addItemCall = retrofitInterface.addItemInCart(jwt, params);
            addItemCall.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    if (response.body().get("response").contentEquals("success")) {
                        Toast.makeText(context, "Item Added To Cart", Toast.LENGTH_SHORT).show();
                        UpdateCartOnFrontend(position, holder, quantity, isProductPresent);
                        ((ShopInventory) context).CheckCartUi();
                    } else
                        Toast.makeText(context, "Error adding in cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

                }
            });
        } else {
            RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
            Call<HashMap<String, String>> updateItemCall = retrofitInterface.updateItemInCart(jwt, params);
            updateItemCall.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    if (response.body().get("response").contentEquals("success")) {
                        Toast.makeText(context, "Item Updated In Cart", Toast.LENGTH_SHORT).show();
                        UpdateCartOnFrontend(position, holder, quantity, isProductPresent);
                        // todo cart ui
                    } else
                        Toast.makeText(context, "Error adding in cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

                }
            });

        }
    }

    private void UpdateCartOnFrontend(int position, View holder, String quantity, Boolean isProductPresent) {
        ((TextView)(holder.findViewById(R.id.quantity))).setText("Qty " + quantity);
        ProductCatalogue product = cartItems[position];
        String price = String.valueOf(CartService.GetInstance().getListOfItems().get(product.getProductId()).getPrice());
        double prevValue = 0;


        if (!isProductPresent) {
            CartItem cartItem = new CartItem(Integer.parseInt(quantity),
                    Double.parseDouble(price));
            CartService.GetInstance().getListOfItems().put(product.getProductId(), cartItem);
            CartService.GetInstance().setTotalItems(CartService.GetInstance().getTotalItems() + Integer.parseInt(quantity));
        } else {
            int prevQuantity = CartService.GetInstance().getListOfItems().get(product.getProductId()).getQuantity();
            prevValue = Double.parseDouble(price) * prevQuantity;

            CartService.GetInstance().getListOfItems().get(product.getProductId()).setQuantity(Integer.parseInt(quantity));
            CartService.GetInstance().setTotalItems(CartService.GetInstance().getTotalItems()
                    + Integer.parseInt(quantity) - prevQuantity);
        }

        CartItem cartItem = CartService.GetInstance().getListOfItems().get(product.getProductId());

        CartService.GetInstance().setTotalValue(CartService.GetInstance().getTotalValue() - prevValue + (cartItem.getPrice() * cartItem.getQuantity()));
    }

    private void PickQuantity(int position, View finalRow) {
        for (int i = 1; i < 11; i++) {
            int finalI = i;
            quantities[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetQuantity(position, finalRow, quantities[finalI].getText().toString());
                    pickQuantity.dismiss();
                }
            });
        }

        quantities[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveItem(position);
                pickQuantity.dismiss();
            }
        });

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickQuantity.dismiss();
            }
        });

        pickQuantity.show();
    }

    private void RemoveItem(int position) {
        ProductCatalogue product = cartItems[position];
        SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", email);
        params.put("productId",product.getProductId());
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String,String>> delteIteminCartCall = retrofitInterface.deleteItemInCart(jwt,params);
        delteIteminCartCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if(response.body().get("response").contentEquals("success")) {
                    Toast.makeText(context,"Item Removed !!",Toast.LENGTH_LONG).show();
                    CartService cartService = CartService.GetInstance();
                    int prevQuantity = cartService.getListOfItems().get(product.getProductId()).getQuantity();
                    double prevPrice = cartService.getListOfItems().get(product.getProductId()).getPrice();
                    cartService.setTotalItems(cartService.getTotalItems()-prevQuantity);
                    cartService.setTotalValue(cartService.getTotalValue()-(prevPrice * prevQuantity));
                    cartService.getListOfItems().remove(product.getProductId());
                    ((Cart) context).RemoveItem(position);
                    // todo check cart ui
                }
                else
                    Toast.makeText(context,"Error removing",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(context,"Connection Error !!1",Toast.LENGTH_LONG).show();
            }
        });

    }
}

