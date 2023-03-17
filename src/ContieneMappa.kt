interface ContieneMappa
//chi la implementa vuol dire che al suo interno ha un oggetto di tipo mappa, deve anche avere un metodo aggiornaMappa
{
    var mappa: Mappa

    fun aggiornaMappa()
}