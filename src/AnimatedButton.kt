import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JButton

class AnimatedButton(size: Dimension, text: String): JButton(), MouseListener
{
    init
    {
        this.text = text
        this.preferredSize = size
        this.addMouseListener(this)
        this.setFocusable(false)
    }

    override fun mouseClicked(e: MouseEvent?) {

    }

    override fun mouseEntered(e: MouseEvent?)
    {
        resizeButton(true)
    }

    override fun mouseExited(e: MouseEvent?) {
        resizeButton(false)

    }

    override fun mousePressed(e: MouseEvent?) {

    }

    override fun mouseReleased(e: MouseEvent?) {

    }

    fun resizeButton(ingrandisci: Boolean)
    {
        if(ingrandisci)
            this.setSize(this.width + 20, this.height + 10)
        else
            this.setSize(this.width - 20, this.height - 10)
    }
}