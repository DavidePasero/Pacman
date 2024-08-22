import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Giallo: Fantasmi()
{
    var icon_path = "sprite"+File.separator+"Fantasmi"+File.separator+"Clyde.png"
    override var sprite: BufferedImage = ImageIO.read(File(icon_path)) as BufferedImage
    override var spriteCorrente: BufferedImage = sprite
    override lateinit var percorso: Array<Pacman.direzione>
    override var speed = 13
    var gialloDirettoSpawn = false//indica se il fantasma giallo sta andando allo spawn

    override fun obiettivo()//se é distante da pacman 8 o meno caselle torna allo spawn, poi gli da la caccia, e poi torna allo spawn
    {
        when(modalita) {
            Mode.CACCIA -> {
                nodoSpavento = spawn
                speed = 13
                spriteCorrente = sprite
                if (gialloDirettoSpawn) {
                    if (posizione == spawn) {
                        percorso = ricercaPercorso(posizione, posPacman, mappa)
                        gialloDirettoSpawn = false
                    } else
                        percorso = ricercaPercorso(posizione, spawn, mappa)
                } else {
                    percorso = ricercaPercorso(posizione, posPacman, mappa)
                    if (percorso.size < 9)
                        gialloDirettoSpawn = true
                }
            }
            Mode.SPAVENTO -> spavento()
            Mode.IN_LETARGO -> letargo()
        }
    }
}