import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JPanel
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.SwingUtilities

class Menu: JPanel(), MouseListener, ActionListener
{
    var dimensionePulsanti = Dimension(150, 50)
    var pulsanteGioca = AnimatedButton(dimensionePulsanti, "Gioca")
    var pulsanteEditor = AnimatedButton(dimensionePulsanti,"Editor")

    init
    {
        var gbc = GridBagConstraints()
        gbc.insets = Insets(20, 0, 0, 0)
        this.layout = GridBagLayout()

        pulsanteGioca.addActionListener(this)
        pulsanteGioca.actionCommand = "gioca"

        pulsanteEditor.addActionListener(this)
        pulsanteEditor.actionCommand = "editor"

        gbc.gridx = 0
        gbc.gridy = 0
        this.add(pulsanteGioca, gbc)

        gbc.gridx = 0
        gbc.gridy = 1
        this.add(pulsanteEditor, gbc)
    }

    override fun mouseEntered(e: MouseEvent?)
    {
    }

    override fun mouseExited(e: MouseEvent?)
    {
    }

    override fun mouseClicked(e: MouseEvent?)
    {

    }

    override fun mouseReleased(e: MouseEvent?)
    {

    }

    override fun mousePressed(e: MouseEvent?)
    {

    }

    override fun actionPerformed(e: ActionEvent?)
    {
        var pacFrame = SwingUtilities.getWindowAncestor(this) as PacFrame //serve per prendere il componente che contiene quello inviato alla funzione, in questo caso prende l'oggetto PacFrame che contiene this (ovvero il pannello corrente)
        if(e?.actionCommand == "gioca")
        {
            pacFrame.switchPanel(1)
        }
        if(e?.actionCommand == "editor")
        {
            pacFrame.switchPanel(2)
        }
    }
}