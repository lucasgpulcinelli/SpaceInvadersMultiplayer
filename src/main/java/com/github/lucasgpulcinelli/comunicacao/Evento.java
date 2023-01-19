package com.github.lucasgpulcinelli.comunicacao;

/**
 * Evento sinaliza os clientes para gerar áudio (funcionalidade não implementada
 * no {@link clienteTerminal}), além mostrar perdas de vida, ganhos de nível e
 * perdas de jogo.
 */
public enum Evento {
    /** nave especial apareceu na tela */
    NAVE_ESPECIAL_APARECEU,

    /** nave especial saiu da tela */
    NAVE_ESPECIAL_SAIU,

    /** inimigos se movimentaram para baixo */
    INIMIGOS_MOVERAM,

    /** algum objeto na tela foi destruído */
    OBJETO_DESTRUIDO,

    /** um inimigo atirou */
    INIMIGO_ATIROU,

    /** um canhão atirou */
    CANHAO_ATIROU,

    /** o jogador 1 perdeu uma vida */
    PERDEU_VIDA_P1,

    /** o jogador 2 perdeu uma vida */
    PERDEU_VIDA_P2,

    /** jogadores ganharam um nível */
    GANHOU_NIVEL,

    /** jogadores perderam o jogo */
    PERDEU_JOGO,
}
