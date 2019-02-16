<?php
    /**
     * Pagina di visualizzazione filtrata delle foto 
     * Author: Francesco Pham
     */

    session_start();
    if (!isset( $_SESSION['user'] ) ) {
        header("location: /index.php");
    }

    include 'database_info.php';

    // Numero di immagini per pagina
    $immaginiPerPagina="10"; 

    // Numero di gruppi di TAG (i gruppi sono ad esempio angolazione, inclinazione, risoluzione..)
    $num_gruppi_tag = 9;  

    //se la pagina non è specificata allora visualizza la prima pagina
    if(!isset($_GET["pag"])) {
        $nuovaQuery = $_SERVER['QUERY_STRING'];
        header("location: /visualizza.php?$nuovaQuery&pag=0");
    }
    
    /*
        Accesso a database, generazione array di foto e visualizzazione delle foto
        Author: Francesco Pham
    */
    function visualizzaFoto(){
        
        //$link = mysqli_connect($dbhost, $dbuser, $dbpass) or die("Unable to Connect to '$dbhost'");
        $mysqli=mysqli_connect($GLOBALS['dbhost'],$GLOBALS['dbuser'],$GLOBALS['dbpass'],$GLOBALS['dbname']);
        // Check connection
        if ($mysqli->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }

        $fotolist = array();
        if(isset($_GET['nomeFoto']) && $_GET['nomeFoto']!='') $fotolist = selezionaFotoPerNome($mysqli);
        else $fotolist = generaListaFotoFiltrata($mysqli);
        stampaListaFoto($mysqli, $fotolist);

        mysqli_close($mysqli);
    }


    /*
        Funzione che ritorna il numero totale di foto da visualizzare per poter effettuare la paginazione
        Return: Numero di totale di foto da visualizzare
    */
    function numeroRighe()
    {
        $mysqli=mysqli_connect($GLOBALS['dbhost'],$GLOBALS['dbuser'],$GLOBALS['dbpass'],$GLOBALS['dbname']);
        // Check connection
        if ($mysqli->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }
        $tags=generaStringaTag();
        global $num_gruppi_tag;
        $selectfotosql = "SELECT foto.ID, foto.INGREDIENTI FROM foto
            INNER JOIN fototag ON foto.ID = fototag.IDFOTO
            WHERE foto.INGREDIENTI != '' AND IDTAG IN (
                SELECT ID 
                FROM tag 
                WHERE TIPO='originale' AND NOME IN ($tags) 
            )
            GROUP BY IDFOTO HAVING COUNT(IDFOTO) = ".$num_gruppi_tag;
        
        $result = mysqli_query($mysqli, $selectfotosql);
        $rows = mysqli_num_rows($result);
        mysqli_close($mysqli);
        return $rows;
    }


    /*
        qui viene fatta una selezione filtrata per nome passato in $_GET['nomeFoto']: 
        viene cercato la foto che possiede tale nome (il nome della foto coincide 
        con il nome del file, estensione compresa)
        Param $mysqli: Connessione al database mysql
        Return: array di foto che possiedono tale nome
        Author: Francesco Pham
    */
    function selezionaFotoPerNome($mysqli){
        $nomefoto = $_GET['nomeFoto'];
        $sql = "SELECT foto.ID,foto.NOME,foto.INGREDIENTI FROM foto WHERE foto.NOME = '$nomefoto'";
        $result = mysqli_query($mysqli, $sql);
        $fotoarray = array();
        if ($result && mysqli_num_rows($result) > 0) {
            $fotoarray = mysqli_fetch_all($result,MYSQLI_ASSOC);
            mysqli_free_result($result);
        }
        return $fotoarray;
    }

    

    /*
        Questa funzione seleziona le foto dal database e le inserisce in un'array 
        pronto per essere visualizzato.
        La selezione è filtrata per attributi (tag) passati come parametri GET: alcuni attributi possono 
        anche non essere specificati, se ciò avviene la ricerca non viene 
        filtrata per tali attributi.
        Param $mysqli: Connessione al database mysql
        Author: Francesco Pham
    */
    function generaListaFotoFiltrata($mysqli){
        $tags=generaStringaTag();
        global $immaginiPerPagina;
        $pagina="";
        if(isset($_GET["pag"]))
        {
            $pagina=$_GET["pag"];
        }
        else
        {
            $pagina="0";
        }
        
        //creazione della query
        //il controllo foto.INGREDIENTI!='' serve a escludere le foto modificate
        global $num_gruppi_tag;
        $selectfotosql = "SELECT foto.ID,foto.NOME,foto.INGREDIENTI, foto.NOTE  FROM foto
            INNER JOIN fototag ON foto.ID = fototag.IDFOTO
            WHERE foto.INGREDIENTI != '' AND IDTAG IN (
                SELECT ID 
                FROM tag 
                WHERE TIPO='originale' AND NOME IN ($tags) 
            )
            GROUP BY IDFOTO HAVING COUNT(IDFOTO) = $num_gruppi_tag
            LIMIT $pagina,$immaginiPerPagina
        ";

        $result = mysqli_query($mysqli, $selectfotosql);
        $fotolist = mysqli_fetch_all($result,MYSQLI_ASSOC);
        mysqli_free_result($result);
        return $fotolist;
    }
    
    /*
        selezione dei tag corrispondenti ad una foto
        Param $idfoto: id della foto 
        Return: lista di tag associati alla foto 
        Author: Francesco Pham
    */
    function generateTagList($mysqli, $idfoto){
        $sql = "SELECT tag.NOME FROM tag
                INNER JOIN fototag ON tag.ID = fototag.IDTAG
                WHERE fototag.IDFOTO = $idfoto
                ORDER BY tag.ID";
        $result = mysqli_query($mysqli, $sql);
        $taglist = mysqli_fetch_all($result,MYSQLI_ASSOC);
        mysqli_free_result($result);
        return $taglist;
    }

    /*
        generazione del codice html con la lista di tag
        Param $taglist: lista di tag da stampare
        Return: codice html della lista di tag
        Author: Francesco Pham
    */
    function printTagList($taglist){
        $htmltaglist = '';
        foreach($taglist as $tag){
            $htmltaglist .= '<li>'.$tag['NOME'].'</li>';
        }
        return $htmltaglist;
    }

    /*
        selezione delle foto modificate corrispondenti ad una foto e i loro tag di modifica
        Param $idfotoorig: id della foto originale
        Return: array associativo dove la chiave è il nome della foto modificata e il valore è il suo tag
        Author: Francesco Pham
    */
    function generateEditedPhotos($mysqli, $idfotoorig){
        $sql = "SELECT foto.NOME, tag.NOME AS TAGNOME
            FROM tag
            INNER JOIN fototag ON tag.ID = fototag.IDTAG
            INNER JOIN modifiche ON fototag.IDFOTO = modifiche.IDMODIFICATA
            INNER JOIN foto ON modifiche.IDMODIFICATA = foto.ID
            WHERE modifiche.IDORIGINALE = $idfotoorig AND tag.TIPO='modifica'";
        $result = mysqli_query($mysqli, $sql);
        $photolist = mysqli_fetch_all($result,MYSQLI_ASSOC);
        mysqli_free_result($result);

        $assoc = array();
        foreach($photolist as $photo){
            if(!array_key_exists($photo['NOME'], $assoc)) {
                $assoc[$photo['NOME']] = array();
            }
            array_push($assoc[$photo['NOME']], $photo['TAGNOME']);
        }
        return $assoc;
    }

    /*
        stampa della lista delle foto modificate.
        Param $modphotolist: lista delle foto modificate
        Return: codice html della lista di foto modificate
        Author: Francesco Pham
    */
    function printEditedPhotos($modphotolist){
        $html = '';
        foreach($modphotolist as $fotonome => $array){
            $html .= '<li>';
            $html .= '<a href="'.generateUrl($fotonome).'">'. $fotonome . '</a>: ';
            foreach($array as $tagmodifica){
                $html .= $tagmodifica . ' ';
            }
            $html .= '</li>';
        }
        return $html;
    }

    /*
        generazione dell'url dove reperire i file delle foto
        Return: url dove reperire i file delle foto
    */
    function generateUrl($nomefile){
        return 'http://'.$_SERVER['HTTP_HOST'].'/foto/'.$nomefile;
    }

    /*
        stampa in html della lista delle foto selezionate e dei corrispondenti tag
        Param $fotolist: lista di foto da stampare
        Author: Francesco Pham
    */
    function stampaListaFoto($mysqli, $fotolist){
        echo'<div class="row">';
        $i=0;
        foreach($fotolist as $foto){
            $i++;
            if($i>3) {
                echo'</div>';
                echo'<div class="row">';
                $i=1;
            }
            echo '
                <div class="col-md-4">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            '.$foto['NOME'].'
                        </div>
                        <div class="panel-body" style="font-size: 17px;">
                            <img src="'.generateUrl($foto['NOME']).'"
                                style="width: 100%; height: auto;"></br></br>
                            <b>Tag</b>:
                            <ul>
                                '.printTagList(generateTagList($mysqli,$foto['ID'])).'
                            </ul>
                            <b>Ingredienti</b>: '.$foto['INGREDIENTI'].'<br>
                            <b>Note</b>: '.$foto['NOTE'].'<br>
                            <b>Modifiche:</b><br>
                            <ul>
                                '.printEditedPhotos(generateEditedPhotos($mysqli, $foto['ID'])).'
                            </ul>
                            <a href="modifica.php?id='.$foto['ID'].'" type="submit" class="btn btn-primary">Modifica</a> 
                        </div>
                    </div>
                </div>';
        }
        echo'</div>'; //fine <div class="row">
    }
?>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Elementi di Ingegneria</title>

    <!-- Bootstrap Core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="vendor/metisMenu/metisMenu.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="dist/css/sb-admin-2.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

</head>

<body>

    <div id="wrapper">

        <!-- Navigation -->
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="inserimento.php">Gestione foto - Elementi di Ingegneria</a>
            </div>
            <!-- /.navbar-header -->

            <div class="navbar-default sidebar" role="navigation">
                <div class="sidebar-nav navbar-collapse">
                    <ul class="nav" id="side-menu">
                        <li>
                            <a href="inserimento.php"><i class="fa fa-pencil fa-fw"></i> Inserimento dati</a>
                        </li>
                        <li>
                            <a href="visualizza.php?pag=0"><i class="fa fa-table fa-fw"></i> Visualizza dati</a>
                        </li>
                        <!--<li>
                            <a href="download.php"><i class="fa fa-download fa-fw"></i> Download dati</a>
                        </li>-->
                        <li>
                            <a href="logout.php"><i class="fa fa-key fa-fw"></i> Logout</a>
                        </li>
                    </ul>
                </div>
                <!-- /.sidebar-collapse -->
            </div>
            <!-- /.navbar-static-side -->
        </nav>

        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Visualizzazione</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>

            <div class="row">
            <?php
                if(isset($_GET["inserita"]))
                {
                    echo '<div class="alert alert-success alert-dismissable">
                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
                            Foto caricata.
                        </div>';
                }
                if(isset($_GET["errore"]))
                {
                    echo '<div class="alert alert-danger alert-dismissable">
                            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>'
                            . 'Errore: ' . $_GET['errore']
                            .'</div>';
                }
            ?>

                <div class="panel panel-default">
                    <div class="panel-heading">
                        Filtro di ricerca
                    </div>
                    <div class="panel-body">
                        <form method="GET" action="#">
                            <!-- Pagina iniziale 0-->
                            <input type="hidden" name="pag" value=0>
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Inclinazione</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="inclinazione" value="si">Inclinata
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="inclinazione" value="no">Non inclinata
                                            </label>
                                        </div>
                                    </div>
                                </div> 
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Angolazione</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="angolazione" value="si">Angolata
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="angolazione" value="no">Non angolata
                                            </label>
                                        </div>
                                    </div>
                                </div>    
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Testo</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="testoPresente" value="si">Presente
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="testoPresente" value="no">Non presente
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
    
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Luce</label>
                                        <select name="luce" class="form-control">
                                            <option value="nessuna">Nessuna scelta</option>
                                            <option value="poca">Poca luce</option>
                                            <option value="ottimale">Luce ottimale</option>
                                            <option value="troppa">Troppa luce</option>
                                        </select>
                                    </div>
                                </div>   
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Etichetta</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="etichettaPiana" value="si">Piana
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="etichettaPiana" value="no">Non piana
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Caratteri </label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="caratteriDanneggiati" value="si">Opachi/Danneggiati
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="caratteriDanneggiati" value="no">Nitidi
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Immagine </label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="immagineNitida" value="si">Nitida
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="immagineNitida" value="no">Sfuocata
                                            </label>
                                        </div>
                                    </div>
                                </div> 
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Mossa</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="mossa" value="si">Foto mossa
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="mossa" value="no">Foto non mossa
                                            </label>
                                        </div>
                                    </div>
                                </div>   
                                <div class="col-md-4">
                                    <div class="form-group">
                                        <label>Risoluzione</label>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="risoluzione" value="alta">Alta risoluzione
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="risoluzione" value="media">Media risoluzione
                                            </label>
                                        </div>
                                        <div class="radio">
                                            <label>
                                                <input type="radio" name="risoluzione" value="bassa">Bassa risoluzione
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>


                            <div class="form-group">
                                <label>Nome foto</label>
                                <input class="form-control" type="text" name="nomeFoto" placeholder="Lasciare vuoto per non cercare in base al nome">
                            </div>
                            <button type="submit" class="btn btn-primary">Cerca</button>
                        </form>
                    </div>
                </div>


                
                <!-- visualizzazione delle foto -->

                <?php
                if($_SERVER["REQUEST_METHOD"] == "GET") {

                    $numeroRighe=numeroRighe(); //numero di foto uscite dal filtro
                    echo "<h3>Risultato ricerca: $numeroRighe foto</h3><br>";
                    visualizzaFoto(); //accesso a database, selezione foto e stampa dei risultati
                   
                    
                    
                }
                ?>

                <div class="row">
                    <div class="col-md-6 col-md-offset-3">

                        <ul class="pagination">
                            

                            <?php 
                                    
                                    
                                    global $immaginiPerPagina;
                                    parse_str($_SERVER['QUERY_STRING'], $query_string);
                                    $query_string['pag'] = ($query_string['pag']-$immaginiPerPagina);
                                    $nuovaQueryIndietro = http_build_query($query_string);

                                    $pagina = $_GET["pag"];
                                    if($pagina==0)
                                    {
                                        echo '<li class="paginate_button previous disabled" aria-controls="dataTables-example"
                                        tabindex="0"><a href="#">Precedente</a></li>';
                                    }
                                    else
                                    {
                                        echo '<li class="paginate_button previous" aria-controls="dataTables-example"
                                        tabindex="0"><a href="http://localhost/visualizza.php?'.$nuovaQueryIndietro.'">Precedente</a></li>';
                                    }

                                    //Stampo tutte le pagine
                                    

                                    //1   2    3    4    5
                                    //0   2    4    6    8


                                    $numeroPagine = ($numeroRighe/$immaginiPerPagina); // 2 Foto per pagina
                                    $ultimaPagina = false;
                                    for($i=0;$i<$numeroPagine;$i++)
                                    {
                                        parse_str($_SERVER['QUERY_STRING'], $query_string);
                                        $pagAttuale=$query_string['pag'];
                                        $query_string['pag'] = ($i*$immaginiPerPagina);
                                        $rdr_str = http_build_query($query_string);
                                      
                                        if(($pagAttuale/$immaginiPerPagina)==($i))
                                        {
                                            echo '<li class="paginate_button active" aria-controls="dataTables-example" tabindex="0"><a href="http://localhost/visualizza.php?'.$rdr_str.'">'.($i+1).'</a></li>';
                                            $ultimaPagina = true;
                                        }
                                        else
                                        {
                                            echo '<li class="paginate_button" aria-controls="dataTables-example" tabindex="0"><a href="http://localhost/visualizza.php?'.$rdr_str.'">'.($i+1).'</a></li>';
                                            $ultimaPagina = false;
                                        }
                                        
                                        
                                    }

                                    
                                    if($ultimaPagina==true)
                                    {
                                        echo '<li class="paginate_button next disabled" aria-controls="dataTables-example" tabindex="0"><a
                                        href="#">Sucessiva</a></li>';
                                    }
                                    else
                                    {
                                        parse_str($_SERVER['QUERY_STRING'], $query_string);
                                        $query_string['pag'] = ($query_string['pag']+$immaginiPerPagina);
                                        $nuovaQueryAvanti = http_build_query($query_string);
                                        echo '<li class="paginate_button next" aria-controls="dataTables-example" tabindex="0"><a
                                        href="http://localhost/visualizza.php?'.$nuovaQueryAvanti.'">Sucessiva</a></li>';
                                    }
                                    
                            ?>
                            
                            
                        </ul>


                    </div>
                </div>
                

            </div>
            <!-- /.row -->
        </div>
        <!-- /#page-wrapper -->

    </div>
    <!-- /#wrapper -->

    <!-- jQuery -->
    <script src="vendor/jquery/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="vendor/bootstrap/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="vendor/metisMenu/metisMenu.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="dist/js/sb-admin-2.js"></script>

    <!-- Morris Charts JavaScript -->
    <script src="vendor/raphael/raphael.min.js"></script>
    <script src="vendor/morrisjs/morris.min.js"></script>
    <script src="data/morris-data.js"></script>




</body>

</html>