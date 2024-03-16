const months = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December'
];

    //self executing function here
    (function() {
//        console.log("js ready!");
//        checkForNotSyncData();

        let today = new Date();
        document.getElementById('calendarDate').valueAsDate = today;
        document.getElementById('months').value = today.getMonth();

        let isoToday = today.toISOString();
        let date = isoToday.substring(0, isoToday.indexOf("T"));
        getData(date);

        google.charts.load('current', {
            packages: ['corechart', 'line']
        });
        google.charts.setOnLoadCallback(drawBasic);

        selectDay();

        getTotalSumOfEnergy();
    })();

    function getTotalSumOfEnergy() {
        $.ajax({
            type: "GET",
            url: "/getTotalSumOfEnergy",
            success: function(result) {
                console.log("getTotalSumOfEnergy ", result);
                let sumEnergy_Wh = Math.round(((result)) * 100) / 100;
                document.getElementById("totalSum").innerHTML = "Total sum of solar energy " + sumEnergy_Wh + " Wh";
            },
            error: function(e) {
                console.err("getTotalSumOfEnergy err: ", e);
            }
        });
    }

    function getMonth(selectObject) {
        let monthNumber = selectObject.value;
        console.log(monthNumber);
        let year = new Date().getFullYear();
        let selectedNewDate = new Date(year, monthNumber, 1, 12);
        console.log(selectedNewDate);

        let isoToday = selectedNewDate.toISOString();
        let newDate = isoToday.substring(0, isoToday.indexOf("T"));
        console.log(newDate);
        getData(newDate);
    }

    function selectDay() {
        let selectedDay = document.getElementById("calendarDate").value;
        console.log("selectedDay " + selectedDay);
        $.ajax({
            type: "POST",
            url: "/getRecordsByDate",
            data: {
                selectedDate: selectedDay
            },
            success: function(result) {
                //console.log("result " + result);
                let jsonArr = JSON.parse(result);
                //console.log("selectDay resp " + jsonArr);
                google.charts.load('current', {
                    packages: ['corechart', 'bar']
                });
                google.charts.setOnLoadCallback(() => {
                    console.log(jsonArr);
                    let timeMap = new Map();
                    let divMap = new Map();
                    for (let i = 0; i < jsonArr.length; i++) {
                        let datetime = new Date(jsonArr[i].datetime);
                        const hour = datetime.getHours();
                        //console.log("datetime " + datetime);
                        //console.log("getHours() " + datetime.getHours());

                        // let power = (jsonArr[i].voltage_mV / 1000.0 * jsonArr[i].current_mA / 1000.0);
                        let power = jsonArr[i].power_W ;
                        if (!timeMap.has(hour)) {
                            timeMap.set(hour, 0);
                            divMap.set(hour, 0);
                        }
                        let sumOfPower = timeMap.get(hour);
                        sumOfPower += power;
                        timeMap.set(hour, sumOfPower);
                        divMap.set(hour, divMap.get(hour) + 1);
                    }
                    //console.log("timeMap " + JSON.stringify(timeMap));

                    let dataMap = [];
                    for (const key of timeMap.keys()) {
                        // console.log("key " + key + " " +  divMap.get(key) + " " + timeMap.get(key));
                        dataMap.push(
                            [
                                { v: [key -3 , 0, 0] }, 
                                timeMap.get(key) / divMap.get(key)
                            ]
                        );
                    }

                //    for (let i = 0; i < jsonArr.length; i++) {
                    //    let datetime = new Date(jsonArr[i].datetime);
                    //    console.log("datetime " + datetime);
                    //    let power = (jsonArr[i].voltage_mV / 1000.0 * jsonArr[i].current_mA / 1000.0) ;
                    //     let power = jsonArr[i].power_W ;

                    //    dataMap.push([{
                    //        v: [datetime.getHours(), datetime.getMinutes(), 0]
                    //    }, power]);
                //    }
                    //console.log("dataMap " + JSON.stringify(dataMap));

                    let data = new google.visualization.DataTable();
                    data.addColumn('timeofday', 'Time of Day');
                    data.addColumn('number', 'W');
                    data.addRows(dataMap);
                    let options = {
                        title: 'Energy level for a day',
                        hAxis: {
                            title: 'Time of Day',
                            format: 'hh:mm',
                        },
                        vAxis: {
                            title: 'Rating (scale of 1-10)'
                        }
                    };

                    let materialChart = new google.charts.Bar(document.getElementById('chart_div2'));
                    materialChart.draw(data, options);
                });
            },
            error: function(e) {
                console.err("getData err: ", e);
            }
        });

    }

    function getData(date) {
        $.ajax({
            type: "POST",
            url: "/getSumWattByDates",
            data: {
                selectedDate: date
            },
            success: function(result) {
                let jsonArr = JSON.parse(result);
                localStorage.setItem("allData", JSON.stringify(jsonArr));
                google.charts.load('current', {
                    packages: ['corechart', 'line']
                });
                google.charts.setOnLoadCallback(drawBasic);
            },
            error: function(e) {
                console.err("getData err: ", e);
            }
        });
    }

    function drawBasic() {
        let allData = JSON.parse(localStorage.getItem("allData"));
        console.log(allData);
        let dataMap = {};
        let sumEnergy = 0;
        for (let i = 0; i < allData.length; i++) {
            const date = new Date(allData[i].date);
            dataMap[date.getDate()] = allData[i].watt_hours;
            sumEnergy += allData[i].watt_hours;
        }
        // sumEnergy = Math.round(((sumEnergy / 1000 ) + Number.EPSILON) * 100) / 100;
        let dataRows = [];

        let year = new Date().getFullYear();
        let monthNumber = document.getElementById("months").value;
        let selectedDate = new Date(year, monthNumber, 1, 12);
//        let selectedDate = new Date(document.getElementById("calendarDate").value);
        console.log("selectedDate >>> " + selectedDate);
        let mm = String(selectedDate.getMonth() + 1).padStart(2, '0');
        let yyyy = selectedDate.getFullYear();
        let days = daysInMonth(mm, yyyy);
        for (let i = 0; i < days; i++) {
            let row = [];
            row[0] = i + 1;
            row[1] = 0;
            if (dataMap.hasOwnProperty(i + 1)) {
                row[1] = dataMap[i + 1];
            }
            dataRows[i] = row;
        }

        let data = new google.visualization.DataTable();
        data.addColumn('number', 'X');
        data.addColumn('number', 'Wh');
        data.addRows(dataRows);
        let options = {
            hAxis: {
                title: 'Days'
            },
            vAxis: {
                title: 'Energy'
            },
            title: "Sum of energy per month: " + sumEnergy + " Wh"
        };
        let chart = new google.visualization.LineChart(document.getElementById('chart_div'));
        chart.draw(data, options);
    }

    function daysInMonth(month, year) {
        return new Date(parseInt(year), parseInt(month), 0).getDate();
    }

    Date.prototype.addDays = function(days) {
        let date = new Date(this.valueOf());
        date.setDate(date.getDate() + days);
        return date;
    }

    /*
    function syncSf() {
        $.ajax({
            type: "GET",
            url: "/syncSfData",
            success: function(result) {
                console.log("syncSf ", result);
            },
            error: function(e) {
                console.err("getData err: ", e);
            }
        });
    }

    function checkForNotSyncData() {
        $.ajax({
            type: "GET",
            url: "/checkUnsyncData",
            success: function(result) {
                console.log("checkForNotSyncData ", result.valueOf());
                $("#sync_button").prop("disabled",result.valueOf());
            },
            error: function(e) {
                console.err("getData err: ", e);
            }
        });
    }
*/
