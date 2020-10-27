
    function updateClock() {
        let now = new Date();
        let time = now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds();
        // a cleaner way than string concatenation
        let date = [now.getDate(),
            now.getMonth()+1,
            now.getFullYear()].join('.');
        document.getElementById('time').innerHTML = [time, date].join(' | ');
        setTimeout(updateClock, 8000);
    }
    updateClock();
    document.getElementById("infoR")

    function updateForm(){
        let data = document.getElementById("picture").innerText;
        document.getElementById("picture").innerHTML=  data;
    }
    updateForm();

    function updateTable(){
        let data = document.getElementById("table").innerText;
        document.getElementById("table").innerHTML =  data;
    }
    updateTable();
