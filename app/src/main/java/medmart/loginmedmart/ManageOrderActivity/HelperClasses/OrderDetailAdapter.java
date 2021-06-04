package medmart.loginmedmart.ManageOrderActivity.HelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import medmart.loginmedmart.ManageOrderActivity.OrderDetail;
import medmart.loginmedmart.R;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewModel> {

    private Context context;
    ArrayList<OrderDetailCard> orderDetailCards;

    public OrderDetailAdapter(Context context) {
        orderDetailCards = new ArrayList<>();
        this.context = context;
    }

    public void SetContent(ArrayList<OrderDetailCard> orderDetailCards) {
        this.orderDetailCards = orderDetailCards;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public OrderDetailViewModel onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_card, parent, false);
        return new OrderDetailAdapter.OrderDetailViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderDetailViewModel holder, int position) {
        OrderDetailCard orderDetailCard = orderDetailCards.get(position);

        holder.medicineName.setText(orderDetailCard.getMedicineName());
        holder.quantity.setText(String.valueOf(orderDetailCard.getQuantity()));
        holder.price.setText(String.valueOf(orderDetailCard.getPrice()));
        holder.totalValue.setText(String.valueOf(orderDetailCard.totalValue));
    }

    @Override
    public int getItemCount() {
        return orderDetailCards.size();
    }

    public class OrderDetailViewModel extends RecyclerView.ViewHolder {
        TextView medicineName, price, quantity, totalValue;

        public OrderDetailViewModel(@NonNull @NotNull View itemView) {
            super(itemView);

            medicineName = itemView.findViewById(R.id.medicine_name);
            price = itemView.findViewById(R.id.medicine_price);
            quantity = itemView.findViewById(R.id.quantity);
            totalValue = itemView.findViewById(R.id.total_value);
        }
    }
}
