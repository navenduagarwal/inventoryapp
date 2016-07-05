package com.example.navendu.inventoryapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.navendu.inventoryapp.data.InventoryDbHelper;

import java.io.ByteArrayOutputStream;

/**
 * Created by navendu on 7/4/2016.
 */
public class AddProductActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddProductActivity.class.getSimpleName();
    private int REQUEST_IMAGE_CAPTURE = 1;
    private byte[] image;

    /*
    This method show the uploaded image in the activity layout
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView testImage = (ImageView) findViewById(R.id.uploaded_image);
            testImage.setImageBitmap(imageBitmap);

            // Convert Bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            image = stream.toByteArray();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final InventoryDbHelper dbHelper = new InventoryDbHelper(this);

        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextProductName = (EditText) findViewById(R.id.product_name);
                EditText editTextProductDesc = (EditText) findViewById(R.id.product_desc);
                EditText editTextQuantity = (EditText) findViewById(R.id.quantity);
                EditText editTextPrice = (EditText) findViewById(R.id.price);
                String productName = editTextProductName.getText().toString();
                if (productName.matches("")) {
                    Toast.makeText(AddProductActivity.this, getResources().getString(R.string.error_product_name),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String productDesc = editTextProductDesc.getText().toString();
                if (productDesc.matches("")) {
                    Toast.makeText(AddProductActivity.this, getResources().getString(R.string.error_product_desc),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String mQuantity = editTextQuantity.getText().toString();
                if (mQuantity.matches("")) {
                    Toast.makeText(AddProductActivity.this, getResources().getString(R.string.error_product_quantity),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity = Integer.parseInt(mQuantity);
                String mPrice = editTextPrice.getText().toString();
                if (mPrice.matches("")) {
                    Toast.makeText(AddProductActivity.this, getResources().getString(R.string.error_product_price),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                double price = Double.parseDouble(mPrice);
                // Check if image has been set or not
                if (image == null) {
                    Toast.makeText(AddProductActivity.this, getResources().getString(R.string.error_product_image), Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean result = dbHelper.insertData(productName, image, productDesc, price, quantity);
                if (result) {
                    Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(AddProductActivity.this, getResources().getString(R.string.success_product_db_insert), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AddProductActivity.this, getResources().getString(R.string.error_product_db_insert),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //capture image
        Button addImage = (Button) findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

    }

}
