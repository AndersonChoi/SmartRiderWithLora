module.exports = function(app,Member,End_device){
	//ADD END DEVICE
	app.post('/api/end_devices', function(req, res){
		if(!req.body.end_device_id){				//if end_device_id is null
			var end_device = new End_device();		//auto increase
			End_device.findOne().sort({end_device_id : -1}).exec(function(err, num){
				if(!num) end_device.end_device_id = 1;
				else end_device.end_device_id = num.end_device_id + 1;
				//console.log(num.end_device_id);
				end_device.save(function(err){
					if(err){
						console.error(err);
						res.json({result: 0});
						return;
					}
					res.json({result: 1});
				});
			});
		}else{										//if end_device_id is not null
			End_device.findOne({end_device_id : req.body.end_device_id}, function(err, end_devices){
				//console.log(end_devices);
				if(!end_devices){					//NEW ADD
					var end_device = new End_device();

					end_device.end_device_id = req.body.end_device_id;

					end_device.save(function(err){
						if(err){
							console.error(err);
							res.json({result: 0});
							return;
						}
						res.json({result: 1});
					});
				}else{								//UPDATE
					var end_device = new End_device();

					end_device.end_device_id = req.body.end_device_id;
					end_device.latitude = req.body.latitude;
					end_device.longitude = req.body.longitude;
					end_device.acceleration = req.body.acceleration;
//					end_device.tracking_count = Member.findOnd({end_device_id : req.body.end_device_id}).tracking_count;
					
					end_device.save(function(err){
						if(err){
							console.error(err);
							res.json({result: 0});
							return;
						}
						res.json({result: 1});
					});
				}
			});
		}
	});
	
	//SHOW ALL END DEVICE
	app.get('/api/end_devices', function(req, res){
		var stream = Member.find().stream();
		var result =[];

		stream.on('data', function (doc) {
			// do something with the mongoose document
			End_device.findOne({end_device_id : doc.end_device_id, latitude : { $ne: "" }, longitude : { $ne: "" }}).sort({time_stamp : -1}).exec(function(err, end_device){
				if(err) return console.log("error occured");
				if (end_device) result = result.concat(end_device);
			});
		}).on('error', function (err) {
			// handle the error
			console.log(err);
		});

		setTimeout(function(){return res.json(result);},1000);
	});

	//SHOW SELECTED END DEVICE VALID CURRENT INFO
	app.get('/api/end_devices/:end_device_id', function(req, res){
		End_device.findOne({end_device_id : req.params.end_device_id, latitude : { $ne: "" }, longitude : { $ne: "" }}).sort({time_stamp : -1}).exec(function(err, end_device){
			if(err) return res.status(500).send({error: 'database failure'});
			res.json(end_device);
		});
	});
	
	//SHOW SELECTED END DEVICE VALID INFO LIST
	app.get('/api/end_devices/:end_device_id/list', function(req, res){
		End_device.find({end_device_id : req.params.end_device_id, latitude : { $ne: "" }, longitude : { $ne: "" }}).sort({time_stamp : -1}).exec(function(err, end_devices){
			if(err) return res.status(500).send({error: 'database failure'});
			res.json(end_devices);
		});
	});
	
	//SHOW SELECTED END DEVICE VALID INFO LIST THAT SElECTED TRACKING COUNT
	app.get('/api/end_devices/:end_device_id/list/:tracking_count', function(req, res){
		End_device.find({end_device_id : req.params.end_device_id, tracking_count : req.params.tracking_count, latitude : { $ne: "" }, longitude : { $ne: "" }}).sort({time_stamp : -1}).exec(function(err, end_devices){
			if(err) return res.status(500).send({error: 'database failure'});
			res.json(end_devices);
		});
	});
	
	// DELETE END DEVICE BEFORE TIMESTAMP
	app.delete('/api/end_devices/:time_stamp', function(req, res){
		End_device.remove({time_stamp : { $lt: req.params.time_stamp }}, function(err){
			if(err) return res.status(500).json({ error: "remove failure" });

			/*
				( SINCE DELETE OPERATION IS IDEMPOTENT, NO NEED TO SPECIFY )
				if(!output.result.n) return res.status(404).json({ error: "book not found" });
				res.json({ message: "book deleted" });
			*/

			res.status(204).end();
		});
	});
}
