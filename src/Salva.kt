import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import java.awt.event.ActionListener
import java.io.File
import java.io.ObjectOutputStream
import java.io.FileOutputStream

class Salva(editor: EditorMappe): JFrame(), ActionListener
{
    var textField = JTextField("Inserire nome cartella")
    var editorMappe = null as EditorMappe?

    init
    {
        editorMappe = editor
        //finestra
        this.title = "Salva"
        this.size = Dimension(500, 300)
        this.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        this.isVisible = true

        //pannello
        var pannelloSalva = JPanel()
        var gbc = GridBagConstraints()
        gbc.insets = Insets(20, 0, 20, 0)
        pannelloSalva.layout = GridBagLayout()
        pannelloSalva.background = Color.WHITE

        //textfield
        var pannelloText = JPanel()
        pannelloText.background = Color.WHITE
        textField.columns = 30
        pannelloText.add(textField)

        //buttons, é stato messo un gridbaglayout per mettere degli insets, dato che sono animatedButtons e si ingrandiscono quando ci si passa sopra il cursore
        var pannelloPulsanti = JPanel()
        var gbcPulsanti = GridBagConstraints()
        gbcPulsanti.insets = Insets(0, 20, 0, 20)
        pannelloPulsanti.layout = GridBagLayout()
        pannelloPulsanti.background = Color.WHITE
        var pulsanteSalva = AnimatedButton(Dimension(75, 25), "Salva")
        pulsanteSalva.actionCommand = "salva"
        pulsanteSalva.addActionListener(this)

        var pulsanteScarta = AnimatedButton(Dimension(75, 25), "Scarta")
        pulsanteScarta.actionCommand = "scarta"
        pulsanteScarta.addActionListener(this)

        gbcPulsanti.gridx = 0
        gbcPulsanti.gridy = 0
        pannelloPulsanti.add(pulsanteSalva, gbcPulsanti)

        gbcPulsanti.gridx = 1
        gbcPulsanti.gridy = 0
        pannelloPulsanti.add(pulsanteScarta, gbcPulsanti)
        pannelloPulsanti.preferredSize = Dimension(pannelloPulsanti.preferredSize.width + 30, pannelloPulsanti.preferredSize.height + 20)//dato che sono degli animatedButtons ho aumentato la grandezza del pannelloPulsanti

        gbc.gridx = 0
        gbc.gridy = 0
        pannelloSalva.add(pannelloText, gbc)

        gbc.gridx = 0
        gbc.gridy = 1
        pannelloSalva.add(pannelloPulsanti, gbc)

        this.add(pannelloSalva)
    }

    override fun actionPerformed(e: ActionEvent?)
    {
        when(e?.actionCommand)
        {
            "salva" ->//salva il tutto come file binario in .MAPPA e dispose la finestra
            {
                var nomeCartella = textField.text
                var checkEsistenza = File("maps/" + nomeCartella)

                var contatoreCartelle = 1

                //controlla che non esista un file con quel nome, altrimenti nomina il corrente con nomeFile + i, dove la i sta per quanti file con lo stesso nome esistono
                while(checkEsistenza.exists())
                {
                    checkEsistenza = File("maps/" + nomeCartella + "(" + contatoreCartelle + ")")
                    contatoreCartelle++
                }

                if(contatoreCartelle == 1)
                    ;
                else
                    nomeCartella += "(" + contatoreCartelle + ")"

                //evidentemente cose che dovrebbero generare eccezioni come la creazione dell'ObjectOutputStream in Kotlin non le genera
                var nomeFile = nomeCartella
                if(editorMappe!!.mappa!!.isComplete)
                    nomeFile += ".MAPPA";
                else
                    nomeFile += ".MAPPAINCOMPLETA"
                var output = ObjectOutputStream(FileOutputStream("maps/" + nomeFile))
                output.writeObject(editorMappe?.mappa)
                output.close()

                this.dispose()
                textField.text = "Inserire nome cartella"
            }
            "scarta" ->
            {
                textField.text = "Inserire nome cartella"
                this.dispose()
            }
        }
    }
}