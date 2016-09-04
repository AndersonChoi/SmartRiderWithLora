var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var memberSchema = new Schema({
	id: String,
	password: String,
	sns: String,
	end_device_id: Number,
	start_latitude: String,
	start_longitude: String,
	end_latitude: String,
	end_longitude: String
},{
	versionKey: false
});

module.exports = mongoose.model('member', memberSchema);
