<?php
$myfile = file("data/blocking_data/blocks.table") or die("Unable to open file!");
$total_sample = 0;
$kbb_t = json_decode(file_get_contents("data/all_makes_table_kbb.json"), true)['table']['tuples'] or die("Cannot load kbb data");
$craiglist_t = json_decode(file_get_contents("data/all_makes_table_craiglist.json"), true)['table']['tuples'];
$kbb = array();
$craiglist = array();

foreach($kbb_t as $t ){
  $kbb[$t['id']] = $t;
}
foreach($craiglist_t as $t ){
  $craiglist[$t['id']] = $t;
}

echo "<table border='1'>";
foreach ($myfile as $line_num => $line) {
  if ($line_num ==0) continue;
  if (rand(0, 900) == 0) {
    echo "<tr>";
    $total_sample += 1 ;
    #echo "Line #<b>{$line_num}</b> : " . htmlspecialchars($line) . "<br />\n";
    list($id1, $id2, $id3) = split(',', $line);
    echo "<td>";
    foreach($kbb[intval($id3)] as $k => $v) {
      if ($v) {
	echo $k. "=>" . $v. "<br/>";
      }
    }
    echo "</td><td>";
    foreach($craiglist[intval($id2)] as $k => $v) {
      if ($v) {
	echo $k. "=>" . $v. "<br/>";
      }
    }
    echo "</td></tr>";
  }
}
echo "</table>";
echo "<h1>{$total_sample}</h1>";
?>
