package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.adapters.HomeAdapter;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCabeleireiro;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCliente;
import com.example.lucas.salao20.fragments.home.FragmentHomeSalaoAgendas;
import com.example.lucas.salao20.fragments.home.FragmentHomeSalaoDados;
import com.example.lucas.salao20.fragments.home.FragmentHomeSalaoPromocoes;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE REF


    //PROGRESDIALOG
    private ProgressDialog progressDialog;

    //CONTROLES
    private static boolean homeActivityAtiva;

    //OBJETOS
    private static CadastroBasico cadastroBasico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();

        receberBundle();

        initControles();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
            mSlidingTabLayout.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","HomeActivity() onStart()");
        homeActivityAtiva = true;
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","HomeActivity() onStop()");
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","HomeActivity() onDestroy()");
        homeActivityAtiva = false;
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
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
        mToolbar.setTitle("HOME");
        //TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);

        if (cadastroBasico == null || cadastroBasico.getNivelUsuario() == null || cadastroBasico.getNivelUsuario() < 3.0){
            this.mAuth.signOut();
        }else {
            if (cadastroBasico.getTipoUsuario() == null || cadastroBasico.getTipoUsuario().isEmpty()){
                this.mAuth.signOut();
            }else{
                switch (cadastroBasico.getTipoUsuario()){
                    case TipoUsuarioENUM.SALAO:
                        mToolbar.setSubtitle("Home do salão");
                        mToolbar.setLogo(R.mipmap.ic_launcher);
                        String[] titles = {FragmentHomeSalaoDados.getTitulo(), FragmentHomeSalaoAgendas.getTitulo(), FragmentHomeSalaoPromocoes.getTitulo()};
                        mViewPager.setAdapter(new HomeAdapter(getSupportFragmentManager(),this,titles, cadastroBasico.getTipoUsuario()));
                        break;
                    case TipoUsuarioENUM.PROFISSIONAl:
                        mToolbar.setSubtitle("Home do cabeleireiro");
                        mToolbar.setLogo(R.mipmap.ic_launcher);
                        String[] titles2 = {FragmentBasicoCabeleireiro.getTitulo()};
                        mViewPager.setAdapter(new HomeAdapter(getSupportFragmentManager(),this,titles2, cadastroBasico.getTipoUsuario()));
                        break;
                    case TipoUsuarioENUM.CLIENTE:
                        mToolbar.setSubtitle("Home do cliente");
                        mToolbar.setLogo(R.mipmap.ic_launcher);
                        String[] titles3 = {FragmentBasicoCliente.getTitulo()};
                        mViewPager.setAdapter(new HomeAdapter(getSupportFragmentManager(),this,titles3, cadastroBasico.getTipoUsuario()));
                        break;
                    default:
                        mAuth.signOut();
                        break;
                }
            }
        }
        setSupportActionBar(mToolbar);

        //PROGREAS DIALOG
        if (this.progressDialog == null){
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setMessage("Sincronizando dados na nuvem ...");
        }

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
            mSlidingTabLayout.setViewPager(mViewPager);
        }
        mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
    }

    private void initControles(){
        //TODO
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() CadastroInicial ");

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

    private void receberBundle(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(CadastroBasico.getNIVEL_USUARIO())){
            if (cadastroBasico == null){
                cadastroBasico = new CadastroBasico();
            }
            cadastroBasico.setNivelUsuario(bundle.getDouble(CadastroBasico.getNIVEL_USUARIO()));
        }
        if (bundle != null && bundle.containsKey(CadastroBasico.getTIPO_USUARIO())){
            if (cadastroBasico == null){
                cadastroBasico = new CadastroBasico();
            }
            cadastroBasico.setTipoUsuario(bundle.getString(CadastroBasico.getTIPO_USUARIO()));
        }
        if (bundle != null && bundle.containsKey(CadastroBasico.getCODIGO_UNICO())){
            if (cadastroBasico == null){
                cadastroBasico = new CadastroBasico();
            }
            cadastroBasico.setCodigoUnico(bundle.getString(CadastroBasico.getCODIGO_UNICO()));
        }

    }

    private void removerFirebaseEvents(){
        //TODO
    }

    //CALL
    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
