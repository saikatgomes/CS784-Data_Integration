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
	 table {
	     table-layout:fixed;
	     overflow:hidden;
	 }
	 td {
	     width: 80px;
	     text-align: center;
	 }
	 td.kbb, td.craiglist {
	     width: 400px;
	     text-align:left;
	 }
	 pre {
	     background-color: ghostwhite;
	     border: 1px solid silver;
	     padding: 10px 20px;
	     margin: 20px; 
	 }
	 .json-key {
	     color: brown;
	 }
	 .json-value {
	     color: navy;
	 }
	 .json-string {
	     color: olive;
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
	$block = Array();
	foreach ($myfile as $line_num => $line) {
	    if ($line_num ==0) continue;
	    list($id1, $id2, $id3) = split(',', $line);
	    if (!isset($block[$id2])) {
		$block[$id2] = [];
	    }
	    $block[$id2][$id1] = intval($id3);
	}
	$kbb_subset = Array(); # kbb data for global database of useful kbb info
	# TODO use some subset of keys to match
	$useful_kbb_key = ["id", "model", "make", "sub_cat", "year", "attr_transmission","attr_engine", "attr_drivetrain"]; #array_keys($kbb_t);#[];
	$useful_craig_key = ["id", "model", "make", "title", "year","attr_title", "attr_cylinders", "attr_drive", "attr_transmission"]; # array_keys($craiglist_t);#[];
	echo "<table class='data' border='1'>";
	echo "<tr class='header'><td>Craiglist</td><td>match-id</td><td>KBB desc</td></tr>";
	foreach ( $block as $crid => $idset ) {
	    $first = [];
	    if (rand(0, 900) == 0) {
		$total_sample += 1 ;
		echo "<tr><td class='craiglist'>";
		foreach($useful_craig_key as $k) {
		    $v = $craiglist[$crid][$k];
		    if ($v) {
			echo $k. " => " . $v. "<br/>";
		    }
		}
		echo "</td><td class='blockid'>\n<select class='ddow' id='id-{$crid}' onchange='Select({$crid}, this.value)'>\n";
		
		foreach($idset as $blockid => $kbbid) {
		    if (!$first) {
			$first = [$blockid, $kbbid];
		    }
		    echo "<option value='{$kbbid}-{$blockid}'>{$blockid}</option>\n";
		    $kbb_subset[$kbbid] = Array();
		    foreach($useful_kbb_key as $ukbb) {
			$v = $kbb[$kbbid][$ukbb];
			if ($v) {
			    $kbb_subset[$kbbid][$ukbb] = $v;
			}
		    }
		}
		echo "</select></td>";
		echo "<td class='kbb' id='desc-{$crid}'>".json_encode($kbb_subset[$first[1]], JSON_PRETTY_PRINT)."</td><td><input type='button' class='button-{$crid}' id='{$first[0]}' value='MAYBE' onclick='Matched(this)'/></td></tr>";
		# if (! isset($gold[$id1])){
		#     $gold[$id1] = 'MAYBE';
		# }
		# echo "</td>";
		# echo "<td><input class='match' id='{$id1}' type='button' value='{$gold[$id1]}' onclick='Matched(this)' /></td>";
		# echo "</tr>";
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
	     KBB_DIC = <?php echo json_encode($kbb_subset, JSON_PRETTY_PRINT)?>;
	     function Matched(event) {
		 //if ($.isEmptyObject(M)) {
		 //    console.log("Initializing M");
		 //    // first initialize the M
		 //    all = $('.match');
		 //    for( var k in  all) {
		 //	 M[all[k].id] = all[k].value;
		 //    }
		 //    console.log(M);
		 //}
		 id = event.id;
		 s = "";
		 nVal = Rotate[event.value];
		 event.value = nVal;
		 M[id] = nVal;
		 console.log(M);
	     }
	     if (!library)
		 var library = {};
	     library.json = {
		 replacer: function(match, pIndent, pKey, pVal, pEnd) {
		     var key = '<span class=json-key>';
		     var val = '<span class=json-value>';
		     var str = '<span class=json-string>';
		     var r = pIndent || '';
		     if (pKey)
			 r = r + key + pKey.replace(/[": ]/g, '') + '</span>: ';
		     if (pVal)
			 r = r + (pVal[0] == '"' ? str : val) + pVal + '</span>';
		     return r + (pEnd || '');
		 },
		 prettyPrint: function(obj) {
		     var jsonLine = /^( *)("[\w]+": )?("[^"]*"|[\w.+-]*)?([,[{])?$/mg;
		     return JSON.stringify(obj, null, 3)
				.replace(/&/g, '&amp;').replace(/\\"/g, '&quot;')
			 .replace(/</g, '&lt;').replace(/>/g, '&gt;')
			 .replace(jsonLine, library.json.replacer);
		 }
	     };
	     function Submit() {
		 $.post("mine_gold.php", M, function(returnedData) {
		     // This callback is executed if the post was successful 
		     alert("SUCCESS!!");
		     console.log("Returned Value:", returnedData);
		 }, "json");
	     }   
	     function Select(id, sel){
		 val = sel.split('-'); // kbbid - blockid
		 //$('#desc-'+id).html('<span>' + library.json.prettyPrint(KBB_DIC[val[0]])+'</span>');
		 $('#desc-'+id).html(JSON.stringify(KBB_DIC[val[0]], undefined, 4));
		 b = $('.button-'+id)[0];
		 b.id = val[1];
		 b.value = M[id] || "MAYBE";
	     }
	    </script>
	    <footer align="center">All rights reserverd to Rahul and Saikat !</footer>
	</div>
    </body>
</html>
