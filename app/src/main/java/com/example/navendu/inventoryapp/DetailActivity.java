package com.example.navendu.inventoryapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navendu.inventoryapp.data.InventoryDbHelper;

public class DetailActivity extends AppCompatActivity {
    private Product product;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //get score values from last activity
        Intent intent = this.getIntent();
        Bundle b = intent.getExtras();
        productId = b.getInt("productParcel");
        updateView();

        //Sale button action
        Button sale = (Button) findViewById(R.id.button_sale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InventoryDbHelper dbHelper = new InventoryDbHelper(DetailActivity.this);
                EditText soldQty = (EditText) findViewById(R.id.sale_edittext);
                String qtySaleText = soldQty.getText().toString();
                int qtySale = Integer.parseInt(qtySaleText);
                if (qtySaleText.matches("") || qtySale < 1 || qtySale > product.getQtyAvl()) {
                    Toast.makeText(DetailActivity.this, getResources().getString(R.string.error_product_sale),
                            Toast.LENGTH_SHORT).show();
                } else {
                    int qtyChange = -qtySale;
                    dbHelper.updateData(productId, product.getQtyAvl(), product.getQtySold(), qtyChange);
                    updateView();
                }
                dbHelper.close();
            }
        });

        //Shipment button action
        Button shipment = (Button) findViewById(R.id.button_shipment);
        shipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InventoryDbHelper dbHelper = new InventoryDbHelper(DetailActivity.this);
                EditText addQty = (EditText) findViewById(R.id.shipment_edittext);
                String qtyAddText = addQty.getText().toString();
                int qtyAdd = Integer.parseInt(qtyAddText);
                if (qtyAddText.matches("") || qtyAdd < 1) {
                    Toast.makeText(DetailActivity.this, getResources().getString(R.string.error_product_shipment),
                            Toast.LENGTH_SHORT).show();
                } else {
                    int qtyChange = qtyAdd;
                    dbHelper.updateData(productId, product.getQtyAvl(), product.getQtySold(), qtyChange);
                    updateView();
                }
                dbHelper.close();
            }
        });

        //order button action
        Button order = (Button) findViewById(R.id.button_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText orderQty = (EditText) findViewById(R.id.order_edittext);
                String qtyOrderText = orderQty.getText().toString();
                int qtyOrder = Integer.parseInt(qtyOrderText);
                if (qtyOrderText.matches("") || qtyOrder < 1) {
                    Toast.makeText(DetailActivity.this, getResources().getString(R.string.error_product_order),
                            Toast.LENGTH_SHORT).show();
                } else {
                    String subject = "Order for " + product.getProductName();
                    String orderMessage = createOrderSummary(product.getProductName(), qtyOrder);
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, orderMessage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    Toast.makeText(DetailActivity.this, "Order Email Created!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //delete record button action
        Button delete = (Button) findViewById(R.id.delete_record);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InventoryDbHelper dbHelper = new InventoryDbHelper(DetailActivity.this);
                String name = product.getProductName();
                if (dbHelper.deleteSingleRecord(productId)) {
                    Toast.makeText(DetailActivity.this, name + " " + getResources().getString(R.string.success_product_delete),
                            Toast.LENGTH_SHORT).show();
                    dbHelper.close();
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, getResources().getString(R.string.error_product_delete),
                            Toast.LENGTH_SHORT).show();
                }
                dbHelper.close();
            }
        });

    }

    public void updateData() {
        InventoryDbHelper dbHelper = new InventoryDbHelper(this);
        product = dbHelper.getData(productId);
        if (product == null) {
            startActivity(new Intent(DetailActivity.this, MainActivity.class));
        }
        dbHelper.close();
    }

    public void updateView() {
        updateData();
        //Set name of current product
        TextView nameTextView = (TextView) findViewById(R.id.detail_name_textview);
        nameTextView.setText(product.getProductName());

        //Set description for current product
        TextView descTextView = (TextView) findViewById(R.id.detail_desc_textview);
        descTextView.setText(product.getProductDesc());

        //Set quantity available of current product
        TextView qtyTextView = (TextView) findViewById(R.id.detail_quantityavl_textview);
        qtyTextView.setText("Quantity Available: " + product.getQtyAvl());

        //Set quantity sold of current product
        TextView qtysoldTextView = (TextView) findViewById(R.id.detail_quantitysold_textview);
        qtysoldTextView.setText("Quantity Sold: " + product.getQtySold());

        //Set price of current product
        TextView priceTextView = (TextView) findViewById(R.id.detail_price_textview);
        priceTextView.setText("Price per unit: $" + product.getProductPrice());

        //Set Product thumbnail
        byte[] imageBytes = product.getProductImage();
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options); // convert ByteArray to bitmap
        ImageView imageView = (ImageView) findViewById(R.id.detail_image);
        imageView.setImageBitmap(bitmap);
    }

    public String createOrderSummary(String productName, int quantity) {
        String message = "Order Summary" +
                "\n Product Name: " + productName +
                "\n Quantity: " + quantity;
        return message;
    }
}
