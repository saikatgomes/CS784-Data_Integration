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
	$ACTIVE="HOME";
	include 'header.php';
	?>

	<br>

	<div class="row">
	    <div class="col-md-1"></div>
	    <div class="col-md-5">
			<div class="jumbotron">
				<h1>Data Integration</h1>
				<p><br></p>
				<p>Buying and selling used cars is a huge industry. For this project we deccied to crawl
				<b><a href='https://madison.craigslist.org/search/cta'>Craiglist</a></b> and
				<b><a href="https://www.kbb.com">Kelly Blue Book</a></b> for used cars. Our goal is to extract
				relavent information from the unstructed Craiglist post and match the post with a car
				configuration on Kelly Blue Book website.<br><br>
				We have crawled <u>4953</u> Craiglist postings and about <u>25412</u> Kelly Blue Book listings.</p>
				<p><br></p>
				<p>
				<a class="btn btn-primary btn-lg" role="button" href="data.html">See Data</a>
				<a class="btn btn-primary btn-lg" role="button" href="code.php">See Code</a>
				<a class="btn btn-primary btn-lg" role="button" href="golden">Mine Gold</a>
				<br><br>
				<div class="panel panel-default">
				  <div class="panel-body">
					<h3>
					<a style="color: #f00"><b>NEW: </b></a>
					<a href="https://docs.google.com/document/d/1UrzwqCmSO5qoGM5CEcJro9lLc_QHC3aCYICA4e_ngVU/edit?usp=sharing" >Final document for the project. [click here]</a>
					</h3>
				  </div>
				</div>
				</p>
			</div> 
	    </div>
	    <div class="col-md-5">
		<br>
        <!--<img src="img/car.png" class="img-responsive" alt="Responsive image">-->
	    <img src="img/confusion_matrix.gif" class="img-responsive" alt="Responsive image">
        <br>
        <h4><b>Class 1 :</b> Matching Cars<br><b>Class 2 :</b> Different Cars</h4>
        <h6>1-See: J. Richard Landis and Gary G. Koch - The Measurement of Observer Agreement for Categorical Data, Biometrics, Vol. 33, No. 1 (Mar., 1977), pp. 159-174.<br>
        Source: <a href="http://www.marcovanetti.com/pages/cfmatrix/">http://www.marcovanetti.com/pages/cfmatrix/</a></h6>
        </div>
	    <div class="col-md-1"></div>
	</div>
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
