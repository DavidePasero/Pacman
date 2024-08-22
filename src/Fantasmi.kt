import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs

abstract class Fantasmi
{
    enum class Mode{CACCIA, SCATTER, SPAVENTO, IN_LETARGO}//CACCIA segue pacman, SCATTER torna allo spawn e ci sta un po', SPAVENTO inizia a girare a caso per la mappa e se viene toccato da PACMAN entra in modalitá IN_LETARGO che consiste nell'andare allo spawn
    var arrayEnum: Array<Pacman.direzione> = arrayOf(Pacman.direzione.NORTH, Pacman.direzione.WEST, Pacman.direzione.EAST, Pacman.direzione.SOUTH)
    var arrayListEnum = ArrayList<Pacman.direzione>(4)

    var spriteSpavento = ImageIO.read(File("sprite"+File.separator+"Fantasmi"+File.separator+"fantasmaSpaventato.png")) as BufferedImage
    var spriteMezzoSpavento = ImageIO.read(File("sprite"+File.separator+"Fantasmi"+File.separator+"fantasmaMezzoSpaventato.png")) as BufferedImage
    var spriteLetargo = ImageIO.read(File("sprite"+File.separator+"Fantasmi"+File.separator+"InLetargo.png")) as BufferedImage

    lateinit var posizione: Point
    lateinit var spawn: Point
    lateinit var mappa: Mappa
    lateinit var posPacman: Point
    lateinit var direzionePacman: Pacman.direzione
    lateinit var modalita: Mode

    abstract var sprite: BufferedImage
    abstract var spriteCorrente: BufferedImage
    abstract var percorso: Array<Pacman.direzione>
    abstract var speed: Int

    var nTic = 0
    lateinit var nodoSpavento: Point//il fantasma si dirige a nodoSpavento quando é spaventato

    fun start(mappa: Mappa, spawn: Point, spawnPacman: Point)//tipo un costruttore, serve per inizializzare il fantasma quando si hanno piú informazioni, tipo la mappa, il proprio spawn e lo spawn di pacmna
    {
        this.nTic = 0
        this.mappa = mappa
        this.spawn = spawn
        this.nodoSpavento = Point(spawn.x, spawn.y)//valore di default
        this.posizione = spawn
        this.posPacman = spawnPacman
        this.direzionePacman = Pacman.direzione.NONE

        this.modalita = Mode.CACCIA
        this.spriteCorrente = sprite
        obiettivo()//cosí si inizializza anche percorso
    }

    fun incrementaTic(nuovaPosPacman: Point, nuovaDirezionePacman: Pacman.direzione)//aggiorna la posizione e la direzione di Pacman e cambia la propria posizione in base a percorso[0]
    {
        posPacman = nuovaPosPacman
        direzionePacman = nuovaDirezionePacman
        nTic++
        if(nTic == speed)
        {
            nTic = 0
            posizione = when (percorso[0])
            {
                Pacman.direzione.WEST ->  Point(posizione.x - 1, posizione.y)
                Pacman.direzione.NORTH -> Point(posizione.x, posizione.y - 1)
                Pacman.direzione.EAST ->  Point(posizione.x + 1, posizione.y)
                Pacman.direzione.SOUTH -> Point(posizione.x, posizione.y + 1)

                else -> posizione
            }
            obiettivo()
        }
    }

    abstract fun obiettivo()//la funzione che deve generare un percorso per pacman in base alla modalitá

    fun spavento()//quando si é in modalitá SPAVENTO tutti i fantasmi si comportano uguali, e cioé cambiano sprite e prendono un punto a caso disponibile nella mappa e ci vanno, quando lo raggiungono ne trovano un altro e ripetono tutto questo finché non vengono mangiati da pacman o tornano normali
    {
        speed = 30
        spriteCorrente = spriteSpavento
        if((nodoSpavento == spawn) || (posizione == nodoSpavento)) {
            var nodi = ArrayList<Point>()
            for (i in 0 until mappa.mappaRaggiungibile.size) {
                for (j in 0 until mappa.mappaRaggiungibile[0].size) {
                    if (mappa.mappaRaggiungibile[i][j]) {
                        nodi.add(Point(j, i))
                    }
                }
            }
            do {
                nodoSpavento = nodi[(Math.random() * (nodi.size - 1)).toInt()]
            } while ((nodoSpavento == spawn) || (nodoSpavento == posizione))
        }
        percorso = ricercaPercorso(posizione, nodoSpavento, mappa)
    }

    fun letargo()//quando si é in modalitá LETARGO tutti i fantasmi si comportano uguale, e cioé tornano allo spawn e cambino il loro sprite
    {
        nodoSpavento = spawn
        if(posizione == spawn)
        {
            modalita = Mode.CACCIA
            spriteCorrente = sprite
        }
        else {
            spriteCorrente = spriteLetargo
            speed = 8
            percorso = ricercaPercorso(posizione, spawn, mappa)
        }
    }
}