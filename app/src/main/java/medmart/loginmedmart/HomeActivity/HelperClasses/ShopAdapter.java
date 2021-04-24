package medmart.loginmedmart.HomeActivity.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import medmart.loginmedmart.R;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    ArrayList<ShopCard> shopCards;
    private RecyclerView.ViewHolder holder;
    private int position;

    public ShopAdapter(ArrayList<ShopCard> shopCards) {
        this.shopCards = shopCards;
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
        holder.shopName.setText(shop.shopName);
        holder.shopDistance.setText(shop.shopDistance);
    }

    @Override
    public int getItemCount() {
        return shopCards.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        ImageView shopImage;
        TextView shopName, shopDistance;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopImage = itemView.findViewById(R.id.shop_image);
            shopName = itemView.findViewById(R.id.shop_name);
            shopDistance = itemView.findViewById(R.id.shop_distance);
        }
    }
}
