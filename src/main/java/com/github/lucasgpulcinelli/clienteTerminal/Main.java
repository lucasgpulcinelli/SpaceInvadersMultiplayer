package com.github.lucasgpulcinelli.clienteTerminal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.github.lucasgpulcinelli.comunicacao.Acao;
import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Evento;
import com.github.lucasgpulcinelli.grafico.InterfaceGrafica;

/**
 * Main implementa o cliente de terminal do Space Invaders Multiplayer.
 *
 * @author Lucas Eduardo Gulka Pulcinelli, nUSP 12547336
 *
 * @see servidor.Main O método principal do servidor
 * @see servidor.ComunicadorJogo
 * @see LeitorInput
 */
public class Main {
    private static final InterfaceGrafica graficos = new GraficoTerminal();

    /**
     * main executa o código completo do cliente do Space Invaders, com uma
     * interface de terminal amigável.
     * Para jogar, uma instância de servidor {@link servidor.Main} deve estar ativa
     * em 127.0.0.1:8080.
     * Os comandos de jogo são:
     * 'w' ou espaço para atirar,
     * 'a' para se mover para a esquerda,
     * 'd' para se mover para a direita,
     * 'q' para sair do jogo.
     *
     * @param args ip do servidor 
     * @throws UnknownHostException   caso não exista servidor em 127.0.0.1:8080
     * @throws IOException            caso o servidor pare de responder
     * @throws ClassNotFoundException caso haja um erro muito grande na comunicação
     *                                e objetos incorretos estejam sendo recebidos
     */
    public static void main(String[] args)
            throws UnknownHostException, IOException, ClassNotFoundException {

        String ip;
        if(args.length == 0){
            ip = "127.0.0.1";
        } else{
            ip = args[0];
        }
        Socket s = new Socket(ip, 8080);
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

        try {
            loopJogo(in, out);
        } catch (InterruptedException e) {
            System.exit(0);
        }
        s.close();
    }

    /**
     * loopJogo executa o código principal que a thread de display e comunicação
     * estará exercendo.
     *
     * @param in  é a stream de objetos de entrada, que deve ter inicialmente um
     *            inteiro com o número do jogado associado, e, posteriormente, a
     *            cada 1/30s, um {@link comunicacao.EstadoJogo} do frame atual.
     * @param out é a stream de objetos de saída, que terá uma nova
     *            {@link comunicacao.Acao} escrita a cada 1/30s
     * @throws InterruptedException   caso a thread tenha sido interrompida
     * @throws ClassNotFoundException caso haja um erro grande na comunicação
     * @throws IOException            caso o servidor deixe de responder
     */
    private static void loopJogo(ObjectInputStream in, ObjectOutputStream out)
            throws InterruptedException, ClassNotFoundException, IOException {

        LeitorInput li = new LeitorInput();
        li.start();

        graficos.printSplashScreen();
        li.esperarTiro();

        int jogador = (Integer) in.readObject();

        //evento de perda de vida
        Evento perdaDeVida = (jogador == 1) ? Evento.PERDEU_VIDA_P1 : Evento.PERDEU_VIDA_P2;

        while (true) {
            // a cada frame:

            // lê o estado atual do jogo
            EstadoJogo estadoJogo = (EstadoJogo) in.readObject();

            // desenha o que está acontecendo
            graficos.desenharTela(estadoJogo.getScore(jogador),
                    estadoJogo.getVidas(jogador), estadoJogo.getDesenhaveis());

            // se o jogo foi perdido, para toda a execução
            if (estadoJogo.getEventos().contains(Evento.PERDEU_JOGO)) {
                graficos.printPerdeuJogo();
                li.parar();
                return;
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
            if(a == Acao.SAIR_JOGO){
                return;
            }
            out.writeObject(a);
        }
    }
}
