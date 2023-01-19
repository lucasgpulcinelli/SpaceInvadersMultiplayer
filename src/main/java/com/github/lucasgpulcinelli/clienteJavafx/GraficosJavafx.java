package com.github.lucasgpulcinelli.clienteJavafx;

import com.github.lucasgpulcinelli.comunicacao.Desenhavel;
import com.github.lucasgpulcinelli.comunicacao.Sprite;
import com.github.lucasgpulcinelli.grafico.InterfaceGrafica;
import com.github.lucasgpulcinelli.sistema.TelaJogo;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * GraficosJavafx implementa a interface gráfica de javaFx para o controlador
 * de jogo principal.
 */
public class GraficosJavafx implements InterfaceGrafica {
    /** painel principal onde todo o jogo estará */
    private final StackPane painel;
    /** gráficos para o desenho de imagens */
    private final GraphicsContext graficos;

    /** texto de avisos importantes (ganho de nível, perda de jogo, etc.) */
    private final Text textoAvisos = new Text();
    /** texto da score do jogador */
    private final Text textoScore = new Text();
    /** texto das vidas do jogador */
    private final Text textoVidas = new Text();

    /** imagens associadas a todos os sprites */
    private final HashMap<Sprite, Image> imagens = new HashMap<>();
    /** imagens secundárias associadas a sprites de inimigos (para animação) */
    private final HashMap<Sprite, Image> imagensSecundarias = new HashMap<>();

    /** se é necessário usar a segunda imagem ou a primeira (para animação) */
    private boolean UsarSegundaImagem = false;

    /** tamanho em x da tela do javaFx */
    private static final int telaX = 1050;
    /** tamanho em y da tela do javaFx */
    private static final int telaY = 600;

    /**
     * Cria uma nova interface gráfica dentro de um painel.
     * O painel deve ter no mínimo 1050x600 pixels.
     *
     * @param painel o painel principal onde todo o jogo estará
     */
    public GraficosJavafx(StackPane painel) {
        this.painel = painel;

        // canvas de jogo onde as imagens estarão
        Canvas c = new Canvas(telaX, telaY);
        StackPane.setAlignment(c, Pos.TOP_CENTER);
        graficos = c.getGraphicsContext2D();
        Platform.runLater(() -> {
            initTextos();
            painel.getChildren().add(c);
        });

        // le todas as imagens da pasta de resources
        for (Sprite s : Sprite.values()) {
            imagens.put(s, new Image(this.getClass().getResource("/res/" + s.name() + ".png").toString()));
        }

        // le todas as imagens secundárias de inimigos
        Sprite[] inimigos = { Sprite.INIMIGO1, Sprite.INIMIGO2, Sprite.INIMIGO3 };
        for (Sprite s : inimigos) {
            imagensSecundarias.put(s,
                    new Image(this.getClass().getResource("/res/" + s.name() + "_2.png").toString()));
        }

        // cria a thread que cuida da animação
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                UsarSegundaImagem = !UsarSegundaImagem;
            }
        }).start();
    }

    /**
     * initTextos inicializa os textos da interface gráfica.
     */
    private void initTextos() {
        StackPane.setAlignment(textoAvisos, Pos.CENTER);
        textoAvisos.setFill(Color.WHITE);
        textoAvisos.setFont(Font.font(Font.getDefault().getName(),
                FontWeight.BOLD, FontPosture.REGULAR, 40));
        textoAvisos.setStroke(Color.BLACK);
        textoAvisos.setStrokeWidth(2);
        textoAvisos.setVisible(false);

        StackPane.setAlignment(textoScore, Pos.TOP_LEFT);
        textoScore.setFill(Color.WHITE);
        textoScore.setFont(Font.font(30));
        textoScore.setText("Score: 00000");

        StackPane.setAlignment(textoVidas, Pos.TOP_RIGHT);
        textoVidas.setLayoutX(-100);
        textoVidas.setFill(Color.WHITE);
        textoVidas.setFont(Font.font(30));
        textoVidas.setText("Vidas: 3");

        painel.getChildren().add(textoAvisos);
        painel.getChildren().add(textoScore);
        painel.getChildren().add(textoVidas);
    }

    /**
     * escreverTexto escreve um aviso na tela.
     *
     * @param texto o texto a ser escrito
     */
    private void escreverTexto(String texto) {
        Platform.runLater(() -> {
            // o texto de avisos é removido e colocado novamente para não ficar
            // atrás de imagens
            painel.getChildren().remove(textoAvisos);
            textoAvisos.setText(texto);
            textoAvisos.setVisible(true);
            painel.getChildren().add(textoAvisos);
        });
    }

    /**
     * @param score a nova score
     * @param vidas o novo número de vidas
     */
    private void setScoreEVidas(int score, int vidas) {
        Platform.runLater(() -> {
            textoScore.setText("Score: " + String.format("%05d", score));
            textoVidas.setText("Vidas: " + Integer.toString(vidas));
        });
    }

    @Override
    public void desenharTela(int score, int vidas, List<Desenhavel> desenhaveis) {
        textoAvisos.setVisible(false);
        setScoreEVidas(score, vidas);

        Platform.runLater(() -> {
            graficos.clearRect(0, 0, telaX, telaY);
            for (Desenhavel d : desenhaveis) {
                Image img = imagens.get(d.getSprite());

                if (UsarSegundaImagem) {
                    Image img2 = imagensSecundarias.get(d.getSprite());
                    if (img2 != null) {
                        img = img2;
                    }
                }

                double x = d.getX() * telaX / TelaJogo.MAX_X - img.getWidth() / 2;
                // y tem um offset para dar espaço aos textos de score e vidas
                double y = d.getY() * (telaY - 50) / TelaJogo.MAX_Y - img.getHeight() / 2 + 50;

                graficos.drawImage(img, x, y);
            }
        });
    }

    @Override
    public void printPerdeuVida() {
        escreverTexto("Perdeu Vida! Aperte 'w'");
    }

    @Override
    public void printGanhouNivel(int nivel) {
        escreverTexto("Ganhou Nivel " + Integer.toString(nivel) + "! Aperte 'w'");
    }

    @Override
    public void printSplashScreen() {
        Main.launch(new String[0]);
    }

    @Override
    public void printPerdeuJogo() {
        escreverTexto("...GAME OVER...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.exit(0);
    }

    /**
     * coloca um erro na tela e sai do jogo com um sinal de erro.
     *
     * @param erro o texto do erro
     */
    public void printErro(String erro) {
        escreverTexto(erro);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
                System.exit(1);
        }).start();
    }
}
