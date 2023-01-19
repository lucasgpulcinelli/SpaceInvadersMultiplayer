package com.github.lucasgpulcinelli.clienteTerminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.github.lucasgpulcinelli.comunicacao.Acao;

/**
 * LeitorInput é responsável por obter as entradas de teclas pelo terminal para
 * o {@link clienteTerminal}.
 * Esta classe funciona por meio de uma thread exclusiva para esse
 * processamento, coletando inputs a cada 1/60s em {@link run} e retornando o
 * valor lido em {@link getAcao}.
 *
 * @see comunicacao.Acao
 */
public class LeitorInput extends Thread {
    private boolean esperando = false;
    private Acao acao = Acao.NENHUMA_ACAO;
    private boolean deveParar = false;

    /**
     * getAcao pega a ação atual lida pela thread leitora.
     *
     * @return a ação do frame atual
     */
    public Acao getAcao() {
        Acao retorno = acao;
        acao = Acao.NENHUMA_ACAO;
        return retorno;
    }

    /**
     * esperarTiro para a execução da thread que chama o método (mas não da thread
     * leitora) até que o usuário atire.
     * Pode ser útil para desenhar na tela informações como ganho de nível, perda de
     * vida, etc.
     *
     * @throws InterruptedException caso a thread atual tenha sido interrompida por
     *                              um sinal do sistema operacional
     */
    synchronized public void esperarTiro() throws InterruptedException {
        esperando = true;
        wait();
    }

    /**
     * run é o método principal da classe, chamado após um start().
     * A cada 1/60 segundos, vai atualizar o valor retornado em {@link getAcao}.
     *
     * Caso o sistema operacional utilizado seja linux, uma otimização é feita para
     * deixar o programa mais interativo: um programa é utilizado para fazer com que
     * não seja necessário apertar enter a cada entrada de teclado.
     *
     * Caso haja uma thread esperando via {@link esperarTiro}, não envia a ação mas
     * notifica a thread que o tiro foi dado caso esse seja o input.
     */
    @Override
    public void run() {
        try {
            BufferedReader leitor = new BufferedReader(
                    new InputStreamReader(System.in));

            if (System.getProperty("os.name").equals("Linux")) {
                // desabilita o enter como buffering do programa
                String[] cmd = { "/bin/sh", "-c", "stty cbreak -echo </dev/tty" };
                Runtime.getRuntime().exec(cmd).waitFor();
            }

            while (!deveParar) {
                if (!leitor.ready()) {
                    Thread.sleep(16, 666); // 60 fps
                    continue;
                }

                int lido = leitor.read();
                char charlido = (char) lido;

                switch (charlido) {
                    case 'a':
                        acao = Acao.ESQUERDA;
                        break;
                    case 'd':
                        acao = Acao.DIREITA;
                        break;
                    case 'w':
                    case ' ':
                        if (!esperando) {
                            acao = Acao.ATIRAR;
                            break;
                        }
                        esperando = false;
                        synchronized (this) {
                            // retorna a thread do esperarTiro a execução normal
                            notify();
                            acao = Acao.ENTRAR_JOGO;
                        }
                        break;
                    case 'q':
                    case 3:
                        // ctrl+C
                        acao = Acao.SAIR_JOGO;
                        return;
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Thread de movimento interrompida");
            System.exit(0);
        } catch (IOException e) {
            System.err.printf("Erro na thead de movimento: %s\n", e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * parar faz com que a thread de leitura em {@link run} pare de coletar input.
     * O valor retornado por getAcao se torna sempre o mesmo que no último frame
     * lido.
     */
    public void parar() {
        deveParar = true;
    }
}
