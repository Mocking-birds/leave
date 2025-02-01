export function getWeather(query){
	return uni.request({
		url: 'https://devapi.qweather.com/v7/weather/now',
		method: 'GET',
		data: {
			key: '55ee64b0e3a845f787fc11c0bdeb27ab',
			location: '106.67,26.40'
		}
	})
}