package com.example.navendu.inventoryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navendu.inventoryapp.data.InventoryDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_main);

        //refresh list view
        final ProductAdapter productAdapter = refreshView();

        //Intent to View and change limited details of a product
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long viewId = view.getId();

                if (viewId == R.id.button_sale_list) {
                    Toast.makeText(MainActivity.this, "Testing", Toast.LENGTH_SHORT).show();
                } else {
                Product productDetail = productAdapter.getItem(i);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Log.v("MainActivity", productDetail.toString());
                int productId = productDetail.getDbId();
                intent.putExtra("productParcel", productId);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            startActivity(new Intent(this, AddProductActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            refreshView();
            return true;
        } else if (id == R.id.action_delete_all) {
            deleteAllData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Populating list view with updated data
    public ProductAdapter refreshView() {
        updateData();
        TextView emptyText = (TextView) findViewById(R.id.list_empty);
        if (products.size() > 0) {
            emptyText.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.VISIBLE);
        } /**/
        ProductAdapter productAdapter = new ProductAdapter(this, products);
        listView.setAdapter(productAdapter);
        return productAdapter;
    }

    //Fetching data
    public void updateData() {
        InventoryDbHelper dbHelper = new InventoryDbHelper(this);
        products = new ArrayList<>();
        products = dbHelper.getAllData();
        dbHelper.close();
        Toast.makeText(this, getResources().getString(R.string.success_records_refresh), Toast.LENGTH_SHORT).show();
    }

    //Deleting data
    public void deleteAllData() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        InventoryDbHelper dbHelper = new InventoryDbHelper(MainActivity.this);
                        int count = dbHelper.deleteAllEntries();
                        Toast.makeText(MainActivity.this, count + getResources()
                                .getString(R.string.success_records_all_deleted), Toast.LENGTH_SHORT).show();
                        dbHelper.close();
                        refreshView();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setMessage(getResources().getString(R.string.dialog_delete_confirm_all)).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
