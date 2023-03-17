import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class PacFrame(width: Int, height: Int): JFrame()
{

    var panel = arrayOf(Menu(), Gioco(), EditorMappe())
    var currentPanel = 0//indica il panel corrente, serve per lo switch di panel

    init
    {
        this.setSize(width, height)
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.setVisible(true)
        switchPanel(0)
    }

    fun switchPanel(nextPanel: Int)//switcha ad un nuovo panel (quindi reinizializza tutto)
    {
        this.addKeyListener(null)
        if((nextPanel == 1) || (nextPanel == 2))
        {
            this.size = Dimension(1000, 1200)
            if(nextPanel == 1)
                this.addKeyListener(panel[1] as Gioco)
        }
        else
            this.setSize(600, 900)
        (panel[currentPanel] as JPanel).setVisible(false)
        this.add((panel[nextPanel] as JPanel))
        currentPanel = nextPanel
        (panel[currentPanel] as JPanel).setVisible(true)
    }

}