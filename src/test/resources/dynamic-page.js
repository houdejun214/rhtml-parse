//$(document.body).append("<h1>Header of the highest order</h1>").append("Question: What is the answer?").append("<code>42</code>");
$(document).ready(function() {
    $(document.body)
      .append("<h1>Header of the highest order</h1>")
      .append("Question: What is the answer?")
      .append("<code>42</code>");
  });
var body = $("body");
console.log("The body is: "+ body.html());