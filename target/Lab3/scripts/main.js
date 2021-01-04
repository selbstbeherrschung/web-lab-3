let rGlobal = 1;

window.alert = function () {

};

function updateClock() {
    let now = new Date();
    let time = now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds();
    // a cleaner way than string concatenation
    let date = [now.getDate(),
        now.getMonth() + 1,
        now.getFullYear()].join('.');
    document.getElementById('time').innerHTML = [time, date].join(' | ');
    setTimeout(updateClock, 8000);
}



function setY(newY) {
    document.getElementById('mainForm:dataY').setAttribute('value', newY);
}

function setR(newR) {
    rGlobal = newR;
}

const clickAnswer = function (event) {

    var box = document.getElementById("image-coordinates").getBoundingClientRect();

    var body = document.body;
    var docEl = document.documentElement;

    // (2)
    var scrollTop = window.pageYOffset || docEl.scrollTop || body.scrollTop;
    var scrollLeft = window.pageXOffset || docEl.scrollLeft || body.scrollLeft;

    // (3)
    var clientTop = docEl.clientTop || body.clientTop || 0;
    var clientLeft = docEl.clientLeft || body.clientLeft || 0;

    // (4)
    var top = box.top + scrollTop - clientTop;
    var left = box.left + scrollLeft - clientLeft;

    var xCord = ((event.clientX - left) / box.width) * 300;
    var yCord = ((event.clientY - top) / box.height) * 300;

    xCord = xCord - 150;
    yCord = yCord - 150;

    var r = parseFloat(rGlobal);

    var x = (xCord / 120) * r;
    var y = -(yCord / 120) * r;

    console.log("Click happened X: " + x + " Y: " + y);

    x = parseFloat(x.toString().slice(0, 5));
    y = parseFloat(y.toString().slice(0, 5));
    r = parseFloat(r.toString().slice(0, 5));

    document.getElementById('textForm:text_input_X').setAttribute('value', x);
    document.getElementById('mainForm:dataY').setAttribute('value', y);
    document.getElementById('mainForm:dataX').setAttribute('value', x);

    document.getElementById('mainForm:sendButton').click();

};

function setX() {
    let x = document.getElementById('textForm:text_input_X').value;
    document.getElementById('mainForm:dataX').setAttribute('value', x);
}

document.addEventListener('DOMContentLoaded', function () {
    updateClock();
    document.getElementById('pictureBox').addEventListener('click', clickAnswer);
    //document.getElementById('image-coordinates').addEventListener('click', clickAnswer);

});