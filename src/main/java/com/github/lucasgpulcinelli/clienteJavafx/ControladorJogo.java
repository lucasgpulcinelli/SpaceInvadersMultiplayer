package com.github.lucasgpulcinelli.clienteJavafx;

import com.github.lucasgpulcinelli.comunicacao.Acao;
import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Evento;
import com.github.lucasgpulcinelli.grafico.InterfaceGrafica;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.scene.layout.StackPane;
import com.github.lucasgpulcinelli.servidor.Main;

/**
 * ControladorJogo realiza a lógica principal do cliente, comunicando com o
 * servidor e controlando a tela e os áudios em um alto nível.
 */
public class ControladorJogo extends Thread {
    /** a interface gráfica de javaFx */
    private final InterfaceGrafica graficos;

    /** a stream de input do servidor */
    private ObjectInputStream in;
    /** a stream de output do servidor */
    private ObjectOutputStream out;
    /** o id do jogador */
    private int jogador = -1;

    /**
     * Cria um novo ControladorJogo conectado a um servidor em um painel da tela.
     * Caso o ip seja "local", um servidor local é iniciado em uma nova thread.
     * A porta no ip é opcional e o valor padrão é 8080.
     *
     * @param ipComPorta o ip do servidor a se conectar ou "local"
     * @param painel     o painel onde os gráficos estarão
     */
    public ControladorJogo(String ipComPorta, StackPane painel) {
        super();

        // inicia os gráficos
        GraficosJavafx graficosJavafx = new GraficosJavafx(painel);
        this.graficos = graficosJavafx;

        // separa a porta do ip
        String[] ipPortaSeparado = ipComPorta.split(":");

        int porta;
        if (ipPortaSeparado.length == 1) {
            // porta padrão
            porta = 8080;
        } else {
            porta = Integer.parseInt(ipPortaSeparado[1]);
        }

        String ip;
        if ("local".equals(ipPortaSeparado[0])) {
            ip = "127.0.0.1";

            // inicia o servidor local
            new Thread(() -> {
                try {
                    Main.main(new String[0]);
                } catch (InterruptedException | IOException e) {
                    graficosJavafx.printErro("Erro inicializando servidor local");
                }
            }).start();
        } else {
            ip = ipPortaSeparado[0];
        }

        // conecta com o servidor
        try {
            Socket s = new Socket(ip, porta);
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());
        } catch (UnknownHostException e) {
            graficosJavafx.printErro("Erro de host");
        } catch (IOException e) {
            graficosJavafx.printErro("Erro de I/O");
        }
    }

    /**
     * run controla todo o loop principal do jogo. É equivalente ao método
     * loopJogo de {@link clienteTerminal.Main}.
     */
    @Override
    public void run() {
        if(in == null){
            return;
        }
        try {
            jogador = (Integer) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("erro recebendo jogador");
            return;
        }

        Evento perdaDeVida = (jogador == 1) ? Evento.PERDEU_VIDA_P1 : Evento.PERDEU_VIDA_P2;
        LeitorInput li = LeitorInput.pegarLeitor();
        TocadorDeAudio ta = new TocadorDeAudio();

        while (true) {
            // a cada frame:
            try {
                if (unicoFrame(li, ta, perdaDeVida)) {
                    System.exit(0);
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * unicoFrame realiza toda a lógica de um único frame do Space Invaders
     * Multiplayer.
     *
     * @param li          leitor de input do jogo
     * @param ta          tocador de áudio do jogo
     * @param perdaDeVida evento de perda de vida associado ao jogador atual
     * @return se o jogo deve acabar agora
     * @throws IOException            se o servidor deixar de responder
     * @throws ClassNotFoundException se ocorrer um erro grave de comunicação
     *                                com o servidor
     * @throws InterruptedException   se o processo for interrompido
     */
    private boolean unicoFrame(LeitorInput li, TocadorDeAudio ta, Evento perdaDeVida)
            throws IOException, ClassNotFoundException, InterruptedException {
        // lê o estado atual do jogo
        EstadoJogo estadoJogo = (EstadoJogo) in.readObject();

        // desenha o que está acontecendo
        graficos.desenharTela(estadoJogo.getScore(jogador),
                estadoJogo.getVidas(jogador), estadoJogo.getDesenhaveis());

        // toca áudio para cada evento (que tenha um som associado)
        for (Evento e : estadoJogo.getEventos()) {
            ta.tocar(e);
        }

        // se o jogo foi perdido, para toda a execução
        if (estadoJogo.getEventos().contains(Evento.PERDEU_JOGO)) {
            graficos.printPerdeuJogo();
            return true;
        }
        // se o seu jogador perdeu vida, avisa ele e espera até ele dar a confirmação
        // para respawn
        if (estadoJogo.getEventos().contains(perdaDeVida)) {
            graficos.printPerdeuVida();
            li.esperarTiro();
            out.writeObject(Acao.ENTRAR_JOGO);
        }
        // se ganhou o nível, celebra!
        if (estadoJogo.getEventos().contains(Evento.GANHOU_NIVEL)) {
            graficos.printGanhouNivel(estadoJogo.getNivel());
            li.esperarTiro();
        }

        // no final, sempre escreve a próxima ação
        Acao a = li.getAcao();
        if (a == Acao.SAIR_JOGO) {
            return true;
        }

        out.writeObject(a);
        return false;
    }

}
