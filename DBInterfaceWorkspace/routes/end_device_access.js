module.exports = function(app,End_device){
	//ADD END DEVICE
	app.post('/api/end_devices', function(req, res){
		if(!req.body.end_device_id){				//if end_device_id is null
			var end_device = new End_device();		//auto increase
			End_device.findOne().sort({id : -1}).exec(function(err, res){
				if(!res){
					end_device.end_device_id = 1;
				}else{
					end_device.end_device_id = res.end_device_id + 1;
				}

				end_device.latitude = req.body.latitude;
				end_device.longitude = req.body.longitude;
				end_device.acceleration = req.body.acceleration;
				end_device.tracking_count = 0;
				end_device.time_stamp = req.body.time_stamp;

				end_device.save(function(err){
					if(err){
						console.error(err);
						res.json({result: 0});
						return;
					}
					res.json({result: 1});
				});
			});
		}else{										//if end_device_id is not null -> assign
			End_device.findOne({end_device_id : req.body.end_device_id}, function(err, member){
				if(!member){
					var end_device = new End_device();

					end_device.end_device_id = req.body.end_device_id;
					end_device.latitude = req.body.latitude;
					end_device.longitude = req.body.longitude;
					end_device.acceleration = req.body.acceleration;
					end_device.tracking_count = 0;
					end_device.time_stamp = req.body.time_stamp;

					end_device.save(function(err){
						if(err){
							console.error(err);
							res.json({result: 0});
							return;
						}
						res.json({result: 1});
					});
				}else{
					res.json({error:'that id is exist'});
				}
			});
		}
    });

	//ADD END DEVICE
	app.get('/api/end_devices', function(req, res){
		End_device.find(function(err, end_devices){
			if(err) return res.status(500).send({error: 'database failure'});
			res.json(end_devices);
		});
	});
}
