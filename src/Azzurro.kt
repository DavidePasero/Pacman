import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Azzurro: Fantasmi()
{
    override var sprite: BufferedImage = ImageIO.read(File("sprite\\Fantasmi\\Inky.png")) as BufferedImage
    override var spriteCorrente: BufferedImage = sprite
    override lateinit var percorso: Array<Pacman.direzione>
    override var speed = 19

    override fun obiettivo()//va 4 caselle davanti a pacman, se non é una posizione disponibile allora va alla sua posizione
    {
        when(modalita) {
            Mode.CACCIA -> {
                speed = 19
                nodoSpavento = spawn
                spriteCorrente = sprite
                var obiettivo = posPacman
                when (direzionePacman) {
                    Pacman.direzione.EAST -> {
                        if ((posPacman.x + 4 > mappa.mappa[0].size - 1) || (false == mappa.mappaRaggiungibile[posPacman.y][posPacman.x + 4]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y][posPacman.x + 4]))
                            ;
                        else
                            obiettivo = Point(posPacman.x + 4, posPacman.y)
                    }
                    Pacman.direzione.SOUTH -> {
                        if ((posPacman.y + 4 > mappa.mappa.size - 1) || (false == mappa.mappaRaggiungibile[posPacman.y + 4][posPacman.x]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y + 4][posPacman.x]))
                            ;
                        else
                            obiettivo = Point(posPacman.x, posPacman.y + 4)
                    }
                    Pacman.direzione.WEST -> {
                        if ((posPacman.x - 4 < 0) || (false == mappa.mappaRaggiungibile[posPacman.y][posPacman.x - 4]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y][posPacman.x - 4]))
                            ;
                        else
                            obiettivo = Point(posPacman.x - 4, posPacman.y)
                    }
                    Pacman.direzione.NORTH -> {
                        if ((posPacman.y - 4 < 0) || (false == mappa.mappaRaggiungibile[posPacman.y - 4][posPacman.x]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y - 4][posPacman.x]))
                            ;
                        else
                            obiettivo = Point(posPacman.x, posPacman.y - 4)
                    }
                }

                        percorso = ricercaPercorso (posizione, obiettivo, mappa)
            }
            Mode.SPAVENTO -> spavento()
            Mode.IN_LETARGO -> letargo()
        }
    }
}