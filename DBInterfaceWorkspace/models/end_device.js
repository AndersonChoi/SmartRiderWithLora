var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var end_deviceSchema = new Schema({
	end_device_id: Number,
	latitude: { type: String, default: "" },
	longitude: { type: String, default: "" },
	acceleration: { type: String, default: "" },
	battery: {type: Number, default: 100},
	speed: { type: String, default: "" },
	tracking_count: { type: Number, default: 0 },
	time_stamp: Date
},{
	versionKey: false
});

module.exports = mongoose.model('end_device', end_deviceSchema);
