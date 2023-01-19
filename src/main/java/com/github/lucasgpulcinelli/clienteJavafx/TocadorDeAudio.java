package com.github.lucasgpulcinelli.clienteJavafx;

import com.github.lucasgpulcinelli.comunicacao.Evento;
import java.net.URL;
import java.util.HashMap;
import javafx.scene.media.AudioClip;

/**
 * TocadorDeAudio controla todos os áudios tocados durante o loop principal do
 * jogo. Não inclui a música de fundo da splash screen, mas inclui todos os
 * efeitos especiais de áudio e a música de fundo do jogo principal.
 *
 * @see ControladorJogo
 */
public class TocadorDeAudio {
    /** todos os áudios relacionados a eventos do jogo */
    private final HashMap<Evento, AudioClip> audios = new HashMap<>();

    /**
     * cria um novo Tocador de áudio e inicia a música de fundo do jogo em
     * background e seu looping.
     */
    public TocadorDeAudio() {
        for (Evento e : Evento.values()) {
            URL url = this.getClass().getResource("/res/" + e.name() + ".wav");
            if (url == null) {
                continue;
            }

            AudioClip ac = new AudioClip(url.toString());
            audios.put(e, ac);
        }

        // inicia a música de fundo
        new Thread(() -> {
            URL url = this.getClass().getResource("/res/musica_background.wav");
            AudioClip musica_background = new AudioClip(url.toString());
            while (true) {
                musica_background.play();
                try {
                    // 29 segundos é o tamanho do áudio da música de fundo
                    Thread.sleep(29000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    /**
     * tocar toca uma música relacionada a um evento do jogo, caso a música
     * exista.
     *
     * @param e o evento.
     */
    public void tocar(Evento e) {
        AudioClip ac = audios.get(e);
        if (ac == null) {
            return;
        }
        ac.play();
    }
}
