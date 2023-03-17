import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.io.File
import java.lang.System.exit
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

//al posto di avere una JLabel che fa da mappa con una sola BufferedImage potrei creare una matrice di JLabel le cui icone sono gli elementi, esse saranno contenute da un'altra JLabel dove si muovono gli sprite
class Gioco: JPanel(), ActionListener, KeyListener, ContieneMappa
{
    //var mappa = generaMappa()//mappa sará la matrice di Sprite
    //var mappaGrafica = generaMappaGrafica()//mappaGrafica sará l'immagine della mappa che verrá cambiata a runTime
    enum class Sprite {BLANK, MURO, PICCOLA_MONETA, GRANDE_MONETA, SPAWN_AZZURRO, SPAWN_BIANCO, SPAWN_GIALLO, SPAWN_ROSSO, PORTALE_VERDE, SPAWN_PACMAN}
    enum class gameStatus {MAPPA_NON_CARICATA, WAITING_START, STARTING, STARTED}

    private var spriteArray = arrayOf(Sprite.BLANK, Sprite.MURO, Sprite.PICCOLA_MONETA, Sprite.GRANDE_MONETA, Sprite.SPAWN_AZZURRO, Sprite.SPAWN_BIANCO, Sprite.SPAWN_GIALLO, Sprite.SPAWN_ROSSO, Sprite.PORTALE_VERDE, Sprite.SPAWN_PACMAN)//serve per facilitare l'accesso all'enum Sprite
    var immagini = leggiImmagini()//é un array di BufferedImage

    var immagineContenitoreMappa = (ImageIO.read(File("icons/ContenitoreMappa.png"))) as BufferedImage

    var dimensioneMatrice = Dimension(27, 31)
    var dimensioneImmagineSingola = Dimension(19, 19)
    var dimensioneImmagineMappa = Dimension(dimensioneMatrice.width * dimensioneImmagineSingola.width, dimensioneMatrice.height * dimensioneImmagineSingola.height)

    override var mappa = Mappa()
    var mappaDinamica = Mappa()

    var spawnPacman = Point(0, 0)
    var spawnRosso = Point(0, 0)
    var spawnGiallo = Point(0, 0)
    var spawnAzzurro = Point(0, 0)
    var spawnRosa = Point(0, 0)
    var quanteMonete = 0//indica quante monete (grandi e piccole) ci sono nella mappa

    var mappaGrafica = generaMappaGrafica()
    var labelMappa = LabelMappa()

    var pannelloMappa: JPanel

    var pulsanteCarica = AnimatedButton(Dimension(75, 40), "Carica")
    var pulsanteGioca = AnimatedButton(Dimension(75, 40), "Gioca")

    var viteRimaste = JLabel("Vite rimaste: ${labelMappa.nViteRimaste}")
    var xpGained = JLabel("XP: ${labelMappa.xp}")

    init
    {
        this.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        gbc.insets = Insets(20, 0, 0, 0)

        val pannelloPulsanti = JPanel()
        pannelloPulsanti.layout = GridBagLayout()
        val gbcPulsanti = GridBagConstraints()
        gbcPulsanti.insets = Insets(20, 0, 0, 0)

        pulsanteCarica.addActionListener(this)
        pulsanteCarica.actionCommand = "carica"

        pulsanteGioca.addActionListener(this)
        pulsanteGioca.actionCommand = "gioca"
        pulsanteGioca.isEnabled = false

        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 0
        pannelloPulsanti.add(pulsanteCarica, gbcPulsanti)

        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 1
        pannelloPulsanti.add(pulsanteGioca, gbcPulsanti)
        pannelloPulsanti.preferredSize = Dimension(pannelloPulsanti.preferredSize.width + 40, pannelloPulsanti.preferredSize.height + 20)

        pannelloMappa = PanelloMappa()
        pannelloMappa.preferredSize = Dimension(immagineContenitoreMappa.width, immagineContenitoreMappa.height)
        pannelloMappa.layout = GridBagLayout()
        val gbcMappa = GridBagConstraints()
        labelMappa.preferredSize = dimensioneImmagineMappa
        labelMappa.layout = GridLayout(dimensioneMatrice.height, dimensioneMatrice.width)
        labelMappa.icon = ImageIcon(mappaGrafica)
        gbcMappa.gridx = 0
        gbcMappa.gridy = 0
        pannelloMappa.add(labelMappa, gbcMappa)

        gbc.gridy = 0
        gbc.gridx = 0
        this.add(pannelloPulsanti, gbc)

        gbc.gridy = 1
        gbc.gridx = 0
        this.add(pannelloMappa, gbc)

        var pannelloPunti = JPanel()
        viteRimaste = JLabel("Vite rimaste: ${labelMappa.nViteRimaste}")
        xpGained = JLabel("XP: ${labelMappa.xp}")
        viteRimaste.font = Font("Font/Amazónica.ttf", Font.PLAIN, 15)
        xpGained.font = Font("Font/Amazónica.ttf", Font.PLAIN, 15)
        pannelloPunti.add(viteRimaste)
        pannelloPunti.add(xpGained)

        gbc.gridy = 2
        gbc.gridx = 0
        this.add(pannelloPunti, gbc)
    }

    fun generaMappaGrafica(): BufferedImage//: BufferedImage//genera la mappa come BufferedImage
    {
        val immagineIntera = BufferedImage(dimensioneImmagineMappa.width, dimensioneImmagineMappa.height, BufferedImage.TYPE_INT_ARGB)

        //se funziona sbovvo tvoppo
        for(i in 0 until dimensioneMatrice.height)
        {
            for (j in 0 until dimensioneImmagineSingola.height)
            {
                for(k in 0 until dimensioneMatrice.width)
                {
                    val elemento = mappa.mappa[i][k]

                    for (l in 0 until dimensioneImmagineSingola.width)
                    {
                        if((elemento == Sprite.PORTALE_VERDE) || (elemento == Sprite.MURO) || (elemento == Sprite.PICCOLA_MONETA) || (elemento == Sprite.GRANDE_MONETA) || (elemento == Sprite.BLANK))
                        {
                            val immagine = immagini[spriteArray.indexOf(mappa.mappa[i][k])]//prende l'immagine BufferedImage della casella corrente
                            immagineIntera.setRGB(k * 19 + l, i * 19 + j, immagine.getRGB(l, j))
                        }
                        else
                        {
                            immagineIntera.setRGB(k * 19 + l, i * 19 + j, 0)
                        }
                    }
                }
            }
        }

        return immagineIntera
        /*var matriceJLabel = arrayOf<Array<JLabel>>()
        for(i in 0 until dimensioneMatrice.height)
        {
            var arrayJLabel = arrayOf<JLabel>()
            for(j in 0 until dimensioneMatrice.width)
            {
                var label = JLabel()
                label.preferredSize = dimensioneImmagineSingola
                label.icon = immagini[spriteArray.indexOf(Sprite.BLANK)]
                label.isFocusable = false
                arrayJLabel += label
            }
            matriceJLabel += arrayJLabel
        }
        return matriceJLabel*/
    }

    fun leggiImmagini(): Array<BufferedImage>
    {
        return Array(pathName.size){ i -> (ImageIO.read(File(pathName[i]))) as BufferedImage}//Per ogni icona/immagine: leggo il file pathName[i] con ImageIO.read, da ció ci ricavo una ImageIcon//, da ImageIcon ricavo una Image con ImageIcon.image e la converto in una BufferedImage
    }

    override fun aggiornaMappa()
    {
        mappaDinamica = mappa
        analizzaMappa()
        pulsanteGioca.isEnabled = true
        labelMappa.icon = ImageIcon(generaMappaGrafica())
        labelMappa.mappaLetta()
    }

    fun analizzaMappa()//trova le coordinate di SPAWN_PACMAN, SPAWN_MOSTRO_ROSSO, SPAWN_MOSTRO_GIALLO, trova anche quante monete piccole e grandi ci sono
    {
        for(i in 0 until dimensioneMatrice.height)
        {
            for(j in 0 until dimensioneMatrice.width)
            {
                when(mappa.mappa[i][j])
                {
                    Sprite.SPAWN_PACMAN -> spawnPacman = Point(j, i)
                    Sprite.SPAWN_ROSSO -> spawnRosso = Point(j, i)
                    Sprite.SPAWN_AZZURRO -> spawnAzzurro = Point(j, i)
                    Sprite.SPAWN_GIALLO -> spawnGiallo = Point(j, i)
                    Sprite.SPAWN_BIANCO -> spawnRosa = Point(j, i)
                    Sprite.GRANDE_MONETA -> quanteMonete++
                    Sprite.PICCOLA_MONETA -> quanteMonete++
                }
            }
        }
    }

    override fun actionPerformed(e: ActionEvent?)
    {
        when(e?.actionCommand)
        {
            "carica" -> Carica(this)
            "gioca" -> labelMappa.iniziazione()
        }
    }

    override fun keyReleased(e: KeyEvent?)
    {}

    override fun keyPressed(e: KeyEvent?)
    {}

    override fun keyTyped(e: KeyEvent?)
    {
        if((labelMappa.statusGioco == gameStatus.STARTING) || (labelMappa.statusGioco == gameStatus.STARTED))
        {
            when (e!!.keyChar)
            {
                'w' -> {
                    labelMappa.pacman.nextDirezione = Pacman.direzione.NORTH
                    if(labelMappa.statusGioco == gameStatus.STARTING)
                    {
                        if(labelMappa.checkNextCasellaPacman(Pacman.direzione.NORTH))
                            labelMappa.pacman.changeDirection(Pacman.direzione.NORTH)
                        labelMappa.inizio()
                    }
                }
                'a' -> {
                    labelMappa.pacman.nextDirezione = Pacman.direzione.WEST
                    if(labelMappa.statusGioco == gameStatus.STARTING)
                    {
                        if(labelMappa.checkNextCasellaPacman(Pacman.direzione.WEST))
                            labelMappa.pacman.changeDirection(Pacman.direzione.WEST)
                        labelMappa.inizio()
                    }
                }
                's' -> {
                    labelMappa.pacman.nextDirezione = Pacman.direzione.SOUTH
                    if(labelMappa.statusGioco == gameStatus.STARTING)
                    {
                        if(labelMappa.checkNextCasellaPacman(Pacman.direzione.SOUTH))
                            labelMappa.pacman.changeDirection(Pacman.direzione.SOUTH)
                        labelMappa.inizio()
                    }
                }
                'd' -> {
                    labelMappa.pacman.nextDirezione = Pacman.direzione.EAST
                    if(labelMappa.statusGioco == gameStatus.STARTING)
                    {
                        if(labelMappa.checkNextCasellaPacman(Pacman.direzione.EAST))
                            labelMappa.pacman.changeDirection(Pacman.direzione.EAST)
                        labelMappa.inizio()
                    }
                }
            }
        }
    }
    /***********************************************************************************************************************************************************************************************************************************************************************/
    //c'é il vero e proprio gioco
    inner class LabelMappa(): JLabel()
    {
        lateinit var tic: Tic
        lateinit var giocaSuono: GiocaSuono
        var pacman = Pacman(spawnPacman)
        var quanteMonetePrese = 0
        var xp = 0//quanti xp sono stati guadagnati
        var nViteRimaste = 4

        var nTicMancantiDefault = 1000
        var nTicMancanti = nTicMancantiDefault//questa variabile indica quanti tic mancano per far tornare in modalitá CACCIA i fantasmi dopo essere andati in modalitá SPAVENTO (sono in totale 15 secondi)

        var aumentoPixel = 0
        val fantasmi: Array<Fantasmi> = arrayOf(Rosso(), Azzurro(), Rosa(), Giallo())//il fantasmi.start avviene in iniziazione()
        var aumentoPixelFantasmi = arrayOf<Int>(4)

        var statusGioco = gameStatus.MAPPA_NON_CARICATA

        init
        {
            this.repaint()
        }

        fun aggiornaXPGained() {xpGained.text = "XP: $xp"}
        fun aggiornaViteRimaste() {viteRimaste.text = "Vite rimaste $nViteRimaste"}

        fun mappaLetta()
        {
            statusGioco = gameStatus.WAITING_START
            pacman.changePosPacman(spawnPacman)
            this.repaint()
        }

        fun iniziazione()
        {
            pulsanteGioca.isVisible = false
            pulsanteCarica.isVisible = false
            statusGioco = gameStatus.STARTING
            pacman.posPacmanMatrice = spawnPacman
            pacman.direzioneCorrente = Pacman.direzione.NONE
            pacman.nextDirezione = Pacman.direzione.NONE
            pacman.nTic = 0
            azzeraAumentoPixel()
            fantasmi[0].start(mappa, spawnRosso, pacman.posPacmanMatrice)
            fantasmi[1].start(mappa, spawnAzzurro, pacman.posPacmanMatrice)
            fantasmi[2].start(mappa, spawnRosa, pacman.posPacmanMatrice)
            fantasmi[3].start(mappa, spawnGiallo, pacman.posPacmanMatrice)
            aumentoPixelFantasmi = Array(4){i -> 0}
            nTicMancanti = nTicMancantiDefault
            this.repaint()
        }

        fun inizio()
        {
            statusGioco = gameStatus.STARTED
            pacman.isStill = false
            tic = Tic(this)
            tic.start()
            giocaSuono = GiocaSuono("sounds/???.wav")
            //giocaSuono.start()
        }

        fun calcolaPixel(posizioneCasella: Point): Point//dato come input due numeri interi che rappresentano le coordinate di una casella restituisce un oggetto di tipo Dimension che rappesenta l'inizio di quella casella in pixel
        {
            return Point(posizioneCasella.x * dimensioneImmagineSingola.width, posizioneCasella.y * dimensioneImmagineSingola.height)
        }

        fun controllaScontro(): Boolean//funzione che controlla se almeno uno dei fantasmi si é scontrato con Pacman
        {
            for(i in 0 until fantasmi.size) {
                if (pacman.posPacmanMatrice == fantasmi[i].posizione) {
                    //se il fantasma si é scontrato ma era in modalita SPAVENTO allora va in modalita LETARGO
                    if ((fantasmi[i].modalita == Fantasmi.Mode.SPAVENTO) || (fantasmi[i].modalita == Fantasmi.Mode.IN_LETARGO)) {
                        fantasmi[i].modalita = Fantasmi.Mode.IN_LETARGO
                        xp += 200
                        aggiornaXPGained()
                    }
                    else
                        return true
                }
            }
            return false
        }

        fun tic()//funzione che chiama esclusivamente il Thread Tic
        {
            //se pacman é fermo allora aspetta semplicemente che gli venga dato un nuovo comando (infatti i comandi wasd non comandano direttamente direzioneCorrente, bensí nextDirezione)
            if(pacman.isStill) {
                if(checkNextCasellaPacman(pacman.nextDirezione)) {
                    pacman.changeDirection(pacman.nextDirezione)
                    azzeraAumentoPixel()
                    pacman.incrementaTic()
                    repaint()
                }
            }
            else {//se non é fermo gli si incrementa il tic, si ridisegna, si controlla se ha preso una moneta, se si allora quella casella diventa BLANK e se ha preso una moneta grande i fantasmi vanno in modalitá spavento, se ha preso tutte le monete (grandi e piccole) allora il player ha vinto
                pacman.incrementaTic()
                repaint()
                if(pacman.nTic == 0)
                {
                    if(mappaDinamica.mappa[pacman.posPacmanMatrice.y][pacman.posPacmanMatrice.x] == Sprite.PICCOLA_MONETA)
                    {
                        mappaDinamica.mappa[pacman.posPacmanMatrice.y][pacman.posPacmanMatrice.x] = Sprite.BLANK
                        quanteMonetePrese++
                        xp += 10
                        aggiornaXPGained()
                    }
                    else if(mappaDinamica.mappa[pacman.posPacmanMatrice.y][pacman.posPacmanMatrice.x] == Sprite.GRANDE_MONETA)
                    {
                        mappaDinamica.mappa[pacman.posPacmanMatrice.y][pacman.posPacmanMatrice.x] = Sprite.BLANK
                        quanteMonetePrese++
                        for(i in 0 until fantasmi.size)
                            fantasmi[i].modalita = Fantasmi.Mode.SPAVENTO
                        nTicMancanti = nTicMancantiDefault
                        xp += 50
                        aggiornaXPGained()
                    }
                    if(quanteMonetePrese == quanteMonete)//si vince
                    {
                        println("HAI VINTO OLLAREGANG")
                        exit(0)
                    }
                    //se pacman si sta muovendo in una direzione ma aveva premuto giá il tasto per cambiarla, allora appena puó (appena é disponibile) la cambia con changeDirection(), altrimenti, in caso non fosse disponibile la caella in nextDirezione si verifica se pacman possa andare nella direzione in cui stava giá andando, e cioé direzioneCorrente, se la prossima casella in direzioneCorrente non é disponibile allora pacman é fermo (isStill = true)
                    if (checkNextCasellaPacman(pacman.nextDirezione)) {
                        if ((pacman.nextDirezione != pacman.direzioneCorrente)) {
                            pacman.changeDirection(pacman.nextDirezione)
                            azzeraAumentoPixel()
                        }
                    }
                    else {
                        pacman.isStill = !checkNextCasellaPacman(pacman.direzioneCorrente)
                    }
                    //nella casella in cui é stato pacman si disegna un rettangolo nero cosí il disegno della PICCOLA_MONETA o GRANDE_MONETA scompare
                    val g = (icon as ImageIcon).image.graphics as Graphics2D
                    val posizione = calcolaPixel(pacman.posPacmanMatrice)
                    g.color = Color.BLACK
                    g.fillRect(posizione.x, posizione.y, dimensioneImmagineSingola.width, dimensioneImmagineSingola.height)
                }
            }
            //parte dei fantasmi
            if(controllaScontro())//se almeno uno dei fantasmi (in modalitá CACCIA) si é scontrato con Pacman si perde una vita e si riposizionano tutti agli spawn
            {
                tic.procedi = false
                if(giocaSuono.clip.isActive)
                    giocaSuono.clip.stop()
                println("HAI PERSO UNA VITA")
                nViteRimaste--
                aggiornaViteRimaste()
                if(nViteRimaste == 0)
                    exit(0)
                iniziazione()
            }
            else
            {
                //si incrementa il tic
                for(i in 0 until fantasmi.size)
                    fantasmi[i].incrementaTic(pacman.posPacmanMatrice, pacman.direzioneCorrente)

                //se c'é almeno un fantasma in modalitá SPAVENTO allora bisogna diminuire nTicMancanti, il quale se é <= 0 va resettato ed i fantasmi tornano in modalitá CACCIA e nTicMancanti torna uguale a nTicMancantiDefault, altrimenti, se non c'é nessuno spaventato nTicMancanti torna uguale a nTicMancantiDefault
                var qualcunoSpaventato = false
                for(i in 0 until fantasmi.size) {
                    if ((fantasmi[i].modalita == Fantasmi.Mode.SPAVENTO) || (fantasmi[i].modalita == Fantasmi.Mode.IN_LETARGO)) {
                        qualcunoSpaventato = true
                        break
                    }
                }

                if(qualcunoSpaventato) {
                    nTicMancanti--
                    //serve per il far lampeggiare i fantasmi quando stanno per finire la modalitá SPAVENTO
                    if(nTicMancanti <= nTicMancantiDefault / 4)
                    {
                        if(nTicMancanti % (15) == 0)
                        {
                            for(i in 0 until fantasmi.size) {
                                if (fantasmi[i].modalita == Fantasmi.Mode.SPAVENTO) {
                                    if (fantasmi[i].spriteCorrente == fantasmi[i].spriteSpavento)
                                        fantasmi[i].spriteCorrente = fantasmi[i].spriteMezzoSpavento
                                    else
                                        fantasmi[i].spriteCorrente = fantasmi[i].spriteSpavento
                                }
                            }
                        }

                    }
                    //quando é finito il periodo di SPAVENTO
                    if (nTicMancanti <= 0) {
                        for (i in 0 until fantasmi.size)
                            fantasmi[i].modalita = Fantasmi.Mode.CACCIA
                        nTicMancanti = nTicMancantiDefault
                    }
                }
                else
                    nTicMancanti = nTicMancantiDefault
                repaint()
            }
        }

        fun azzeraAumentoPixel()
        {
            aumentoPixel = 0
        }

        fun checkNextCasellaPacman(direzioneCorrente: Pacman.direzione): Boolean//controlla se la casella in direzioneCorrente sia libera
        {
            val check = when(direzioneCorrente)
            {
                Pacman.direzione.WEST ->  ((pacman.posPacmanMatrice.x - 1 < 0) || (mappa.mappa[pacman.posPacmanMatrice.y][pacman.posPacmanMatrice.x - 1] == Sprite.MURO))
                Pacman.direzione.NORTH -> ((pacman.posPacmanMatrice.y - 1 < 0) || (mappa.mappa[pacman.posPacmanMatrice.y - 1][pacman.posPacmanMatrice.x] == Sprite.MURO))
                Pacman.direzione.EAST ->  ((pacman.posPacmanMatrice.x + 1 >= dimensioneMatrice.width) || (mappa.mappa[pacman.posPacmanMatrice.y][pacman.posPacmanMatrice.x + 1] == Sprite.MURO))
                Pacman.direzione.SOUTH -> ((pacman.posPacmanMatrice.y + 1 >= dimensioneMatrice.height) || (mappa.mappa[pacman.posPacmanMatrice.y + 1][pacman.posPacmanMatrice.x] == Sprite.MURO))
                else -> true
            }
            return !check
        }

        override fun paintComponent(g: Graphics?)
        {
            super.paintComponent(g)
            val g2d = g as Graphics2D
            when(statusGioco)
            {
                gameStatus.WAITING_START ->//disegna gli spawn
                {
                    var spawn = calcolaPixel(spawnPacman)
                    g2d!!.drawImage(immagini[9], spawn.x, spawn.y, null)//stampa pacman

                    spawn = calcolaPixel(spawnRosso)
                    g2d!!.drawImage(immagini[7], spawn.x, spawn.y, null)//stampa Mostro rosso

                    spawn = calcolaPixel(spawnGiallo)
                    g2d!!.drawImage(immagini[6], spawn.x, spawn.y, null)//stampa Mostro giallo

                    spawn = calcolaPixel(spawnRosa)
                    g2d!!.drawImage(immagini[5], spawn.x, spawn.y, null)//stampa Mostro bianco

                    spawn = calcolaPixel(spawnAzzurro)
                    g2d!!.drawImage(immagini[4], spawn.x, spawn.y, null)//stampa Mostro azzurro
                }
                gameStatus.STARTING ->//disegna gli sprite grandi ma sono immobili (di Pacman disegna lo sprite pacman.spriteNone, il cerchio intero giallo insomma)
                {
                    var posPacman = calcolaPixel(pacman.posPacmanMatrice)
                    g2d!!.drawImage(pacman.spriteNone, posPacman.x - 5, posPacman.y - 5, null)
                    for(i in 0 until fantasmi.size) {
                        var posizionePixel = calcolaPixel(fantasmi[i].posizione)
                        g2d!!.drawImage(fantasmi[i].sprite, posizionePixel.x - 5, posizionePixel.y - 5, null)
                    }
                }
                gameStatus.STARTED ->
                {
                    if(pacman.nTic == 0)
                        azzeraAumentoPixel()
                    var posPacman = calcolaPixel(pacman.posPacmanMatrice)
                    if(pacman.isStill)
                        g2d!!.drawImage(pacman.spriteCorrente, (posPacman.x - 5), (posPacman.y - 5), null)
                    else
                    {
                        aumentoPixel += calcolaAumentoPixel(pacman.nTic, pacman.speed)
                        when (pacman.direzioneCorrente)
                        {
                            Pacman.direzione.WEST ->  g2d!!.drawImage(pacman.spriteCorrente, (posPacman.x - 5) - aumentoPixel, (posPacman.y - 5), null)
                            Pacman.direzione.NORTH -> g2d!!.drawImage(pacman.spriteCorrente, (posPacman.x - 5), (posPacman.y - 5) - aumentoPixel, null)
                            Pacman.direzione.EAST ->  g2d!!.drawImage(pacman.spriteCorrente, (posPacman.x - 5) + aumentoPixel, (posPacman.y - 5), null)
                            Pacman.direzione.SOUTH -> g2d!!.drawImage(pacman.spriteCorrente, (posPacman.x - 5), (posPacman.y - 5) + aumentoPixel, null)
                        }
                    }

                    /***************************************************************************************************///parte dei fantasmi
                    for(i in 0 until fantasmi.size)
                    {
                        if(fantasmi[i].nTic == 0)
                            aumentoPixelFantasmi[i] = 0
                        else
                            aumentoPixelFantasmi[i] += calcolaAumentoPixel(fantasmi[i].nTic, fantasmi[i].speed)
                        val posizione = calcolaPixel(fantasmi[i].posizione)
                        when (fantasmi[i].percorso[0])
                        {
                            Pacman.direzione.WEST ->  g2d.drawImage(fantasmi[i].spriteCorrente, (posizione.x - 5) - aumentoPixelFantasmi[i], (posizione.y - 5), null)
                            Pacman.direzione.NORTH -> g2d.drawImage(fantasmi[i].spriteCorrente, (posizione.x - 5), (posizione.y - 5) - aumentoPixelFantasmi[i], null)
                            Pacman.direzione.EAST ->  g2d.drawImage(fantasmi[i].spriteCorrente, (posizione.x - 5) + aumentoPixelFantasmi[i], (posizione.y - 5), null)
                            Pacman.direzione.SOUTH -> g2d.drawImage(fantasmi[i].spriteCorrente, (posizione.x - 5), (posizione.y - 5) + aumentoPixelFantasmi[i], null)
                            Pacman.direzione.NONE ->  g2d.drawImage(fantasmi[i].spriteCorrente, (posizione.x - 5), (posizione.y - 5), null)
                        }
                    }
                }
            }
        }

        fun calcolaAumentoPixel(nTic: Int, speed: Int): Int
        {
            var differenza = speed - dimensioneImmagineSingola.width//width e height devono essere uguali
            when {
                differenza > 0 -> {
                    if((nTic in speed/2 - differenza..speed/2 + differenza) && (nTic % 2 == 0))
                        return 0
                    else
                        return ceil(dimensioneImmagineSingola.width.toDouble() / speed.toDouble()).toInt()
                }
                differenza < 0 -> {
                    if((nTic in speed/2 - differenza..speed/2 + differenza) && (nTic % 2 == 0))
                        return ceil(dimensioneImmagineSingola.width.toDouble() / speed.toDouble()).toInt()
                    else
                        return floor(dimensioneImmagineSingola.width.toDouble() / speed.toDouble()).toInt()
                }
                else -> return 1
            }
        }
    }

    inner class PanelloMappa: JPanel()
    {
        init
        {
            this.repaint()
        }

        override fun paintComponent(g: Graphics?)
        {
            super.paintComponent(g)
            g?.drawImage(immagineContenitoreMappa, 0, 0, null)
        }
    }
}