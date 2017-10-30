package com.example.lucas.salao20.activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.google.firebase.auth.FirebaseAuth;

public class TesteActivity extends CommonActivity {
    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro_inicial,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_ajuda:
                Toast.makeText(this,String.valueOf(id), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_logout:
                mAuth.signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initUser() {

    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() CadastroInicial teste");

        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged CadastroInicial");
                if(mAuth.getCurrentUser() == null){
                    Log.i("script","getFirebaseAuthResultHandler() getCurrentUser() == null CadastroInicial");
                    callLoginActivity();
                }else if (mAuth.getCurrentUser().getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() uid == null CadastroInicial");
                    mAuth.signOut();
                }
            }
        };
        return( callback );
    }

    public void teste(View view) {
        mAuth.signOut();
    }
}
