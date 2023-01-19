package com.github.lucasgpulcinelli.grafico;

import java.util.List;

import com.github.lucasgpulcinelli.comunicacao.Desenhavel;

/**
 * InterfaceGrafica identifica o que uma interface gráfica do spaceInvaders deve
 * ter.
 * 
 * Todas as funções inclusas devem poder ser chamadas a todo o frame de desenho
 * (30fps), não mantendo controle de tempo a ser pausado nem de input para modificar a tela.
 * 
 * @see comunicacao.Desenhavel
 * @see comunicacao.EstadoJogo
 */
public interface InterfaceGrafica {
    /**
     * desenharTela desenha um único frame de desenhaveis na tela, colocando informações adicionais.
     * @param score score do jogador atual
     * @param vidas vidas do jogador atual
     * @param desenhaveis Desenhaveis a serem desenhados na tela.
     */
    public void desenharTela(int score, int vidas, List<Desenhavel> desenhaveis);

    /**
     * printPerdeuVida desenha a tela para falar que um jogador perdeu uma vida.
     */
    public void printPerdeuVida();

    /**
     * printGanhouNivel desenha a tela para falar que um jogador ganhou um nível.
     * @param nivel nível que acabou de ser ganho
     */
    public void printGanhouNivel(int nivel);

    /**
     * printSplashScreen desenha a tela de início de jogo.
     */
    public void printSplashScreen();

    /**
     * printPerdeuJogo desenha a tela para falar que um jogador perdeu o jogo.
     */
    public void printPerdeuJogo();
}
