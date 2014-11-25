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
    include "mine_gold.php";
    
    #$data = file_get_contents('test_tuples.json');
    $data = sample(1);
    $json_data = json_decode($data,true);
    #print_r($json_data);

    #just take the first row for now!
    $blocking_id = $json_data[0]['blockingid'];
    $src1 = $json_data[0]['T1'];
    $src2 = $json_data[0]['T2'];

?>
<script>

function send_result(aChoice){
    console.log("SRG the best! "+ aChoice);

    $.ajax({
      type: 'POST',
	  url: "http://seclab6.cs.wisc.edu:5000/golden/mine_gold.php",
	  crossDomain: true,
	  data: { "blockingid": <?php echo $blocking_id?>, "choice": aChoice},
	  //dataType: 'json',
	  success: function(responseData, textStatus, jqXHR) {
	  alert("posted" + responseData + " ... reload!");
	  var div = document.getElementById('srg');
	  div.style.display = 'block';
	  location.reload();
	},
	  error: function (responseData, textStatus, errorThrown) {
	  alert('POST failed.');
	}
      });
};

</script>

<div class="row">
    <div class="col-md-12">
        <center>
        <button onclick="send_result(1)" type="button" class="btn btn-success">Is A Match</button>
        &nbsp;
        <button onclick="send_result(0)" type="button" class="btn btn-danger">Not A Match</button>
        &nbsp;
        <button onclick="send_result(-1)" type="button" class="btn btn-warning">Not Relevant</button>
        <br>
        <div id="srg" class="panel panel-default" style="display:none">
          <div class="panel-body">
              Loading new data ...
          </div>
        </div>
        </center>
    </div>
</div>
<br>
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
            <?php
                foreach($src1 as $key => $value){
                    echo "<tr>";
                    echo "<td><i>" . $key . '</i></td><td>' . $value . '</td>';
                    echo "</tr>";
                }
            ?>  
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
            <?php
                foreach($src2 as $key => $value){
                    echo "<tr>"; 
                    echo "<td><i>" . $key . '</i></td><td>' . $value . '</td>';
                    echo "</tr>"; 
                }
            ?>
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
