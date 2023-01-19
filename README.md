# Space Invaders Multiplayer

This project is a recreation of the space invaders arcade videogame made in java 8, using javafx. The game is written fully in brazilian portuguese, and all documentation aside from this preamble, the final note and LICENSE file is in that language.

Este projeto é uma recriação do jogo de arcade space invaders feito em java 8, usando javafx.

## Compilando o jogo
É necessário obter uma cópia da biblioteca jfxrt.jar (biblioteca base do javafx) e colocá-la no diretório lib/; após isso, basta instalar maven e utilizar `mvn package`. O arquivo .jar de saída estará no diretório target (claramente, para utilizar o jar é necessário ter uma instalação funcional do javafx).

É importante notar que há um subsistema funcional de áudio, entretanto alguns arquivos foram retirados por questão de direitos autorais. O jogo funciona normalmente sem esses áudios, mas os seguintes arquivos podem ser adicionados no diretório src/main/resources/res/ para completar a funcionalidade:
- GANHOU\_NIVEL.wav (som de ganho de nível)
- inicial.wav (música da splash screen)
- musica\_background.wav (música durante o jogo em si)
- PERDEU\_JOGO.wav (som de perda de jogo)
Também é interessante ver que que os áudios de background podem ter problemas de sobreposição de dois áudios ou momentos de silêncio.

## Final Note / Nota Final
This game uses a title screen asset from spaceinvaders.de;
Este jogo usa uma tela inicial de jogo pertencente a spaceinvaders.de.

