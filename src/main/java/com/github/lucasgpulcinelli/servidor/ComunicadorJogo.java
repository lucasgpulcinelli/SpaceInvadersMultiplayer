package com.github.lucasgpulcinelli.servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.lucasgpulcinelli.comunicacao.Acao;
import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Evento;
import com.github.lucasgpulcinelli.sistema.Canhao;
import com.github.lucasgpulcinelli.sistema.Personagem;
import com.github.lucasgpulcinelli.sistema.TelaJogo;

/**
 * ComunicadorJogo implementa a forma de receber e enviar informações do
 * servidor para um cliente e vice-versa.
 *
 * Ele funciona criando uma nova thread que escuta em um ServerSocket e espera
 * uma conexão, enviando o estado do jogo a cada 1/30s e recebendo ações do
 * jogador.
 *
 * A thread, depois de conectada a um cliente, pode criar novos canhões e os
 * conectar às ações do jogador.
 *
 * @see sistema.Canhao
 * @see comunicacao.Acao
 * @see comunicacao.EstadoJogo
 */
public class ComunicadorJogo extends Thread {
    private final ServerSocket servidor;
    private boolean conectado = false;
    private final int jogador;


    /**
     * Cria um novo comunicador de jogo para um determinado jogador.
     *
     * @param servidor socket do servidor para que a thread comunicadora aceite
     *                 conexões
     * @param jogador  número associado ao jogador, pode ser 1 ou 2
     */
    public ComunicadorJogo(ServerSocket servidor, int jogador) {
        this.servidor = servidor;
        this.jogador = jogador;
    }

    /**
     * @return se a thread de conexão já aceitou uma conexão ou está esperando um
     *         cliente.
     */
    public boolean getConectado() {
        return this.conectado;
    }

    /**
     * dormirFrame dorme durante 1/30 segundos, e, caso o jogador seja o jogador 1,
     * limpa o buffer de Eventos.
     *
     * Isso é feito para manter consistência nos eventos de cada frame entre todos
     * os jogadores.
     *
     * @throws InterruptedException caso Thread.Sleep() seja interrompida
     */
    private void dormirFrame() throws InterruptedException {
        Thread.sleep(33, 333);

        if (jogador == 1) {
            EstadoJogo.pegarUnicoEstado().clearEventos();
        }
    }

    /**
     * run espera até que um cliente esteja conectado e inicia o método de conexão.
     *
     * O Space Invaders Multiplayer não é implementado tendo em mente o caso que os
     * jogadores se desconectem e reconectem livremente do jogo, então, caso um
     * cliente se desconcte, um evento de perda de jogo será registrado e tratado
     * como desistência.
     */
    @Override
    public void run() {
        while (true) {
            Socket s;
            try {
                s = this.servidor.accept();
                this.conectado = true;
                cuidarConexao(s);
                s.close();
            } catch (IOException e) {
                //ignora, mas perde o jogo da mesma forma
            } catch (InterruptedException e) {
                System.err.println("Interrompido");
            } catch (ClassNotFoundException e) {
                System.err.println("Erro grave de conexão");
            }

            EstadoJogo.pegarUnicoEstado().perderJogo();
            this.conectado = false;
        }
    }

    /**
     * cuidarConexao faz todas as ações de conectividade com um cliente, até que ele
     * se desconecte.
     *
     * A conexão funciona da seguinte forma: primeiro envia-se o id do jogador
     * associado ao cliente, depois, a cada frame, envia-se o estado atual de jogo
     * e, após dormir, recebe-se a ação do frame.
     *
     * @param s a socket relacionada a conexão
     * @throws IOException            caso o cliente tenha se desconectado.
     * @throws InterruptedException   caso o processo seja interrompido
     * @throws ClassNotFoundException caso o cliente tente enviar uma classe que não
     *                                seja {@link comunicacao.EstadoJogo}
     */
    public void cuidarConexao(Socket s)
            throws IOException, InterruptedException, ClassNotFoundException {

        Canhao canhao = new Canhao(TelaJogo.MAX_X * 0.2f + 0.5f,
                TelaJogo.MAX_Y - 0.5f, jogador);

        ObjectOutputStream saida = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());

        EstadoJogo ej = EstadoJogo.pegarUnicoEstado();

        // qual evento representa a perda de vida para esse jogador.
        Evento perdaDeVida;
        if (jogador == 1)
            perdaDeVida = Evento.PERDEU_VIDA_P1;
        else
            perdaDeVida = Evento.PERDEU_VIDA_P2;

        // no primeiro frame, envia o id do jogador
        saida.writeObject(jogador);

        while (true) {
            // comunicação deve também ser sincronizada com relação aos personagens pois
            // EstadoJogo contém apenas uma cópia de leitura dos desenhaveis.
            synchronized (Personagem.getPersonagens()) {
                synchronized (ej) {
                    // envia o estado atual de jogo
                    saida.writeObject(ej);
                }
            }

            // se o jogador perdeu uma vida, espera até ele entrar novamente
            if (ej.getEventos().contains(perdaDeVida)) {
                while ((Acao) entrada.readObject() != Acao.ENTRAR_JOGO) {
                    dormirFrame();
                }
                // como o canhão anterior foi destruido, cria um novo
                canhao = new Canhao(
                        TelaJogo.MAX_X * 0.2f + 0.5f,
                        TelaJogo.MAX_Y - 0.5f, jogador);
            }

            // reseta a saida, pois, caso contrário, o cliente sempre receberia o mesmo
            // estado: o objeto enviado sempre é o mesmo, então ObjectOutputStream tenta
            // otimizar a comunicação mandando uma versão em cache do EstadoJogo.
            saida.reset();
            dormirFrame();
            canhao.agir((Acao) entrada.readObject());
        }
    }
}
