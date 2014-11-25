
<!-- Bootstrap -->
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->

<?php
if (empty($HOME)) {
    $HOME="http://pages.cs.wisc.edu/~saikat/projects/data_integration/";
    # $HOME = "/"; #$_SERVER['HTTP_HOST']."/";
}
$home_class="";
$data_class="";
$blocking_class="";
$code_class="";
$team_class="";
$gold_class = "";
$active_class="class='active'";
if (!empty($ACTIVE)){
    if(strcmp($ACTIVE,"HOME")==0){
        $home_class =$active_class;
    } elseif(strcmp($ACTIVE,"DATA")==0){
        $data_class =$active_class;
    } elseif(strcmp($ACTIVE,"BLOCKING")==0){
        $blocking_class =$active_class;
    } elseif(strcmp($ACTIVE,"CODE")==0){
        $code_class =$active_class;
    } elseif(strcmp($ACTIVE,"TEAM")==0){
        $team_class =$active_class;
    } elseif(strcmp($ACTIVE,"GOLD")==0){
        $gold_class =$active_class;
    }
}
?>

<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
	<!-- Brand and toggle get grouped for better mobile display -->
	<div class="navbar-header">
	    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
		<span class="sr-only">Toggle navigation</span>
		<span class="icon-bar"></span>
		<span class="icon-bar"></span>
		<span class="icon-bar"></span>
	    </button>
	    <a class="navbar-brand" href="#">CS 789: Data Model and Language</a>
	</div>

	<!-- Collect the nav links, forms, and other content for toggling -->
	<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
	    <ul class="nav navbar-nav">
		<li <?php echo $home_class?>><a href=<?php echo $HOME ?>>Home</a></li>
		<li <?php echo $data_class?>><a href=<?php echo $HOME."data.php" ?>>Data</a></li>
		<li <?php echo $blocking_class?>><a href=<?php echo $HOME."block.php" ?>>Blocking</a></li>
		<li <?php echo $code_class?>><a href=<?php echo $HOME."code.php" ?>>Code</a></li>
		<li <?php echo $gold_class?>><a href=<?php echo $HOME."golden/" ?>>Mine Gold</a></li>
	    </ul>
	    <ul class="nav navbar-nav navbar-right">
		<li <?php echo $team_class?>><a href=<?php echo $HOME."team.php" ?>>Team</a></li>
	    </ul>
	</div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="bootstrap-table-master/src/bootstrap-table.js"></script>
