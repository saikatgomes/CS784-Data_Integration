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
    $ACTIVE="BLOCKING";
    include 'header.php';
?>


<div class="row">
  <div class="col-md-1"></div>
  <div class="col-md-10">
    <center>
    <div class="panel panel-primary" align="left" style="width: 80%">
	<div class="panel-body">
        <center><h2><b>Blocking</b></h2>
            <a class="btn btn-primary btn" role="button" href="data/blocking_data/blocks.table">Results</a>
            <a class="btn btn-primary btn" role="button" href="code.php">Code</a>
        </center></p>
	<div class="panel panel-default">
	  <div class="panel-body" style="padding:2em">
            <p>
	      
	      Complete cross product of matching tables can generate unrealistic number of tuplese
	      and makes the final complex matching procedure to be very inefficient. We have
	      nearly <b>4953</b> tuples in Craiglist <b>25412</b> tuples in KBB table. Cross join will 
	      generate <b>125,865,636</b> many tuple pairs. Clearly there is strictly less than 4953 
	      matching tuples. To decrease the false matches, we do first level "mis-matching" of the data
	      and decrease the number to <b>295,502</b> (99.31% decrease). <br/><br/>
	    </p>
	    <p>
	      <h3>Methodology</h3>
	      <ul>
		<li><b>Reverse Indexed KBB tuples</b><br/>  We created a dictionary of keywords in KBB using only 
		  "make", "model" and "year" and then a reverse index from each keyword to the set of kbb_ids 
		  that contain that keyword. There are 62 keywords in "make", 717 keywords in "model" and 21 
		  in "year". For few "makes" and "model" contain multiple words we split them into two keywords.
		</li><br/>
		<li>
		  <b> Bag of words for Craiglist tuples</b><br/> For each tuple in Craiglist we
		  build a bag of words (BoW) containing the unique words from "title", "attr_title"
		  and "year".
		</li><br/>
		<li><b>Voting</b> <br/> After creating BoW, we take majority weighted voting of the ids for each
		  <i>closest</i> matched Keywords in KBB. A keyword is called <i>Closest</i> to a word in BoW if 
		  it is at an edit distance of 2. Also, after weighted voting only ids of KBB are kept in final tuple pairs
		  which are above a certain threshold.
		</li>
	    </p>
	  </div>
	  
	</div>
	</center>
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
