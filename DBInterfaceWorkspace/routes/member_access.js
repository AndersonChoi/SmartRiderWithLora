module.exports = function(app,Member){
	// GET ALL MEMBERS
	app.get('/api/members', function(req,res){
		Member.find(function(err, members){
			if(err) return res.status(500).send({error: 'database failure'});
			res.json(members);
		})
	});

	// CREATE MEMBER
	app.post('/api/members', function(req, res){
		if(req.body.id && req.body.password){
			Member.findOne({id : req.body.id}, function(err, members){
				if(!members){
					var member = new Member();
	
					member.id = req.body.id;
					member.password = req.body.password;
					member.sns = req.body.sns;

					member.save(function(err){
						if(err){
							console.error(err);
							res.json({result: 0});
							return;
						}
						res.json({result: 1});
					});
				}else{
					res.json({error:'that Id is exist'});
				}
			});
		}else{
			res.json({error:'not correct form!!'});
		}
	});

	// GET SINGLE MEMBER
	app.get('/api/members/:member_id', function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			if(err) return res.status(500).json({error: err});
			if(!member) return res.status(404).json({error: 'member not found'});
			res.json(member);	
		})
	});

	// UPDATE THE MEMBER SNS OR END_DEVICE_ID
	app.post('/api/members/:member_id/update', function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			if(err) return res.status(500).json({ error: 'database failure' });
			if(!member) return res.status(404).json({ error: 'member not found' });

			if(req.body.sns) member.sns = req.body.sns;
			if(req.body.end_device_id) member.end_device_id = req.body.end_device_id;

			member.save(function(err){
				if(err) res.status(500).json({error: 'failed to update'});
				res.json({message: 'member info updated'});
			});
		});	
	});

	//MEMBER'S EXERCISE START
	app.get('/api/members/:member_id/exercise/activate',function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			member.tracking_count++;
			member.tracking_flag = true;

			member.save(function(err){
				if(err) res.status(500).json({error: 'failed to update'});
				res.json({message: 'member excercise activated'});
			});
		});
	});

	//MEMBER'S EXERCISE START
	app.get('/api/members/:member_id/exercise/deactivate',function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			member.tracking_flag = false;

			member.save(function(err){
				if(err) res.status(500).json({error: 'failed to update'});
				res.json({message: 'member excercise deactivated'});
			});
		});
	});

	// UPDATE THE MEMBER GEOFENCE
	app.post('/api/members/:member_id/geofence', function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			if(err) return res.status(500).json({ error: 'database failure' });
			if(!member) return res.status(404).json({ error: 'member not found' });

			member.start_latitude = req.body.start_latitude;
			member.start_longitude = req.body.start_longitude;
			member.end_latitude = req.body.end_latitude;
			member.end_longitude = req.body.end_longitude;

			member.save(function(err){
				if(err) res.status(500).json({error: 'failed to update'});
				res.json({message: 'geofence info updated'});
			});
		});	
	});

	// UPDATE THE MEMBER TOKEN
	app.post('/api/members/:member_id/token', function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			if(err) return res.status(500).json({ error: 'database failure' });
			if(!member) return res.status(404).json({ error: 'member not found' });

			member.token = req.body.token;

			member.save(function(err){
				if(err) res.status(500).json({error: 'failed to update'});
				res.json({message: 'token data updated'});
			});
		});	
	});

	// DELETE MEMBER
	app.delete('/api/members/:member_id', function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			if(err) return res.status(500).json({ error: 'database failure' });
			if(!member) return res.status(404).json({ error: 'member not found' });

			member.remove(function(err, output){
				if(err) return res.status(500).json({ error: "remove failure" });

				/* ( SINCE DELETE OPERATION IS IDEMPOTENT, NO NEED TO SPECIFY )
					if(!output.result.n) return res.status(404).json({ error: "book not found" });
					res.json({ message: "book deleted" });
				*/

				res.json({result: "jobs done."});
			});
		});
	});
}
