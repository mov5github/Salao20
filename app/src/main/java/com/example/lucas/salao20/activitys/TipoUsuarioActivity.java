package com.example.lucas.salao20.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.adapters.TipoUsuarioAdapter;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.tipoUsuario.FragmentTipoUsuario;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class TipoUsuarioActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //HANDLER
    private Handler handler;


    //OBJETOS
    private CadastroBasico cadastroBasico = null;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //ALERT DIALOG
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    //CONTROLES
    private boolean processandoClique = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_usuario);

        this.handler  = new Handler();
        this.mAuth = FirebaseAuth.getInstance();
        this.mAuthListener = getFirebaseAuthResultHandler();
        this.mAuth.addAuthStateListener(this.mAuthListener);

        initBundle();
        initView();
    }

    private void initBundle(){
        if (getIntent().hasExtra(CadastroBasico.getCADASTRO_BASICO())){
            Bundle bundle = getIntent().getExtras().getBundle(CadastroBasico.getCADASTRO_BASICO());

            if (bundle == null || !bundle.containsKey(CadastroBasico.getNIVEL_USUARIO()) || bundle.getDouble(CadastroBasico.getNIVEL_USUARIO()) != 1.0
                        || !bundle.containsKey(CadastroBasico.getNOME()) || bundle.get(CadastroBasico.getNOME()).toString().isEmpty()
                        || !bundle.containsKey(CadastroBasico.getSOBRENOME()) || bundle.get(CadastroBasico.getSOBRENOME()).toString().isEmpty()){
                mAuth.signOut();
            }else{
                if (cadastroBasico == null){
                    cadastroBasico = new CadastroBasico();
                }
                cadastroBasico.setNivelUsuario(bundle.getDouble(CadastroBasico.getNIVEL_USUARIO()));
                cadastroBasico.setNome(bundle.getString(CadastroBasico.getNOME()));
                cadastroBasico.setSobrenome(bundle.getString(CadastroBasico.getSOBRENOME()));
            }
        }
    }

    private void initView() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
        mToolbar.setTitle("TIPO DE USUARIO");
        mToolbar.setSubtitle("Tipo de  usuário");
        mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);

        //TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);
        String[] titles = {FragmentTipoUsuario.getTITULO()};
        mViewPager.setAdapter(new TipoUsuarioAdapter(getSupportFragmentManager(),this,titles));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));

        //ALERT DIALOG
        this.builder = new AlertDialog.Builder(this);
        //define um botão como positivo
        builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                callSplashScreenActivity();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                cadastroBasico.setTipoUsuario(null);
                processandoClique = false;
            }
        });
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() CadastroInicial232 ");

        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged CadastroInicial11");
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



    public void confirmarTipoCadastro(View view) {
        if (!this.processandoClique){
            this.processandoClique = true;
            switch (view.getId()){
                case R.id.rl_cadastro_cliente:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.CLIENTE);
                    builder.setTitle("Salvar cadastro como Cliente ?");
                    builder.setMessage("Ao criar uma conta como Cliente você podera se vincular a um ou mais salões online, para ter acesso a promoções, agendar horários com seus cabeleireiros e muito mais !");
                    break;
                case R.id.btn_cadastro_cliente:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.CLIENTE);
                    builder.setTitle("Salvar cadastro como Cliente ?");
                    builder.setMessage("Ao criar uma conta como Cliente você podera se vincular a um ou mais salões online, para ter acesso a promoções, agendar horários com seus cabeleireiros e muito mais !");
                    break;
                case R.id.rl_cadastro_salao:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.SALAO);
                    builder.setTitle("Salvar cadastro como Salão ?");
                    builder.setMessage("Ao criar uma conta como Salão você estara abrindo um salão online; podendo definir os serviços prestados, abrir uma agenda para que seus clientes possam agendar horários, adicionar os cabeleireiros que realizaram os serviços no seu salão, gerar promoções e muito mais !");
                    break;
                case R.id.btn_cadastro_salao:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.SALAO);
                    builder.setTitle("Salvar cadastro como Salão ?");
                    builder.setMessage("Ao criar uma conta como Salão você estara abrindo um salão online; podendo definir os serviços prestados, abrir uma agenda para que seus clientes possam agendar horários, adicionar os cabeleireiros que realizaram os serviços no seu salão, gerar promoções e muito mais !");
                    break;
                case R.id.rl_cadastro_profissional:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.PROFISSIONAl);
                    builder.setTitle("Salvar cadastro como Profissional ?");
                    builder.setMessage("Ao criar uma conta como Profissional você podera se vincular a um ou mais salões online ja existentes, os clientes destes salões poderam agendar horarios com você, você podera gerenciar seus serviços prestados no decorrer do mês e muito mais !");
                    break;
                case R.id.btn_cadastro_profissional:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.PROFISSIONAl);
                    builder.setTitle("Salvar cadastro como Profissional ?");
                    builder.setMessage("Ao criar uma conta como Profissional você podera se vincular a um ou mais salões online ja existentes, os clientes destes salões poderam agendar horarios com você, você podera gerenciar seus serviços prestados no decorrer do mês e muito mais !");
                    break;
                default:
                    builder.setTitle("ERRO");
                    builder.setMessage("erro!");
                    break;
            }

            this.alertDialog = builder.create();
            this.alertDialog.show();
        }
    }

    //CALL
    private void callSplashScreenActivity() {
        Bundle bundle = new Bundle();
        Bundle auxBundle = new Bundle();
        auxBundle.putString(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
        auxBundle.putString(CadastroBasico.getNOME(),cadastroBasico.getNome());
        auxBundle.putString(CadastroBasico.getSOBRENOME(),cadastroBasico.getSobrenome());
        bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
