<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />
<style>
body {
	font: Arial 12px;
	text-align: center;
}

.link {
	stroke: #ccc;
}

.node text {
	pointer-events: none;
	font: sans-serif;
}
</style>

<!-- 
	<spring:url value="/css/main.css" var="springCss" />
	<link href="${springCss}" rel="stylesheet" />
	 -->
<c:url value="/css/main.css" var="jstlCss" />
<link href="${jstlCss}" rel="stylesheet" />
<script
	src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/d3/3.4.11/d3.min.js"></script>

<c:url value="/js/cloud.js" var="jstlJs" />
<script src="${jstlJs}"></script>
</head>
<body>
	<h3>Hashtags Analysis</h3>
	<!-- 	<nav class="navbar navbar-inverse"> -->
	<!-- 		<div class="container"> -->
	<!-- 			<div class="navbar-header"> -->
	<!-- 				<a class="navbar-brand" href="#">Spring Boot</a> -->
	<!-- 			</div> -->
	<!-- 			<div id="navbar" class="collapse navbar-collapse"> -->
	<!-- 				<ul class="nav navbar-nav"> -->
	<!-- 					<li class="active"><a href="#">Home</a></li> -->
	<!-- 					<li><a href="#about">About</a></li> -->
	<!-- 				</ul> -->
	<!-- 			</div> -->
	<!-- 		</div> -->
	<!-- 	</nav> -->

	<!-- 	<div class="container"> -->

	<!-- 		<div class="starter-template"> -->
	<!-- 			<h1>Spring Boot Web JSP Example</h1> -->
	<%-- 			<h2>Message: ${message}</h2> --%>
	<!-- 		</div> -->

	<!-- 	</div> -->
	<!-- /.container -->

	<script type="text/javascript"
		src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<div id="chart"></div>
	<script>
		words = ${data};
		
		drawWordCloud(words);
		function drawWordCloud(word_count) {
		/* var text_string = "<c:out value="${message}"/>";
		
		drawWordCloud(text_string);

		function drawWordCloud(text_string) {
			var common = "";
			var word_count = {};
			var words = text_string.split(/[ '\-\(\)\*":;\[\]|{},.!?]+/);
			if (words.length == 1) {
				word_count[words[0]] = 1;
			} else {
				words.forEach(function(word) {
					var word = word.toLowerCase();
					if (word != "" && common.indexOf(word) == -1
							&& word.length > 1) {
						if (word_count[word]) {
							word_count[word]++;
						} else {
							word_count[word] = 1;
						}
					}
				})
			} */
		
		
			var svg_location = "#chart";
			var width = $(document).width();
			var height = $(document).height();

			var fill = d3.scale.category20();

			var word_entries = d3.entries(word_count);

			var xScale = d3.scale.linear().domain(
					[ 0, d3.max(word_entries, function(d) {
						return d.value;
					}) ]).range([ 10, 100 ]);

			d3.layout.cloud().size([ width, height ]).timeInterval(20).words(
					word_entries).fontSize(function(d) {
				return xScale(+d.value);
			}).text(function(d) {
				return d.key;
			}).rotate(function() {
				return ~~(Math.random() * 2) * 90;
			}).font("Impact").on("end", draw).start();

			function draw(words) {
				d3.select(svg_location).append("svg").attr("width", width)
						.attr("height", height).append("g").attr(
								"transform",
								"translate(" + [ width >> 1, height >> 1 ]
										+ ")").selectAll("text").data(words)
						.enter().append("text").style("font-size", function(d) {
							return xScale(d.value) + "px";
						}).style("font-family", "Impact").style("fill",
								function(d, i) {
									return fill(i);
								}).attr("text-anchor", "middle").attr(
								"transform",
								function(d) {
									return "translate(" + [ d.x, d.y ]
											+ ")rotate(" + d.rotate + ")";
								}).text(function(d) {
							return d.key;
						});
			}

			d3.layout.cloud().stop();
		}
	</script>
</body>

</html>
