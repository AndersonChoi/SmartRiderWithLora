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
		Member.findOne({id : req.body.id}, function(err, member){
			if(!member){
				var member = new Member();

				member.id = req.body.id;
				member.password = req.body.password;
				member.end_device_id = req.body.end_device_id;
				member.sns = req.body.sns;
				member.start_latitude = req.body.start_latitude;
				member.start_longitude = req.body.start_longitude;
				member.end_latitude = req.body.end_latitude;
				member.end_longitude = req.body.end_longitude;

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
    });

	// GET SINGLE MEMBER
	app.get('/api/members/:member_id', function(req, res){
		Member.findOne({id : req.params.member_id}).sort({id : -1}).exec(function(err, member){
			if(err) return res.status(500).json({error: err});
			if(!member) return res.status(404).json({error: 'mamber not found'});
			res.json(member);	
		})
	});

	// UPDATE THE MEMBER
	app.post('/api/members/:member_id', function(req, res){
		Member.findOne({id : req.params.member_id}, function(err, member){
			if(err) return res.status(500).json({ error: 'database failure' });
			if(!member) return res.status(404).json({ error: 'member not found' });

			if(req.body.password == member.password){
//				if(req.body.id) book.title = req.body.title;
//				if(req.body.passwd) book.author = req.body.author;
				member.end_device_id = req.body.end_device_id;
				member.sns = req.body.sns;

				member.save(function(err){
					if(err) res.status(500).json({error: 'failed to update'});
					res.json({message: 'member info updated'});
				});
			}else{
				res.json({message: 'password not correct'});
			}
		});	
	});

	// DELETE MEMBER
	app.delete('/api/members', function(req, res){
		Member.findOne({id : req.body.id}, function(err, member){
			if(err) return res.status(500).json({ error: 'database failure' });
			if(!member) return res.status(404).json({ error: 'member not found' });

			if(req.body.password == member.password){
				member.remove(function(err, output){
					if(err) return res.status(500).json({ error: "remove failure" });

					/* ( SINCE DELETE OPERATION IS IDEMPOTENT, NO NEED TO SPECIFY )
						if(!output.result.n) return res.status(404).json({ error: "book not found" });
						res.json({ message: "book deleted" });
					*/

					res.status(204).end();
				});
			}
		});
	});
}
