var sockets = {};

var listen = function(app) {
  var io = require('socket.io').listen(app);

  io.sockets.on('connection', function(socket) {
    sockets[socket.id] = socket;
    
    socket.emit('handshake', {hello: 'there'});

    socket.on('disconnect', function() {
      delete sockets[socket.id];
    });
  });

  return io;
};

var onData = function(data) {
  for (id in sockets) {
    sockets[id].emit('data', data);
  }
};

module.exports = {
  listen: listen
, data: onData
};