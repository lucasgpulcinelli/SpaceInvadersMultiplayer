package com.github.lucasgpulcinelli.sistema;

import java.security.InvalidParameterException;

import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Evento;
import com.github.lucasgpulcinelli.comunicacao.Sprite;

/**
 * Inimigo implementa o principal problema dentro do jogo: os aliens.
 * 
 * Eles atuam em união: todos iniciam andando da esquerda para a direita, até
 * que, quando um deles chega a um canto da tela, todos são avisados para
 * passarem a se mover para baixo por alguns frames e mudarem de direção,
 * repetindo o procedimento para o outro lado e descendo até chegarem no final
 * da tela verticalmente, momento em que eles ganham.
 * 
 * A classe é responsável também por enviar um evento quando os inimigos
 * iniciam a movimentação para baixo, além de manter a contagem de quantos
 * inimigos ainda estão vivos para indicar um ganho de nível.
 * 
 * Para que a classe funcione corretamente {@link finalFrameInimigos} deve ser
 * chamado todo o frame, após as ações de todas as instâncias de inimigo.
 */
public class Inimigo extends Personagem {
    /** número de inimigos ainda vivos. */
    private static int inimigosVivos = 0;
    /** se os inimigos estão descendo ou se movimentando para os lados. */
    private static boolean descendo = false;
    /** se os inimigos estão indo para a esquerda ou direita. */
    private static boolean paraEsquerda = false;
    /** quantos frames os inimigos ainda tem para andar para baixo. */
    private static int framesDescendo = 16;

    /** score obtida quando o inimigo morre. */
    private int scoreQuandoMorto;

    /**
     * Cria um Inimigo com base em uma posição de centro e um sprite, que deve ser
     * correspondente a essa classe.
     * 
     * @param x      posição do centro do inimigo no eixo x
     * @param y      posição do centro do inimigo no eixo y
     * @param sprite sprite representante do inimigo
     */
    public Inimigo(float x, float y, Sprite sprite) {
        super(sprite, x, y, 0.45f, 0.45f);

        switch (sprite) {
            case INIMIGO1:
                scoreQuandoMorto = 30;
                break;
            case INIMIGO2:
                scoreQuandoMorto = 20;
                break;
            case INIMIGO3:
                scoreQuandoMorto = 10;
                break;
            default:
                throw new InvalidParameterException();
        }

        inimigosVivos++;
    }

    /**
     * caso um inimigo morra, reduz o número de inimigos vivos.
     */
    @Override
    boolean morrerSeNecessario() {
        boolean morto = super.morrerSeNecessario();
        if (morto)
            inimigosVivos--;
        return morto;
    }

    /**
     * Para facilitar a alteração de movimentos, o ângulo é baseado no fato de se
     * todos os inimigos estão indo para baixo ou para um dos lados.
     */
    @Override
    public float getAngulo() {
        if (descendo)
            return (float) -Math.PI / 2;
        return (paraEsquerda) ? 0 : (float) Math.PI;
    }

    /**
     * Para facilitar a alteração de movimentos, a velocidade é baseada no fato de
     * se todos os inimigos estão se movendo para baixo ou não.
     * 
     * Além disso, como no jogo original, quanto mais inimigos vivos, mais devagar o
     * conjunto todo anda, mas só horizontalmente.
     */
    @Override
    public float getVelocidade() {
        if (descendo)
            return 1 / 16f;
        return 1 / 1000f * (60 - inimigosVivos);
    }

    /**
     * O inimigo, a cada frame, tem uma chance de atirar, além de ter que checar se
     * no próximo frame uma movimentação para baixo terá de ser iniciada.
     * 
     * Além disso, caso um inimigo não possa se mover e já esteja indo para baixo,
     * isso significa que o inimigo chegou no final da tela, o que implica que os
     * jogadores perderam. Nesse caso, ocorrerá a chamada do método correspondente
     * de {@link EstadoJogo}.
     */
    @Override
    synchronized public boolean frame() {
        boolean retorno = super.frame();

        if (Math.random() < 1 / 2000f)
            new Tiro(getX(), getY(), true);

        if (!podeMover()) {
            if (descendo) {
                EstadoJogo.pegarUnicoEstado().perderJogo();
            }
            paraEsquerda = !paraEsquerda; // troca de lado
            descendo = true;
        }

        return retorno;
    }

    /**
     * A score quando um inimigo é morto depende do seu tipo. 30 pontos para o
     * inimigo 1, 20 para o 2, e 10 para o 3. Assim como no jogo original.
     */
    @Override
    public int getScoreMorto() {
        return scoreQuandoMorto;
    }

    /**
     * finalFrameInimigos realiza todas as alterações necessárias para manter o
     * passo dos inimigos igualitário.
     * 
     * Em especial, o método cuida de quantos frames o conjunto ainda tem que se
     * mover para baixo e se tal ação ainda é necessária. O método também adiciona o
     * evento de movimento dos inimigos caso esse seja o caso.
     * 
     * @return verdadeiro caso todos os inimigos tenham sido destruídos, ou seja, se
     *         os jogadores ganharam o jogo nesse frame.
     */
    public static boolean finalFrameInimigos() {
        if (descendo) {
            if (framesDescendo == 16) {
                EstadoJogo.pegarUnicoEstado().addEvento(Evento.INIMIGOS_MOVERAM);
            }
            if (--framesDescendo == 0) {
                framesDescendo = 16;
                descendo = false;
            }
        }

        return inimigosVivos == 0;
    }

    /**
     * resetarInimigos retorna os estados internos da classe ao padrão inicial,
     * ideal para o início de um próximo nível.
     */
    public static void resetarInimigos() {
        descendo = false;
        paraEsquerda = false;
        framesDescendo = 16;
    }
}
