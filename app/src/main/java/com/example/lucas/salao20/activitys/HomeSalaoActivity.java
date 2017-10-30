package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.adapters.HomeClienteAdapter;
import com.example.lucas.salao20.adapters.HomeSalaoAdapter;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.fragments.home.profissional.FragmentHomeProfissionalAgendas;
import com.example.lucas.salao20.fragments.home.profissional.FragmentHomeProfissionalCadeiras;
import com.example.lucas.salao20.fragments.home.profissional.FragmentHomeProfissionalDados;
import com.example.lucas.salao20.fragments.home.salao.FragmentHomeSalaoAgendas;
import com.example.lucas.salao20.fragments.home.salao.FragmentHomeSalaoDados;
import com.example.lucas.salao20.fragments.home.salao.FragmentHomeSalaoPromocoes;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.geral.CadastroComplementar;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

public class HomeSalaoActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE REF
    private DatabaseReference refRaiz;

    //FIREBASE VEL
    private ValueEventListener valueEventListenerSetarToolbar;

    //PROGRESDIALOG
    private ProgressDialog progressDialog;

    //CONTROLES
    private static boolean homeSalaoActivityAtiva;

    //OBJETOS
    private static CadastroBasico cadastroBasico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_salao);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();

        receberBundle();

        initControles();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && mToolbar != null && mSlidingTabLayout != null){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
            mSlidingTabLayout.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","HomeSalaoActivity() onStart()");
        homeSalaoActivityAtiva = true;
        mAuth.addAuthStateListener(mAuthListener);
        setarToolbar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","HomeSalaoActivity() onStop()");
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","HomeSalaoActivity() onDestroy()");
        homeSalaoActivityAtiva = false;
        removerFirebaseEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
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
                callLoginActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        if (cadastroBasico == null || cadastroBasico.getNivelUsuario() == null || cadastroBasico.getNivelUsuario() < 3.0){
            this.mAuth.signOut();
        }else {
            //TOOLBAR
            mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
            mToolbar.setTitle("HOME SALAO");
            mToolbar.setSubtitle("Código único do salão #");
            mToolbar.setLogo(R.mipmap.ic_launcher);
            setSupportActionBar(mToolbar);

            //TABS
            mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);
            String[] titles = {FragmentHomeSalaoDados.getTITULO(), FragmentHomeSalaoAgendas.getTITULO(), FragmentHomeSalaoPromocoes.getTITULO()};
            mViewPager.setAdapter(new HomeSalaoAdapter(getSupportFragmentManager(),this,titles));

            //PROGREAS DIALOG
            if (this.progressDialog == null){
                this.progressDialog = new ProgressDialog(this);
                this.progressDialog.setMessage("Sincronizando dados na nuvem ...");
            }

            //SLIDING TAB LAYOUT
            mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
            mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setViewPager(mViewPager);
            mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
            mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));

            mViewPager.setCurrentItem(1);
        }
    }

    private void initControles(){
        //TODO
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() HOME ");

        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged HOME");
                if(mAuth.getCurrentUser() == null){
                    Log.i("script","getFirebaseAuthResultHandler() getCurrentUser() == null HOME");
                    callLoginActivity();
                }else if (mAuth.getCurrentUser().getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() uid == null HOME");
                    mAuth.signOut();
                }
            }
        };
        return( callback );
    }

    private void receberBundle(){
        if (cadastroBasico == null) {
            cadastroBasico = new CadastroBasico();
        }
        if (mAuth.getCurrentUser() == null){
            Log.i("script","inicio user null");
        }else{
            Log.i("script","inicio user not null");
            if (getIntent().hasExtra(CadastroBasico.getCADASTRO_BASICO())){
                Log.i("script", "extra != null");
                Bundle bundle = getIntent().getExtras().getBundle(CadastroBasico.getCADASTRO_BASICO());

                if (bundle != null && bundle.containsKey(CadastroBasico.getCODIGO_UNICO())){
                    cadastroBasico.setCodigoUnico(bundle.getString(CadastroBasico.getCODIGO_UNICO()));
                }else {
                    Log.i("script", "cod unico == null");
                    mAuth.signOut();
                }
                if (bundle != null && bundle.containsKey(CadastroBasico.getNIVEL_USUARIO())){
                    cadastroBasico.setNivelUsuario(bundle.getDouble(CadastroBasico.getNIVEL_USUARIO()));
                }else {
                    Log.i("script", "nivel usuario == null");
                    mAuth.signOut();
                }
                if (bundle != null && bundle.containsKey(CadastroBasico.getUSER_METADATA_UID())){
                    cadastroBasico.setUserMetadataUid(bundle.getString(CadastroBasico.getUSER_METADATA_UID()));
                }else {
                    Log.i("script", "user metadata uid == null");
                    mAuth.signOut();
                }
            }else {
                Log.i("script", "extra == null");
                mAuth.signOut();
            }
            exibirCadastroBAsico();
        }
    }

    private void removerFirebaseEvents(){}

    private void setarToolbar(){
        if (this.refRaiz == null){
            this.refRaiz = LibraryClass.getFirebase();
        }
        if (this.valueEventListenerSetarToolbar == null){
            this.valueEventListenerSetarToolbar = new ValueEventListener(){

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.exists() && dataSnapshot.getValue() != null){
                        String nomeSalao = dataSnapshot.getValue(String.class);
                        mToolbar.setTitle(nomeSalao.substring(0,1).toUpperCase().concat(nomeSalao.substring(1)));
                    }
                    refRaiz.child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(cadastroBasico.getUserMetadataUid()).child(CadastroBasico.getCODIGO_UNICO()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.exists() && dataSnapshot.getValue() != null){
                                mToolbar.setSubtitle("Código único do salão #"+dataSnapshot.getValue(String.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("script","valueEventListenerSetarToolbar onCancelled");
                            if (homeSalaoActivityAtiva){
                                setarToolbar();
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("script","valueEventListenerSetarToolbar onCancelled");
                    if (homeSalaoActivityAtiva){
                        setarToolbar();
                    }
                }
            };
        }
        refRaiz.child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(cadastroBasico.getUserMetadataUid()).child(CadastroComplementar.getCADASTRO_COMPLEMENTAR()).child(CadastroComplementar.getNOME_SALAO()).addValueEventListener(this.valueEventListenerSetarToolbar);
    }


    //CALL
    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void exibirCadastroBAsico(){
        String cadastroB = "HOME CADASTRO BASICO \n";
        if (this.cadastroBasico.getTipoUsuario() != null && !this.cadastroBasico.getTipoUsuario().isEmpty()){
            cadastroB += "tipo user = "+this.cadastroBasico.getTipoUsuario()+"\n";
        }
        if (this.cadastroBasico.getCodigoUnico() != null){
            cadastroB += "cod unico = "+this.cadastroBasico.getCodigoUnico()+"\n";
        }
        if (this.cadastroBasico.getNivelUsuario() != null){
            cadastroB += "nivel user = "+this.cadastroBasico.getNivelUsuario().toString()+"\n";
        }
        if (this.cadastroBasico.getUserMetadataUid() != null && !this.cadastroBasico.getUserMetadataUid().isEmpty()){
            cadastroB += "usermetadatauid = "+this.cadastroBasico.getUserMetadataUid()+"\n";
        }
        if (this.cadastroBasico.getNome() != null && !this.cadastroBasico.getNome().isEmpty()){
            cadastroB += "nome = "+this.cadastroBasico.getNome()+"\n";
        }
        if (this.cadastroBasico.getSobrenome() != null && !this.cadastroBasico.getSobrenome().isEmpty()){
            cadastroB += "sobrenome = "+this.cadastroBasico.getSobrenome()+"\n";
        }
        Log.i("script", cadastroB);
    }

    //GETERS SETTERS
    public static CadastroBasico getCadastroBasico() {
        return cadastroBasico;
    }

}
