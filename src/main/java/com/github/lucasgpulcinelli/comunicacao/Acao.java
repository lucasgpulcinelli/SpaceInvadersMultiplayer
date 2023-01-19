package com.github.lucasgpulcinelli.comunicacao;

/**
 * Acao descreve todas as possíveis ações que um jogador pode fazer para
 * interagir com o jogo.
 * 
 * @see sistema.Canhao
 * @see servidor.ComunicadorJogo
 */
public enum Acao {
    /** jogador foi para a esquerda */
    ESQUERDA,

    /** jogador foi para a direita */
    DIREITA,

    /** jogador atirou */
    ATIRAR,

    /** jogador não fez nada no frame */
    NENHUMA_ACAO,

    /** jogador entrou no jogo */
    ENTRAR_JOGO,

    /** jogador saiu do jogo */
    SAIR_JOGO
}
