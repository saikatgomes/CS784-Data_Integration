<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CS 784 - Data Integration Project</title>
    <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
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
        <li><a href="index.html">Home</a></li>
        <li><a href="data.html">Data</a></li>
        <li class="active"><a href="#">Code</a></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li><a href="team.html">Team</a></li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>

<div class="row">
  <div class="col-md-1"> </div>
  <div class="col-md-10"></div>
	<div class="panel panel-default">
	  <div class="panel-body">
	    <center>Please click on the links below to see the code.</center>
	  </div>
	</div>
  <div class="col-md-1"> </div>
  </div>
</div>

<div class="row">
  <div class="col-md-1"></div>
  <div class="col-md-10">
  <div class="panel-group" id="accordion"> 
    <div class="panel panel-default">
        <div class="panel-heading">
             <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
                  crawl_craiglist_cars.py 
                </a>
             </h4>
        </div>
        <div id="collapseOne" class="panel-collapse collapse">
            <div class="panel-body">
              Craiglist postings about used cars crawler. <br/>
	      <pre class="prettyprint linenums">
		<?php echo file_get_contents('code/crawl_craiglist_cars.py', FILE_USE_INCLUDE_PATH); ?>
	      </pre>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo">
                    crawl_kbb.py
                </a>
            </h4>
        </div>
        <div id="collapseTwo" class="panel-collapse collapse">
            <div class="panel-body">
                Script to crawl KKB.com <br/>
	      <pre class="prettyprint linenums">
		<?php echo file_get_contents('code/crawl_kbb.py', FILE_USE_INCLUDE_PATH); ?>
	      </pre>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordian" href="#collapseThree">
                    distribute_kbb.sh
                </a>
            </h4>
        </div>
        <div id="collapseThree" class="panel-collapse collapse">
            <div class="panel-body">
                Run KBB crawler in a distributed cluster.<br/>
	      <pre class="prettyprint linenums">
		<?php echo file_get_contents('code/distribute_kbb.sh', FILE_USE_INCLUDE_PATH); ?>
	      </pre>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordian" href="#collapseFour">
                    serialize_json.py
                </a>
            </h4>
        </div>
        <div id="collapseFour" class="panel-collapse collapse">
            <div class="panel-body">
                Serializes the json data from both the sources into the requried format.<br/>
	      <pre class="prettyprint linenums">
		<?php echo file_get_contents('code/serialize_json.py', FILE_USE_INCLUDE_PATH); ?>
	      </pre>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordian" href="#collapseSix">
                    combine.py
                </a>
            </h4>
        </div>
        <div id="collapseSix" class="panel-collapse collapse">
            <div class="panel-body">
                combines all the data crawled from kbb.com in a cluster. <br/>
	      <pre class="prettyprint linenums">
		<?php echo file_get_contents('code/combine.py', FILE_USE_INCLUDE_PATH); ?>
	      </pre>
            </div>
        </div>
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
