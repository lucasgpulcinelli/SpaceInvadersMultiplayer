package com.github.lucasgpulcinelli.clienteJavafx;

import com.github.lucasgpulcinelli.comunicacao.Acao;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

/**
 * LeitorInput controla a entrada de teclado para o jogo. É um singleton.
 * Para utilizá-lo, é necessário, antes de criar um novo leitor, registrar uma
 * Cena de javaFx que terá os inputs do usuário. É recomendado registrá-la
 * junto ao início da aplicação.
 * 
 * @see ControladorJogo
 */
public class LeitorInput {
    /** caracteriza o aspecto de singleton */
    private static LeitorInput unicoLeitor = null;
    /** se uma thread está esperando por um tiro do jogador */
    private boolean esperando = false;
    /** a ação atual do jogador */
    private Acao acaoAtual = Acao.NENHUMA_ACAO;

    /** cria um novo LeitorInput, privado pelo aspecto de singleton */
    private LeitorInput() {
    }

    /**
     * registra uma cena que recebe os inputs do usuário.
     * 
     * @param cena a cena a ser registrada
     */
    public static void registrarCena(Scene cena) {
        unicoLeitor = new LeitorInput();
        cena.addEventHandler(KeyEvent.KEY_PRESSED, (Event event) -> {
            unicoLeitor.cuidarDoInput(event);
        });
    }

    /**
     * @return o único leitor de input ou null caso não haja uma cena registrada
     */
    public static LeitorInput pegarLeitor() {
        return unicoLeitor;
    }

    /**
     * cuidarDoInput atualiza a ação atual com base num evento de aperto de
     * tecla.
     * 
     * @param e o evento de tecla enviado pela cena de javaFx.
     */
    private void cuidarDoInput(Event e) {
        KeyEvent ke = (KeyEvent) e;
        if (ke.getText().length() == 0) {
            return;
        }

        switch (ke.getText().charAt(0)) {
            case 'a':
                acaoAtual = Acao.ESQUERDA;
                break;
            case 'd':
                acaoAtual = Acao.DIREITA;
                break;
            case 'w':
            case ' ':
                if (!esperando) {
                    acaoAtual = Acao.ATIRAR;
                    break;
                }

                esperando = false;
                synchronized (this) {
                    notify();
                    acaoAtual = Acao.ENTRAR_JOGO;
                }
                break;

            case 'q':
                acaoAtual = Acao.SAIR_JOGO;
                break;
        }
    }

    /**
     * esperarTiro para a thread de execução até o jogador atirar.
     * 
     * @throws InterruptedException se houve uma interrupção
     */
    synchronized public void esperarTiro() throws InterruptedException {
        esperando = true;
        wait();
    }

    /**
     * @return a ação atual
     */
    public Acao getAcao() {
        Acao retorno = acaoAtual;
        acaoAtual = Acao.NENHUMA_ACAO;
        return retorno;
    }
}
