class Tic(labelMappa: Gioco.LabelMappa): Thread("Tic")
{
    var mappa: Gioco.LabelMappa
    var procedi = true
    init
    {
        mappa = labelMappa
    }

    override fun run()
    {
        while (procedi) {
            var ms = System.currentTimeMillis()
            while (System.currentTimeMillis() < ms + 10) {
            }
            mappa.tic()
        }

    }
}