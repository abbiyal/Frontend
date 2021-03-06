package medmart.loginmedmart.ShopInventoryActivity.HelperClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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

import medmart.loginmedmart.CartManagement.CartService;
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
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

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
                            progressDialog.dismiss();
                            Toast.makeText(context,"Item Removed !!",Toast.LENGTH_LONG).show();
                            CartService cartService = CartService.GetInstance();
                            int prevQuantity = cartService.getListOfItems().get(product.getProductId()).getQuantity();
                            double prevPrice = cartService.getListOfItems().get(product.getProductId()).getPrice();
                            cartService.setTotalItems(cartService.getTotalItems()-prevQuantity);
                            cartService.setTotalValue(cartService.getTotalValue()-(prevPrice * prevQuantity));
                            cartService.getListOfItems().remove(product.getProductId());
                            holder.quantity.setVisibility(View.GONE);
                            holder.addToCart.setVisibility(View.VISIBLE);
                            ((ShopInventory) context).CheckCartUi();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error removing", Toast.LENGTH_LONG).show();
                        }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                progressDialog.dismiss();
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

        HashMap<String, CartItem> cartItemHashMap = CartService.GetInstance().getListOfItems();

        if (cartItemHashMap.size() > 0 && CartService.GetInstance().getShopId() == shopId &&
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
                if (CartService.GetInstance().getTotalItems() > 0 && CartService.GetInstance().getShopId() != shopId) {
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
                    CartService.GetInstance().ClearCart();
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

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        Boolean isProductPresent = CartService.GetInstance().getListOfItems().containsKey(product.getProductId());

        SharedPreferences sharedPreferences = context.getSharedPreferences("Login_Cookie", MODE_PRIVATE);
        String jwt = "Bearer " + sharedPreferences.getString("jwt", "No JWT FOUND");
        String email = sharedPreferences.getString("email", "No email FOUND");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", email);
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
                    progressDialog.dismiss();
                    if (response.body().get("response").contentEquals("success")) {
                        Toast.makeText(context, "Item Added To Cart", Toast.LENGTH_SHORT).show();
                        UpdateCartOnFrontend(product, holder, quantity, isProductPresent);
                        ((ShopInventory) context).CheckCartUi();
                    } else
                        Toast.makeText(context, "Error adding in cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        } else {
            RetrofitInterface retrofitInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
            Call<HashMap<String, String>> updateItemCall = retrofitInterface.updateItemInCart(jwt, params);
            updateItemCall.enqueue(new Callback<HashMap<String, String>>() {
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    progressDialog.dismiss();
                    if (response.body().get("response").contentEquals("success")) {
                        Toast.makeText(context, "Item Updated In Cart", Toast.LENGTH_SHORT).show();
                        UpdateCartOnFrontend(product, holder, quantity, isProductPresent);
                        ((ShopInventory) context).CheckCartUi();
                    } else
                        Toast.makeText(context, "Error adding in cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                    progressDialog.dismiss();
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
            CartService.GetInstance().getListOfItems().put(product.getProductId(), cartItem);
            CartService.GetInstance().setTotalItems(CartService.GetInstance().getTotalItems() + Integer.parseInt(quantity));
        } else {
            int prevQuantity = CartService.GetInstance().getListOfItems().get(product.getProductId()).getQuantity();
            prevValue = Double.parseDouble(product.getPrice()) * prevQuantity;

            CartService.GetInstance().getListOfItems().get(product.getProductId()).setQuantity(Integer.parseInt(quantity));
            CartService.GetInstance().setTotalItems(CartService.GetInstance().getTotalItems()
                    + Integer.parseInt(quantity) - prevQuantity);
        }

        CartItem cartItem = CartService.GetInstance().getListOfItems().get(product.getProductId());

        CartService.GetInstance().setTotalValue(CartService.GetInstance().getTotalValue() - prevValue + (cartItem.getPrice() * cartItem.getQuantity()));
        CartService.GetInstance().setShopId(shopId);
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
