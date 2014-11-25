<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CS 784 - Data Integration Project</title>

    <!-- Bootstrap -->
    <link href="../bootstrap/css/bootstrap.min.css" rel="stylesheet">

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
    $ACTIVE="GOLD";
    include '../header.php';
?>
<div class="row">
    <div class="col-md-12">
        <center>
        <button type="button" class="btn btn-success">Is A Match</button>
        <button type="button" class="btn btn-danger">Not A Match</button>
        <button type="button" class="btn btn-warning">Not Relevant</button>
        </center>
    </div>
</div>

<div class="row">
  <div class="col-md-1"></div>
  <div class="col-md-5">
    <div class="panel panel-primary">
	<div class="panel-body">
	<p>
	<table class="table table-hover table-condensed">
	      <thead>
		<tr>
		  <th>Attribute</th>
		  <th>Description</th>
		</tr>
	      </thead>
	      <tbody>
		<tr>
		  <td>id</td>
		  <td>craiglist posting id</td>
		</tr>
		<tr>
		  <td>title</td>
		  <td>title of the posting</td>
		</tr>
		<tr>
		  <td>body</td>
		  <td>body fo the posting</td>
		</tr>
		<tr>
		  <td>cost</td>
		  <td>listed cost of the posting</td>
		</tr>
		<tr>
		  <td>location</td>
		  <td>location of the posting</td>
		</tr>
		<tr>
		  <td>make</td>
		  <td>make of the car</td>
		</tr>
		<tr>
		  <td>year</td>
		  <td>year of the car</td>
		</tr>
		<tr>
		  <td>posted</td>
		  <td>date of the posting</td>
		</tr>
		<tr>
		  <td>updated</td>
		  <td>date of last update of the posting</td>
		</tr>
		<tr>
		  <td>attr_VIN</td>
		  <td>VIM of the car</td>
		</tr>
		<tr>
		  <td>attr_condition</td>
		  <td>car condition</td>
		</tr>
		<tr>
		  <td>attr_cylinders</td>
		  <td>number of cylinders in engine</td>
		</tr>
		<tr>
		  <td>attr_drive</td>
		  <td>drive type</td>
		</tr>
		<tr>
		  <td>attr_fuel</td>
		  <td>fuel type</td>
		</tr>
		<tr>
		  <td>attr_odometer</td>
		  <td>odometer reading</td>
		</tr>
		<tr>
		  <td>attr_paint color</td>
		  <td>color of the car</td>
		</tr>
		<tr>
		  <td>attr_size</td>
		  <td>engine size</td>
		</tr>
		<tr>
		  <td>attr_size / dimensions</td>
		  <td>car dimensions</td>
		</tr>
		<tr>
		  <td>attr_title</td>
		  <td>DMV title type</td>
		</tr>
		<tr>
		  <td>attr_title status</td>
		  <td>DMV title status</td>
		</tr>
		<tr>
		  <td>attr_transmission</td>
		  <td>transmission type</td>
		</tr>
		<tr>
		  <td>attr_type</td>
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
	<p>
	<table class="table table-hover table-condensed">
	      <thead>
		<tr>
		  <th>Attribute</th>
		  <th>Description</th>
		</tr>
	      </thead>
	      <tbody>
		<tr>
		  <td>id</td>
		  <td>KBB vehicle id</td>
		</tr>
		<tr>
		  <td>make</td>
		  <td>make of the car</td>
		</tr>
		<tr>
		  <td>category</td>
		  <td>car category</td>
		</tr>
		<tr>
		  <td>year</td>
		  <td>year of the car</td>
		</tr>
		<tr>
		  <td>model</td>
		  <td>car model</td>
		</tr>
		<tr>
		  <td>sub_cat</td>
		  <td>sub category for the car</td>
		</tr>
		<tr>
		  <td>attr_drivetrain</td>
		  <td>drive train of the car</td>
		</tr>
		<tr>
		  <td>attr_engine</td>
		  <td>engine type</td>
		</tr>
		<tr>
		  <td>attr_transmission</td>
		  <td>transmission type.</td>
		</tr>
		<tr>
		  <td>url</td>
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
