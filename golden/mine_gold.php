<?php
ini_set('memory_limit', '-1');

switch ($_SERVER['HTTP_ORIGIN']) {
    case 'http://pages.cs.wisc.edu': 
    case 'https://pages.cs.wisc.edu':
    header('Access-Control-Allow-Origin: '.$_SERVER['HTTP_ORIGIN']);
    header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
    header('Access-Control-Max-Age: 1000');
    header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    break;
}

$conf_file = "conf.json";
$conf = json_decode(file_get_contents($conf_file), true) or die("Unable to open conffile - ". $conf_file);
$sample_f = $conf["Outputdir"]."/sample_blocks.json";
$result_f = $conf["Outputdir"]."/result.txt";

function setup($conf_file) {
    echo $conf_file;
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
    global $sample_f, $result_f, $conf;
    $rand_sample = json_decode(file_get_contents($sample_f), true) or die("Unable to open conffile - ". $sample_f);
    $cnt_a = Array();
    foreach(file($result_f) as $l => $line) {
	list($idb,$cnt) = split(',', $line);
	$cnt_a[intval($idb)] += 1;
    }
    $arr = [];
    foreach ($cnt_a as $id => $c) {
	for ($i=0; $i< ($conf["Overlap"]-$c+1); $i++) {
	    array_push($arr, $id);
	}
    }
    $res = [];
    $rand_get = array_rand($arr, $num_send);
    if ($num_send == 1) {
	$rand_get = [$rand_get];
    }
    foreach($rand_get as $x => $n) {
	$r = order_attr($rand_sample[$arr[$n]]);
	$r["blockingid"] = $arr[$n];
	array_push($res, $r);
    }
    return json_encode($res, JSON_PRETTY_PRINT);
}

function save_res($post) {
    # API: 
    # blockingid=id
    # res=1 (or 0,-1)
    global $sample_f, $result_f, $conf;
    $blockid = $post['blockingid'];
    $res = $post['choice'];
    
    $fp = fopen($result_f, 'a');
    fwrite($fp, "{$blockid},{$res}\n");
    fclose($fp);    
}

function kbb_values( $str ){
    $s = array();
    foreach(explode('&', $str ) as $t) {
	array_push($s,explode('|', $t)[0]);
    }
    return implode(',', $s);
}
    
function order_attr( $json_obj ) {
    # T1
    $t1 = $json_obj["T1"];
    $r1 = [];
    $r1["id"]    = $t1["id"];
    $r1["title"] = $t1["attr_title"] or $t1["title"];
    $r1["year"]  = $t1["year"];
    $r1["make"]  = $t1["make"];
    $r1["type"]  = $t1["attr_type"];
    $r1["cylinders"]  = $t1["attr_cylinders"];
    $r1["transmission"]  = $t1["attr_transmission"];
    $r1["drive"]  = $t1["attr_drive"];
    $r1["body"]  = $t1["body"];

    #T2
    $t2 = $json_obj["T2"];
    $r2 = [];
    $r2["id"]    = $t2["id"];
    $r2["title"] = "{$t2['year']} {$t2['make']} {$t2['model']} {$t2['category']}" ;
    $r2["year"]  = $t2["year"];
    $r2["make"]  = "{$t2['make']} {$t2['model']}";
    $r2["type"]  = "{$t2['category']} {$t2['sub_cat']}";
    $r2["cylinders"]  = kbb_values($t2['attr_engine']);
    $r2["transmission"]  = kbb_values($t2['attr_transmission']);
    $r2["drive"]  = kbb_values($t2['attr_drivetrain']);
    
    $ret = array(
	'T1' => $r1,
	'T2' => $r2
    );
    return $ret;
}

if ($_SERVER['REQUEST_METHOD'] == "GET" ) {
    if ($_GET["setup"] == 1) {
     	# Setup the stuffs
     	# read conffile
     	setup($_GET["conffile"]);
    }
    # else {
    # 	#n$num_send = $_GET["num_send"] || 1;
    # 	#sample($num_send);
    # }
}
else if ($_SERVER['REQUEST_METHOD'] == "POST") {
    save_res($_POST);
    echo "--";
    print_r( $_POST);
}

#
#setup($argv[1]);
# save_res($_GET);
#

?>
