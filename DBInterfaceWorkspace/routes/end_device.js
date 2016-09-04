module.exports = function(app,End_device){
	//ADD END DEVICE
	app.post('/api/end_devices', function(req, res){
		if(!req.body.end_device_id){
			var end_device = new End_device();
			End_device.findOne().sort({id : -1}).exec(function(err, res){
				if(!res){
					end_device.id = 1;
				}else{
					end_device.id = geofences.id + 1;
				}

				end_device.end_device_id = req.body.end_device_id;
				end_device.latitude = req.body.latitude;
				end_device.longitude = req.body.longitude;
				end_device.acceleration = req.body.acceleration;
				end_device.state = req.body.state;
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
		}else{
			End_device.findOne({end_device_id : req.body.end_device_id}, function(err, member){
				if(!member){
					var end_device = new End_device();

					end_device.end_device_id = req.body.end_device_id;
					end_device.latitude = req.body.latitude;
					end_device.longitude = req.body.longitude;
					end_device.acceleration = req.body.acceleration;
					end_device.state = req.body.state;
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
}
