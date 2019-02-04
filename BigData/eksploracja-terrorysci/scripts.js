var histogramData = [];

var n = 1000; // liczba osób
var p = 3/10; // prawdopodobienstwo ze osoba danego dnia bedzie nocowac w hotelu
var h = 100; // liczba hoteli
var d = 100; // liczba dni
var pairs = {};
for(var i = 0; i < d; i++) {
    var map = {};
    for(var j = 0; j < n; j++) {
        if(Math.random() < p) {
            var hotelNr = Math.floor(Math.random()*h);
            if(!map[hotelNr]) {
                map[hotelNr] = [j];
            } else {
                map[hotelNr].push(j);
            }
        }
    }
    for(j = 0; j < h; j++) {
        if(map[j] && map[j].length > 1) {
            for(var a = 0; a < map[j].length; a++) {
                for(var b = a+1; b < map[j].length; b++) {
                    var i1 = map[j][a];
                    var i2 = map[j][b];
                    var key = i1+';'+i2;
                    if(pairs[key]) {
                        pairs[key]++
                    } else {
                        pairs[key] = 1
                    }
                }
            }
        }
    }
    console.log("Calculated day:", i+1)
}

for(key in pairs) {
    if(pairs.hasOwnProperty(key)) {
        var value = pairs[key];
        if(histogramData[value]) {
            histogramData[value]++
        } else {
            histogramData[value] = 1
        }
    }
}

console.log(pairs);

//console.dir(histogramData);
var trace = {
    y: histogramData,
    type: 'bar'
};
var data = [trace];
Plotly.newPlot('myDiv', data);