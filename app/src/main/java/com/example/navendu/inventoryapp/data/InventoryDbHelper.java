package com.example.navendu.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.navendu.inventoryapp.Product;
import com.example.navendu.inventoryapp.data.InventoryContract.ProductEntry;

import java.util.ArrayList;

/**
 * Manages a local database for Inventory data
 */
public class InventoryDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "inventory.db";
    private static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();
    //whenever we change the database schema, we must increment the database version
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ProductEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_IMAGE + " BLOB NOT NULL, " +
                ProductEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                ProductEntry.COLUMN_QTY_AVL + " INTEGER NOT NULL, " +
                ProductEntry.COLUMN_QTY_SOLD + " INTEGER NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(ProductEntry.TABLE_NAME);
    }

    // Insert data in the table while creating product
    public boolean insertData(String productName, byte[] image, String desc, double price, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newProductValues = new ContentValues();
        Long productRowId;
        boolean result;

        newProductValues.put(ProductEntry.COLUMN_NAME, productName);
        newProductValues.put(ProductEntry.COLUMN_IMAGE, image);
        newProductValues.put(ProductEntry.COLUMN_SHORT_DESC, desc);
        newProductValues.put(ProductEntry.COLUMN_PRICE, price);
        newProductValues.put(ProductEntry.COLUMN_QTY_AVL, qty);
        newProductValues.put(ProductEntry.COLUMN_QTY_SOLD, 0);
        productRowId = db.insert(ProductEntry.TABLE_NAME, null, newProductValues);
        if (productRowId != -1) {
            Log.i(LOG_TAG, "Product inserted successfully");
            result = true;
        } else {
            Log.i(LOG_TAG, "Product insert failed");
            result = false;
        }
        db.close();
        return result;
    }

    //Get Data from table
    public Product getData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.query(
                ProductEntry.TABLE_NAME,
                null, // all the columns
                ProductEntry._ID + " = " + id,
                null, //values for where clause
                null, //column to group by
                null, //column to filter by row groups
                null //sort order
        );
        result.moveToFirst();
        int productId = result.getInt(result.getColumnIndex(ProductEntry._ID));
        String productName = result.getString(result.getColumnIndex(ProductEntry.COLUMN_NAME));
        String productDesc = result.getString(result.getColumnIndex(ProductEntry.COLUMN_SHORT_DESC));
        int currentQuantityAvl = result.getInt(result.getColumnIndex(ProductEntry.COLUMN_QTY_AVL));
        int currentQuantitySold = result.getInt(result.getColumnIndex(ProductEntry.COLUMN_QTY_SOLD));
        byte[] image = result.getBlob(result.getColumnIndex(ProductEntry.COLUMN_IMAGE));
        double price = result.getDouble(result.getColumnIndex(ProductEntry.COLUMN_PRICE));
        Product product = new Product(productId, productName, image, productDesc
                , price, currentQuantityAvl, currentQuantitySold);
        db.close();
        return product;
    }

    //Delete all table entries
    public int deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result;
        result = db.delete(ProductEntry.TABLE_NAME, null, null);
        db.close();
        return result;
    }

    //Delete one table entry
    public boolean deleteSingleRecord(int recordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result;
        result = db.delete(ProductEntry.TABLE_NAME, ProductEntry._ID + "= ?", new String[]{Long.toString(recordId)});
        db.close();
        return (result > 0);
    }

    //Update product quantity data in the table
    public void updateData(int recordId, int qtyavl, int qtysold, int quantityPosChange) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(ProductEntry._ID, recordId);
        updatedValues.put(ProductEntry.COLUMN_QTY_AVL, qtyavl + quantityPosChange);
        if (quantityPosChange < 0) {
            updatedValues.put(ProductEntry.COLUMN_QTY_SOLD, qtysold - quantityPosChange);
        }
        db.update(ProductEntry.TABLE_NAME, updatedValues, ProductEntry._ID + "= ?", new String[]{Long.toString(recordId)});
    }

    public ArrayList<Product> getAllData() {
        ArrayList<Product> productList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor getList = db.rawQuery("SELECT * FROM " + ProductEntry.TABLE_NAME, null);
        getList.moveToFirst();
        while (!getList.isAfterLast()) {
            int productId = getList.getInt(getList.getColumnIndex(ProductEntry._ID));
            String productName = getList.getString(getList.getColumnIndex(ProductEntry.COLUMN_NAME));
            String productDesc = getList.getString(getList.getColumnIndex(ProductEntry.COLUMN_SHORT_DESC));
            int currentQuantityAvl = getList.getInt(getList.getColumnIndex(ProductEntry.COLUMN_QTY_AVL));
            int currentQuantitySold = getList.getInt(getList.getColumnIndex(ProductEntry.COLUMN_QTY_SOLD));
            byte[] image = getList.getBlob(getList.getColumnIndex(ProductEntry.COLUMN_IMAGE));
            double price = getList.getDouble(getList.getColumnIndex(ProductEntry.COLUMN_PRICE));
            productList.add(new Product(productId, productName, image, productDesc
                    , price, currentQuantityAvl, currentQuantitySold));
            getList.moveToNext();
        }
        return productList;
    }
}
