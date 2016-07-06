var svg;
var WIDTH;
var HEIGHT;
var GROUP;
var DEFAULT_GROUP_NAME = "familien-kicker";

function init(){
  initModal();
  initTextFocus();

  svg = d3.select("svg")
      .attr("width", "100%")
      .attr("height", "100%")
      .call(d3.behavior.zoom().on("zoom", function () {
        svg.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")")
      }))
      .append("g");

  var groupName = getUrlParameterByName('groupName');
  if(groupName == null || typeof groupName == "undefined"){
    groupName = DEFAULT_GROUP_NAME;
  }
  d3.json("/group?groupName="+groupName, function(error, group){
    hideWaiting();
    if(error){
      var errorGroupName = JSON.parse(error.response).groupName;
      if(error.status == 404){
        d3.select("#errorTitle").text("Oups - unable to find your prediction group.");
        d3.select("#errorMessage").html("Please check<br>"+
                                        "<a href='http://www.kicktipp.com/"+errorGroupName +"/' target='_blank'>http://www.kicktipp.com/"+errorGroupName +"/</a>");
      }else{
        d3.select("#errorTitle").text("Oups - something went wrong.");
        d3.select("#errorMessage").html("Please check<br>"+
                                        "<a href='http://www.kicktipp.com/"+errorGroupName +"/'>http://www.kicktipp.com/"+errorGroupName +"/</a><br>"+
                                        "You can try again later or change your input.");
      }
      d3.select("#modalErrorDialog").style("display","block");
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

  var marginTop = 20;
  var marginLeft = 50;
  var marginRight = calcMaxMemberNameWidth()+20;
  var width = WIDTH - marginLeft - marginRight;

  var contentWidth = width + marginLeft + marginRight;
  var matchTitleFontSize = contentWidth / GROUP.orderedMatches.length;

  var marginBottom = calcMaxMatchTitleHeight(matchTitleFontSize)+30;

  var margin = {top : marginTop, right: marginRight, bottom: marginBottom, left: marginLeft};

  var height = HEIGHT - margin.top - margin.bottom;

  var x = d3.scale.linear().range([0, width]);
  var y = d3.scale.linear().range([height, 0]);

  var maxScore = d3.max(GROUP.orderedScores, function(scores) { return d3.max(scores, function(point){ return point}); });
  x.domain([0, GROUP.orderedMatches.length-1]);
  y.domain([0, maxScore]);

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
      .style({
        "text-anchor": "end",
        "font-size": matchTitleFontSize+"px"
      })
      .classed("bonus", function(d, i){
        return GROUP.orderedMatches[i].title.indexOf("Bonus") > -1;
      });

  var line = d3.svg.line()
      .x(function(points, i) { return x(i); })
      .y(function(points) { return y(points); });

  g.append("g")
      .attr("class", "y axis")
      .call(yAxis)
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Points")
      .on("click", function(e){
        return;
        console.log("update!");
        svg.selectAll("g.lines path")
            .data(GROUP.orderedPositions)
            .enter()
            .append("path")
            .attr("d", line)
            .transition();
      });

  /* VERTICAL HELP LINES */
  var verticalHelpLinesGroup = g.append("g")
      .classed("help-lines", true);
  GROUP.orderedMatches.forEach(function (match, i) {
    if(match.title == "Day Bonus") {
      verticalHelpLinesGroup.append("line")
          .attr("x1", x(i))
          .attr("y1", y(0))
          .attr("x2", x(i))
          .attr("y2", y(maxScore));
    }
  });
  /* LINES */
  var linesGroup = g.append("g").classed("lines", true)
      .selectAll("path")
      .data(GROUP.orderedScores, function(d, i){console.log(i, d); return d;})
      .enter()
      .append("path")
      .attr("d", line)
      .attr("stroke-linejoin", "round")
      .attr("class", function(d, i){return usernameToClassname(GROUP.orderedUsernames[i]);})
      .on("mouseover", function(d, j){
        //unhighlight all
        d3.select("g.members text.highlight").classed("highlight", false);
        d3.select("g.lines path.highlight").classed("highlight", false);

        //highlight user
        var usernameToHighlightClassname = d3.select(this).attr("class");
        d3.select("g.lines path." + usernameToHighlightClassname)
            .classed("highlight", true)
            .moveToFront();
        d3.select("g.members text."+usernameToHighlightClassname)
            .classed("highlight", true)
            .moveToFront();
      });


  /* MEMBERS */
  var numberOfMatches = GROUP.orderedMatches.length;
  var membersGroup = g.append("g").classed("members", true)
      .selectAll("text")
      .data(GROUP.orderedUsernames, function(d, i){console.log(i, d); return d;})
      .enter()
      .append("text")
      .attr("x", x(numberOfMatches-1))
      .attr("y", function(d, i){return y(GROUP.orderedScores[i][numberOfMatches-1]);})
      .attr("dy", ".35em")
      .attr("dx", ".25em")
      .attr("class", function(d){return usernameToClassname(d);})
      .text(function(d){return d;})
      .on("mouseover", function(d, j){
        //unhighlight all
        d3.select("g.members text.highlight").classed("highlight", false);
        d3.select("g.lines path.highlight").classed("highlight", false);

        //highlight user
        var usernameToHighlightClassname = d3.select(this).attr("class");
        d3.select("g.lines path."+usernameToHighlightClassname)
            .classed("highlight", true)
            .moveToFront();
        d3.select("g.members text."+usernameToHighlightClassname)
            .classed("highlight", true)
            .moveToFront();
      });

}

function usernameToClassname(username){
  /* replace whitespaces and "." with _ */
  var whitespaceFree = username.replace(/[\s\.]/gi, "_");
  /* add _ if starts with number */
  if (!isNaN(parseInt(whitespaceFree))) {
    return "_"+whitespaceFree;
  }
  return whitespaceFree;
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
  });
};

function updateWindow(){
  WIDTH = window.innerWidth
  HEIGHT = window.innerHeight;
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
  var modal = document.getElementById('modalErrorDialog');
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

function calcMaxMemberNameWidth(){
  var tempGroup = svg.append("g");
  GROUP.orderedUsernames.forEach(function (name) {
    tempGroup.append("text")
        .attr("x", 0)
        .attr("y", 0)
        .style("font-weight","bold")
        .text(name);
  });
  var bbox = tempGroup.node().getBBox();
  tempGroup.remove();
  return bbox.width;
}

function calcMaxMatchTitleHeight(matchTitleFontSize){
  var tempGroup = svg.append("g");
  GROUP.orderedMatches.forEach(function (match) {
    tempGroup.append("text")
        .attr("x", 0)
        .attr("y", 0)
        .style("font-size", matchTitleFontSize+"px")
        .text(match.title);
  });
  var bbox = tempGroup.node().getBBox();
  tempGroup.remove();
  return bbox.width;
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

function hideWaiting(){
  d3.select("div#waiting").style("display", "none");
}