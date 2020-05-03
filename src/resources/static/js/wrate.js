var wrgraph = document.getElementById('wrgraph').getContext('2d');
//var prgraph = document.getElementById('prgraph').getContext('2d');
//var brgraph = document.getElementById('brgraph').getContext('2d');


// $.GET("") {

// };

//const response = JSON.parse(response);



var myChart = new Chart(wrgraph, {
    type: 'line',
    data: {
        labels: ['10.5', '10.6', '10.7', '10.8', '10.9', '10.10'],
        datasets: [{
            label: 'Winrate',
            data: [48, 51.5, 53.1, 51, 49, 49],
            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)'
            ],
            borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'
            ],
            borderWidth: 1
        }]
    },
    options: {
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: false
                }
            }]
        }
    }
});