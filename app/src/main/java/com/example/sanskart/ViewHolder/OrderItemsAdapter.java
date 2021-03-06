package com.example.sanskart.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sanskart.Model.OrderItem;
import com.example.sanskart.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.w3c.dom.Text;

public class OrderItemsAdapter extends FirebaseRecyclerAdapter<OrderItem, OrderItemsAdapter.OrderHolder> {

    private OnItemClickListener listener;

    public OrderItemsAdapter(@NonNull FirebaseRecyclerOptions<OrderItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull final OrderItem model)
    {
        holder.mobile_no.setText("Mobile No: " + model.getPhone_Number());
        holder.cart_size.setText("Price: " + model.getCartTotalAmount() + "₹");
        holder.name.setText("Name: " + model.getCustomer_name());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model.getOrderID());
            }
        });
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,
                parent, false);
        OrderHolder holder = new OrderHolder(v);
        return holder;

    }

    class OrderHolder extends RecyclerView.ViewHolder {
        TextView mobile_no;
        TextView cart_size;
        TextView name;
        Button accept;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            mobile_no = itemView.findViewById(R.id.text_view_mob);
            cart_size = itemView.findViewById(R.id.text_view_cart);
            name = itemView.findViewById(R.id.text_view_name);
            accept = itemView.findViewById(R.id.button_send);

        }
    }

    public interface OnItemClickListener{
        void onItemClick(String orderid);
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

}

