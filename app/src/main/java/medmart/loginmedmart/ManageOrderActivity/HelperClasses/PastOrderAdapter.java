package medmart.loginmedmart.ManageOrderActivity.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import medmart.loginmedmart.CartActivity.HelperClasses.CartAdapter;
import medmart.loginmedmart.ManageOrderActivity.OrderDetail;
import medmart.loginmedmart.R;

public class PastOrderAdapter extends RecyclerView.Adapter<PastOrderAdapter.PastOrderViewHolder> {
    ArrayList<PastOrderCard> pastOrderCards;
    Context context;

    public PastOrderAdapter(Context context) {
        pastOrderCards = new ArrayList<>();
        this.context = context;
    }

    public void SetContent(ArrayList<PastOrderCard> pastOrderCards) {
        this.pastOrderCards = pastOrderCards;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public PastOrderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_order_card, parent, false);
        return new PastOrderAdapter.PastOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PastOrderViewHolder holder, int position) {
        PastOrderCard pastOrderCard = pastOrderCards.get(position);

        holder.shopName.setText(pastOrderCard.getShopName());
        holder.price.setText("Rs. " + String.valueOf(pastOrderCard.getPrice()));
        holder.status.setText(pastOrderCard.getStatus());
        holder.dateTime.setText(pastOrderCard.getDateTime());

        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetail.class);
                intent.putExtra("orderId", pastOrderCard.getOrderId());
                intent.putExtra("shopName", pastOrderCard.getShopName());
                intent.putExtra("shopAddress", pastOrderCard.getShopAddress());
                intent.putExtra("deliveryAddress", pastOrderCard.getDeliveryAddress());
                intent.putExtra("dateTime", pastOrderCard.getDateTime());
                intent.putExtra("totalValue", String.valueOf(pastOrderCard.getPrice()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pastOrderCards.size();
    }

    public class PastOrderViewHolder extends RecyclerView.ViewHolder {
        TextView shopName, price, status, dateTime;
        Button viewDetails;

        public PastOrderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            shopName = itemView.findViewById(R.id.shop_name);
            price = itemView.findViewById(R.id.order_price);
            status = itemView.findViewById(R.id.status);
            dateTime = itemView.findViewById(R.id.datetime);
            viewDetails = itemView.findViewById(R.id.view_details);
        }
    }
}
