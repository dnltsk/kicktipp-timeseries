var svg;
var WIDTH;
var HEIGHT;
var GROUP;
var DEFAULT_GROUP_NAME = "familien-kicker";

function init(){
  initModal();
  initTextFocus();

  svg = d3.select("svg");

  var groupName = getUrlParameterByName('groupName');
  if(groupName == null || typeof groupName == "undefined"){
    groupName = DEFAULT_GROUP_NAME;
  }
  d3.json("/group?groupName="+groupName, function(error, group){

    if(error){
      d3.select("#myModal").style("display","block")
      return;
    }

    updateWindow();

    group.orderedScores.forEach(function(userScores){
      userScores.unshift(0);
    });
    group.orderedMatches.unshift({
      "title":"start",
      "kickoffTime":"-"
    });

    group.orderedUsernames.forEach(function (username, i) {
      var classname = usernameToClassname(username);
      var userColor = randomColor();
      createClass("path." + classname, "stroke: "+userColor+";");
      createClass("text." + classname, "fill: "+userColor+";");
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
      .style("text-anchor", "end")
      .classed("bonus", function(d, i){
        return GROUP.orderedMatches[i].title.indexOf("Bonus") > -1;
      });

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


  var linesGroup = g.append("g")
                   .classed("lines", true);

  GROUP.orderedScores.forEach(function (playerTimeseries, i) {
    var classname = usernameToClassname(GROUP.orderedUsernames[i]);
    linesGroup.append("path")
        .datum(playerTimeseries)
        .attr("d", line)
        .classed(classname, true)
        .on("mouseover", function(d, j){
          //unhighlight all
          d3.select("g.members text.highlight").classed("highlight", false);
          d3.select("g.lines path.highlight").classed("highlight", false);

          //highlight user
          var usernameToHighlightClassname = d3.select(this).attr("class");
          d3.select("g.lines path."+usernameToHighlightClassname)
              .classed("highlight", true)
              .moveToBack();
          d3.select("g.members text."+usernameToHighlightClassname)
              .classed("highlight", true)
              .moveToFront();
        });;
  });

  var membersGroup = g.append("g")
      .classed("members", true);

  var numberOfMatches = GROUP.orderedMatches.length;
  GROUP.orderedUsernames.forEach(function (username, i){
    var classname = usernameToClassname(username);
    membersGroup.append("text")
        .attr("x", x(numberOfMatches-1))
        .attr("y", y(GROUP.orderedScores[i][numberOfMatches-1]))
        .attr("dy", ".35em")
        .classed(classname, true)
        .text(username)
        .on("mouseover", function(d, j){
          //unhighlight all
          d3.select("g.members text.highlight").classed("highlight", false);
          d3.select("g.lines path.highlight").classed("highlight", false);

          //highlight user
          var usernameToHighlightClassname = d3.select(this).attr("class");
          d3.select("g.lines path."+usernameToHighlightClassname)
              .classed("highlight", true)
              .moveToBack();
          d3.select("g.members text."+usernameToHighlightClassname)
              .classed("highlight", true)
              .moveToFront();
        });
  });
}

function usernameToClassname(username){
  return username.replace(/\s/gi, "-");
}

function createClass(name,rules){
  var style = document.createElement('style');
  style.type = 'text/css';
  document.getElementsByTagName('head')[0].appendChild(style);
  if(!(style.sheet||{}).insertRule)
    (style.styleSheet || style.sheet).addRule(name, rules);
  else
    style.sheet.insertRule(name+"{"+rules+"}",0);
}

d3.selection.prototype.moveToFront = function() {
  return this.each(function(){
    this.parentNode.appendChild(this);
  });
};

d3.selection.prototype.moveToBack = function() {
  return this.each(function(){
    this.parentNode.insertBefore(this, this.parentNode.firstChild);
    //this.parentNode.appendChild(this);
  });
};

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
    return null;
  }
}

function initModal(){
  var modal = document.getElementById('myModal');
  var span = document.getElementsByClassName("close")[0];
  span.onclick = function () {
    modal.style.display = "none";
  };
  window.onclick = function (event) {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  };
}

function initTextFocus() {
  var defaultGrouName = "familien-kicker";
  var inputNode = d3.select(".desc input").node();
  var groupName = getUrlParameterByName("groupName");
  if(groupName == null){
    inputNode.value = defaultGrouName;
  }else{
    inputNode.value = groupName;
  }
  inputNode.focus();
}