package com.github.lucasgpulcinelli.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Sprite;
import com.github.lucasgpulcinelli.sistema.Base;
import com.github.lucasgpulcinelli.sistema.Inimigo;
import com.github.lucasgpulcinelli.sistema.NaveEspecial;
import com.github.lucasgpulcinelli.sistema.Personagem;
import com.github.lucasgpulcinelli.sistema.TelaJogo;

/**
 * Main é a classe principal do jogo Space Invaders Multiplayer, criando uma
 * sessão do servidor de jogo para dois jogadores em 0.0.0.0:8080.
 */
public class Main {
    /**
     * main inicializa o código de execução principal, criando duas threads: uma
     * para cuidar dos inputs de cada jogador, e controlando na própria thread a
     * lógica do jogo.
     * 
     * @param _args inutilizado
     * @throws InterruptedException caso a thread tenha sido interrompida
     * @throws IOException          caso a socket do servidor não possa ser criada
     */
    public static void main(String[] _args) throws InterruptedException, IOException {
        ServerSocket server = new ServerSocket(8080);

        EstadoJogo ej = EstadoJogo.criar(Personagem.getDesenhaveis());

        ComunicadorJogo cj1 = new ComunicadorJogo(server, 1);
        cj1.start();

        while (!cj1.getConectado()) {
            // espera o jogador 1 se conectar
            Thread.sleep(16, 666);
        }

        // o jogador 2 é opcional, então não precisa esperar ele se conectar para
        // começar o jogo
        ComunicadorJogo cj2 = new ComunicadorJogo(server, 2);
        cj2.start();

        System.out.println("Jogo iniciado");

        initBases();

        while (true) {
            initInimigos(ej.getNivel());
            while (true) {
                boolean ganhouNivel = frameTodos();
                if (ganhouNivel) {
                    ej.ganhouNivel();
                    Thread.sleep(1000, 0);
                    Inimigo.resetarInimigos();
                    break;
                }
                Thread.sleep(16, 666); // a lógica do jogo roda em 60 fps
            }
        }
    }

    private static void initBases() {
        for (int i = 0; i < 4; i++) {
            new Base(7 * i + 4 + 0.5f, TelaJogo.MAX_Y - 3 + 0.5f);
        }
    }

    private static void initInimigos(int nivel) {
        Sprite sprite = Sprite.INIMIGO1;

        for (int i = 0; i < 5; i++) {
            if (i >= 3) {
                sprite = Sprite.INIMIGO3;
            } else if (i >= 1) {
                sprite = Sprite.INIMIGO2;
            }

            for (int j = 0; j < 11; j++) {
                new Inimigo(j * 2 + 1 + 0.5f, i * 2 + 2 + nivel + 0.5f, sprite);
            }
        }
    }

    private static boolean frameTodos() {
        List<Personagem> personagens = Personagem.getPersonagens();

        for (int i = 0, mortos = 0; i + mortos < personagens.size(); i++) {
            // roda o método frame, e caso o desenhavel tenha morrido, a List personagens
            // vai ter get(i) com o valor do próximo Personagem (pois o personagem anterior
            // foi removido)

            Personagem p = personagens.get(i + mortos);
            mortos += (p.frame()) ? 1 : 0;
        }

        // tenta criar uma nave especial
        if (Math.random() < 0.0003)
            NaveEspecial.tentarGerar();

        // finaliza o frame dos inimigos e retorna se o jogador ganhou
        return Inimigo.finalFrameInimigos();
    }
}