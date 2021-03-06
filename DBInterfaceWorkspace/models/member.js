var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var memberSchema = new Schema({
	id: String,
	password: String,
	sns: { type: String, default: "Hello I'm rider!" },
	start_latitude: { type: String, default: "" },
	start_longitude: { type: String, default: "" },
	end_latitude: { type: String, default: "" },
	end_longitude: { type: String, default: "" },
	end_device_id: { type: Number, default: null },
	tracking_count: { type: Number, default: 0 },
	tracking_flag: { type: Boolean, default: false },
	token: { type: String, default: "" }
},{
	versionKey: false
});

module.exports = mongoose.model('member', memberSchema);
