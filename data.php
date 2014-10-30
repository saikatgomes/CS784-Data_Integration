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
    $ACTIVE="DATA";
    include 'header.php';
?>


<div class="row">
  <div class="col-md-1"></div>
  <div class="col-md-5">
    <div class="panel panel-primary">
	<div class="panel-body">
        <h2><center>Table 1: <b>Craigslist</b></center></h2>
        <p><center>
            <a class="btn btn-primary btn" role="button" href="data/all_makes_table_craiglist.json">JSON Data</a>
            <a class="btn btn-primary btn" role="button" href="craig_webpages.php">HTML Pages</a>
            <a class="btn btn-primary btn" role="button">Formatted Table</a>
        </center></p>
	<div class="panel panel-default">
	  <div class="panel-body">
	    <b>Description:</b> <br>Each tuple was extracted from crawled data from used car postings in Craigslist.
	  </div>
	</div>
	<p>
	<table class="table table-hover table-condensed">
	      <thead>
		<tr>
		  <th>Attribute</th>
		  <th>Type</th>
		  <th>Description</th>
		</tr>
	      </thead>
	      <tbody>
		<tr>
		  <td>id</td>
		  <td>integer</td>
		  <td>craiglist posting id</td>
		</tr>
		<tr>
		  <td>title</td>
		  <td>string</td>
		  <td>title of the posting</td>
		</tr>
		<tr>
		  <td>body</td>
		  <td>string</td>
		  <td>body fo the posting</td>
		</tr>
		<tr>
		  <td>cost</td>
		  <td>string</td>
		  <td>listed cost of the posting</td>
		</tr>
		<tr>
		  <td>location</td>
		  <td>string</td>
		  <td>location of the posting</td>
		</tr>
		<tr>
		  <td>make</td>
		  <td>string</td>
		  <td>make of the car</td>
		</tr>
		<tr>
		  <td>year</td>
		  <td>integer</td>
		  <td>year of the car</td>
		</tr>
		<tr>
		  <td>posted</td>
		  <td>string</td>
		  <td>date of the posting</td>
		</tr>
		<tr>
		  <td>updated</td>
		  <td>string</td>
		  <td>date of last update of the posting</td>
		</tr>
		<tr>
		  <td>attr_VIN</td>
		  <td>string</td>
		  <td>VIM of the car</td>
		</tr>
		<tr>
		  <td>attr_condition</td>
		  <td>string</td>
		  <td>car condition</td>
		</tr>
		<tr>
		  <td>attr_cylinders</td>
		  <td>string</td>
		  <td>number of cylinders in engine</td>
		</tr>
		<tr>
		  <td>attr_drive</td>
		  <td>string</td>
		  <td>drive type</td>
		</tr>
		<tr>
		  <td>attr_fuel</td>
		  <td>string</td>
		  <td>fuel type</td>
		</tr>
		<tr>
		  <td>attr_odometer</td>
		  <td>string</td>
		  <td>odometer reading</td>
		</tr>
		<tr>
		  <td>attr_paint color</td>
		  <td>string</td>
		  <td>color of the car</td>
		</tr>
		<tr>
		  <td>attr_size</td>
		  <td>string</td>
		  <td>engine size</td>
		</tr>
		<tr>
		  <td>attr_size / dimensions</td>
		  <td>string</td>
		  <td>car dimensions</td>
		</tr>
		<tr>
		  <td>attr_title</td>
		  <td>string</td>
		  <td>DMV title type</td>
		</tr>
		<tr>
		  <td>attr_title status</td>
		  <td>string</td>
		  <td>DMV title status</td>
		</tr>
		<tr>
		  <td>attr_transmission</td>
		  <td>string</td>
		  <td>transmission type</td>
		</tr>
		<tr>
		  <td>attr_type</td>
		  <td>string</td>
		  <td>car type</td>
		</tr>
	      </tbody>
	</table>
	</p>
	</div>
    </div>
  </div>
  <div class="col-md-5">
    <div class="panel panel-primary">
	<div class="panel-body">
        <h2><center>Table 2: <b>KBB</b></center></h2>
        <p><center>
            <a class="btn btn-primary btn" role="button" href="data/all_makes_table_kbb.json">JSON Data</a>
            <a class="btn btn-primary btn" role="button" href="webpages/kbb/">HTML Pages</a>
            <a class="btn btn-primary btn" role="button">Formatted Table</a>
        </center></p>
	<div class="panel panel-default">
	  <div class="panel-body">
	    <b>Description:</b> <br>Each tuple was extracted from crawled data from Kelly Blue Book listing.
	  </div>
	</div>
	<p>
	<table class="table table-hover table-condensed">
	      <thead>
		<tr>
		  <th>Attribute</th>
		  <th>Type</th>
		  <th>Description</th>
		</tr>
	      </thead>
	      <tbody>
		<tr>
		  <td>id</td>
		  <td>integer</td>
		  <td>KBB vehicle id</td>
		</tr>
		<tr>
		  <td>make</td>
		  <td>string</td>
		  <td>make of the car</td>
		</tr>
		<tr>
		  <td>category</td>
		  <td>string</td>
		  <td>car category</td>
		</tr>
		<tr>
		  <td>year</td>
		  <td>integer</td>
		  <td>year of the car</td>
		</tr>
		<tr>
		  <td>model</td>
		  <td>string</td>
		  <td>car model</td>
		</tr>
		<tr>
		  <td>sub_cat</td>
		  <td>string</td>
		  <td>sub category for the car</td>
		</tr>
		<tr>
		  <td>attr_drivetrain</td>
		  <td>string</td>
		  <td>drive train of the car</td>
		</tr>
		<tr>
		  <td>attr_engine</td>
		  <td>string</td>
		  <td>engine type</td>
		</tr>
		<tr>
		  <td>attr_transmission</td>
		  <td>string</td>
		  <td>transmission type.</td>
		</tr>
		<tr>
		  <td>url</td>
		  <td>string</td>
		  <td>url</td>
		</tr>
	      </tbody>
	</table>
	</p>
	</div>
    </div>
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
