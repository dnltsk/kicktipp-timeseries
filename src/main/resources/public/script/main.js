var svg;
var WIDTH;
var HEIGHT;
var GROUP;
var DEFAULT_GROUP_NAME = "familien-kicker";

function init(){
  svg = d3.select("svg");

  //var groupName = d3.select("input").node().value;
  var groupName = getUrlParameterByName('groupName');
  if(groupName == null || typeof groupName == "undefined"){
    groupName = DEFAULT_GROUP_NAME;
  }
  d3.json("/group?groupName="+groupName, function(group){
    updateWindow();

    group.orderedScores.forEach(function(userScores){
      userScores.unshift(0);
    });
    group.orderedMatches.unshift({
      "title":"start",
      "kickoffTime":"-"
    });

    GROUP = group;
    resetChart();
  });
}

function resetChart(){

  if(typeof GROUP == "undefined"){
    return;
  }

  svg.selectAll("*").remove();

  var margin = {top: 20, right: 150, bottom: 220, left: 50},
      width = WIDTH - margin.left - margin.right,
      height = HEIGHT - margin.top - margin.bottom;

  var x = d3.scale.linear().range([0, width]);
  var y = d3.scale.linear().range([height, 0]);

  x.domain([0, GROUP.orderedMatches.length-1]);
  //x.domain(GROUP.orderedMatches.map(function(match){return match.title;}));
  y.domain([0, d3.max(GROUP.orderedScores, function(scores) { return d3.max(scores, function(point){ return point}); })]);

  var xAxis = d3.svg.axis().scale(x)
      .tickFormat(function(d){
        return GROUP.orderedMatches[d].title;
      })
      .orient("bottom")
      .ticks(GROUP.orderedMatches.length);

  var yAxis = d3.svg.axis().scale(y).orient("left");


  var g = svg
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  g.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis)
      .selectAll("text")
      .attr("y", 0)
      .attr("x", -9)
      .attr("dy", ".35em")
      .attr("transform", "rotate(270)")
      .style("text-anchor", "end");

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

  GROUP.orderedScores.forEach(function (playerTimeseries, i) {
    g.append("path")
        .datum(playerTimeseries)
        .attr("class", "line")
        .attr("d", line)
        .style({
          "stroke": randomColor,
          "stroke-width": 2
        });
  });

  var numberOfMatches = GROUP.orderedMatches.length;
  GROUP.orderedUsernames.forEach(function (username, i){
    g.append("text")
        .attr("x", x(numberOfMatches-1))
        .attr("y", y(GROUP.orderedScores[i][numberOfMatches-1]))
        .attr("dy", ".35em")
        .text(username);
  });
}

function updateWindow(){
  WIDTH = window.innerWidth
  HEIGHT = window.innerHeight;
  //svg.attr("width", WIDTH).attr("height", HEIGHT);
  resetChart();
}
window.onresize = updateWindow;


function getUrlParameterByName(name, url) {
  try {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
  }catch(e){
    console.log("error in getUrlParameterByName()", e);
    return null;
  }
}