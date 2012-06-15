global._ = require('underscore');

var http = require('http')
  , url = require('url')
  , util = require('util')
  , fs = require('fs');

var app = http.createServer(function(req, res) {
  var uri = url.parse(req.url).pathname;
  
  if('/metrics' == uri) {
    metrics(req, res);
  } else if (/\.html$/.test(uri)) {
    staticFiles(req, res, uri, 'text/html');
  } else if (/\/js\/.*/.test(uri)) {
    staticFiles(req, res, uri, 'text/javascript');
  } else if (/\/css\/.*/.test(uri)) {
    staticFiles(req, res, uri, 'text/css');
  } else if (/\/images\/.*/.test(uri)) {
    var match = uri.match(/\.(.*)$/);
	var ext = match? match[1] : '*';
    staticFiles(req, res, uri, 'image/' + ext);
  } else {
    res.end('Hi');
  }
}).listen(3001, 'localhost');

var socket = require('./socket.js')
  , io = socket.listen(app);

console.log("Metrics client started on 3001");

var staticFiles = function(req, res, uri, content_type) {
  var onStat = function(err, stat) {
    if (err) {
      res.writeHead(404, {'Content-Type': 'text/html'});
      res.end('Not Found');
    } else {
      res.writeHead(200, {
        'Content-Type': content_type? content_type : 'text/html'
        , 'Content-Length': stat.size
      });
      fs.createReadStream(path).pipe(res);
    }
  };

  var path = __dirname + '/public' + uri;
  fs.stat(path, onStat);
};

var metrics = function(req, res) {
  if (req.method != 'POST') {
    res.end('What\'s up');
  }
  
  var data = '';
  req.on('data', function(chunk) {
    data += chunk;
  });
  
  req.on('end', function() {
    socket.data(data);
    res.end('Got it');
  });
};