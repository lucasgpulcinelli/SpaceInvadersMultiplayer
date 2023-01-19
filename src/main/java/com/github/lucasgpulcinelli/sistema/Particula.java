package com.github.lucasgpulcinelli.sistema;

import com.github.lucasgpulcinelli.comunicacao.EstadoJogo;
import com.github.lucasgpulcinelli.comunicacao.Evento;
import com.github.lucasgpulcinelli.comunicacao.Sprite;

/**
 * Particula é uma classe de objetos temporários que vivem por um pequeno
 * período de tempo, servindo apenas para aspectos visuais.
 *
 * Todas as particulas são criadas por {@link Tiro}.
 */
public class Particula extends Personagem {
    /** conta quanto tempo a particula deve viver */
    private int contador = 10;

    /**
     * Cria uma nova partícula com base nas coordenas de seu centro 
     * @param x posição x do centro
     * @param y posição y do centro
     */
    public Particula(float x, float y) {
        super(Sprite.PARTICULA, x, y, 0.1f, 0.1f);
        EstadoJogo.pegarUnicoEstado().addEvento(Evento.OBJETO_DESTRUIDO);
    }

    /** Uma partícula deve morrer quando o contador zerar. */
    @Override
    synchronized public boolean frame() {
        if (contador-- == 0) {
            matar();
        }
        return super.frame();
    }
}
