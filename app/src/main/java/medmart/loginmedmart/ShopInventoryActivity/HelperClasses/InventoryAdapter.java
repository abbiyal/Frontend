package medmart.loginmedmart.ShopInventoryActivity.HelperClasses;

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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import medmart.loginmedmart.CartManagement.Cart;
import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CommonAdapter.SearchCard;
import medmart.loginmedmart.R;
import medmart.loginmedmart.ShopInventoryActivity.ShopInventory;
import medmart.loginmedmart.UtilityClasses.RetrofitInstance;
import medmart.loginmedmart.UtilityClasses.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewModel> {

    private ArrayList<SearchCard> searchCards;
    private Dialog pickQuantity;
    private NumberPicker numberPicker;
    private Context context;
    private long shopId;
    private int[] quantityIds = {R.id.remove_item, R.id.quantity1, R.id.quantity2, R.id.quantity3, R.id.quantity4
            , R.id.quantity5, R.id.quantity6, R.id.quantity7, R.id.quantity8, R.id.quantity9, R.id.quantity10};
    private TextView[] quantities = new TextView[11];
    private ImageView cancel_dialog;

    public InventoryAdapter(Context context, long shopId) {
        this.context = context;
        pickQuantity = new Dialog(context);
        pickQuantity.setContentView(R.layout.quantity_dialogbox);
        pickQuantity.setCancelable(false);
        this.shopId = shopId;

        for (int i = 0; i < 11; i++) {
            quantities[i] = pickQuantity.findViewById(quantityIds[i]);
        }

        cancel_dialog = pickQuantity.findViewById(R.id.cancel_dialog);
    }

    private void RemoveItem(SearchCard product, InventoryViewModel holder) {
        Cart cart = Cart.GetInstance();
        int prevQuantity = cart.getListOfItems().get(product.getProductId()).getQuantity();
        double prevPrice = cart.getListOfItems().get(product.getProductId()).getPrice();
        cart.setTotalItems(cart.getTotalItems()-prevQuantity);
        cart.setTotalValue(cart.getTotalValue()-(prevPrice * prevQuantity));
        cart.getListOfItems().remove(product.getProductId());
        holder.quantity.setVisibility(View.GONE);
        holder.addToCart.setVisibility(View.VISIBLE);

        // todo remove item;
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
                            Toast.makeText(context,"Cart Empty !!",Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(context,"Error emptying cart",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                Toast.makeText(context,"Connection Error !!1",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void SetContent(ArrayList<SearchCard> searchCards) {
        this.searchCards = searchCards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_card_view, parent, false);
        return new InventoryAdapter.InventoryViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewModel holder, int position) {
        SearchCard search = searchCards.get(position);
        holder.medicinePrice.setText(search.getPrice());
        holder.medicineName.setText(search.getMedicineName());
        holder.medicineCompany.setText(search.getMedicineCompany());
        holder.medicineSize.setText(search.getMedicineSize());
        String type = search.getType();

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

        HashMap<String, CartItem> cartItemHashMap = Cart.GetInstance().getListOfItems();

        if (cartItemHashMap.size() > 0 && Cart.GetInstance().getShopId() == shopId &&
                cartItemHashMap.containsKey(search.getProductId())) {
            holder.addToCart.setVisibility(View.GONE);
            holder.quantity.setText("Quantity " + cartItemHashMap.get(search.getProductId()).getQuantity());
            holder.quantity.setVisibility(View.VISIBLE);
        } else {
            holder.addToCart.setVisibility(View.VISIBLE);
            holder.quantity.setVisibility(View.GONE);
        }

        holder.quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickQuantity(search, holder);
            }
        });

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Cart.GetInstance().getTotalItems() > 0 && Cart.GetInstance().getShopId() != shopId) {
                    // todo add item from new shop,, empty cart and add this nd tell backend as well
                    new AlertDialog.Builder(context)
                            .setTitle("Discard Cart")
                            .setMessage("Items have been added from different shop. Are you sure you want to " +
                                    "discard it")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ClearBackendCart(search, holder);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                } else {
                    PickQuantity(search, holder);
                }
            }
        });
    }

    private void ClearBackendCart(SearchCard search, InventoryViewModel holder) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
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
                    Toast.makeText(context, "cart Empty !!!", Toast.LENGTH_SHORT).show();
                    Cart.GetInstance().ClearCart();
                    PickQuantity(search, holder);
                } else
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return searchCards.size();
    }

    public void PickQuantity(SearchCard product, InventoryViewModel holder) {
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

    private void SetQuantity(SearchCard product, InventoryViewModel holder, String quantity) {
        Boolean isProductPresent = Cart.GetInstance().getListOfItems().containsKey(product.getProductId());

        SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", email);
        params.put("shopId", String.valueOf(shopId));
        params.put("productId", product.getProductId());
        params.put("quantity", quantity);

        if (!isProductPresent) {
            params.put("price", String.valueOf(product.getPrice()));
            params.put("productName", product.getMedicineName());

            RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
            Call<HashMap<String, String>> addItemCall = retrofitInterface.addItemInCart(jwt, params);
            addItemCall.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    if (response.body().get("response").contentEquals("success")) {
                        Toast.makeText(context, "Item Added To Cart", Toast.LENGTH_SHORT).show();
                        UpdateCartOnFrontend(product, holder, quantity, isProductPresent);
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
                        UpdateCartOnFrontend(product, holder, quantity, isProductPresent);
                        ((ShopInventory) context).CheckCartUi();
                    } else
                        Toast.makeText(context, "Error adding in cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

                }
            });

        }
    }

    private void UpdateCartOnFrontend(SearchCard product, InventoryViewModel holder, String quantity, Boolean isProductPresent) {
        holder.addToCart.setVisibility(View.GONE);
        holder.quantity.setText("Quantity " + quantity);
        holder.quantity.setVisibility(View.VISIBLE);
        double prevValue = 0;

        if (!isProductPresent) {
            CartItem cartItem = new CartItem(Integer.parseInt(quantity),
                    Double.parseDouble(product.getPrice()));
            Cart.GetInstance().getListOfItems().put(product.getProductId(), cartItem);
            Cart.GetInstance().setTotalItems(Cart.GetInstance().getTotalItems() + Integer.parseInt(quantity));
        } else {
            prevValue = Double.parseDouble(product.getPrice()) * Integer.parseInt(quantity);
            Cart.GetInstance().getListOfItems().get(product.getProductId()).setQuantity(Integer.parseInt(quantity));
            Cart.GetInstance().setTotalItems(-Cart.GetInstance().getTotalItems()
                    + Integer.parseInt(quantity));
        }

        CartItem cartItem = Cart.GetInstance().getListOfItems().get(product.getProductId());

        Cart.GetInstance().setTotalValue(Cart.GetInstance().getTotalValue() - prevValue + (cartItem.getPrice() * cartItem.getQuantity()));
        Cart.GetInstance().setShopId(shopId);
    }

    public class InventoryViewModel extends RecyclerView.ViewHolder {
        ImageView medicineImage;
        TextView medicineName, medicineCompany, medicineSize, medicinePrice;
        Button addToCart, quantity;

        public InventoryViewModel(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.medicine_image);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineCompany = itemView.findViewById(R.id.medicine_company);
            medicineSize = itemView.findViewById(R.id.medicine_size);
            medicinePrice = itemView.findViewById(R.id.medicine_price);
            addToCart = itemView.findViewById(R.id.add_to_cart);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
