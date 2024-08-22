import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseListener
import java.io.File
import java.awt.Color
import java.awt.event.MouseEvent
import java.io.IOException
import javax.imageio.ImageIO
import java.awt.Image
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import java.io.ObjectInputStream
import java.io.FileInputStream

class Carica(contenitoreMappa: ContieneMappa): JFrame(), ActionListener
{
    var nomiMappe: Array<String>
    var nomiFileMappe: Array<String>
    var qualeMappaSelezionata = -1
    var accettaMappa = AnimatedButton(Dimension(89, 34), "Accetta")
    var etichettaNomeMappe = emptyArray<LabelCartella>()
    var contieneMappa: ContieneMappa

    init
    {
        contieneMappa = contenitoreMappa
        this.title = "Seleziona mappa"
        this.size = Dimension(450, 600)
        this.layout = BorderLayout()

        nomiFileMappe = nomiMappe()
        if(nomiFileMappe.isEmpty())
        {
            nomiMappe = emptyArray()
            var pannelloCentrale = JPanel()
            var etichettaErrore = JLabel("Non esiste alcuna mappa, reinstalla i file di gioco")
            pannelloCentrale.add(etichettaErrore)
            this.add(pannelloCentrale, BorderLayout.CENTER)
        }
        else
        {
            nomiMappe = Array(nomiFileMappe.size) { i -> nomiFileMappe[i].subSequence(0, nomiFileMappe[i].indexOf(".")).toString()}
            var pannelloCentrale = JPanel()
            pannelloCentrale.layout = GridLayout(nomiMappe.size, 1)
            var pannelloMappe = Array(nomiMappe.size) { i -> JPanel() }
            etichettaNomeMappe = Array(nomiMappe.size){i -> LabelCartella(nomiMappe[i], i, nomiFileMappe[i])}

            for (i in 0 until nomiMappe.size) {
                pannelloMappe[i].background = Color(200, 200, 200)
                etichettaNomeMappe[i].background = Color(200, 200, 200)

                pannelloMappe[i].add(etichettaNomeMappe[i])
                pannelloCentrale.add(pannelloMappe[i])
            }

            var barra = JScrollPane(pannelloCentrale)
            this.add(barra, BorderLayout.CENTER)
        }

        var pannelloPulsanti = JPanel()
        accettaMappa.isEnabled = false
        accettaMappa.actionCommand = "accetta"
        accettaMappa.addActionListener(this)
        pannelloPulsanti.add(accettaMappa)
        pannelloPulsanti.preferredSize = Dimension(pannelloPulsanti.preferredSize.width, pannelloPulsanti.preferredSize.height + 30)
        this.add(pannelloPulsanti, BorderLayout.SOUTH)
        this.isVisible = true
    }

    fun nomiMappe(): Array<String>//restituisce un array formato da tutti i nomi dei file .MAPPA presenti nella cartella Mappe
    {
        val mappeDirectory = File("Mappe Custom")
        val listaMappe = mappeDirectory.listFiles()
        if(listaMappe != null)
        {
            val nomeMappe = ArrayList<String>()
            for (i in 0 until listaMappe.size)//elimina i file che non sono in .MAPPA
            {
                if (listaMappe[i].name.contains(".MAPPA", false))
                {
                    if(listaMappe[i].name.contains(".MAPPAINCOMPLETA", false) && contieneMappa is Gioco)//se siamo in Gioco non si possono usare le mappe incomplete, ragion per cui viene eliminata dalla lista
                        ;
                    else
                        nomeMappe.add(listaMappe[i].name)
                }
                else
                    listaMappe[i].delete()
            }

            return nomeMappe.toTypedArray()
        }
        else
            return emptyArray()
    }

    override fun actionPerformed(e: ActionEvent?)
    {
        if(e?.actionCommand == "accetta")
        {
            var nomeFile = nomiFileMappe[qualeMappaSelezionata]
            var input = ObjectInputStream(FileInputStream("Mappe Custom"+File.separator+"" + nomeFile))
            contieneMappa.mappa = input.readObject() as Mappa
            contieneMappa.aggiornaMappa()
            input.close()
            this.dispose()
        }
    }

    inner class LabelCartella(nomeMappa: String, qualeMappa: Int, nomeIntero: String): JLabel(), MouseListener//nomeIntero sarebbe il nome del file intero, cosí per capire se é INCOMPLETA, in caso fosse INCOMPLETA allora la cartella é un po diversa
    {
        var selected = false
        var qualeMappa = 0
        var isComplete: Boolean

        init
        {
            this.text = "                              " + nomeMappa
            this.preferredSize = Dimension(300, 40)
            //this.setBackground(new Color(200, 200, 200));
            this.isOpaque = true
            this.qualeMappa = qualeMappa
            this.addMouseListener(this)
            isComplete = "INCOMPLETA" !in nomeIntero//la mappa é completa se non é INCOMPLETA
        }

        override fun paintComponent(g: Graphics)
        {
            super.paintComponent(g)
            var cartellaChiusa: Image? = null
            var cartellaAperta: Image? = null
            try {
                if (selected)
                {
                    if(isComplete)
                        cartellaAperta = ImageIO.read(File("Icone"+File.separator+"CartellaAperta.png"))
                    else
                        cartellaAperta = ImageIO.read(File("Icone"+File.separator+"CartellaApertaIncompleta.png"))
                }
                else
                {
                    if(isComplete)
                        cartellaChiusa = ImageIO.read(File("Icone"+File.separator+"CartellaChiusa.png"))
                    else
                        cartellaChiusa = ImageIO.read(File("Icone"+File.separator+"CartellaChiusaIncompleta.png"))
                }
            } catch (e: IOException) {
                println(e.message)
            }

            if (selected)
                g.drawImage(cartellaAperta, 10, 0, null)
            else
                g.drawImage(cartellaChiusa, 20, 0, null)
        }

        override fun mouseClicked(me: MouseEvent) {
            selected = !selected

            if (qualeMappaSelezionata == -1) {
                qualeMappaSelezionata = qualeMappa
                accettaMappa.setEnabled(true)
                this.repaint()
            } else {
                if (selected) {
                    etichettaNomeMappe[qualeMappaSelezionata].selected = false
                    etichettaNomeMappe[qualeMappaSelezionata].repaint()
                    qualeMappaSelezionata = qualeMappa
                    accettaMappa.isEnabled = true
                    this.repaint()
                } else {
                    qualeMappaSelezionata = -1
                    accettaMappa.isEnabled = false
                    this.repaint()
                }
            }
            repaint()
        }

        override fun mousePressed(me: MouseEvent) {}

        override fun mouseReleased(me: MouseEvent) {}

        override fun mouseEntered(me: MouseEvent)
        {
            this.background = Color(150, 150, 150)
        }

        override fun mouseExited(me: MouseEvent)
        {
            this.background = Color(200, 200, 200)
        }
    }
}