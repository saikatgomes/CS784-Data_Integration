<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CS 784 - Data Integration Project</title>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>

<?php
    $HOME="";
    $ACTIVE="";
    include 'header.php';
?>



<br>
<br>


<center>
<div class="row">
  <div class="col-md-12">
<font face="Courier New"><b>
<?php
	$dir = "./webpages/craiglist";
	$dh  = opendir($dir);
	while (false !== ($filename = readdir($dh))) {
	    $files[] = $filename;
	}
	
	$count=0;	
	$tot=sizeof($files)-2;
	print "<p> Total number of pages: <b>".$tot."</b></p><p>";

	rsort($files);
	foreach ($files as $b)
	{
		if($b != "." && $b!=".."){
			print "<button type=\"button\" class=\"btn btn-primary\"><a href='webpages/craiglist/".$b."'><b><span class=\"glyphicon glyphicon-pushpin\"></span> ".$b."</b></a></button>";
			$count++;
			if ($count % 5 ==0){
				print "</p><p>";
			}
		}
	}	
	print "</p>";
?>
</b></font>
  </div>
  </div>
</div>
</center>

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
          m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

              ga('create', 'UA-55710269-1', 'auto');
                ga('send', 'pageview');

                </script>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="bootstrap/js/bootstrap.min.js"></script>
	<script src="bootstrap-table-master/src/bootstrap-table.js"></script>
  </body>
</html>
