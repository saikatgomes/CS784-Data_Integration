<?php
ini_set('memory_limit', '-1');


$conf_file = "conf.json";
$conf = json_decode(file_get_contents($conf_file), true) or die("Unable to open conffile - ". $conf_file);
$sample_f = $conf["Outputdir"]."/sample_blocks.json";
$result_f = $conf["Outputdir"]."/result.txt";
#print_r($conf);

function setup($conf_file) {
    global $sample_f, $result_f;
    $conf = json_decode(file_get_contents($conf_file), true) or die("Unable to open conffile - ". $conf_file);

    $block_f = file($conf["TableInfo"]["BlockingTable"]) or die("Unable to open file - ".$conf['TableInfo']['BlockingTable']);

    $T1_t = json_decode(file_get_contents($conf["TableInfo"]["Table-1"]), true)['table']['tuples'] or die("Cannot load kbb data - ". $conf["TableInfo"]["Table-1"]);
    $T2_t = json_decode(file_get_contents($conf["TableInfo"]["Table-2"]), true)['table']['tuples'] or die("Cannot load craig data - ". $conf["TableInfo"]["Table-2"]);

    $T1 = array();
    $T2 = array();
    
    $total_sample = 0;
    foreach($T1_t as $t ){
	$T1[$t['id']] = $t;
    }
    foreach($T2_t as $t ){
	$T2[$t['id']] = $t;
    }

    $allblocks = [];
    foreach ($block_f as $line_num => $line) {
	array_push($allblocks, $line);
    }

    $block = [];
    $rand_sample = array_rand($allblocks, $conf["NumGoldenSample"]);
    $fp = fopen($result_f, 'w') or die("DIAF - {$result_f} cannot be created");
    foreach($rand_sample as $l => $n) {
	list($idb, $id1, $id2) = split(',', $allblocks[$n]);
	if (is_null($idb) || is_null($id1) || is_null($id2)) continue;
	$block[$idb] = Array(
	    "T1" => $T1[intval($id1)],
	    "T2" => $T2[intval($id2)]
	);
	fwrite($fp, "{$idb},0\n");
    }
    $json_string = json_encode($block, JSON_PRETTY_PRINT);
    file_put_contents($sample_f, $json_string);
    fclose($fp);
}
function sample($num_send=1) {
    # API: /?num_send=1 
    # read the sample file and pick one
    $rand_sample = json_decode(file_get_contents($sample_f), true) or die("Unable to open conffile - ". $sample_f);
    $cnt_a = Array();
    foreach(file($result_f) as $l => $line) {
	list($idb,$cnt) = split(',', $line);
	$cnt_a[intval($idb)] += 1;
    }
    $arr = [];
    foreach ($cnt_a as $id => $c) {
	for ($i=0; $i<$conf["Overlap"]-$c+1; $i++) {
	    array_push($arr, $id);
	}
    }
    $res = [];
    $rand_get = array_rand($arr, $num_send);
    if ($num_send == 1) {
	$res = $rand_sample[$arr[$rand_get]];
	#print_r($res);
	return json_encode([$res], JSON_PRETTY_PRINT);
    }
    else {
	foreach($rand_get as $x => $n) {
	    array_push($res, $rand_sample[$arr[$n]]);
	}
	return json_encode($res, JSON_PRETTY_PRINT);
	#print_r ($res);
    }
}
if ($_SERVER['REQUEST_METHOD'] == "GET" ) {
    # if ($_GET["setup"] == 1) {
    # 	# Setup the stuffs
    # 	# read conffile
    # 	setup($_GET["conffile"]);
    # }
    # else {
    # 	#n$num_send = $_GET["num_send"] || 1;
    # 	#sample($num_send);
    # }
}
else if ($_SERVER['REQUEST_METHOD'] == "POST") {
    # API: 
    # blockingid=id
    # res=1 (or 0,-1)
    $blockid = $_POST['blockingid'];
    $res = $_POST['choice'];
    
    $fp = fopen($result_f, 'a');
    fwrite($fp, "{$blockid},{$res}\n");
    fclose($fp);
}

#
#parse_str(implode('&', array_slice($argv, 1)), $_GET);
#setup($_GET["conffile"]);
?>
