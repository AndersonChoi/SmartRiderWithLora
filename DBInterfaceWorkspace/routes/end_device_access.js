module.exports = function(app,End_device){
	//ADD END DEVICE
	app.post('/api/end_devices', function(req, res){
		if(!req.body.end_device_id){				//if end_device_id is null
			var end_device = new End_device();		//auto increase
			End_device.findOne().sort({end_device_id : -1}).exec(function(err, num){
				if(!num) end_device.end_device_id = 1;
				else end_device.end_device_id = num.end_device_id + 1;
				console.log(num.end_device_id);
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
				if(!end_devices){			//NEW ADD
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
				}else{						//UPDATE
					var end_device = new End_device();

					end_device.end_device_id = req.body.end_device_id;
					end_device.latitude = req.body.latitude;
					end_device.longitude = req.body.longitude;
					end_device.acceleration = req.body.acceleration;

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
		End_device.find(function(err, end_devices){
			if(err) return res.status(500).send({error: 'database failure'});
			res.json(end_devices);
		});
	});

	//SHOW SELECTED END DEVICE
	app.get('/api/end_devices/:end_device_id', function(req, res){
		End_device.findOne({end_device_id : req.params.end_device_id, latitude : { $ne: "" }, longitude : { $ne: "" }}).sort({time_stamp : -1}).exec(function(err, end_device){
			if(err) return res.status(500).send({error: 'database failure'});
			res.json(end_device);
		});
	});
	
	// DELETE MEMBER
	app.delete('/api/end_devices/:time_stamp', function(req, res){
		End_device.find({time_stamp : { $lt: req.params.timestamp }}, function(err, end_devices){
			if(err) return res.status(500).json({ error: 'database failure' });
			if(!end_devices) return res.status(404).json({ error: 'member not found' });

			end_devices.remove(function(err, output){
				if(err) return res.status(500).json({ error: "remove failure" });

				/* ( SINCE DELETE OPERATION IS IDEMPOTENT, NO NEED TO SPECIFY )
					if(!output.result.n) return res.status(404).json({ error: "book not found" });
					res.json({ message: "book deleted" });
				*/

				res.status(204).end();
			});
		});
	});

}
