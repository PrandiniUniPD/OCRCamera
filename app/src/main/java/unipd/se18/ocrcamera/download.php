<?php
session_start();
if (!isset( $_SESSION['user'] ) ) {
  header("location: /index.php");
}

/**
Generate a Zip archive containing images data.
*/

$zip = new ZipArchive;
$download = 'tmp/fotoDb.zip';
zip->open($download, (ZipArchive::CREATE | ZipArchive::OVERWRITE));

//add each file to the zip
foreach (glob("foto/*") as $file) {
        $zip->addFile($file);
}
$zip->close();

$file=$download;
//send the image to the client if possible
if (headers_sent()) {
   echo 'HTTP header already sent';
} else {
   if (!is_file($file)) {
       header($_SERVER['SERVER_PROTOCOL'].' 404 Not Found');
       echo 'File not found';
} else if (!is_readable($file)) {
       header($_SERVER['SERVER_PROTOCOL'].' 403 Forbidden');
       echo 'File not readable';
} else {
       header($_SERVER['SERVER_PROTOCOL'].' 200 OK');
       header("Content-Type: application/zip");
       header("Content-Transfer-Encoding: Binary");
       header("Content-Length: ".filesize($file));
       header("Content-Disposition: attachment; filename=\"".basename($file)."\"");
       readfile($file);
       exit;
   }
}
?>
