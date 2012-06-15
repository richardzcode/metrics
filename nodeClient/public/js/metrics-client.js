(function($) {
  var Tracker = function(aspect, key) {
    this.init(aspect, key);
  };
  
  var _proto = Tracker.prototype;
  
  _proto.init = function(aspect, key) {
    this.aspect = aspect;
    this.key = key;
    
    // 800 * 300; 30px padding for text info.
    this.width = 450;
    this.height = 200;
    this.padding = 30;
    
    this.points = [];
    this.maxPoints = 200;
  };
  
  _proto.getCanvas = function() {
    var id = '#track_' + this.key
      , el = $(id)
      , w = this.width + this.padding * 2
      , h = this.height + this.padding * 2;
    
    var canvas = document.getElementById(id);
    if (!canvas) {
      $('<canvas id="' + id + '"></canvas>')
        .css({width: w + 'px', height: h + 'px'})
        .appendTo($('#trackers'));
      
      canvas = document.getElementById(id);
    }
    
    canvas.width = w;
    canvas.height = h;
    
    return canvas;
  }
  
  _proto.render = function() {
    var canvas = this.getCanvas()
      , context = canvas.getContext('2d');
    
    context.fillStyle = '#333';
    context.strokeStyle = '#333';
    context.lineWidth = 1;
    
    var evaluation = this.evaluate();
    this.drawTitle(context);
    this.drawAxis(context, evaluation);
    
    // Points
    this.drawPoints(context, evaluation);
  };
    
  _proto.drawTitle = function(context) {
    context.font = '14px Verdana';
    context.fillText(this.title(), this.padding, 14);
  };
    
  _proto.drawAxis = function(context, evaluation) {
    context.beginPath();
    context.moveTo(this.padding,              this.padding);
    context.lineTo(this.padding,              this.height + this.padding);
    context.lineTo(this.width + this.padding, this.height + this.padding);
    
    var point = {x: this.padding, y: this.padding};
    context.moveTo(point.x - 3, point.y + 3);
    context.lineTo(point.x,     point.y);
    context.lineTo(point.x + 3, point.y + 3);
    
    point = {x: this.width + this.padding, y: this.height + this.padding};
    context.moveTo(point.x - 3, point.y - 3);
    context.lineTo(point.x,     point.y);
    context.lineTo(point.x - 3, point.y + 3);
    
    context.stroke();
  };
  
  _proto.drawPoints = function(context, evaluation) {
    console.log('Should override');
  };
  
  _proto.drawDots = function(context, dots, style) {
    var dot = dots.shift();
    context.beginPath();
    context.moveTo(dot.x, dot.y);
    while (true) {
      dot = dots.shift();
      if (!dot) {
        break;
      }
      context.lineTo(dot.x, dot.y);
    }
    context.strokeStyle = style || '#333';
    context.stroke();
  };
  
  _proto.addPoint = function(point) {
    this.points.push(point);
    if (this.points.length > this.maxPoints) { this.points.shift(); }
    
    this.render();
  };
  
  _proto.title = function() {
    return this.aspect + ':' + this.key;
  };
  
  _proto.evaluate = function() {
    console.log('Should override');
    return {maxTs: 0, maxVal: 0, minVal: Number.MAX_VALUE};
  };
  
  _proto.test = function() { console.log('test'); };
  
  var Counter = function(aspect, key) {
    this.init(aspect, key);
  };
  var _cproto = Counter.prototype;
  _cproto.__proto__ = Tracker.prototype;
  
  var Gauger = function(aspect, key) {
    this.init(aspect, key);
  };
  var _gproto = Gauger.prototype;
  _gproto.__proto__ = Tracker.prototype;
  
  var Logger = function(aspect, key) {
    this.init(aspect, key);
  };
  var _lproto = Logger.prototype;
  _lproto.__proto__ = Tracker.prototype;
  
  _cproto.evaluate = function() {
    var maxTs = 0
      , maxVal = 0
      , minVal = Number.MAX_VALUE
      , startVal = 0
      , factor = 1;
    
    if (this.points.length == 0) {
      return {maxTs: maxTs, maxVal: maxVal, minVal: minVal, startVal: startVal, factor: factor};
    }
    
    for (var i = 0; i < this.points.length; i ++) {
      var point = this.points[i];
      if (maxTs < point.ts) { maxTs = point.ts; }
      if (maxVal < point.data.count) { maxVal = point.data.count; }
      if (minVal > point.data.count) { minVal = point.data.count; }
    }
    
    // Add some margin
    maxVal = Math.floor(maxVal * 1.05);
    
    factor = Math.ceil(maxVal / this.height);
    if (!factor) { factor = 1; }
    
    if (maxVal - minVal < this.height * factor / 10) {
      startVal = minVal;
    }
    
    return {maxTs: maxTs, maxVal: maxVal, minVal: minVal, startVal: startVal, factor: factor};
  }
  
  _cproto.drawPoints = function(context, evaluation) {
    var step = 4
      , x = this.padding
      , dots = [];
    
    for (var i = 0; i < this.points.length; i ++) {
      var point = this.points[i];
      y = this.height + this.padding - (point.data.count - evaluation.startVal) / evaluation.factor;
      dots.push({x: x, y: y});
      
      x += step;
    }
    
    this.drawDots(context, dots, 'green');
  };
  
  _gproto.evaluate = function() {
    var maxTs = 0
      , maxVal = 0
      , minVal = Number.MAX_VALUE
      , startVal = 0
      , factor = 1;
    
    if (this.points.length == 0) {
      return {maxTs: maxTs, maxVal: maxVal, minVal: minVal, startVal: startVal, factor: factor};
    }
    
    for (var i = 0; i < this.points.length; i ++) {
      var point = this.points[i];
      if (maxTs < point.ts) { maxTs = point.ts; }
      if (maxVal < point.data.max) { maxVal = point.data.max; }
      if (minVal > point.data.min) { minVal = point.data.min; }
    }
    
    // Add some margin
    maxVal = Math.floor(maxVal * 1.1);
    
    factor = Math.ceil(maxVal / this.height);
    if (!factor) { factor = 1; }
    
    if (maxVal - minVal < this.height * factor / 10) {
      startVal = minVal;
    }
    
    return {maxTs: maxTs, maxVal: maxVal, minVal: minVal, startVal: startVal, factor: factor};
  }
  
  _gproto.drawPoints = function(context, evaluation) {
    var step = 4
      , x = this.padding
      , mins = []
      , means = []
      , p90s = []
      , p95s = []
      , p99s = []
      , maxs = [];
    
    var getY = function(val) {
      return this.height + this.padding - (val - evaluation.startVal) / evaluation.factor;
    };
    
    for (var i = 0; i < this.points.length; i ++) {
      var point = this.points[i];
      
      mins.push({x: x, y: getY.call(this, point.data.min)});
      means.push({x: x, y: getY.call(this, point.data.mean)});
      p90s.push({x: x, y: getY.call(this, point.data.percentile90)});
      p95s.push({x: x, y: getY.call(this, point.data.percentile95)});
      p99s.push({x: x, y: getY.call(this, point.data.percentile99)});
      maxs.push({x: x, y: getY.call(this, point.data.max)});
      
      x += step;
    }
    
    this.drawDots(context, mins);
    this.drawDots(context, means, '#999');
    this.drawDots(context, p90s, 'grean');
    this.drawDots(context, p95s, 'blue');
    this.drawDots(context, p99s, 'yellow');
    this.drawDots(context, maxs, 'red');
  };
  
  _lproto.evaluate = function() {
    // Nothing
  };
  
  _lproto.drawAxis = function() {
    // Nothing
  };
  
  _lproto.drawPoints = function(context, evaluation) {
    var x = this.padding
      , y = this.padding
      , lineHeight = 14
      , noSpace = false;
    
    for (var i = this.points.length - 1; i >= 0; i --) {
      var point = this.points[i];
      for (var j = point.data.logs.length - 1; j >= 0; j --) {
        var log = point.data.logs[j];
        context.fillText(log, x, y);
        y += lineHeight;
        if (y > this.height + this.padding) {
          noSpace = true;
          break;
        }
      }
      if (noSpace) {
        break;
      }
    }
  };
  
  var trackers = {};
  
  var onData = function(data) {
    var obj = $.parseJSON(data);
    if (obj == null) {
      return;
    }
    
    var id = obj.aspect + ":" + obj.key
      , tracker = trackers[id];
    if (!tracker) {
      switch(obj.aspect) {
        case 'counter':
          tracker = new Counter(obj.aspect, obj.key);
          break;
        case 'gauger':
          tracker = new Gauger(obj.aspect, obj.key);
          break;
        case 'logger':
          tracker = new Logger(obj.aspect, obj.key);
          break;
      }
    }
    if (tracker) {
      trackers[id] = tracker;  
      
      tracker.addPoint({
        ts: obj.ts
      , data: obj.data
      });
    }
  };
  
  $(document).ready(function() {
    var socket = io.connect('http://localhost:3001');
    
    socket.on('handshake', function(data) {
      $('#status').text('Waiting for metrics data...');
    });
    
    socket.on('data', function(data) {
      onData(data);
    });
  });
  
})(jQuery);