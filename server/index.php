<html>
<head>
<link rel="stylesheet" type="text/css" href="iventory.css">
<?php
header("Content-Type: text/html; charset=UTF-8");
define("DB_FILENAME", "../../sqlite/iventory20160209.sqlite3");
define("PAGE_NAME", "./index.php");

function textwrap($text)
{
  return (htmlentities($text, ENT_QUOTES, "UTF-8"));  
}

if (is_string($_GET['number']) and is_string($_GET['checked']) and is_string($_GET['user']))
  {
    $iv_number = htmlentities($_GET['number'], ENT_QUOTES, "utf-8");
    $iv_checked = htmlentities($_GET['checked'], ENT_QUOTES, "utf-8");
    $iv_user = htmlentities($_GET['user'], ENT_QUOTES, "utf-8");
  }

function updateRecord($number, $checked, $user)
{
  if($checked == '0' or $checked == '1'){
    if($checked == '0'){ $num_checked=0; }
    if($checked == '1'){ $num_checked=1; }

    $db = new SQLite3(DB_FILENAME);
    $query = "UPDATE iventory SET checked='".$num_checked."', date='".date("c", time())."', check_person='".$user."' WHERE number='".$number."'";
    //$query = "UPDATE iventory SET checked='".$num_checked."' WHERE number='".$number."'";
    $result = $db->exec($query);
    $db->close();
  }
}


function showRecord ()
{
  $db = new SQLite3(DB_FILENAME);
  $result = $db->query("SELECT * FROM iventory");
  print ("<table class=\"bordered\" id=\"main_table\">\n<thead> <th>Number</th> <th>Team</th> <th>Name</th> <th>Place</th> <th>Place Detail</th> <th>Stored Date</th> <th>Checked Date</th>  <th>Confirmor</th></thead>\n");

  while ($record = $result->fetchArray())
    {
      print("<tr>");
      print("<td>".textwrap($record['number'])."</td>");
      print("<td>".textwrap($record['team'])."</td>");
      print("<td>".textwrap($record['name'])."</td>");
      print("<td>".textwrap($record['place'])."</td>");
      print("<td>".textwrap($record['place_detail'])."</td>");
      print("<td>".textwrap($record['stored_date'])."</td>");
      $class = ($record['checked']==1)?"checked":"notchecked";
      print("<td class=".$class.">".textwrap($record['date'])."</td>");
      print("<td class=".$class.">".textwrap($record['check_person'])."</td>");
      //print("<td class=".$class.">".textwrap($record['checked'])."</td>");
      print("</tr>\n");
    }
  print ("</table>\n");
  $db->close();
}

if (isset($iv_number) and isset($iv_checked) and isset($iv_user)){
  updateRecord($iv_number, $iv_checked, $iv_user);
  header("Location: ".$_SERVER['PHP_SELF']);
  exit();
}

?>
</head>

<body>
<h1>Welcome to Iventory Server</h1>
<div class="getapp">
<a class="getapp" href="./qrreader.apk">Get Android App!</a>
</div>
<div>
<form action="./index.php" method="get">
  User : <input class="text" type="text" name="user" size="10" maxlength="8"/>
  Number : <input class="text" type="text" name="number" size="10" maxlength="8"/>
  <input class="radio" type="radio" name="checked" value="1" checked> OK
  <input class="radio" type="radio" name="checked" value="0"> Wrong
   <input class="submit" type="submit" name="Record" value="Record"/>
</form>
</div>


<div class="result">
<?php
showRecord();
?>
</div>

</body>
</html>
