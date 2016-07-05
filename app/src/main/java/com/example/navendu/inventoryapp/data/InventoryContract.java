package com.example.navendu.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Defines tables and columns required to maintain a product inventory
 */
public class InventoryContract {

    /* Inner class that defines the table contents of table*/
    public static final class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "product";

        //Name , stored as String
        public static final String COLUMN_NAME = "name";
        //Image, stored as BLOB
        public static final String COLUMN_IMAGE = "image";
        //Short Description, stored as text
        public static final String COLUMN_SHORT_DESC = "short_desc";
        // Price per quantity for the product, stored as real;
        public static final String COLUMN_PRICE = "price";
        //quantity of items available in the inventory
        public static final String COLUMN_QTY_AVL = "qty_avl";
        //quantity of items sold from the store
        public static final String COLUMN_QTY_SOLD = "qty_sold";

    }
}
