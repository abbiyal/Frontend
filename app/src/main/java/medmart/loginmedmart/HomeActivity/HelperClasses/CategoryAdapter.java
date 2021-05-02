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

public class CategoryAdapter extends RecyclerView .Adapter<CategoryAdapter.CategoryViewHolder> {
    ArrayList<CategoryCard> categoryCards;

    public void SetContent(ArrayList<CategoryCard> categoryCards) {
        this.categoryCards = categoryCards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card_view, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryCard categoryCard = categoryCards.get(position);
        holder.imageView.setImageResource(categoryCard.getImage());
        holder.categoryName.setText(categoryCard.getName());
    }

    @Override
    public int getItemCount() {
        return categoryCards.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
