package medmart.loginmedmart.CommonAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import medmart.loginmedmart.ProductResultActivity.ProductResult;
import medmart.loginmedmart.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewModel> {

    private ArrayList<SearchCard> searchCards;
    private Context context;

    public SearchAdapter(Context context) {
        searchCards = new ArrayList<>();
        this.context = context;
    }

    public void SetContent(ArrayList<SearchCard> searchCards) {
        this.searchCards = searchCards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_card, parent, false);
        return new SearchAdapter.SearchViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewModel holder, int position) {
        SearchCard search = searchCards.get(position);
        holder.medicineImage.setImageResource(search.getImage());
        holder.medicineName.setText(search.medicineName);
        holder.medicineCompany.setText(search.medicineCompany);
        holder.medicineSize.setText(search.medicineSize);

        holder.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductResult.class);
                intent.putExtra("productid", searchCards.get(position).getProductId());
                intent.putExtra("medicinename", searchCards.get(position).getMedicineName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchCards.size();
    }

    public class SearchViewModel extends RecyclerView.ViewHolder{
        ImageView medicineImage;
        TextView medicineName, medicineCompany, medicineSize;
        Button buyNow;

        public SearchViewModel(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.medicine_image);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineCompany = itemView.findViewById(R.id.medicine_company);
            medicineSize = itemView.findViewById(R.id.medicine_size);
            buyNow = itemView.findViewById(R.id.buy_now);
        }
    }
}
