<?php
define("DB_FILENAME", "../../sqlite/iventory20160209.sqlite3");

function getData($id)
{
  $db = new SQLite3(DB_FILENAME);
  $result = $db->query("SELECT * FROM iventory WHERE number='".$id."'");

  while ($record = $result->fetchArray())
    {
      print($record['name']."\t".$record['place']."\t".$record['checked']);
    }
  $db->close();
}

function getAll() 
{
  $db = new SQLite3(DB_FILENAME);
  $result = $db->query("SELECT * FROM iventory");
  while ($record = $result->fetchArray())
    {
      print($record['number']."<br />");
    }
  $db->close();
}

//getAll();

if (is_string($_GET['number']))
{
  getData(htmlentities($_GET['number']));
} else {
  getData("7PVG G4");
}

?>
