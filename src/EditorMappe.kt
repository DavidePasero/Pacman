import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.Border

class EditorMappe: JPanel(), ActionListener, ContieneMappa
{
    var spriteArray = arrayOf(Gioco.Sprite.BLANK, Gioco.Sprite.MURO, Gioco.Sprite.PICCOLA_MONETA, Gioco.Sprite.GRANDE_MONETA, Gioco.Sprite.SPAWN_AZZURRO, Gioco.Sprite.SPAWN_BIANCO, Gioco.Sprite.SPAWN_GIALLO, Gioco.Sprite.SPAWN_ROSSO, Gioco.Sprite.PORTALE_VERDE, Gioco.Sprite.SPAWN_PACMAN)//serve per facilitare l'accesso all'enum Sprite
    var iconaStringa = arrayOf("Vuoto", "Muro", "Moneta piccola", "Moneta grande", "Spawn mostro azzurro", "Spawn mostro bianco", "Spawn mostro  giallo", "Spawn mostro rosso", "Portale", "Spawn Pacman")
    var dimensioneMappe = Dimension(27, 31)
    var mousePressed = false

    var immagini = leggiImmagini()

    var selectedItem = Gioco.Sprite.BLANK
    var labelItemSelezionato = JLabel("Item selezionato: Vuoto")//stará sopra il pannelloPulsanti ed indica quale item é stato selezionato

    override var mappa = Mappa()
    var pulsantiMappa = generaMatricePulsantiMappa()

    init
    {
        this.layout = BorderLayout()//BorderLayout perché devo mettere il pannello con i pulsanti a sinistra e la mappa a destra
        this.background = Color.WHITE

        //pannelloPulsanti che sará a sinistra
        var pannelloPulsanti = JPanel()
        pannelloPulsanti.background = Color.WHITE
        pannelloPulsanti.layout = GridBagLayout()
        var gbcEsternoPulsanti = GridBagConstraints()
        gbcEsternoPulsanti.insets = Insets(0, 10, 0, 0)

        labelItemSelezionato.font = Font("Font/Amazónica.ttf", Font.PLAIN, 15)

        var labelPulsanti = JLabel()
        labelPulsanti.preferredSize = Dimension(320, 587)

        var gbcPulsanti = GridBagConstraints()
        gbcPulsanti.insets = Insets(15, 15, 15, 15)
        labelPulsanti.layout = GridBagLayout()

        var pannelloMuro = PannelloIcona("Vuoto", 0)
        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 0
        labelPulsanti.add(pannelloMuro, gbcPulsanti)

        var pannelloBlank = PannelloIcona("Muro", 1)
        gbcPulsanti.gridx = 1
        gbcPulsanti.gridy = 0
        labelPulsanti.add(pannelloBlank, gbcPulsanti)

        var pannelloMonetaPiccola = PannelloIcona("Moneta piccola", 2)
        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 1
        labelPulsanti.add(pannelloMonetaPiccola, gbcPulsanti)

        var pannelloMonetaGrande = PannelloIcona("Moneta grande", 3)
        gbcPulsanti.gridx = 1
        gbcPulsanti.gridy = 1
        labelPulsanti.add(pannelloMonetaGrande, gbcPulsanti)

        var pannelloSpawnAzzurro = PannelloIcona("Spawn mostro azzurro", 4)
        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 2
        labelPulsanti.add(pannelloSpawnAzzurro, gbcPulsanti)

        var pannelloSpawnBianco = PannelloIcona("Spawn mostro bianco", 5)
        gbcPulsanti.gridx = 1
        gbcPulsanti.gridy = 2
        labelPulsanti.add(pannelloSpawnBianco, gbcPulsanti)

        var pannelloSpawnGiallo = PannelloIcona("Spawn mostro giallo", 6)
        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 3
        labelPulsanti.add(pannelloSpawnGiallo, gbcPulsanti)

        var pannelloSpawnRosso = PannelloIcona("Spawn mostro rosso", 7)
        gbcPulsanti.gridx = 1
        gbcPulsanti.gridy = 3
        labelPulsanti.add(pannelloSpawnRosso, gbcPulsanti)

        var pannelloPortaleVerde = PannelloIcona("Portale", 8)
        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 4
        labelPulsanti.add(pannelloPortaleVerde, gbcPulsanti)

        var pannelloSpawnPacman = PannelloIcona("Spawn Pacman", 9)
        gbcPulsanti.gridx = 1
        gbcPulsanti.gridy = 4
        labelPulsanti.add(pannelloSpawnPacman, gbcPulsanti)

        labelPulsanti.icon = ImageIcon(ImageIO.read(File("icons/ContenitorePulsantiEditor.png")))

        gbcEsternoPulsanti.gridy = 0
        gbcEsternoPulsanti.gridx = 0
        pannelloPulsanti.add(labelItemSelezionato, gbcEsternoPulsanti)

        gbcEsternoPulsanti.gridy = 1
        gbcEsternoPulsanti.gridx = 0
        pannelloPulsanti.add(labelPulsanti, gbcEsternoPulsanti)

        //pannelloMappa, a destra: É costituito da una JLabel che contiene una matrice di pulsanti con cui si creerá la mappa di gioco
        var pannelloMappa = JPanel()
        var pannelloButton = JPanel()//il pannello dei pulsanti salva, carica, clear
        var pannelloLabelMappa = JPanel()//pannello dove c'é labelMappa

        pannelloMappa.background = Color.WHITE
        var gbcMappa = GridBagConstraints()
        gbcMappa.insets = Insets(0, 0, 30, 10)
        pannelloMappa.layout = GridBagLayout()

        var gbcButton = GridBagConstraints()
        pannelloButton.layout = GridBagLayout()
        gbcButton.insets = Insets(0, 30, 0, 0)
        pannelloButton.background = Color.WHITE

        pannelloLabelMappa.layout = BorderLayout()
        pannelloLabelMappa.background = Color.WHITE

        var pulsanteSalva = AnimatedButton(Dimension(75, 25), "Salva")
        pulsanteSalva.actionCommand = "salva"
        pulsanteSalva.addActionListener(this)

        var pulsanteCarica = AnimatedButton(Dimension(75, 25), "Carica")
        pulsanteCarica.actionCommand = "carica"
        pulsanteCarica.addActionListener(this)

        var pulsanteClear = AnimatedButton(Dimension(75, 25), "Clear")
        pulsanteClear.actionCommand = "clear"
        pulsanteClear.addActionListener(this)

        gbcButton.gridx = 0; gbcButton.gridy = 0
        pannelloButton.add(pulsanteSalva, gbcButton)

        gbcButton.gridx = 1; gbcButton.gridy = 0
        pannelloButton.add(pulsanteCarica, gbcButton)

        gbcButton.gridx = 2; gbcButton.gridy = 0
        pannelloButton.add(pulsanteClear, gbcButton)

        pannelloButton.preferredSize = Dimension(pannelloButton.preferredSize.width + 40, pannelloButton.preferredSize.height + 20)

        var labelMappa = JLabel()
        labelMappa.background = Color(0, 0, 0)
        labelMappa.preferredSize = Dimension(540,620)
        labelMappa.layout = GridLayout(31, 27)

        pannelloLabelMappa.add(labelMappa, BorderLayout.CENTER)

        for(i in 0 until dimensioneMappe.height)
        {
            for(j in 0 until dimensioneMappe.width)
            {
                labelMappa.add(pulsantiMappa[i][j])
            }
        }

        gbcMappa.gridx = 0
        gbcMappa.gridy = 0
        pannelloMappa.add(pannelloButton, gbcMappa)

        gbcMappa.gridx = 0
        gbcMappa.gridy = 1
        pannelloMappa.add(pannelloLabelMappa, gbcMappa)

        this.add(pannelloPulsanti, BorderLayout.WEST)
        this.add(pannelloMappa, BorderLayout.EAST)
    }

    override fun actionPerformed(e: ActionEvent?)
    {
        when(e?.actionCommand)
        {
            "salva" ->
            {
                var mappaCorretta = mappa.checkCompleta()
                if(!mappaCorretta)
                {
                    var popUpWarningWindow = JFrame("Warning")
                    popUpWarningWindow.layout = GridBagLayout()
                    var gbc = GridBagConstraints()

                    popUpWarningWindow.setLocationRelativeTo(null)
                    popUpWarningWindow.size = Dimension(500, 300)
                    popUpWarningWindow.contentPane.background = Color.WHITE
                    popUpWarningWindow.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
                    var warningLabel =
                        JLabel("Questa mappa non puó essere usata, ma puoi ancora salvarla e modificarla")
                    warningLabel.font = Font("Font/Amazónica.ttf", Font.PLAIN, 12)
                    gbc.gridx = 0
                    gbc.gridy = 0
                    popUpWarningWindow.add(warningLabel, gbc)
                    popUpWarningWindow.isVisible = true
                }

                Salva(this)
            }
            "carica" -> Carica(this)
            "clear" -> clear()
        }
    }

    fun generaMatricePulsantiMappa(): Array<Array<PulsanteMappa>>//crea la matrice di pulsanti
    {
        var pulsanti = arrayOf<Array<PulsanteMappa>>()//é cosí che si dichiarano le matrici in kotlin, come array di array
        for(i in 0 until dimensioneMappe.height)
        {
            var array = arrayOf<PulsanteMappa>()
            for(j in 0 until dimensioneMappe.width)
            {
                var pulsante = PulsanteMappa(j, i)
                pulsante.addEditorMappe(this)
                array += pulsante
            }
            pulsanti += array
        }

        return pulsanti
    }

    override fun aggiornaMappa()
    {
        for(i in 0 until dimensioneMappe.height)
        {
            for(j in 0 until dimensioneMappe.width)
            {
                pulsantiMappa[i][j].changeIconMappa()
            }
        }
    }

    fun leggiImmagini(): Array<ImageIcon>//crea l'array di immagini
    {
        return Array(pathName.size) { i ->
            ImageIcon(ImageIO.read(File(pathName[i])))
        }
    }

    fun clear()
    {
        mappa.mappa = mappa.inizializzaMappa()//lol
        aggiornaMappa()
    }

    inner class PannelloIcona(titolo: String, numeroIcona: Int): JPanel(), MouseListener, ActionListener
    {
        var numeroPulsante = numeroIcona
        var pulsante = JButton()

        init
        {
            var titoloPulsante = JLabel(titolo)
            var dimensioneFont = 12
            if(titolo.length > 14)
                dimensioneFont = 10
            titoloPulsante.font = Font("Font/Amazónica.ttf", Font.PLAIN, dimensioneFont)
            var icona = immagini[numeroPulsante]
            var gbc = GridBagConstraints()
            this.layout = GridBagLayout()
            this.background = Color.WHITE
            gbc.insets = Insets(0,0, 10, 0)
            pulsante.preferredSize = Dimension(50, 50)
            pulsante.background = Color(0, 0, 0)
            pulsante.addActionListener(this)
            pulsante.addMouseListener(this)
            pulsante.icon = icona
            pulsante.isBorderPainted = false
            pulsante.isFocusable = false
            gbc.gridx = 0
            gbc.gridy = 0
            this.add(titoloPulsante, gbc)
            gbc.gridx = 0
            gbc.gridy = 1
            this.add(pulsante, gbc)
        }

        override fun mouseClicked(e: MouseEvent?) {}

        override fun mouseEntered(e: MouseEvent?)
        {
            pulsante.background = Color(50, 50, 50)
        }

        override fun mouseExited(e: MouseEvent?)
        {
            pulsante.background = Color(0, 0, 0)
        }

        override fun mousePressed(e: MouseEvent?) {}

        override fun mouseReleased(e: MouseEvent?){}

        override fun actionPerformed(e: ActionEvent?)
        {
            selectedItem = spriteArray[numeroPulsante]//troppo easy con spriteArray, prima c'era un when() enorme
            labelItemSelezionato.text = "Item selezionato: ${iconaStringa[numeroPulsante]}"
        }
    }

    class PulsanteMappa(x: Int, y: Int): JButton(), ActionListener, MouseListener//l'idea di questa classe é quella di creare dei pulsanti che si autogesticano gli eventi per migliorare le prestazioni, dato che se avessi lasciato gestire gli eventi da EditorMappe() per ogni pulsante avrebbe dovuto controllare tutti i pulsanti prima
    {
        var posX = 0
        var posY = 0
        var editorMappe = null as EditorMappe?
        init
        {
            this.background = Color(0, 0, 0)
            this.actionCommand = "action"//inutile ma lo metto lo stesso per evitare errori stupidi
            this.preferredSize = Dimension(19,19)
            this.icon = ImageIcon(ImageIO.read(File("sprite/Texture/daInserire.png")))
            this.isBorderPainted = false
            this.isFocusable = false
            this.addMouseListener(this)
            this.addActionListener(this)
            posX = x
            posY = y
        }

        fun addEditorMappe(editorMappe: EditorMappe)
        {
            this.editorMappe = editorMappe
        }

        override fun mouseClicked(e: MouseEvent?)
        {
            changeIcon()
        }

        override fun mouseEntered(e: MouseEvent?)
        {
            this.background = Color(50, 50, 50)
            if(editorMappe?.mousePressed == true)
            {
                changeIcon()
            }
        }

        override fun mouseExited(e: MouseEvent?)
        {
            this.background = Color(0, 0, 0)
        }

        override fun mousePressed(e: MouseEvent?)
        {
            editorMappe?.mousePressed = true
            changeIcon()
        }

        override fun mouseReleased(e: MouseEvent?)
        {
            editorMappe?.mousePressed = false
        }

        override fun actionPerformed(e: ActionEvent?)
        {
            changeIcon()
        }

        fun changeIcon()//funzione che serve, e che viene chiamata, quando questo pulsante é premuto: cambia l'icona in base al editorMappe!!.selectedItem e cambia mappa[posY][posX] con selectedItem
        {
            var spriteCorretto = editorMappe!!.spriteArray.indexOf(editorMappe!!.selectedItem)
            editorMappe!!.mappa.mappa[posY][posX] = editorMappe!!.selectedItem
            this.icon = editorMappe!!.immagini[spriteCorretto]
        }

        fun changeIconMappa()//funzione che serve, e che viene chiamata, solo quando abbiamo caricato una mappa dal file, cambia l'icona del pulsante in base alla mappa
        {
            var spriteCorretto = editorMappe!!.spriteArray.indexOf(editorMappe!!.mappa.mappa[posY][posX])
            this.icon = editorMappe!!.immagini[spriteCorretto]
        }
    }
}
