package medmart.loginmedmart.CommonAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import medmart.loginmedmart.R;
import medmart.loginmedmart.ShopInventoryActivity.ShopInventory;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    ArrayList<ShopCard> shopCards;
    private RecyclerView.ViewHolder holder;
    private int position;
    private Context context;

    public ShopAdapter(Context context) {
        shopCards = new ArrayList<>();
        this.context = context;
    }

    public void SetContent(ArrayList<ShopCard> shopCards) {
        this.shopCards = shopCards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_card_view, parent, false);
        return new ShopAdapter.ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopCard shop = shopCards.get(position);
        holder.shopImage.setImageResource(shop.getImage());
        holder.shopName.setText(shop.getShopName());
        holder.shopDistance.setText(shop.getShopDistance());
        holder.shopPrice.setText(shop.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopInventory.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopCards.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        ImageView shopImage;
        TextView shopName, shopDistance, shopPrice;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopImage = itemView.findViewById(R.id.shop_image);
            shopName = itemView.findViewById(R.id.shop_name);
            shopDistance = itemView.findViewById(R.id.shop_distance);
            shopPrice = itemView.findViewById(R.id.shop_price);
        }
    }
}
