package com.github.lucasgpulcinelli.sistema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.lucasgpulcinelli.comunicacao.Desenhavel;
import com.github.lucasgpulcinelli.comunicacao.Sprite;

/**
 * Personagem implementa a fundação lógica do jogo.
 * 
 * Todo o personagem tem uma velocidade, um ângulo de movimento, um tamanho em x
 * e y que cria uma hitbox, além de métodos para matar, morrer, pegar a score
 * ganha ao jogador quando morto, além de funções de movimento, colisão e o
 * método frame, que roda para cada Personagem todos os frames para fazer o jogo
 * acontecer.
 * 
 * Além disso, a classe Personagem também mantém uma lista de personagens que
 * contém todos os membros vivos que devem ser mandados aos clientes.
 */
public abstract class Personagem extends Desenhavel {
    /** lista de todos os personagens vivos */
    private static final ArrayList<Personagem> personagens = new ArrayList<>();

    /** velocidade do personagem em um frame */
    private float velocidade = 0;

    /** ângulo de movimento que o personagem tem com relação ao eixo x positivo */
    private float angulo = 0;

    /** hitbox em x (é o valor do centro até a borda do retângulo de hitbox) */
    private final float tamanhoX;
    /** hitbox em y (é o valor do centro até a borda do retângulo de hitbox) */
    private final float tamanhoY;

    /** se o personagem deve morrer no próximo frame */
    boolean deveMorrer = false;

    /**
     * Cria um novo personagem e o prepara para ser desenhado em uma tela e enviado
     * para o(s) cliente(s).
     * 
     * @param sprite   sprite do personagem
     * @param x        posição x do centro
     * @param y        psoição y do centro
     * @param tamanhoX raio da hitbox até a borda em x
     * @param tamanhoY raio da hitbox até a borda em y
     */
    public Personagem(Sprite sprite, float x, float y, float tamanhoX, float tamanhoY) {
        super(sprite, x, y);
        this.tamanhoX = tamanhoX;
        this.tamanhoY = tamanhoY;
        synchronized (personagens) {
            personagens.add(this);
        }
    }

    /**
     * @return uma lista imutável dos desenhaveis representados pelos personagens.
     */
    public static List<Desenhavel> getDesenhaveis() {
        return Collections.unmodifiableList(personagens);
    }

    /**
     * @return uma lista imutável dos personagens.
     */
    public static ArrayList<Personagem> getPersonagens() {
        return personagens;
    }

    /**
     * matar marca o personagem para morrer no próximo frame.
     */
    public void matar() {
        deveMorrer = true;
    }

    /**
     * @return score ganha pelo jogador ao matar o personagem, é 0 na maioria dos
     *         casos.
     */
    public int getScoreMorto() {
        return 0;
    }

    /**
     * morrerSeNecessário retira o personagem da lista de personagens vivos caso ele
     * tenha sido marcado para morrer, efetivamente o retirando de toda a lógica e
     * comunicação.
     * 
     * @return se o personagem morreu ou não
     */
    boolean morrerSeNecessario() {
        if (!deveMorrer)
            return false;
        synchronized (personagens) {
            personagens.remove(this);
        }
        return true;
    }

    /**
     * acaoDeColisao implementa a lógica do que ocorre quando um personagem colide
     * com outro.
     * 
     * @param p o segundo personagem que se está colidindo
     */
    public void acaoDeColisao(Personagem p) {
    }

    /**
     * frame é onde está todo o código de lógica do jogo.
     * 
     * O método é responsável por diversas coisas:
     * matar o personagem se necessário,
     * movimentar o personagem,
     * checar e tratar colisões com outros personagens e
     * realizar qualquer tipo de lógica especifica para subclasses dos personagens
     * 
     * ela deve ser chamada (como o próprio nome diz) para cada frame e para cada
     * personagem no array de personagens vivos.
     * 
     * @return se o personagem morreu nesse frame
     */
    synchronized public boolean frame() {
        if (morrerSeNecessario())
            return true;

        if (!(podeMover()))
            return false;

        float x = getX() + (float) (getVelocidade() * -Math.cos(getAngulo()));
        float y = getY() + (float) (getVelocidade() * -Math.sin(getAngulo()));

        setX(x);
        setY(y);

        Personagem colidido;
        if ((colidido = checarColisoes()) != null) {
            this.acaoDeColisao(colidido);
            colidido.acaoDeColisao(this);
        }

        return false;
    }

    /**
     * @return se o personagem pode se mover no próximo frame ou irá bater em um
     *         canto da tela.
     */
    public boolean podeMover() {
        float x = getX() + (float) (getVelocidade() * -Math.cos(getAngulo()));
        float y = getY() + (float) (getVelocidade() * -Math.sin(getAngulo()));

        if (x < 0 + tamanhoX || x > TelaJogo.MAX_X - tamanhoX)
            return false;
        if (y < 0 + tamanhoY || y > TelaJogo.MAX_Y - tamanhoY)
            return false;

        return true;
    }

    /**
     * @return o tamanho do raio do personagem em x
     */
    public float getTamanhoX() {
        return tamanhoX;
    }

    /**
     * @return o tamanho do raio do personagem em y
     */
    public float getTamanhoY() {
        return tamanhoY;
    }

    /**
     * @return a velocidade do personagem
     */
    public float getVelocidade() {
        return velocidade;
    }

    /**
     * coloca um novo valor para a velocidade
     * 
     * @param velocidade o novo valor
     */
    synchronized public void setVelocidade(float velocidade) {
        this.velocidade = velocidade;
    }

    /**
     * @return o ângulo do personagem
     */
    public float getAngulo() {
        return angulo;
    }

    /**
     * coloca um novo valor para o ângulo
     * 
     * @param angulo o novo valor
     */
    synchronized public void setAngulo(float angulo) {
        this.angulo = angulo;
    }

    /**
     * @param p o personagem que se quer checar
     * @return se os dois personagens colidem
     */
    private boolean colide(Personagem p) {
        float xIntersecaol = Math.max(getX() - tamanhoX, p.getX() - p.getTamanhoX());
        float yIntersecaol = Math.max(getY() - tamanhoY, p.getY() - p.getTamanhoY());

        float xIntersecaou = Math.min(getX() + tamanhoX, p.getX() + p.getTamanhoX());
        float yIntersecaou = Math.min(getY() + tamanhoY, p.getY() + p.getTamanhoY());

        return xIntersecaol <= xIntersecaou && yIntersecaol <= yIntersecaou;
    }

    /**
     * checarColisoes vê se o personagem colide com algum outro. Somente retornando
     * o primeiro que o fizer.
     * 
     * @return o personagem que colide, ou null
     */
    private Personagem checarColisoes() {
        for (Personagem p : personagens) {
            if (p == this)
                continue;
            if (colide(p))
                return p;
        }

        return null;
    }
}