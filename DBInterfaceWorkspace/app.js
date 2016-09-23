/*eslint-env node*/

//------------------------------------------------------------------------------
// node.js starter application for Bluemix
//------------------------------------------------------------------------------

// This application uses express as its web server
// for more info, see: http://expressjs.com
var express = require('express')
	, bodyParser = require('body-parser')
	, mongoose = require('mongoose');

// cfenv provides access to your Cloud Foundry environment
// for more info, see: https://www.npmjs.com/package/cfenv
var cfenv = require('cfenv');

// create a new express server
var app = express();

// serve the files out of ./public as our main files
//app.use(express.static(__dirname + '/public'));

// get the app environment from Cloud Foundry
var appEnv = cfenv.getAppEnv();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var End_device = require(__dirname + '/models/end_device')
, router_end_device = require(__dirname + '/routes/end_device_access')(app,End_device);

var Member = require(__dirname + '/models/member')
, router_member = require(__dirname + '/routes/member_access')(app,Member,End_device);

app.get('/', function (req, res) {
res.send('How to use? <a href="https://github.com/AndersonChoi/SmartRiderWithLora/wiki/SR-DB-Interface" target="_blank">Here</a>');
});

// start server on the specified port and binding host
app.listen(appEnv.port, '0.0.0.0', function() {
  // print a message when the server starts listening
  console.log("server starting on " + appEnv.url);
});

var db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
	console.log("Connected to mongod server");
});

mongoose.connect('mongodb://wonyoung:dnjsdud1@aws-us-east-1-portal.11.dblayer.com:28085,aws-us-east-1-portal.10.dblayer.com:11361/bikeDatabase',{ mongos: true });

//mongodb://wonyoung:dnjsdud1@aws-us-east-1-portal.11.dblayer.com:28085,aws-us-east-1-portal.10.dblayer.com:11361/bikeDatabase
