package medmart.loginmedmart.SearchActivity.HelperClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import medmart.loginmedmart.HomeActivity.HelperClasses.ShopAdapter;
import medmart.loginmedmart.HomeActivity.HelperClasses.ShopCard;
import medmart.loginmedmart.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewModel> {

    private ArrayList<SearchCard> searchCards;

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
    }

    @Override
    public int getItemCount() {
        return searchCards.size();
    }

    public class SearchViewModel extends RecyclerView.ViewHolder{
        ImageView medicineImage;
        TextView medicineName, medicineCompany, medicineSize;

        public SearchViewModel(@NonNull View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.medicine_image);
            medicineName = itemView.findViewById(R.id.medicine_name);
            medicineCompany = itemView.findViewById(R.id.medicine_company);
            medicineSize = itemView.findViewById(R.id.medicine_size);
        }
    }
}
