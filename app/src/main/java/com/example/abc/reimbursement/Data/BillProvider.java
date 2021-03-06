package com.example.abc.reimbursement.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@link ContentProvider} for Pets app.
 */
public class BillProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BillProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int EXPENSES = 200;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int EXPENSES_ID = 201;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(BillContract.CONTENT_AUTHORITY, BillContract.PATH_EXPENSES, EXPENSES);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(BillContract.CONTENT_AUTHORITY, BillContract.PATH_EXPENSES + "/#", EXPENSES_ID);
    }

    /**
     * Database helper object
     */
    private BillDbHelper mBillDbHelper;

    @Override
    public boolean onCreate() {
        mBillDbHelper = new BillDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mBillDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(BillContract.BillEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EXPENSES_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BillContract.BillEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BillContract.BillEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:

                try {
                    return insertPet(uri, contentValues);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) throws ParseException {

        // Check that the name is not null
        String username = values.getAsString(BillContract.BillEntry.COLUMN_EXPENSE_NAME);
        if (username.isEmpty() || username.trim().equals("") ){
            Toast.makeText(getContext(),"Name cannot be empty" ,Toast.LENGTH_LONG).show();
            return null ;

        }


        String query = "Select * from Expenses where name ='"+ username+ "'";

        SQLiteDatabase database;// = mBillDbHelper.getReadableDatabase();
        /*Cursor cursor = database.rawQuery( query,null);
        cursor.moveToFirst();
        if(cursor.getCount()>=1) {
            Toast.makeText(getContext(), "Enter another name ", Toast.LENGTH_LONG).show();
            return null;
        }*/


          String startdate = values.getAsString(BillContract.BillEntry.COLUMN_EXPENSE_STARTDATE);
            String enddate = values.getAsString(BillContract.BillEntry.COLUMN_EXPENSE_ENDDATE);
        if (startdate!=null && startdate.trim()!=null ||(enddate!=null && enddate.trim()!=null)) {

            Date date1=null;
            Date date2=null;
            try
            {
                date1 = new SimpleDateFormat("dd/MM/yyyy").parse(startdate);
                date2 = new SimpleDateFormat("dd/MM/yyyy").parse(enddate);
                if (date1.after(date2)) {
                    Toast.makeText(getContext(), "Select dates appropriately", Toast.LENGTH_LONG).show();
                    return null;
                }
                if (date2.after(date1)) {

                }
                if (date1.equals(date2)) {

                }
            }catch (Exception ex) {


            }



        }





        // Check that the gender is valid


        // No need to check the breed, any value is valid (including null).

        // Get writeable database
        database = mBillDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(BillContract.BillEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }




    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mBillDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BillContract.BillEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EXPENSES_ID:
                // Delete a single row given by the ID in the URI
                selection = BillContract.BillEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BillContract.BillEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }



    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EXPENSES:
                return BillContract.BillEntry.CONTENT_LIST_TYPE;
            case EXPENSES_ID:
                return BillContract.BillEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    public void addEntry( String name, byte[] image) throws SQLiteException {
        SQLiteDatabase database = mBillDbHelper.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(BillContract.BillEntry.COLUMN_EXPENSE_KEY_NAME,    name);

        cv.put(BillContract.BillEntry.COLUMN_EXPENSE_KEY_IMAGE,   image);
        database.insert(String.valueOf(EXPENSES), null, cv );
    }

}


