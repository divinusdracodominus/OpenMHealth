// index.js

var maxValue = Math.max(...chartData);
var chartDataSum = 0;
chartData.map(chartDataElement => chartDataSum += chartDataElement);

// maxValue = Math.max(...chartData) + 1
// maxValue = 10;
// console.log(maxValue);
  
document.getElementById('idQuantity').innerHTML = (chartData[chartData.length - 1]).toFixed(2);
document.getElementById('idQuality').innerHTML = sleepQuality;
document.getElementById('idWeekAverageHours').innerHTML = ( chartDataSum / chartData.length ).toFixed(2);

if ( sleepQuality == "Bad") {
  document.getElementById('idScore').innerHTML = 50;
} else if ( sleepQuality == "Good") {
  document.getElementById('idScore').innerHTML = 75;
} else if ( sleepQuality == "Deep") {
  document.getElementById('idScore').innerHTML = 90;
} else {
  document.getElementById('idScore').innerHTML = "Not provided";
}

const ctx = document.getElementById('myChart').getContext('2d');
const chart = new Chart(ctx, {
  type: 'line',
  data: {
    labels: chartLabels, // X-axis labels
    datasets: [{
      data: chartData, // Y-axis data
      borderColor: '#ffffff6f', // Set the line color to orange
      borderWidth: 1.5,
      backgroundColor: '#ffffff2c', // Set the background color to gray
      fill: true, // Fill the area below the line
      lineTension: 0.3
    }]
  },
  options: {
    interaction: {
      intersect: false,
      mode: 'index',
    },
    responsive: true,
    legend: {
      display: false // Hide the legend
    },
    tooltips: {
      enabled: true, // Enable hover visibility
      callbacks: {
        label: function(tooltipItem, data) {
          // Return the label for the data point
          return data.labels[tooltipItem.index];
        }
      }
    },
    scales: {
      xAxes: [{
        display: true, // Hide the x-axis
        gridLines: {
          display: false // Hide the vertical grid lines
        }        
      }],

      yAxes: [{
        display: true, // Hide the y-axis
        position: 'left',
        gridLines: {
          display:false
        },
        ticks: {
          min: 0, // Set the minimum value to 0
          max: maxValue,
          // stepSize: 1
        }
      }]
    },
    annotation: {
      annotations: [{
        type: 'line',
        mode: 'vertical',
        scaleID: 'x-axis-0',
        value: 'April',
        borderColor: 'rgba(0, 0, 0, 0.5)',
        borderWidth: 1,
        label: {
          enabled: false // set to false to make it always visible
        }
      }]
    },
      
    backgroundColor: 'tomato' // Set the background color to 'tomato'

  }
});
  
// Add a gradient below the line
var gradient = ctx.createLinearGradient(0, 0, 0, chart.height);
gradient.addColorStop(0, 'rgba(255, 255, 255, 0.173)');
gradient.addColorStop(1, 'rgba(255, 255, 255, 0.100)');
chart.data.datasets[0].backgroundColor = gradient;
chart.update();
