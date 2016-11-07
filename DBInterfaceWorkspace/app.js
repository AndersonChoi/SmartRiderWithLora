/*eslint-env node*/

//------------------------------------------------------------------------------
// node.js starter application for Bluemix
//------------------------------------------------------------------------------

// This application uses express as its web server
// for more info, see: http://expressjs.com
var express = require('express')
	, http = require('http')
	, appClient = require('ibmiotf')
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
	, Member = require(__dirname + '/models/member')
	, router_end_device = require(__dirname + '/routes/end_device_access')(app,Member,End_device)
	, router_member = require(__dirname + '/routes/member_access')(app,Member);

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

//-------------------------------MQTT IOTF------------------------------------//

var appClientConfig = {
	"org": "50ytef",
	"id": "GW_meshlium_5838",
	"auth-key": "a-50ytef-nsz8mb3ykl",
	"auth-token": "&dCBeGwpm67Ru83l*y"
};

var applicationClient = new appClient.IotfApplication(appClientConfig);

applicationClient.connect();
applicationClient.on("connect", function(){
	console.log("MQTT connected");
	applicationClient.subscribeToDeviceEvents();
});

applicationClient.on("deviceEvent", function(deviceType, deviceId, eventType, format, payload){
	console.log("deviceEvent: " + deviceType + " deviceId: " + deviceId + " eventType: " + eventType + " format: " + format + " payload: " + payload);
	var ts = new Date(JSON.parse(payload).t);
	ts = ts.toISOString();

	var tracking_flag = false;
	var tracking_count = 0;

	var end_device_id = parseInt(deviceId.slice(-2));
	Member.findOne({end_device_id : end_device_id}, function(err, member){
		tracking_flag = member.tracking_flag;
		tracking_count = member.tracking_count;
	});
	
	End_device.findOne({time_stamp: ts}, function(err, end_device){
		if(!end_device) end_device = new End_device();
		
		end_device.end_device_id = end_device_id;
		var str = JSON.parse(payload).d.STR;
		var acc = str.slice(0,5).trim() + ";" + str.slice(5,10).trim() + ";" + str.slice(10,15).trim();
		var battery = parseInt(str.slice(15,18));
		if(tracking_flag) end_device.tracking_count = tracking_count;
		if(parseFloat(str.slice(18,28)) != 0){
			end_device.latitude = str.slice(18,28).trim();
			end_device.longitude = str.slice(28,38).trim();
		}
		var speed = str.slice(38).trim();
		end_device.acceleration = acc;
		end_device.battery = battery;
		end_device.time_stamp = ts;
		end_device.speed = speed;
		
		end_device.save(function(err){
			if(err){
				console.error(err);
				return;
			}
		});
	});
	
});

//-----------------------------------Geofence Timer------------------------------------//

setInterval(function(){
/*
	var stream = Member.find({'start_latitude' : { $ne: "" }, 'start_longitude' : { $ne: "" }},function(err,member){
		console.log(member);	
	}).stream();
*/
	var stream = Member.find({start_latitude : { $ne: "" }, start_longitude : { $ne: "" }}).stream();

	stream.on('data', function (doc) {
		// do something with the mongoose document
		End_device.findOne({end_device_id : doc.end_device_id, latitude : { $ne: "" }, longitude : { $ne: "" }}).sort({time_stamp : -1}).exec(function(err, end_device){
			if(err) return console.log("error occured");

			//if out of geofence
			if(!((doc.end_latitude < end_device.latitude && end_device.latitude < doc.start_latitude) && (doc.start_longitude < end_device.longitude && end_device.longitude < doc.end_longitude))){
				// Set up post data
				var post_data = '{"notification" : { "body" : "out of geofence!!", "title" : "watch your bike!!" }, "data" : { "noti" : "geofence notification" } , ';
				post_data += '"to": "' + doc.token + '" }';

				var post_options = {
					host: 'fcm.googleapis.com',
					path: '/fcm/send',
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Authorization': 'key=AIzaSyBWlsAzGkuSAzACRUVHhxulReWyXvA8Y20'
					}
				};

				// Set up the request
				var post_req = http.request(post_options, function(res) {
					res.setEncoding('utf8');
					res.on('data', function (chunk) {
						console.log('Response: ' + chunk);
					});
				});

				// post the data
				post_req.write(post_data);
				post_req.end();
			}
		});
	}).on('error', function (err) {
		// handle the error
		console.log("error occured in GCM Notitication");
		console.log(err);
	});
}, 5000);
