var express = require('express')
	, http = require('http')
	, app = express()
	, server = http.createServer(app)
	, bodyParser = require('body-parser')
	, mongoose = require('mongoose')
	, appport = process.env.VCAP_APP_PORT || 8888;

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var End_device = require(__dirname + '/models/end_device')
	, router_end_device = require(__dirname + '/routes/end_device')(app,End_device);

var Member = require(__dirname + '/models/member')
	, router_member = require(__dirname + '/routes/member')(app,Member,End_device);

app.get('/', function (req, res) {
	res.send('How to use?<br/><pre>'	
			 +'<ul><li>[POST]\t /api/members\t\t\t\t\t Add member'
			 +'<br/>\t\tbody type:\t\tid<br/>\t\t\t\t\tpasswd<br/>\t\t\t\t\tend_device_id</li>'
			 +'<li>[GET]\t\t /api/members\t\t\t\t\t Lists of members</li>'
			 +'<li>[GET]\t\t /api/members/:member_id\t\t Info of selected member_id</li>'
			 +'<li>[DELETE]\t /api/members\t\t\t\t\t Delete the member</li></ul></pre>');
});

server.listen(appport, function() {
	console.log('Express server listening on port ' + server.address().port);
});

var db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
	console.log("Connected to mongod server");
});

mongoose.connect('mongodb://wonyoung:dnjsdud1@aws-us-east-1-portal.11.dblayer.com:28085,aws-us-east-1-portal.10.dblayer.com:11361/bikeDatabase',{ mongos: true });

//mongodb://wonyoung:dnjsdud1@aws-us-east-1-portal.11.dblayer.com:28085,aws-us-east-1-portal.10.dblayer.com:11361/bikeDatabase
