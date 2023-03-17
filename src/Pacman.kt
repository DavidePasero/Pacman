import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import java.awt.image.AffineTransformOp
import java.util.Collections.rotate
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.nio.Buffer


class Pacman(spawnPacman: Point)
{
    enum class direzione {NONE, NORTH, WEST, EAST, SOUTH}

    var posPacmanMatrice: Point
    var spriteNone = ImageIO.read(File("sprite/Pacman/pacmanNone.png")) as BufferedImage
    var arraySpriteAperto = arrayOf<BufferedImage>()
    var arraySpriteChiuso = arrayOf<BufferedImage>()
    var sprite1o2 = 1

    //variabili utili per LabelMappa
    var spriteCorrente: BufferedImage
    var direzioneCorrente = direzione.NONE
    var nextDirezione = direzioneCorrente
    var speed = 15//la speed é in numero di tic, quindi 5 vuol dire che si sposta ogni 5 tic e cioé ogni 25ms
    var nTic = 0//aumenta di 1 ad ogni tic fino a tornare a 0 al tic numero speed
    var isStill = true

    init
    {
        posPacmanMatrice = spawnPacman
        generaDirezioniPacman()
        spriteCorrente = BufferedImage(arraySpriteAperto[0].height, arraySpriteAperto[0].width, TYPE_INT_ARGB)
        changeDirection(direzione.EAST)
    }

    fun changePosPacman(nuovaPos: Point)
    {
        posPacmanMatrice = nuovaPos
    }

    fun changeDirection(nuovaDirezione: direzione)
    {//ruota spriteCorrente
        if(nuovaDirezione != direzioneCorrente) {//se nuovaDirezione é uguale alla direzioneCorrente allora nTic deve rimanere uguale (e anche nextDirezione)
            nTic = 0
            direzioneCorrente = nuovaDirezione
            nextDirezione = direzione.NONE
        }
        isStill = false

        //var gradi = when(nuovaDirezione)
        when(direzioneCorrente)
        {
            direzione.EAST ->
            {
                if(sprite1o2 == 1)
                    spriteCorrente = arraySpriteAperto[0]
                else
                    spriteCorrente = arraySpriteChiuso[0]
            }
            direzione.SOUTH ->
            {
                if(sprite1o2 == 1)
                    spriteCorrente = arraySpriteAperto[1]
                else
                    spriteCorrente = arraySpriteChiuso[1]
            }
            direzione.WEST ->
            {
                if(sprite1o2 == 1)
                    spriteCorrente = arraySpriteAperto[2]
                else
                    spriteCorrente = arraySpriteChiuso[2]
            }
            direzione.NORTH ->
            {
                if(sprite1o2 == 1)
                    spriteCorrente = arraySpriteAperto[3]
                else
                    spriteCorrente = arraySpriteChiuso[3]
            }
            else -> -1
        }
    }

    fun incrementaTic()//cambia lo sprite di pacman (con la bocca chiusa o aperta) e se nTic == speed allora cambia di posizione
    {
        if(nTic % ((speed / 3) * 2) == 0) {//cambia lo sprite da bocca aperta a chiusa ogni 2/3 di speed
            if (sprite1o2 == 1) {
                sprite1o2 = 2
                changeDirection(direzioneCorrente)
            } else {
                sprite1o2 = 1
                changeDirection(direzioneCorrente)
            }
        }

        if(nTic < speed)
        {
            nTic++
        }
        else//se é arrrivato a speed tic torna a 0 e lui va nella nuova casella
        {
            when(direzioneCorrente)
            {
                direzione.WEST -> changePosPacman(Point(posPacmanMatrice.x - 1, posPacmanMatrice.y))
                direzione.NORTH -> changePosPacman(Point(posPacmanMatrice.x,  posPacmanMatrice.y - 1))
                direzione.EAST -> changePosPacman(Point(posPacmanMatrice.x + 1, posPacmanMatrice.y))
                direzione.SOUTH -> changePosPacman(Point(posPacmanMatrice.x, posPacmanMatrice.y + 1))
            }
            nTic = 0
        }
    }

    fun generaDirezioniPacman()//genera gli array di sprite pacman ruotati in tutte le direzioni
    {
        fun gira(g: Graphics2D, rads: Double, immagineCopiata: BufferedImage)//ruota immagineCopiata di gradi rads e la disegna su g
        {
            g.translate((immagineCopiata.height - immagineCopiata.width) / 2, (immagineCopiata.height - immagineCopiata.width) / 2)
            g.rotate(rads, (immagineCopiata.height / 2).toDouble(), (immagineCopiata.width / 2).toDouble())
            g.drawRenderedImage(immagineCopiata, null)
        }
        fun simmetriaOrizzontale(immagineCopiata: BufferedImage): BufferedImage//flippa l'immagine verticalmente
        {
            var tx = AffineTransform.getScaleInstance(-1.0, 1.0)
            tx.translate(-immagineCopiata.getWidth(null).toDouble(), 0.0)
            var op = AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
            return (op.filter(immagineCopiata, null))
        }

        var pacmanAperto = ImageIO.read(File("sprite/Pacman/pacman.png")) as BufferedImage

        for(i in 0 until 4) {
            if(i != 2) {
                var copiaPacmanAperto = BufferedImage(pacmanAperto.width, pacmanAperto.height, TYPE_INT_ARGB)
                var g2d = copiaPacmanAperto.graphics as Graphics2D
                gira(g2d, Math.toRadians((i * 90).toDouble()), pacmanAperto)
                arraySpriteAperto += copiaPacmanAperto
            }
            else
            {
                var copiaPacmanAperto = simmetriaOrizzontale(pacmanAperto)
                arraySpriteAperto += copiaPacmanAperto
            }
        }

        var pacmanChiuso = ImageIO.read(File("sprite/Pacman/pacmanBoccaChiusa.png")) as BufferedImage

        for(i in 0 until 4) {
            if(i != 2) {
                var copiaPacmanChiuso = BufferedImage(pacmanAperto.width, pacmanAperto.height, TYPE_INT_ARGB)
                var g2d = copiaPacmanChiuso.graphics as Graphics2D
                gira(g2d, Math.toRadians((i * 90).toDouble()), pacmanChiuso)
                arraySpriteChiuso += copiaPacmanChiuso
            }
            else
            {
                var copiaPacmanChiuso = simmetriaOrizzontale(pacmanChiuso)
                arraySpriteChiuso += copiaPacmanChiuso
            }
        }
    }
}