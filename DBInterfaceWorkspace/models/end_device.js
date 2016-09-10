var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var end_deviceSchema = new Schema({
	end_device_id: Number,
	latitude: String,
	longitude: String,
	acceleration: String,
	tracking_count: Number,
	time_stamp: Date
},{
	versionKey: false
});

module.exports = mongoose.model('end_device', end_deviceSchema);
