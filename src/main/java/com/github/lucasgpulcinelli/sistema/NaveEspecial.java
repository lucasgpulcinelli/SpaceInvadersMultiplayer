package com.github.lucasgpulcinelli.sistema;

import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Evento;
import com.github.lucasgpulcinelli.comunicacao.Sprite;

/**
 * NaveEspecial implementa o personagem mais peculiar do jogo.
 * 
 * A cada frame, caso não exista outra NaveEspecial no jogo, existe uma chance
 * pequena que uma nova NaveEspecial apareça e possa ser destruída para garantir
 * uma quantidade aleatória de pontos.
 * 
 * A nave se movimenta de um lado para outro no topo da tela, podendo ser
 * detruída por um canhão ou fugir.
 */
public class NaveEspecial extends Personagem {
    /** singleton da nave, só pode haver uma a cada dado momento. */
    private static NaveEspecial unicaNave = null;
    /** se a nave foi morta por um canhão ou só saiu da tela. */
    private boolean mortoPorCanhao = true;

    /**
     * cria uma nova nave especial. Como é um singleton, o construtor é privado.
     */
    private NaveEspecial() {
        super(Sprite.NAVEESPECIAL, 0, 1, 0.5f, 0.5f);

        boolean vemDaEsquerda = Math.random() < 0.5;
        setX(vemDaEsquerda ? 0.5f : TelaJogo.MAX_X - 0.5f);
        setVelocidade(0.05f);
        setAngulo(vemDaEsquerda ? (float) Math.PI : 0);

        EstadoJogo.pegarUnicoEstado().addEvento(Evento.NAVE_ESPECIAL_APARECEU);
    }

    /**
     * tentarGerar cria uma naveEspecial caso uma já não exista.
     */
    public static void tentarGerar() {
        if (unicaNave != null) {
            return;
        }
        unicaNave = new NaveEspecial();
    }

    /**
     * A nave pode prover 50, 100, 150, 200 ou 300 pontos quando destruida,
     * escolhido aleatoriamente, como no jogo original.
     */
    @Override
    public int getScoreMorto() {
        final int[] scores = { 50, 100, 150, 200, 300 };
        int i = (int) (Math.random() * 5);

        return scores[i];
    }

    /**
     * A nave deve enviar o evento de sair da tela somente se ela não foi destruída
     * por um canhão quando "morre". Além disso, é necessário manter o cuidado caso
     * a nave morra de resetar o singleton para permitir novas naves.
     */
    @Override
    boolean morrerSeNecessario() {
        boolean morreu = super.morrerSeNecessario();
        if (morreu) {
            unicaNave = null;
            if (!mortoPorCanhao)
                EstadoJogo.pegarUnicoEstado().addEvento(
                        Evento.NAVE_ESPECIAL_SAIU);
        }
        return morreu;
    }

    /**
     * A nave deve "morrer" sem criar partículas caso ela não consiga mais se mover,
     * simulando uma saída da tela.
     */
    @Override
    synchronized public boolean frame() {
        if (!podeMover()) {
            matar();
            mortoPorCanhao = false;
        }
        return super.frame();
    }
}
