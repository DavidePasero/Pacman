import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Rosso: Fantasmi()
{
    var icon_path = "sprite"+File.separator+"Fantasmi"+File.separator+"Blinky.png"
    override var sprite: BufferedImage = ImageIO.read(File(icon_path)) as BufferedImage
    override var spriteCorrente: BufferedImage = sprite
    override lateinit var percorso: Array<Pacman.direzione>
    override var speed = 16

    override fun obiettivo()
    {
        when(modalita) {
            Mode.CACCIA -> {
                speed = 16
                nodoSpavento = spawn
                spriteCorrente = sprite
                percorso = ricercaPercorso(posizione, posPacman, mappa)
            }
            Mode.SPAVENTO -> spavento()
            Mode.IN_LETARGO -> letargo()
        }
    }
}