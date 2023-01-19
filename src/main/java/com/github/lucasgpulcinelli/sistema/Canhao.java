package com.github.lucasgpulcinelli.sistema;

import java.util.ArrayList;

import com.github.lucasgpulcinelli.comunicacao.Acao;
import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Sprite;

/**
 * Canhao implementa as ações e o jogador em si, conectando o mundo do sistema
 * interno do jogo ao cliente.
 * Por meio do método {@link agir} as ações do jogador são enviadas para o loop
 * principal do jogo.
 * 
 * Um canhão só pode ter um tiro, assim como no jogo original.
 * 
 * @see servidor.ComunicadorJogo
 * @see comunicacao.Acao
 */
public class Canhao extends Personagem {
    /** única velocidade de movimento que o canhão pode ter. */
    private static final float velocidadeCanhao = 0.5f;

    /**
     * número de frames que o jogador ainda deve se mover, para manter uma transição
     * suave de movimentos.
     */
    private int framesMovimento = 0;

    /** Tiro associado ao jogador */
    private Tiro tiro = null;

    /** Id do jogador associado ao canhão */
    private final int jogador;

    /**
     * Cria um novo canhão associado a um certo jogador
     * 
     * @param x       posição x do centro do canhão
     * @param y       posição y do centro do canhão
     * @param jogador id do jogador associado, pode ser 1 ou 2
     */
    public Canhao(float x, float y, int jogador) {
        super(Sprite.CANHAO, x, y, 0.5f, 0.5f);
        this.jogador = jogador;
    }

    /**
     * Um canhão faz o jogador associado perder uma vida quando morre.
     */
    @Override
    public void matar() {
        EstadoJogo.pegarUnicoEstado().perderVida(jogador);
        super.matar();
    }

    /**
     * agir realiza uma ação dentro do loop principal do jogo, é o método
     * diretamente controlada pelo jogador no cliente.
     * 
     * @param acao ação a ser realizada
     */
    synchronized public void agir(Acao acao) {
        switch (acao) {
            case ESQUERDA:
                framesMovimento = 2;
                setVelocidade(velocidadeCanhao);
                setAngulo((float) 0);
                break;
            case DIREITA:
                framesMovimento = 2;
                setVelocidade(velocidadeCanhao);
                setAngulo((float) Math.PI);
                break;
            case ATIRAR:
                if (tiro == null)
                    tiro = new Tiro(getX(), getY(), jogador);
                break;
            case SAIR_JOGO:
            case ENTRAR_JOGO:
            case NENHUMA_ACAO:
                break;
        }
    }

    /**
     * Um canhão, além das funções usuais, deve se mover apenas quando a ação for
     * compatível, além de manter controle do tiro associado.
     */
    @Override
    synchronized public boolean frame() {
        boolean retorno = super.frame();
        if (--framesMovimento == 0) {
            setVelocidade(0);
        }

        ArrayList<Personagem> ps = Personagem.getPersonagens();
        synchronized (ps) {
            if (ps.indexOf(tiro) < 0) {
                tiro = null;
            }
        }

        return retorno;
    }

    /**
     * Quando um inimigo colide com um canhão, o canhão imediatamente morre.
     */
    @Override
    public void acaoDeColisao(Personagem p) {
        if (p.getClass() == Inimigo.class) {
            matar();
        }
    }
}
