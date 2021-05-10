// 참고자료 : https://colab.research.google.com/drive/1a2Z0DBGbefkk3os3MbxtBFspOx5idXMV#scrollTo=k4OrsuXwwHzN


const express = require('express');
const mariadb = require('mariadb/callback');
const app = express();

app.listen('8080', () => {
	console.log('Server Started');
});

var dbc = mariadb.createConnection({
    host: "localhost",
    database: "CatDoctor",
    user: "root",
    password: "mariadb"
});

dbc.connect((err) => {
	if (err) throw err;
	console.log('Database Connected');
});


app.get('/disease', (req, res) => {
	var query = `SELECT * FROM disease`;
	dbc.query(query, (err, result, fields)=> {
		if (err) return console.log(err);
		res.send(result);
	});
});

app.get('/symptom', (req, res) => {
	var query = `SELECT * FROM symptom`;
	dbc.query(query, (err, result, fields)=> {
		if (err) return console.log(err);
		res.send(result);
	});
});

app.get('/user', (req, res)=>{
	var query = 'SELECT * FROM user';
	dbc.query(query, (err, result, fields)=>{
		if (err) return console.log(err);
		res.send(result);
	});
});

app.post('/insert', (req, res) => {
	//console.log(req.body.name);
	var query = `INSERT INTO user VALUES (NULL, "${req.body.user_id}", "${req.body.password}", "${req.body.name}", "${req.body.nickname}")`;
	//console.log(query);
	dbc.query(query, (err, result, fields)=> {
		if (err) return console.log(err);

		//res.send(result);
		var str = req.body.name;
		str += hasJongsung(req.body.name)? "을" : "를";
		str += " 추가했습니다.";
		res.send(str)
	});
})

function hasJongsung(str) {
	//var jong = String.fromCharCode(str.charCodeAt(str.length - 1));
	//console.log(jong);
	var jong = str.charCodeAt(str.length - 1);
	return ((jong - 44032 ) % 28 == 0)? false : true;
}