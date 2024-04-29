package com.cw.services;

import com.cw.dao.ProcessoDAO;
import com.cw.dao.RegistroDAO;
import com.cw.dao.RegistroJpaDAO;
import com.cw.database.JPAUtil;
import com.cw.models.*;
import com.github.britooo.looca.api.core.Looca;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.text.SimpleDateFormat;
import java.util.*;

public class AtualizarRegistro extends TimerTask {
    private Sessao sessao;
    private Alerta alerta;

    private Looca looca = new Looca();
    private SystemInfo oshi = new SystemInfo();
    private ProcessoDAO processoDAO = new ProcessoDAO();

    private Boolean registrarProcessos = true;

    public AtualizarRegistro(Sessao sessao, Alerta alerta) {
        this.sessao = sessao;
        this.alerta = alerta;
    }

    public void run() {

        Date date = new Date();

        String padrao = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatador = new SimpleDateFormat(padrao);

        String padraoSql = formatador.format(date);


        try {

             EntityManager em = JPAUtil.getEntityManager();

             RegistroJpaDAO registroJpaDAO = new RegistroJpaDAO(em);



            RegistroJpa registro = new RegistroJpa(
                    padraoSql,
                    looca.getProcessador().getUso()*10,
                    looca.getMemoria().getEmUso(),
                    looca.getMemoria().getDisponivel()
            );

            em.getTransaction().begin();

            registroJpaDAO.registrar(registro);

            em.getTransaction().commit();
            em.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void registrarProcessos(Registro r) {
        if (!alerta.getRegistrarProcessos()) return;

        for (OSProcess processo : oshi.getOperatingSystem().getProcesses()) {
            if ((!processo.getPath().contains("C:\\Windows\\System32\\") && !processo.getPath().isEmpty())) {
                Processo p = new Processo(
                        processo.getName(),
                        processo.getPath(),
                        processo.getResidentSetSize(),
                        r.getIdRegistro());

                processoDAO.inserirProcesso(p);
            }
        }

        alerta.setRegistrarProcessos(false);
        new Timer().schedule(new IntervaloRegistroProcessos(alerta), 15000);

        alerta.listarProcessosEmAlerta(processoDAO.buscarDezProcessosComMaisMemoria(r), r);
    }

    public void exibirUltimoRegistro(Registro r, String[] alerta) {
        System.out.println("""
                ----------------------------
                Registro %s
                ----------------------------
                Uso de CPU: %.2f%% %s
                Uso de RAM: %.2f%% %s
                ----------------------------
                """.formatted(
                        r.getDtHora(),
                        r.getUsoCpu() > 100.0 ? 100.0 : r.getUsoCpu(),
                        alerta[0].equals("cpu") ? "⚠ ALERTA ⚠" : "",
                        Conversor.converterPorcentagem((r.getDisponivelRam()+r.getUsoRam()), r.getUsoRam()),
                        alerta[1].equals("ram") ? "⚠ ALERTA ⚠" : ""
        ));
    }
}

