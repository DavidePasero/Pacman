import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import java.io.Serializable
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import kotlin.test.assertTrue

class Mappa(): Serializable
{
    var dimensioneMappa = Dimension(27,31)
    var mappa = inizializzaMappa()
    lateinit var mappaRaggiungibile: Array<Array<Boolean>>//indica per ogni blocco se é raggiungibile o no
    lateinit var mappaNodi: Array<Array<Boolean>>
    var isComplete = false//indica se la mappa cosí com'é é salvabile oppure no
    init
    {
        inzializzaMappaRaggiungibileMappaNodi()
    }

    fun inizializzaMappa(): Array<Array<Gioco.Sprite>>
    {
        var mappa = arrayOf<Array<Gioco.Sprite>>()
        for(i in 0 until dimensioneMappa.height)
        {
            var rigaMappa = arrayOf<Gioco.Sprite>()
            for(j in 0 until dimensioneMappa.width)
            {
                rigaMappa += Gioco.Sprite.BLANK
            }

            mappa += rigaMappa
        }

        return mappa
    }

    fun inzializzaMappaRaggiungibileMappaNodi()
    {
        mappaRaggiungibile = arrayOf<Array<Boolean>>()
        mappaNodi = arrayOf()
        for(i in 0 until dimensioneMappa.height)
        {
            var rigaMappaRaggiungibile = arrayOf<Boolean>()
            var rigaMappaNodi = arrayOf<Boolean>()
            for(j in 0 until dimensioneMappa.width)
            {
                rigaMappaRaggiungibile += true
                rigaMappaNodi += false
            }

            mappaRaggiungibile += rigaMappaRaggiungibile
            mappaNodi += rigaMappaNodi
        }

    }

    fun checkCompleta(): Boolean//controlla se la mappa this rispetta le regole
    {
        var quantiSpawn = Array<Int>(5){i -> 0}//contiene il numero di qua
        var quanteMonetePiccole = 0
        var quanteMoneteGrandi = 0
        var spawnPacman = Point(0, 0)
        for(i in 0 until dimensioneMappa.height)
        {
            for(j in 0 until dimensioneMappa.width)
            {
                when(mappa[i][j])
                {
                    Gioco.Sprite.SPAWN_ROSSO -> quantiSpawn[0] += 1
                    Gioco.Sprite.SPAWN_GIALLO -> quantiSpawn[1] += 1
                    Gioco.Sprite.SPAWN_BIANCO -> quantiSpawn[2] += 1
                    Gioco.Sprite.SPAWN_AZZURRO -> quantiSpawn[3] += 1
                    Gioco.Sprite.SPAWN_PACMAN -> {quantiSpawn[4] += 1; spawnPacman = Point(j, i)}
                    Gioco.Sprite.PICCOLA_MONETA -> quanteMonetePiccole++
                    Gioco.Sprite.GRANDE_MONETA -> quanteMoneteGrandi++
                }
            }
        }

        //le regole sono che: non si puó mettere piú di uno spawn per entitá, non si possono mettere meno di 10 monete piccole, e non si possono mettere 0 monete grandi o piú di 10
        for(i in 0 until quantiSpawn.size)
        {
            if(quantiSpawn[i] != 1)
                return false
        }
        if(quanteMonetePiccole < 10)
        {
            isComplete = false
            return false
        }
        else if((quanteMoneteGrandi < 1) or (quanteMoneteGrandi > 10))
        {
            isComplete = false
            return false
        }

        for(i in 0 until mappa.size)
        {
            for(j in 0 until mappa[0].size)
            {
                if((mappa[i][j] != Gioco.Sprite.MURO) && (mappa[i][j] != Gioco.Sprite.BLANK) && (mappa[i][j] != Gioco.Sprite.SPAWN_PACMAN))
                {
                    if(Pacman.direzione.NONE == ricercaPercorso(Point(j, i), spawnPacman, this)[0])
                    {
                        isComplete = false
                        return false
                    }
                    else
                    {
                        fun nodo(punto: Point): Boolean//indica se il punto della mappa x,y é un nodo, ovvero un punto dove un fantasma puó cambiare direzione (quindi un punto dove si puó solo andare NORTH-SOUTH non é un nodo, lo é invece NORTH-WEST ad esempio)
                        {
                            var quanteDirezioni = 0
                            if((punto.y - 1 >= 0) && (mappa[punto.y - 1][punto.x] != Gioco.Sprite.MURO))
                                quanteDirezioni++
                            if((punto.x + 1 < mappa[0].size) && (mappa[punto.y][punto.x + 1] != Gioco.Sprite.MURO))
                                quanteDirezioni++
                            if((punto.y + 1 < mappa.size) && (mappa[punto.y + 1][punto.x] != Gioco.Sprite.MURO))
                                quanteDirezioni++
                            if((punto.x - 1 >= 0) && (mappa[punto.y][punto.x - 1] != Gioco.Sprite.MURO))
                                quanteDirezioni++
                            return quanteDirezioni > 2
                        }
                        mappaNodi[i][j] = nodo(Point(j, i))
                    }
                }
                else if(mappa[i][j] == Gioco.Sprite.MURO)
                {
                    mappaRaggiungibile[i][j] = false
                }
                else if(mappa[i][j] == Gioco.Sprite.BLANK)
                {
                    if(Pacman.direzione.NONE == ricercaPercorso(Point(j, i), spawnPacman, this)[0])
                        mappaRaggiungibile[i][j] = false
                }
            }
        }

        isComplete = true
        return true
    }
}