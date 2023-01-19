package com.github.lucasgpulcinelli.sistema;

import java.io.Serializable;

import com.github.lucasgpulcinelli.comunicacao.Sprite;

/**
 * Base representa uma base de defesa no jogo, que é efetivamente um conjunto de
 * pedaços.
 * As bases são inicializadas somente uma vez no início do jogo e se mantém até
 * o fim sem serem restauradas, como no jogo original.
 */
public class Base implements Serializable {
    /**
     * PedacoBase representa uma unidade de defesa da base.
     * Cada pedaço pode levar 5 tiros antes de ser completamente destruída.
     * Os pedaços levam dano de tiros vindo tanto de inimigos quanto de canhões, e
     * são imediatamente destruídos caso um inimigo passe por ele.
     * 
     * @see Inimigo
     */
    public class PedacoBase extends Personagem {
        /** vida que o pedaço ainda tem restante. */
        private int vida = 4;

        /**
         * Cira um pedaço da base tendo as coordenadas de seu centro. 
         * @param x posição x do centro
         * @param y posição y do centro
         */
        public PedacoBase(float x, float y) {
            super(Sprite.BASE5, x, y, 0.45f, 0.45f);
        }

        /**
         * Uma base só deve morrer realmente caso sua vida chegue a um valor negativo,
         * caso ela ainda tenha vida restante, subtrai-se um do valor e um novo sprite é
         * colocado no lugar do anterior para dar a ilusão de levar dano.
         */
        @Override
        boolean morrerSeNecessario() {
            if (!deveMorrer)
                return false;

            if (vida-- == 0) {
                return super.morrerSeNecessario();
            }
            deveMorrer = false;

            final Sprite[] sprites = { Sprite.BASE1, Sprite.BASE2, Sprite.BASE3,
                    Sprite.BASE4, Sprite.BASE5 };
            setSprite(sprites[vida]);

            return false;
        }

        /**
         * caso um inimigo colida com uma base, imediatamente ela deve perder toda a
         * vida e morrer, como no jogo original.
         */
        @Override
        public void acaoDeColisao(Personagem p) {
            if (p.getClass() == Inimigo.class) {
                vida = 0;
                matar();
            }
        }
    }

    /**
     * Cria uma base completa em uma posição especificada
     * 
     * @param x coordenada x central da base
     * @param y coordenada y do centro da parte superior da base
     */
    public Base(float x, float y) {
        new PedacoBase(x, y);
        new PedacoBase(x - 1, y);
        new PedacoBase(x + 1, y);
        new PedacoBase(x - 1, y + 1);
        new PedacoBase(x + 1, y + 1);
    }
}
