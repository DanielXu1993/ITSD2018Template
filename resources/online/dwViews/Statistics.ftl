<html>

<head>
<!-- Web page title -->
<title>Top Trumps</title>

<!-- Import JQuery, as it provides functions you will probably find useful (see https://jquery.com/) -->
<script src="https://code.jquery.com/jquery-2.1.1.js"></script>
<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.js"></script>
<link rel="stylesheet"
	href="https://code.jquery.com/ui/1.11.1/themes/flick/jquery-ui.css">

<!-- Optional Styling of the Website, for the demo I used Bootstrap (see https://getbootstrap.com/docs/4.0/getting-started/introduction/) -->
<link rel="stylesheet"
	href="http://dcs.gla.ac.uk/~richardm/TREC_IS/bootstrap.min.css">
<script src="http://dcs.gla.ac.uk/~richardm/vex.combined.min.js"></script>
<script>
	vex.defaultOptions.className = 'vex-theme-os';
</script>
<link rel="stylesheet"
	href="http://dcs.gla.ac.uk/~richardm/assets/stylesheets/vex.css" />
<link rel="stylesheet"
	href="http://dcs.gla.ac.uk/~richardm/assets/stylesheets/vex-theme-os.css" />
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">

</head>

<body onload="initalize()">
	<!-- Call the initalize method when the page loads -->

	<div class="container">
		<div style="background-color: black; font-size: 30; color: white;">Top
			Trumps Game</div>
		<div style="height: 20"></div>
		<div class="row">

			<div class="col-sm-12">
				<ul class="list-group">
					<li class="list-group-item" style="background-color: cornsilk;font-size: 25"
						onclick="javascript:window.location.href='/toptrumps/game'">CLICK TO START A New
						Game</li>
					<li class="list-group-item list-group-item-light"><pre
							id="stats" style="font-size: 20"></pre></li>
				</ul>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		function initalize() {
			// First create a CORS request, this is the message we are going to send (a get request in this case)
			var xhr = createCORSRequest('GET',
					"http://localhost:7777/toptrumps/displayStats"); // Request type and URL
			// Message is not sent yet, but we can check that the browser supports CORS
			if (!xhr) {
				alert("CORS not supported");
			}
			// CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
			// to do when the response arrives
			xhr.onload = function(e) {
				var responseText = xhr.response; // the text of the response
				var statsContext = eval(responseText);
				$("#stats").html(statsContext);
			};
			// We have done everything we need to prepare the CORS request, so send it
			xhr.send();
		}

		// This is a reusable method for creating a CORS request. Do not edit this.
		function createCORSRequest(method, url) {
			var xhr = new XMLHttpRequest();
			if ("withCredentials" in xhr) {

				// Check if the XMLHttpRequest object has a "withCredentials" property.
				// "withCredentials" only exists on XMLHTTPRequest2 objects.
				xhr.open(method, url, true);

			} else if (typeof XDomainRequest != "undefined") {

				// Otherwise, check if XDomainRequest.
				// XDomainRequest only exists in IE, and is IE's way of making CORS requests.
				xhr = new XDomainRequest();
				xhr.open(method, url);

			} else {

				// Otherwise, CORS is not supported by the browser.
				xhr = null;

			}
			return xhr;
		}
	</script>
</body>
</html>
