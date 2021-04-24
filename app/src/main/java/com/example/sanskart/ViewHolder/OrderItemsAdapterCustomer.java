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

public class OrderItemsAdapterCustomer extends FirebaseRecyclerAdapter<OrderItem, OrderItemsAdapterCustomer.OrderHolderCustomer> {

    private OnItemClickListener listener;

    public OrderItemsAdapterCustomer(@NonNull FirebaseRecyclerOptions<OrderItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHolderCustomer holder, int position, @NonNull final OrderItem model)
    {
        holder.mobile_no.setText("Mobile No: " + model.getPhone_Number());
        holder.cart_size.setText("Price: " + model.getCartTotalAmount() + "₹");
        holder.name.setText("Name: " + model.getCustomer_name());

        if(model.getStatus().equals("0"))
            holder.status.setText("Order Status: Order Pending");
        else
            holder.status.setText("Order Status: Delivered");

        holder.feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model.getOrderID());
            }
        });
    }

    @NonNull
    @Override
    public OrderHolderCustomer onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_customer,
                parent, false);
        OrderHolderCustomer holder = new OrderHolderCustomer(v);
        return holder;

    }

    class OrderHolderCustomer extends RecyclerView.ViewHolder {
        TextView mobile_no;
        TextView cart_size;
        TextView status;
        TextView name;
        Button feedback;

        public OrderHolderCustomer(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.text_view_status);
            mobile_no = itemView.findViewById(R.id.text_view_mob);
            cart_size = itemView.findViewById(R.id.text_view_cart);
            name = itemView.findViewById(R.id.text_view_name);
            feedback = itemView.findViewById(R.id.button_feedback);

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

