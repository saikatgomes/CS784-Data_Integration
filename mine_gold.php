<?php
ini_set('memory_limit', '-1');
$gold_file = "data/golden_data.json";
if ($_SERVER['REQUEST_METHOD'] == "POST") {
  $gold = json_decode(file_get_contents($gold_file), true);
  foreach($_POST as $k => $v) {
    if ($gold[$k] and $gold[$k] != $v) {
      echo "Watch {$k}, {$v}, {$gold[$k]}";
    }
    $gold[$k] = $v;
  }
  $json_string = json_encode($gold, JSON_PRETTY_PRINT);
  file_put_contents($gold_file, $json_string);
  echo $json_string;
  exit();
}
?>
<!DOCTYPE html>
<meta charset='UTF-8'>
<title>Golden data</title>
<html>
  <header> 
    <style>
     table, td, tr {
       border: 1px solid black;
       }
     td.kbb, td.craiglist {
       width: 400px;
     }
    </style>
    <h2>Randomly generated ids from KBB and Craiglist to match</h2>
  </header>
  <script src='//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js'></script>
  <body align="center">
    <?php    
    $myfile = file("data/blocking_data/blocks.table") or die("Unable to open file!");
    $total_sample = 0;
    $kbb_t = json_decode(file_get_contents("data/all_makes_table_kbb.json"), true)['table']['tuples'] or die("Cannot load kbb data");
    $craiglist_t = json_decode(file_get_contents("data/all_makes_table_craiglist.json"), true)['table']['tuples'];
    $gold = json_decode(file_get_contents($gold_file), true);
    $kbb = array();
    $craiglist = array();
    
    foreach($kbb_t as $t ){
      $kbb[$t['id']] = $t;
    }
    foreach($craiglist_t as $t ){
      $craiglist[$t['id']] = $t;
    }

    echo "<table class='data' border='1'>";
    echo "<tr class='header'><td>KBB</td><td>Craiglist</td><td>Match</td></tr>";
    foreach ($myfile as $line_num => $line) {
      if ($line_num ==0) continue;
      if (rand(0, 90000) == 0) {
	echo "<tr>";
	$total_sample += 1 ;
	#echo "Line #<b>{$line_num}</b> : " . htmlspecialchars($line) . "<br />\n";
	list($id1, $id2, $id3) = split(',', $line);
	echo "<td class='kbb'>";
	# selected.append($id1);

	# TODO use some subset of keys to match
	$useful_kbb_key = array_keys($kbb_t);#[];
	$useful_craig_key = array_keys($craiglist_t);#[];
    
	foreach($kbb[intval($id3)] as $k => $v) {
	  if ($v) {
	    echo $k. "=>" . $v. "<br/>";
	  }
	}
	echo "</td><td class='craiglist'>";
	foreach($craiglist[intval($id2)] as $k => $v) {
	  if ($v) {
	    echo $k. "=>" . $v. "<br/>";
	  }
	}
	if (! isset($gold[$id1])){
	  $gold[$id1] = 'MAYBE';
	}
	echo "</td>";
	echo "<td><input class='match' id='{$id1}' type='button' value='{$gold[$id1]}' onclick='Matched(this)' /></td>";
	echo "</tr>";
      }
    }
    echo "</table>";
    echo "<h1>{$total_sample}</h1>";
    ?>
    <div align="center">
      <input id='submit' type='button' value="Submit all matched data" onclick="Submit()"/>
      <script>
       // Main data array id => Yes/No, Maybe
       M = {};

       Rotate = {"MAYBE": "YES", "YES": "NO", "NO": "MAYBE"};

       function Matched(event) {
	 if ($.isEmptyObject(M)) {
	   console.log("Initializing M");
	   // first initialize the M
	   all = $('.match');
	   for( var k in  all) {
	     M[all[k].id] = all[k].value;
	   }
	   console.log(M);
	 } 
	 id = event.id;
	 s = "";
	 nVal = Rotate[event.value];
	 event.value = nVal;
	 M[id] = nVal;
       }

       function Submit() {
	 $.post("mine_gold.php", M, function(returnedData) {
	   // This callback is executed if the post was successful 
	   alert("SUCCESS!!");
	   console.log("Returned Value:", returnedData);
	 }, "json");
       }   
      </script>
      <footer align="center">All rights reserverd to Rahul and Saikat !</footer>
    </div>
  </body>
</html>
