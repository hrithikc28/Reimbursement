package com.example.abc.reimbursement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner mySpinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Main2Activity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
        switch(i)
        {
            case (1) :
            {


                Intent intent = new Intent (Main2Activity.this , Pop.class );
                startActivity(intent);
                break;
            }

            case (2):
            {

                Intent intent = new Intent (Main2Activity.this , Travel.class );
                startActivity(intent);
                break;




            }
            case (4):
            {

                Intent intent = new Intent (Main2Activity.this , TeamExpenseActivity.class );
                startActivity(intent);
                break;




            }
            case (5):
            {

                Intent intent = new Intent (Main2Activity.this , MiscellaneousExpenseActivity.class );
                startActivity(intent);
                break;




            }
            default :
            {
                Toast.makeText(this, "Slct other", Toast.LENGTH_SHORT).show();
                break;

            }

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}