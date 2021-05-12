package medmart.loginmedmart.ShopInventoryActivity.HelperClasses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import medmart.loginmedmart.CartManagement.Cart;
import medmart.loginmedmart.CartManagement.CartItem;
import medmart.loginmedmart.CommonAdapter.SearchCard;
import medmart.loginmedmart.R;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewModel> {

    private ArrayList<SearchCard> searchCards;
    private Dialog pickQuantity;
    private NumberPicker numberPicker;
    private Context context;
    private long shopId;
    private int[] quantityIds = {R.id.remove_item, R.id.quantity1, R.id.quantity2, R.id.quantity3, R.id.quantity4
            , R.id.quantity5, R.id.quantity6, R.id.quantity7, R.id.quantity8, R.id.quantity9, R.id.quantity10};
    private TextView[] quantities = new TextView[11];

    public InventoryAdapter(Context context, long shopId) {
        this.context = context;
        pickQuantity = new Dialog(context);
        pickQuantity.setContentView(R.layout.quantity_dialogbox);
        pickQuantity.setCancelable(false);
        this.shopId = shopId;

        for (int i = 0; i < 11; i++) {
            quantities[i] = pickQuantity.findViewById(quantityIds[i]);
        }

        quantities[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveItem();
            }
        });
    }

    private void RemoveItem() {

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
//        System.out.println("here on binding " + search.getProductId());

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
                                    Cart.GetInstance().ClearCart();
                                    PickQuantity(search, holder);
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

        pickQuantity.show();
    }

    private void SetQuantity(SearchCard product, InventoryViewModel holder, String quantity) {
        holder.addToCart.setVisibility(View.GONE);
        holder.quantity.setText("Quantity " + quantity);
        holder.quantity.setVisibility(View.VISIBLE);
        double prevValue = 0;

        if (!Cart.GetInstance().getListOfItems().containsKey(product.getProductId())) {
            CartItem cartItem = new CartItem(Integer.parseInt(quantity),
                    Double.parseDouble(product.getPrice()));
            Cart.GetInstance().getListOfItems().put(product.getProductId(), cartItem);
            Cart.GetInstance().setTotalItems(Cart.GetInstance().getTotalItems() + 1);
        } else {
            prevValue = Double.parseDouble(product.getPrice()) * Integer.parseInt(quantity);
            Cart.GetInstance().getListOfItems().get(product.getProductId()).setQuantity(Integer.parseInt(quantity));
        }

        CartItem cartItem = Cart.GetInstance().getListOfItems().get(product.getProductId());

        Cart.GetInstance().setTotalValue(Cart.GetInstance().getTotalValue() - prevValue + (cartItem.getPrice() * cartItem.getQuantity()));
        Cart.GetInstance().setShopId(shopId);
        // todo backend call to add cart
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
