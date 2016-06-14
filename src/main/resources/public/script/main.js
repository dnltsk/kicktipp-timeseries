function init(){
  console.log("init()");
  var groupName = d3.select("input").node().value;
  console.log(groupName);
  d3.json("/group?groupName="+groupName, function(group){
    console.log("group", group);
  });
}