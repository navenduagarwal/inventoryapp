package com.example.navendu.inventoryapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by navendu on 7/4/2016.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, ArrayList<Product> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product currentProduct = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //Set name of current product
        TextView nameTextView = (TextView) convertView.findViewById(R.id.list_name_textview);
        nameTextView.setText(currentProduct.getProductName());

        //Set quantity of current product
        TextView qtyTextView = (TextView) convertView.findViewById(R.id.list_quantity_textview);
        qtyTextView.setText("Quantity: " + currentProduct.getQtyAvl());

        //Set price of current product
        TextView priceTextView = (TextView) convertView.findViewById(R.id.list_price_textview);
        priceTextView.setText("Price per unit: $" + currentProduct.getProductPrice());

        //Set Product thumbnail
        byte[] imageBytes = currentProduct.getProductImage();
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options); // convert ByteArray to bitmap
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_image);
        imageView.setImageBitmap(bitmap);

        return convertView;
    }
}
