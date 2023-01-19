package com.github.lucasgpulcinelli.sistema;

import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Evento;
import com.github.lucasgpulcinelli.comunicacao.Sprite;

/**
 * Tiro implementa o Personagem mais rápido do jogo.
 * 
 * Todo o tiro deve vir ou de {@link Canhao} ou de {@link Inimigo}, e mantém
 * valores que guardam informação desse fato.
 * 
 * Tiro é a única classe que instancia {@link Particula}
 */
public class Tiro extends Personagem {
    /** se o tiro já colidiu com algo */
    private boolean jaMatouPersonagem = false;

    /**
     * jogador associado ao tiro (ou -1 caso ele seja vindo do inimigo), importante
     * para discernir pontos
     */
    int jogador;

    /**
     * Cria um tiro com base nas posições de centro e em um boolean se ele pertence
     * a um inimigo.
     * Caso se queira criar um tiro pertencente a um jogador, favor utilizar o
     * segundo construtor para esta classe, pois se não houver um número explicito
     * de jogador, o Tiro irá jogar a exceção InvalidParameterException quando
     * tentar contabilizar pontos para um jogador não especificado.
     * 
     * @param x              posição x do centro do tiro
     * @param y              posição y do centro do tiro
     * @param vindoDoInimigo se vem do inimigo ou de um canhão
     */
    public Tiro(float x, float y, boolean vindoDoInimigo) {
        super(Sprite.TIRO, x, y, 0.25f, 0.5f);
        this.setVelocidade(vindoDoInimigo ? -0.1f : 0.4f);
        this.setAngulo((float) Math.PI / 2);

        EstadoJogo.pegarUnicoEstado().addEvento(
                vindoDoInimigo ? Evento.INIMIGO_ATIROU : Evento.CANHAO_ATIROU);

        if (vindoDoInimigo) {
            jogador = -1;
        }
    }

    /**
     * O mesmo que o outro construtor, mas especifica um número de jogador.
     * 
     * @param x       posição x do centro do tiro
     * @param y       posição y do centro do tiro
     * @param jogador número do jogador associado
     */
    public Tiro(float x, float y, int jogador) {
        this(x, y, false);
        this.jogador = jogador;
    }

    /**
     * Ação de colisão do tiro, pode ser bem diferente dependendo se vem de um
     * inimigo ou não.
     * 
     * Caso um tiro seja de um canhão, ignora outros canhões e atinge inimigos, além
     * de adicionar pontuações ao jogador dono do tiro; caso contrário, ignora
     * inimigos e atinge canhões.
     * 
     * Indiferente desses fatos, todos os tiros só podem matar um único personagem,
     * gerar uma única partícula, e jamais interagem com outras partículas.
     * 
     * @see Canhao
     * @see Inimigo
     * @see Particula
     */
    @Override
    public void acaoDeColisao(Personagem p) {
        if (p.getClass() == Particula.class)
            return;
        if (jogador != -1 && p.getClass() == Canhao.class)
            return;
        if (jogador == -1 && p.getClass() == Inimigo.class)
            return;

        // tiros de inimigos não afetam tiros de outros inimigos, de canhões da mesma
        // forma
        if (p.getClass() == Tiro.class) {
            Tiro outroTiro = (Tiro) p;
            if (outroTiro.jogador == -1 && jogador == -1) {
                return;
            }
            if(outroTiro.jogador > 0 && jogador > 0){
                return;
            }
        }

        if (jaMatouPersonagem)
            return;
        jaMatouPersonagem = true;

        if (jogador != -1) {
            EstadoJogo.pegarUnicoEstado().addScore(jogador, p.getScoreMorto());
        }

        p.matar();
        this.matar();
        new Particula(p.getX(), p.getY());
    }

    /**
     * Caso um tiro não possa mais se mover, mata ele sem gerar partículas, dando a
     * impressão que ele saiu da tela.
     */
    @Override
    public synchronized boolean frame() {
        if (!this.podeMover()) {
            this.matar();
        }
        return super.frame();
    }
}