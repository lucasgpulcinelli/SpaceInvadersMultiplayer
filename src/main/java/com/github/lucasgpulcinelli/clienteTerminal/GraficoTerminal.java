package com.github.lucasgpulcinelli.clienteTerminal;

import java.util.ArrayList;
import java.util.List;

import com.github.lucasgpulcinelli.comunicacao.Desenhavel;
import com.github.lucasgpulcinelli.comunicacao.Sprite;
import com.github.lucasgpulcinelli.grafico.InterfaceGrafica;
import com.github.lucasgpulcinelli.sistema.TelaJogo;

/**
 * GraficoTerminal controla o display de desenhaveis e telas especiais para o
 * {@link clienteTerminal}. O output fica na tela e é sobrescrevido somente
 * quando é chamado outro método desta classe.
 */
public class GraficoTerminal implements InterfaceGrafica {
    // tamanhos máximos para altura e comprimento da tela.
    private static final int TAM_ALTU = (int) TelaJogo.MAX_Y;
    private static final int TAM_COMP = (int) TelaJogo.MAX_X;

    // buffer de caracteres a serem desenhados.
    private final char buffer[][] = new char[TAM_ALTU][TAM_COMP];

    /**
     * spriteToChar converte um sprite para o seu caracter correspondente.
     * 
     * @param s é o caracter para ser convertido
     * 
     * @return caracter correspondente ao Sprite s
     */
    private char spriteToChar(Sprite s) {
        switch (s) {
            case CANHAO:
                return '^';
            case INIMIGO1:
                return 'i';
            case INIMIGO2:
                return 'm';
            case INIMIGO3:
                return 'H';
            case TIRO:
                return '|';
            case PARTICULA:
                return '@';
            case BASE1:
                return '.';
            case BASE2:
                return ',';
            case BASE3:
                return ':';
            case BASE4:
                return '#';
            case BASE5:
                return '$';
            case NAVEESPECIAL:
            default:
                return '?';
        }
    }

    /**
     * encherBufferVazio coloca ' ' em todo o buffer de desenho.
     */
    private void encherBufferVazio() {
        for (int i = 0; i < TAM_ALTU; i++) {
            for (int j = 0; j < TAM_COMP; j++) {
                buffer[i][j] = ' ';
            }
        }
    }

    /**
     * retornaTela utiliza caracteres especiais para retornar o cursor para o
     * início da tela.
     */
    private void retornarTela() {
        for (int i = 0; i < (TAM_ALTU + 4); i++) {
            // "\033[F" é o caracter especial que vai uma linha para cima,
            // funciona no NetBeans.
            System.out.print("\033[F");
        }
    }

    /**
     * printMensagem coloca uma mensagem na tela formatada de uma forma boa.
     */
    private void printMensagem(String mensagem) {
        retornarTela();

        int i;
        for (i = 0; i < (TAM_ALTU + 4) / 2 - 1; i++) {
            System.out.println();
        }

        for (int j = 0; j < TAM_COMP + 2; j++)
            System.out.print('-');
        System.out.println();
        i++;

        int espacosBrancosLados = (int) (TAM_COMP - mensagem.length()) / 2;

        System.out.print('|');
        for (int j = 0; j < espacosBrancosLados; j++) {
            System.out.print(' ');
        }
        System.out.print(mensagem);
        for (int j = 0; j < espacosBrancosLados + (TAM_COMP - mensagem.length()) % 2; j++) {
            System.out.print(' ');
        }
        System.out.println('|');
        i++;

        for (int j = 0; j < TAM_COMP + 2; j++) {
            System.out.print('-');
        }
        System.out.println();
        i++;

        for (; i < TAM_ALTU + 2; i++) {
            System.out.println();
        }

        System.out.println("|     Aperte \'w\' ou espaço     |");
        System.out.println();
    }

    /**
     * desenharTela desenha um frame de desenhaveis completo no terminal.
     * 
     * @see grafico.InterfaceGrafica
     */
    @Override
    public void desenharTela(int score, int vidas, List<Desenhavel> desenhaveis) {
        retornarTela();

        // resseta buffer de jogo
        encherBufferVazio();

        // para cada desenhavel, coloca ele na tela de buffer
        for (Desenhavel d : desenhaveis) {
            buffer[(int) d.getY()][(int) d.getX()] = spriteToChar(d.getSprite());
        }

        // print do frame de cima
        for (int i = 0; i < TAM_COMP + 2; i++) {
            System.out.print("-");
        }
        System.out.println();

        // print de score e vidas
        System.out.printf("|Score: %05d     Vidas:", score);
        for (int i = 0; i < 3; i++) {
            System.out.printf(" %c", (i < vidas) ? '^' : ' ');
        }
        System.out.println(" |");

        // print do frame de jogo
        for (int i = 0; i < TAM_COMP + 2; i++) {
            System.out.print("-");
        }
        System.out.println();

        // print da tela de jogo em si e frame dos lados
        for (int i = 0; i < TAM_ALTU; i++) {
            System.out.print('|');
            for (int j = 0; j < TAM_COMP; j++) {
                System.out.print(buffer[i][j]);
            }
            System.out.println('|');
        }

        // print do frame de baixo
        for (int i = 0; i < TAM_COMP + 2; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    @Override
    public void printPerdeuVida() {
        printMensagem("Perdeu vida!");
    }

    @Override
    public void printGanhouNivel(int nivel) {
        printMensagem(String.format("ganhou nível %2d!", nivel));
    }

    @Override
    public void printSplashScreen() {
        desenharTela(0, 3, new ArrayList<>());
        printMensagem("<<SPACE INVADERS>>");
    }

    @Override
    public void printPerdeuJogo() {
        printMensagem("...GAME OVER...");
    }
}
