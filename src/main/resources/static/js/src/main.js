"use strict";

async function main() {
    let today = new Date();
    document.getElementById("calendarDate").valueAsDate = today;
    document.getElementById("months").value = today.getMonth();

    let isoToday = today.toISOString();
    let date = isoToday.substring(0, isoToday.indexOf("T"));
    await getData(date);

    google.charts.load("current", {
        packages: ["corechart", "line"],
    });
    google.charts.setOnLoadCallback(drawBasic);

    await selectDay();

    await getTotalSumOfEnergy();
    console.log("Init - OK");
}

async function getTotalSumOfEnergy() {
    $.ajax({
        type: "GET",
        url: "/getTotalSumOfEnergy",
        success: function (result) {
            let sumEnergy_Wh = Math.round(result * 100) / 100;
            let totalSumData =
                "Total sum of solar energy " + sumEnergy_Wh + " Wh";
            document.getElementById("totalSum").innerHTML = totalSumData;
        },
        error: function (err) {
            console.error("ERROR: getTotalSumOfEnergy", err);
        },
    });
}

async function selectDay() {
    let selectedDay = document.getElementById("calendarDate").value;
    $.ajax({
        type: "GET",
        url: "/getRecordsByDate/" + selectedDay,
        success: function (result) {
            let jsonArr = JSON.parse(result);
            google.charts.load("current", {
                packages: ["corechart", "bar"],
            });
            google.charts.setOnLoadCallback(() => {
                let timeOffSetHours = 3;
                let dataMap = [];
                for (let i = 0; i < jsonArr.length; i++) {
                    let datetime = new Date(jsonArr[i].datetime);
                    let power =
                        (jsonArr[i].voltage_mV * jsonArr[i].current_mA) /
                        1000.0 /
                        1000.0;
                    dataMap.push([
                        {
                            v: [
                                datetime.getHours() - timeOffSetHours,
                                datetime.getMinutes(),
                                0,
                            ],
                        },
                        power,
                    ]);
                }

                let data = new google.visualization.DataTable();
                data.addColumn("timeofday", "Time of Day");
                data.addColumn("number", "W");
                data.addRows(dataMap);
                let options = {
                    title: "Energy level for a day",
                    hAxis: {
                        title: "Time of Day",
                        format: "hh:mm",
                    },
                    vAxis: {
                        title: "Rating (scale of 1-10)",
                    },
                };

                let materialChart = new google.charts.Bar(
                    document.getElementById("chart_div2"),
                );
                materialChart.draw(data, options);
            });
        },
        error: function (err) {
            console.error("ERROR: selectDay", err);
        },
    });
}

async function getData(date) {
    $.ajax({
        type: "GET",
        url: "/getSumWattByDates/" + date,
        success: function (result) {
            let jsonArr = JSON.parse(result);
            localStorage.setItem("allData", JSON.stringify(jsonArr));
            google.charts.load("current", {
                packages: ["corechart", "line"],
            });
            google.charts.setOnLoadCallback(drawBasic);
        },
        error: function (err) {
            console.error("ERROR: getData", err);
        },
    });
}

function drawBasic() {
    let allData = JSON.parse(localStorage.getItem("allData"));
    let dataMap = {};
    let sumEnergy = 0;
    for (let i = 0; i < allData.length; i++) {
        const date = new Date(allData[i].date);
        let day = date.getDate();
        dataMap[day] = allData[i].watt_hours;
        sumEnergy += allData[i].watt_hours;
    }
    let yield_today_Wh = dataMap[new Date().getDate()];

    document.getElementById("totalToday").innerHTML =
        "Solar energy per day  " + yield_today_Wh + " Wh";

    let dataRows = [];
    let year = new Date().getFullYear();
    let monthNumber = document.getElementById("months").value;
    let selectedDate = new Date(year, monthNumber, 1, 12);
    let mm = String(selectedDate.getMonth() + 1).padStart(2, "0");
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
    data.addColumn("number", "X");
    data.addColumn("number", "Wh");
    data.addRows(dataRows);
    let options = {
        hAxis: {
            title: "Days",
        },
        vAxis: {
            title: "Energy",
        },
        title: "Sum of energy per month: " + sumEnergy + " Wh",
    };
    let chart = new google.visualization.LineChart(
        document.getElementById("chart_div"),
    );
    chart.draw(data, options);
}


function getMonth(selectObject) {
    let monthNumber = selectObject.value;
    let year = new Date().getFullYear();
    let selectedNewDate = new Date(year, monthNumber, 1, 12);
    let isoToday = selectedNewDate.toISOString();
    let newDate = isoToday.substring(0, isoToday.indexOf("T"));
    getData(newDate);
}

function daysInMonth(month, year) {
    return new Date(parseInt(year), parseInt(month), 0).getDate();
}

Date.prototype.addDays = function (days) {
    let date = new Date(this.valueOf());
    date.setDate(date.getDate() + days);
    return date;
};

main();

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
