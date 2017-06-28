package com.example.lucas.salao20.geral.geral;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.geral.profissional.ExpedienteProfissional;
import com.example.lucas.salao20.geral.profissional.ServicosProfissional;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 09/05/2017.
 */

public class Profissional {
    private String idProfissional;
    private Long dataInsercao;
    private String uIDSalao;
    private String uIDProfissional;
    private ServicosProfissional servicosProfissional;
    private ExpedienteProfissional expedienteProfissional;
    private CadastroComplementar cadastroComplementar;
    private CadastroBasico cadastroBasico;

    //ENUM
    @Exclude
    private static final String DATA_DE_INSERCAO = "dataDeInserção";
    @Exclude
    private static final String UID_SALÃO = "uidSalão";
    @Exclude
    private static final String UID_PROFISSIONAL = "uidProfissional";
    @Exclude
    private static final String SERVICOS = "serviços";
    @Exclude
    private static final String EXPEDIENTE = "expediente";


    public Profissional() {
    }

    public Profissional(int foto) {
        this.cadastroComplementar = new CadastroComplementar();
        this.cadastroComplementar.setFoto(foto);
    }

    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        /*if (this.nome != null && !this.nome.isEmpty()){
            result.put(NOME,this.nome);
        }
        if (this.icone != null){
            result.put(ICONE,this.icone);
        }
        if (this.duracao != null){
            result.put(DURACAO,this.duracao);
        }
        if (this.preco != null){
            result.put(PRECO,this.preco);
        }
        if (this.descricao != null && !this.descricao.isEmpty()){
            result.put(DESCRICAO,this.descricao);
        }
        if (this.dataInsercao != null){
            result.put(DATA_DE_INSERCAO,this.dataInsercao);
        }*/
        return result;
    }

    //GETTERS SETTERS
    public String getIdProfissional() {
        return idProfissional;
    }
    public void setIdProfissional(String idProfissional) {
        this.idProfissional = idProfissional;
    }

    public Long getDataInsercao() {
        return dataInsercao;
    }
    public void setDataInsercao(Long dataInsercao) {
        this.dataInsercao = dataInsercao;
    }

    public String getuIDSalao() {
        return uIDSalao;
    }
    public void setuIDSalao(String uIDSalao) {
        this.uIDSalao = uIDSalao;
    }

    public String getuIDProfissional() {
        return uIDProfissional;
    }
    public void setuIDProfissional(String uIDProfissional) {
        this.uIDProfissional = uIDProfissional;
    }

    public void setServicosProfissional(ServicosProfissional servicosProfissional) {
        this.servicosProfissional = servicosProfissional;
    }
    public ServicosProfissional getServicosProfissional() {
        return servicosProfissional;
    }

    public ExpedienteProfissional getExpedienteProfissional() {
        return expedienteProfissional;
    }
    public void setExpedienteProfissional(ExpedienteProfissional expedienteProfissional) {
        this.expedienteProfissional = expedienteProfissional;
    }

    public CadastroComplementar getCadastroComplementar() {
        return cadastroComplementar;
    }
    public void setCadastroComplementar(CadastroComplementar cadastroComplementar) {
        this.cadastroComplementar = cadastroComplementar;
    }

    public CadastroBasico getCadastroBasico() {
        return cadastroBasico;
    }
    public void setCadastroBasico(CadastroBasico cadastroBasico) {
        this.cadastroBasico = cadastroBasico;
    }

    public static String getDataDeInsercao() {
        return DATA_DE_INSERCAO;
    }

    public static String getUidSalão() {
        return UID_SALÃO;
    }

    public static String getUidProfissional() {
        return UID_PROFISSIONAL;
    }

    public static String getSERVICOS() {
        return SERVICOS;
    }

    public static String getEXPEDIENTE() {
        return EXPEDIENTE;
    }
}
