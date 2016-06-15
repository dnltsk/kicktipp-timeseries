var svg;
var WIDTH;
var HEIGHT;
var GROUP;

function init(){
  svg = d3.select("svg");

  //var groupName = d3.select("input").node().value;
  var groupName = "dummy"
  d3.json("/group?groupName="+groupName, function(group){
    updateWindow();
    GROUP = group;
    resetChart();
  });
}

function resetChart(){

  if(typeof GROUP == "undefined"){
    return;
  }

  svg.selectAll("*").remove();

  var usernames = GROUP.orderedUsernames;
  var matches = GROUP.orderedMatches;

  var table = [];
  usernames.forEach(function (username, i) {
    var points = [];
    matches.forEach(function(match, j){
      points.push(match.orderedPlayerPoints[i]);
    });
    table.push(points);
  });

  var margin = {top: 20, right: 20, bottom: 30, left: 50},
      width = WIDTH - margin.left - margin.right,
      height = HEIGHT - margin.top - margin.bottom;

  var x = d3.scale.linear().range([0, width]);
  var y = d3.scale.linear().range([height, 0]);

  var xAxis = d3.svg.axis().scale(x).orient("bottom");
  var yAxis = d3.svg.axis().scale(y).orient("left");

  x.domain([0, matches.length-1]);
  y.domain([0, d3.max(matches, function(match) { return d3.max(match.orderedPlayerPoints, function(point){ return point}); })]);

  var g = svg
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  g.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis);

  g.append("g")
      .attr("class", "y axis")
      .call(yAxis)
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Points");

  var line = d3.svg.line()
      .x(function(points, i) { return x(i); })
      .y(function(points) { return y(points); });

  table.forEach(function (playerTimeseries, i) {
    g.append("path")
        .datum(playerTimeseries)
        .attr("class", "line")
        .attr("d", line)
        .style({
          "stroke": randomColor,
          "stroke-width": 2
        });
  });
}

function updateWindow(){
  WIDTH = window.innerWidth
  HEIGHT = window.innerHeight;
  //svg.attr("width", WIDTH).attr("height", HEIGHT);
  resetChart();
}
window.onresize = updateWindow;