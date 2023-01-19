package com.github.lucasgpulcinelli.comunicacao;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Estadojogo descreve todo o estado atual do jogo, sendo um sigleton, pois só
 * pode haver um jogo por servidor.
 * 
 * @see Desenhavel
 * @see Evento
 */
public final class EstadoJogo implements Serializable {
    /**
     * lista de desenhaveis existentes, somente é lido mas será alterado por outras
     * threads.
     */
    private final List<Desenhavel> desenhaveis;

    /** lista de eventos novos no frame, é tanto lido quanto escrito. */
    private final ArrayList<Evento> eventos;

    /** lista dos eventos para o próximo frame. */
    private final ArrayList<Evento> eventosProximoFrame = new ArrayList<>();

    /** score do jogador 1 */
    private int scoreP1;
    /** score do jogador 2 */
    private int scoreP2;

    /** número de vidas do jogador 1 */
    private int vidasP1;

    /** número de vidas do jogador 1 */
    private int vidasP2;

    /** nível que os jogadores estão */
    private int nivel;

    /** indica se os jogadores perderam o jogo */
    private boolean perdeuJogo = false;

    /** singleton, único estadoJogo permitido. */
    private static EstadoJogo unicoEstadoJogo = null;

    /**
     * cria um novo estadoJogo, privado pois é um singleton.
     * 
     * @param desenhaveis lista de desenhaveis que será modificada pelo resto do
     *                    programa
     * @param eventos     lista de eventos que será controlada pelo EstadoJogo.
     */
    private EstadoJogo(List<Desenhavel> desenhaveis, ArrayList<Evento> eventos) {
        this.scoreP1 = 0;
        this.scoreP2 = 0;
        this.vidasP1 = 3;
        this.vidasP2 = 3;
        this.desenhaveis = desenhaveis;
        this.eventos = eventos;
    }

    /**
     * @return o único EstadoJogo existente, ou null.
     */
    public static final EstadoJogo pegarUnicoEstado() {
        return unicoEstadoJogo;
    }

    /**
     * cria um novo estadoJogo para ser utilizado.
     * 
     * @param desenhaveis lista de desenhaveis que será modificada pelo resto do
     *                    programa
     * @return o novo Estadojogo
     */
    public static final EstadoJogo criar(List<Desenhavel> desenhaveis) {
        unicoEstadoJogo = new EstadoJogo(desenhaveis, new ArrayList<>());
        return unicoEstadoJogo;
    }

    /**
     * @return uma lista não modificável de desenhaveis.
     */
    public final List<Desenhavel> getDesenhaveis() {
        return Collections.unmodifiableList(desenhaveis);
    }

    /**
     * @return uma lista não modificável de eventos.
     */
    public final List<Evento> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    /**
     * @param e Evento a ser adicionado a lista do próximo frame.
     */
    synchronized public final void addEvento(Evento e) {
        eventosProximoFrame.add(e);
    }

    /**
     * clearEventos limpa a lista de eventos do frame atual e copia os valores do
     * frame anterior para o do atual.
     * O buffer é eventosProximoFrame é importante pois clearEventos pode ser
     * chamado a qualquer momento na visão de outra thread, então seria possível
     * perder Eventos no meio do programa quando um addEvento() fosse chamado em uma
     * thread e logo depois um clearEventos fosse chamado em uma segunda.
     * 
     * O evento Evento.PERDEU_JOGO é especial pois quando os jogadores
     * perdem um jogo eles perdem para sempre (o evento nunca é limpo).
     */
    synchronized public final void clearEventos() {
        eventos.clear();
        if (perdeuJogo) {
            eventos.add(Evento.PERDEU_JOGO);
        }
        eventos.addAll(eventosProximoFrame);
        eventosProximoFrame.clear();
    }

    /**
     * adiciona um nível ao valor e adiciona o evento de ganho de nível.
     */
    public final void ganhouNivel() {
        nivel++;
        addEvento(Evento.GANHOU_NIVEL);
    }

    /**
     * @return o nivel atual
     */
    public final int getNivel() {
        return nivel;
    }

    /**
     * perderVida faz um jogador perder uma vida.
     * 
     * @param jogador o jogador a perder a vida. Deve ser um número entre 1 e 2
     *                inclusivo
     */
    synchronized public final void perderVida(int jogador) {
        switch (jogador) {
            case 1:
                vidasP1--;
                addEvento(Evento.PERDEU_VIDA_P1);
                break;
            case 2:
                vidasP2--;
                addEvento(Evento.PERDEU_VIDA_P2);
                break;
            default:
                throw new InvalidParameterException();
        }

        if (vidasP1 == 0 || vidasP2 == 0) {
            // se qualquer um dos jogadores perdeu todas as vidas, o jogo acaba
            perderJogo();
        }
    }

    /**
     * perderJogo faz os jogadores perderem o jogo para sempre (este evento nunca é
     * limpo).
     */
    synchronized public final void perderJogo() {
        perdeuJogo = true;
        // não é necessário esperar o próximo frame, ele não seria limpo da mesma forma.
        eventos.add(Evento.PERDEU_JOGO);
    }

    /**
     * @param jogador jogador a pegar a score de
     * @return score do jogador
     */
    public final int getScore(int jogador) {
        switch (jogador) {
            case 1:
                return scoreP1;
            case 2:
                return scoreP2;
            default:
                throw new InvalidParameterException();
        }
    }

    /**
     * @param jogador jogador para adicionar score em
     * @param score   score a ser adicionada
     */
    public final void addScore(int jogador, int score) {
        switch (jogador) {
            case 1:
                scoreP1 += score;
                break;
            case 2:
                scoreP2 += score;
                break;
            default:
                throw new InvalidParameterException();
        }
    }

    /**
     * @param jogador jogador a pegar as vidas de
     * @return número de vidas entre 0 e 3
     */
    public final int getVidas(int jogador) {
        switch (jogador) {
            case 1:
                return vidasP1;
            case 2:
                return vidasP2;
            default:
                throw new InvalidParameterException();
        }
    }
}
