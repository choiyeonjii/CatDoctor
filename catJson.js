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
	password: "mint8120"
});

dbc.connect((err) => {
	if (err) throw err;
	console.log('Database Connected');
});


app.get('/symptom', (req, res) => {
	var query = `SELECT * FROM symptom`;
	dbc.query(query, (err, result, fields) => {
		if (err) return console.log(err);
		res.send(result);
	});
});

app.get('/symptom_classify', (req, res) => {
	var query = `SELECT * FROM symptom WHERE symptom_classify=${req.query.symptom_classify}`;
	dbc.query(query, (err, result, fields) => {
		if (err) return console.log(err);
		res.send(result);
	});
});

app.get('/symptom_distinct', (req, res) => {
	var query = `SELECT distinct symptom_classify, image FROM symptom`;
	dbc.query(query, (err, result, fields) => {
		if (err) return console.log(err);
		res.send(result);
	});
});

app.get('/disease_id', (req, res) => {
	var query = `SELECT disease.id FROM disease inner join disease_symptom on disease_symptom.symptom_id in ${req.query.symptom_id} and disease_symptom.disease_id=disease.id`;
	dbc.query(query, (err, result, fields)=> {
		if (err) return;
		res.send(result);
	});
});

app.get('/disease', (req, res) => {
	var query = `SELECT * FROM disease where id in ${req.query.id}`;
	dbc.query(query, (err, result, fields)=> {
		if (err) return console.log(err);
		res.send(result);
	});
});


app.use('/image', express.static('images'));
app.use(express.urlencoded());

app.get('/user', (req, res) => {
	var query = 'SELECT * FROM user';
	dbc.query(query, (err, result, fields) => {
		if (err) return console.log(err);
		res.send(result);
	});
});

app.post('/insert', (req, res) => {
	//console.log(req.body.name);
	var query = `INSERT INTO user VALUES (NULL, "${req.body.user_id}", "${req.body.password}", "${req.body.name}", "${req.body.nickname}")`;
	//console.log(query);
	dbc.query(query, (err, result, fields) => {
		if (err) return console.log(err);

		//res.send(result);
		var str = req.body.name;
		str += hasJongsung(req.body.name) ? "을" : "를";
		str += " 추가했습니다.";
		res.send(str)
	});
})
function hasJongsung(str) {
	//var jong = String.fromCharCode(str.charCodeAt(str.length - 1));
	//console.log(jong);
	var jong = str.charCodeAt(str.length - 1);
	return ((jong - 44032) % 28 == 0) ? false : true;
}
