package com.github.lucasgpulcinelli.clienteJavafx;

import java.io.IOError;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

/**
 * Main inicia a aplicação da tela inicial do cliente de Javafx.
 *
 * @author Lucas Eduardo Gulka Pulcinelli, nUSP 12547336
 *
 * @see ControladorJogo
 * @see GraficosJavafx
 */
public class Main extends Application {
    /** se a música da splash screen deve parar agora */
    private static boolean musicaDeveParar = false;

    /** o áudio da música inicial */
    private static AudioClip musicaInicial = null;

    /**
     * pararMusica para a música da splash screen e a thread que cuida do seu
     * looping.
     */
    public static void pararMusica() {
        musicaDeveParar = true;
        if (musicaInicial == null) {
            return;
        }
        musicaInicial.stop();
    }

    /**
     * start inicia a aplicação de javafx na tela inicial, registra a cena do
     * {@link LeitorInput} e inicia a música de fundo da splash screen.
     *
     * @param palco o palco a aplicação do javafx
     * @throws IOException se não for possível processar a ScreenInicial.fxml
     *
     * @see ControladorScreenInicial
     */
    @Override
    public void start(Stage palco) throws IOException {
        Parent documentoFXML = FXMLLoader.load(
                getClass().getResource("/ScreenInicial.fxml"));

        Scene cena = new Scene(documentoFXML);
        LeitorInput.registrarCena(cena);

        StackPane sp = (StackPane) cena.lookup("#PainelCentral");

        BackgroundImage bg = new BackgroundImage(
                new Image(this.getClass().getResource("/res/background.png").toString()),
                null, null, null, null);

        sp.setBackground(new Background(bg));

        // inicia a música de fundo
        new Thread(() -> {
            musicaInicial = new AudioClip(this.getClass().getResource("/res/inicial.wav").toString());
            while (!musicaDeveParar) {
                musicaInicial.play();
                try {
                    Thread.sleep(95000);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        palco.setScene(cena);
        palco.show();
    }

    /**
     * main é a função que inicia o código de javaFx.
     *
     * @param args passado diretamente para o processamento de javaFx.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
