import java.awt.Point
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

//A*:
//I nodi saranno le nostre caselle della mappa, il costo di una casella all'altra è di 1, per fare la h(n) semplicemente si fa il teorema di pitagora. I nodi vengono numerati da nodo(0,0) a nodo(mappa.width - 1, mappa.height - 1)
fun ricercaPercorso(partenza: Point, arrivo: Point, mappa: Mappa): Array<Pacman.direzione>
{
    fun funzioneEuristica(partenza: Point, arrivo: Point): Double
    {
        return sqrt((abs((arrivo.x - partenza.x).toDouble())).pow(2) + (abs((arrivo.y - partenza.y).toDouble())).pow(2) )
    }

    var arrayPercorso = ArrayList<Pacman.direzione>(0)
    var openSet = ArrayList<Nodo>(0)
    var closedSet = ArrayList<Nodo>(0)

    var nodoPartenza = Nodo(partenza, null, funzioneEuristica(partenza, arrivo))
    openSet.add(nodoPartenza)
    closedSet.add(nodoPartenza)
    var nodoMinore = nodoPartenza
    var ultimoNodo = nodoPartenza
    var posUltimoNodo = nodoPartenza.posNodo
    //var primoCiclo = true
    /************************funziona********************************/
    while((posUltimoNodo != arrivo) && ((openSet.isNotEmpty()/* || primoCiclo*/)))
    {
        openSet.remove(nodoMinore)
        //primoCiclo = false
        fun cercaInClosedSet(posizioneNodo: Point): Boolean//restituisce true se il nodo in posizione posizioneNodo non é giá presente nel closedSet
        {
            for(i in 0 until closedSet.size)
            {
                if(posizioneNodo == closedSet[i].posNodo)
                    return false
            }
            return true
        }

        fun cercaInOpenSet(nodo: Nodo): Boolean//restituisce true se il nodo é da aggiungere
        {
            var nodoDaAggiungere = true//indica se il nodo corrente é da aggiungere, non é da aggiungere solo nel caso in cui sia gia nell'openSet
            for(i in 0 until openSet.size) {
                if (openSet[i].posNodo == nodo.posNodo) {
                    if (openSet[i].costo > nodo.costo)
                        openSet[i] = nodo
                    nodoDaAggiungere = false
                    break
                }
            }
            return nodoDaAggiungere
        }

        fun cercaMinoreCosto(): Int//il nome parla da se, cerca il nodo nell'open set con costo minore
        {
            var minore = 0
            for(i in 0 until openSet.size)
            {
                if(openSet[minore].costo > openSet[i].costo)
                    minore = i
            }
            return minore
        }
        //si parte a controllare la casella a Nord e si prosegue in verso orario. Le caselle che non sono "MURO" vengono aggiunte all'open set e ne viene calcolata la funzione di costo
        if((posUltimoNodo.y - 1 >= 0) && (mappa.mappa[posUltimoNodo.y - 1][posUltimoNodo.x] != Gioco.Sprite.MURO) && (cercaInClosedSet(Point(posUltimoNodo.x, posUltimoNodo.y - 1))))
        {//viene aggiunto un nodo nell'open set, alla funzione euristica viene aggiunto un 1.0 perche é la distanza tra una casella e l'altra
            var nodo = Nodo(Point(posUltimoNodo.x, posUltimoNodo.y - 1), ultimoNodo, funzioneEuristica(Point(posUltimoNodo.x, posUltimoNodo.y - 1), arrivo) + 1.0)
            if(cercaInOpenSet(nodo))
                openSet.add(nodo)
        }
        if((posUltimoNodo.x + 1 < mappa.mappa[0].size) && (mappa.mappa[posUltimoNodo.y][posUltimoNodo.x + 1] != Gioco.Sprite.MURO) && (cercaInClosedSet(Point(posUltimoNodo.x + 1, posUltimoNodo.y))))
        {
            var nodo = Nodo(Point(posUltimoNodo.x + 1, posUltimoNodo.y), ultimoNodo, funzioneEuristica(Point(posUltimoNodo.x + 1, posUltimoNodo.y), arrivo) + 1.0)
            if(cercaInOpenSet(nodo))
                openSet.add(nodo)
        }
        if((posUltimoNodo.y + 1 < mappa.mappa.size) && (mappa.mappa[posUltimoNodo.y + 1][posUltimoNodo.x] != Gioco.Sprite.MURO) && (cercaInClosedSet(Point(posUltimoNodo.x, posUltimoNodo.y + 1))))
        {
            var nodo = Nodo(Point(posUltimoNodo.x, posUltimoNodo.y + 1), ultimoNodo, funzioneEuristica(Point(posUltimoNodo.x, posUltimoNodo.y + 1), arrivo) + 1.0)
            if(cercaInOpenSet(nodo))
                openSet.add(nodo)
        }
        if((posUltimoNodo.x - 1 >= 0) && (mappa.mappa[posUltimoNodo.y][posUltimoNodo.x - 1] != Gioco.Sprite.MURO) && (cercaInClosedSet(Point(posUltimoNodo.x - 1, posUltimoNodo.y))))
        {
            var nodo = Nodo(Point(posUltimoNodo.x - 1, posUltimoNodo.y), ultimoNodo, funzioneEuristica(Point(posUltimoNodo.x - 1, posUltimoNodo.y), arrivo) + 1.0)
            if(cercaInOpenSet(nodo))
                openSet.add(nodo)
        }

        if(openSet.isNotEmpty()) {
            nodoMinore = openSet[cercaMinoreCosto()]//rappresenta il nodo con costo minore ALL'INTERNO DELL'OPEN SET
            closedSet.add(nodoMinore)

            ultimoNodo = nodoMinore
            posUltimoNodo = nodoMinore.posNodo
        }
    }

    if((posUltimoNodo == arrivo)) {
        //ricostruiamo il percorso
        var arrayPercorsoRicostruito = ArrayList<Nodo?>(0)
        var nodoDaAggiungere = ultimoNodo as Nodo?
        while (nodoDaAggiungere != null) {
            arrayPercorsoRicostruito.add(nodoDaAggiungere)
            nodoDaAggiungere = nodoDaAggiungere?.nodoDiPartenza
        }
        arrayPercorsoRicostruito.reverse()

        for (i in 0 until arrayPercorsoRicostruito.size - 1) {
            when (Point(
                arrayPercorsoRicostruito[i + 1]!!.posNodo.x - arrayPercorsoRicostruito[i]!!.posNodo.x,
                arrayPercorsoRicostruito[i + 1]!!.posNodo.y - arrayPercorsoRicostruito[i]!!.posNodo.y
            )) {
                Point(0, -1) -> arrayPercorso.add(Pacman.direzione.NORTH)
                Point(+1, 0) -> arrayPercorso.add(Pacman.direzione.EAST)
                Point(0, +1) -> arrayPercorso.add(Pacman.direzione.SOUTH)
                Point(-1, 0) -> arrayPercorso.add(Pacman.direzione.WEST)
            }
        }
        if(arrayPercorso.isNotEmpty())
            return arrayPercorso.toTypedArray()
        else {
            arrayPercorso.add(Pacman.direzione.NONE)
            return arrayPercorso.toTypedArray()
        }
    }
    else
    {
        arrayPercorso.add(Pacman.direzione.NONE)
        return arrayPercorso.toTypedArray()
    }
}

private class Nodo(posNodo: Point, nodoDiPartenza: Nodo?, funzioneDiCosto: Double)//posNodo indica la posizione del nodo che stiamo creando, nodoDiPartenza indica da che nodo stiamo arrivando, quindi se siamo in nodo(2,1) e siamo arrivati dal nodo(1,1) allora nodoDiPartenza = 1,1
{
    var posNodo = posNodo
    var nodoDiPartenza = nodoDiPartenza
    var costo = funzioneDiCosto
}
