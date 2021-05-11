package medmart.loginmedmart.ShopInventoryActivity.HelperClasses;

import android.app.Dialog;
import android.content.Context;
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

import medmart.loginmedmart.CommonAdapter.SearchCard;
import medmart.loginmedmart.R;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewModel> {

    private ArrayList<SearchCard> searchCards;
    private Dialog pickQuantity;
    private NumberPicker numberPicker;
    private Context context;

    public InventoryAdapter(Context context) {
        this.context = context;
        pickQuantity = new Dialog(context);
        pickQuantity.setContentView(R.layout.quantity_dialogbox);
        pickQuantity.setCancelable(false);

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

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickQuantity(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchCards.size();
    }

    public void PickQuantity(InventoryViewModel holder) {
        numberPicker = pickQuantity.findViewById(R.id.numberPicker);
        Button button1 = pickQuantity.findViewById(R.id.button1);
        Button button2 = pickQuantity.findViewById(R.id.button2);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(false);

        numberPicker.setClickable(true);
        numberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addToCart.setVisibility(View.GONE);
                holder.quantity.setVisibility(View.VISIBLE);
                pickQuantity.dismiss();
            }
        });

        pickQuantity.show();
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
