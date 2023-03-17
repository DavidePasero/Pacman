import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import java.io.File
import javax.sound.sampled.AudioInputStream

class GiocaSuono(nomeNuovoFile: String): Thread()
{
    var nomeFile = nomeNuovoFile
    lateinit var clip: Clip//é pubblica perché quando pacman muore deve stopparsi
    override fun run()
    {
        try {
            val audioInputStream = AudioSystem.getAudioInputStream(File(nomeFile).absoluteFile)
            clip = AudioSystem.getClip()
            clip.open(audioInputStream)
            clip.start()
        } catch (ex: Exception) {
            println("Error with playing sound.")
            ex.printStackTrace()
        }
    }
}