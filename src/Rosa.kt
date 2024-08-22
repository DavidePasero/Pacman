import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Rosa: Fantasmi()
{
    var icon_path = "sprite"+File.separator+"Fantasmi"+File.separator+"Pinky.png"
    override var sprite: BufferedImage = ImageIO.read(File(icon_path)) as BufferedImage
    override var spriteCorrente: BufferedImage = sprite
    override lateinit var percorso: Array<Pacman.direzione>
    override var speed = 19

    override fun obiettivo()//fa come il blu ma al posto di andare 4 posizioni davanti alla posizione di pacman, va 4 posizioni in piú rispetto a pacman in una direzione a caso
    {
        when(modalita) {
            Mode.CACCIA -> {
                speed = 19
                nodoSpavento = spawn
                spriteCorrente = sprite
                var obiettivo = posPacman
                for (i in 0 until arrayEnum.size) {
                    if (arrayEnum[i] == direzionePacman)
                        ;
                    else
                        arrayListEnum.add(arrayEnum[i])
                }
                var direzione: Pacman.direzione = arrayListEnum[(Math.random() * 3).toInt()]
                when (direzione) {
                    Pacman.direzione.EAST -> {
                        if ((posPacman.x + 6 > mappa.mappa[0].size - 1) || (false == mappa.mappaRaggiungibile[posPacman.y][posPacman.x + 6]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y][posPacman.x + 6]))
                            ;
                        else
                            obiettivo = Point(posPacman.x + 6, posPacman.y)
                    }
                    Pacman.direzione.SOUTH -> {
                        if ((posPacman.y + 4 > mappa.mappa.size - 1) || (false == mappa.mappaRaggiungibile[posPacman.y + 4][posPacman.x]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y + 4][posPacman.x]))
                            ;
                        else
                            obiettivo = Point(posPacman.x, posPacman.y + 4)
                    }
                    Pacman.direzione.WEST -> {
                        if ((posPacman.x - 2 < 0) || (false == mappa.mappaRaggiungibile[posPacman.y][posPacman.x - 2]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y][posPacman.x - 2]))
                            ;
                        else
                            obiettivo = Point(posPacman.x - 2, posPacman.y)
                    }
                    Pacman.direzione.NORTH -> {
                        if ((posPacman.y - 5 < 0) || (false == mappa.mappaRaggiungibile[posPacman.y - 5][posPacman.x]) || (Gioco.Sprite.MURO == mappa.mappa[posPacman.y - 5][posPacman.x]))
                            ;
                        else
                            obiettivo = Point(posPacman.x, posPacman.y - 5)
                    }
                }

                percorso = ricercaPercorso(posizione, obiettivo, mappa)
            }
            Mode.SPAVENTO -> spavento()
            Mode.IN_LETARGO -> letargo()
        }
    }
}