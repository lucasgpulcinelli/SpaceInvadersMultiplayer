package com.github.lucasgpulcinelli.clienteJavafx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.StackPane;

/**
 * ControladorScreenInicial implementa a lógica da tela inicial.
 */
public class ControladorScreenInicial {
    /** a área de texto para o ip do servidor */
    @FXML
    private TextField areaDeTexto;

    /**
     * acaoDeEnter inicia o jogo principal quando um ip é colocado na área de
     * texto.
     *
     * @param evento inutilizado
     */
    @FXML
    private void acaoDeEnter(ActionEvent evento) {
        StackPane painel = new StackPane();

        BackgroundImage bg = new BackgroundImage(
                new Image(this.getClass().getResource("/res/background.png").toString()),
                null, null, null, null);

        painel.setBackground(new Background(bg));

        Main.pararMusica();
        areaDeTexto.getScene().setRoot(painel);

        ControladorJogo cj = new ControladorJogo(areaDeTexto.getText(), painel);
        cj.start();
    }
}
