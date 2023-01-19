package com.github.lucasgpulcinelli.comunicacao;

import java.io.Serializable;

/**
 * Desenhavel descreve um objeto que aparece na tela, não necessariamente tendo
 * lógica. É importante ser uma classe simples e com o máximo de métodos final
 * possível para reduzir o tamanho do objeto, já que ele será passado pela
 * internet pelo {@link servidor.ComunicadorJogo}.
 */
public abstract class Desenhavel implements Serializable {
    /**
     * sprite do Desenhavel
     */
    private Sprite sprite;
    
    /**
     * posição de centro do Desenhaevel
     */
    private float x, y;

    /**
     * cria um novo desenhavel pronto para ser enviado.
     * 
     * @param sprite sprite inicial do Desenhavel
     * @param x      posição central do sprite na coordenada x
     * @param y      posição central do sprite na coordenada y
     */
    public Desenhavel(Sprite sprite, float x, float y) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    /**
     * @return o sprite atual do Desenhavel
     */
    public final Sprite getSprite() {
        return this.sprite;
    }

    /**
     * @return a posição na coordenada x atual
     */
    public final float getX() {
        return this.x;
    }

    /**
     * @return a posição na coordenada y atual.
     */
    public final float getY() {
        return this.y;
    }

    /**
     * @param s novo sprite a ser utilizado
     */
    protected final void setSprite(Sprite s) {
        this.sprite = s;
    }

    /**
     * @param x nova posição na coordenada x a ser utilizada
     */
    protected final void setX(float x) {
        this.x = x;
    }

    /**
     * @param y nova posição na coordenada y a ser utilizada
     */
    protected final void setY(float y) {
        this.y = y;
    }
}
