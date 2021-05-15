package medmart.loginmedmart.CartActivity.HelperClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CartManagement.CartService;
import medmart.loginmedmart.CommonAdapter.SearchCard;
import medmart.loginmedmart.R;
import medmart.loginmedmart.ShopInventoryActivity.HelperClasses.InventoryAdapter;
import medmart.loginmedmart.ShopInventoryActivity.ShopInventory;
import medmart.loginmedmart.UtilityClasses.ProductCatalogue;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewModel> {

    private ArrayList<ProductCatalogue> productCatalogues;
    private Dialog pickQuantity;
    private Context context;
    private int[] quantityIds = {R.id.remove_item, R.id.quantity1, R.id.quantity2, R.id.quantity3, R.id.quantity4
            , R.id.quantity5, R.id.quantity6, R.id.quantity7, R.id.quantity8, R.id.quantity9, R.id.quantity10};
    private TextView[] quantities = new TextView[11];
    private ImageView cancel_dialog;

    public CartAdapter(Context context) {
        this.context = context;
        pickQuantity = new Dialog(context);
        pickQuantity.setContentView(R.layout.quantity_dialogbox);
        pickQuantity.setCancelable(false);

        for (int i = 0; i < 11; i++) {
            quantities[i] = pickQuantity.findViewById(quantityIds[i]);
        }

        cancel_dialog = pickQuantity.findViewById(R.id.cancel_dialog);
    }

    @NonNull
    @Override
    public CartViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card_view, parent, false);
        return new CartAdapter.CartViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewModel holder, int position) {
        ProductCatalogue productCatalogue = productCatalogues.get(position);
        CartItem cartItem = CartService.GetInstance().getListOfItems().get(productCatalogue.getProductId());

        holder.medicinePrice.setText("Rs. " + String.valueOf(cartItem.getPrice()));
        holder.medicineName.setText(productCatalogue.getProductName());
        holder.medicineSize.setText(productCatalogue.getSize());
        holder.quantity.setText("Qty: "+cartItem.getQuantity());
        String type = productCatalogue.getType();

        if (type.contentEquals("GEL")) {
            holder.medicineImage.setImageResource(R.drawable.gel);
        } else if (type.contentEquals("TABLET")) {
            holder.medicineImage.setImageResource(R.drawable.tablet);
        } else if (type.contentEquals("SPRAY")) {
            holder.medicineImage.setImageResource(R.drawable.spray3);
        } else if (type.contentEquals("SYRUP")) {
            holder.medicineImage.setImageResource(R.drawable.syrup3);
        } else {
            holder.medicineImage.setImageResource(R.drawable.powder);
        }


        holder.quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickQuantity(productCatalogue, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productCatalogues.size();
    }

    public void PickQuantity(ProductCatalogue product, CartViewModel holder) {
        for (int i = 1; i < 11; i++) {
            int finalI = i;
            quantities[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetQuantity(product, holder, quantities[finalI].getText().toString());
                    pickQuantity.dismiss();
                }
            });
        }

        quantities[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveItem(product, holder);
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

    private void SetQuantity(ProductCatalogue product, CartViewModel holder, String quantity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", email);
        params.put("shopId", String.valueOf(CartService.GetInstance().getShopId()));
        params.put("productId", product.getProductId());
        params.put("quantity", quantity);
        RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        Call<HashMap<String, String>> updateItemCall = retrofitInterface.updateItemInCart(jwt, params);
        updateItemCall.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (response.body().get("response").contentEquals("success")) {
                    Toast.makeText(context, "Item Updated In Cart", Toast.LENGTH_SHORT).show();
                    UpdateCartOnFrontend(product, holder, quantity);
                    // todo cart ui setting
                } else
                    Toast.makeText(context, "Error adding in cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
    }

    private void UpdateCartOnFrontend(ProductCatalogue product, CartViewModel holder, String quantity) {
        holder.quantity.setText("Qty: "+quantity);
        CartItem cartItem = CartService.GetInstance().getListOfItems().get(product.getProductId());
        int prevQuantity = cartItem.getQuantity();
        double prevValue = cartItem.getPrice() * prevQuantity;

        CartService.GetInstance().getListOfItems().get(product.getProductId()).setQuantity(Integer.parseInt(quantity));
        CartService.GetInstance().setTotalItems(CartService.GetInstance().getTotalItems()
                + Integer.parseInt(quantity) - prevQuantity);

        cartItem = CartService.GetInstance().getListOfItems().get(product.getProductId());
        CartService.GetInstance().setTotalValue(CartService.GetInstance().getTotalValue() - prevValue + (cartItem.getPrice() * cartItem.getQuantity()));
    }

    private void RemoveItem(ProductCatalogue product, CartViewModel holder) {
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
                    productCatalogues.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), productCatalogues.size());
                    // todo cart ui setting
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

    public void SetContent(ArrayList<ProductCatalogue> productCatalogues) {
        this.productCatalogues = productCatalogues;
        notifyDataSetChanged();
    }

    public class CartViewModel extends RecyclerView.ViewHolder {
        ImageView medicineImage, deleteItem;
        TextView medicineName, medicineSize, medicinePrice;
        Button quantity;

        public CartViewModel(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.medicine_image);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineSize = itemView.findViewById(R.id.medicine_size);
            medicinePrice = itemView.findViewById(R.id.medicine_price);
            deleteItem = itemView.findViewById(R.id.delete_icon);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
