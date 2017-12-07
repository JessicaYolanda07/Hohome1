package com.example.corei5.hohome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class Contactos extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private GoogleApiClient api;
    private final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        registerService();
        registerEvents();
    }

    private void registerEvents() {
        Button btn = (Button)this.findViewById(R.id.google);
        btn.setOnClickListener(this);
    }

    private void registerService() {
        GoogleSignInOptions options = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        api = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "EROR ", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.google){
            Intent google = Auth.GoogleSignInApi.getSignInIntent(api);
            this.startActivityForResult(google,RC_SIGN_IN );

        }
    }
    @Override
    public void onActivityResult(int r1, int r2,Intent data ) {
        super.onActivityResult(r1,r2, data);
        if (r1 == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            loadData(result);
        }
    }

    private void loadData(GoogleSignInResult result) {
        if(result.isSuccess()) {
            GoogleSignInAccount datos =  result.getSignInAccount();
            TextView txt = (TextView)this.findViewById(R.id.correo);
            txt.setText(datos.getEmail());
        }else{
            Toast.makeText(this, "ERROR no conoces el password", Toast.LENGTH_SHORT).show();
        }
    }


}
