<template>
	<view>
		部门管理
		<button @click="locationClick">打开位置信息</button>
		<!-- <map
		  id="map"
		  longitude="116.39742"
		  latitude="39.909"
		  scale="14"
		  layer-style="1"
		  :show-location="showLocation"
		  :makers="covers"
		  style="width: 100%;height: 300px;"
		></map> -->
		<!-- <map
		  longitude="116.39742"
		  latitude="39.909"
		  :makers="covers"
		  style="width: 100%;height: 300px;"
		></map> -->

		<map :latitude="latitude" :longitude="longitude" :markers="covers" style="width: 100%; height: 300px;">
		</map>
		<button @click="getInformation">展示</button>
	</view>
</template>

<script>
	import QQMap from "@/qqmap-wx-jssdk1.2/qqmap-wx-jssdk.js"

	export default {
		data() {
			return {
				//维度
				latitude: 40.040417,
				longitude: 116.273514,

				//QQMap实例
				qqmap: null,

				showLocation: true,

				covers: [{
					id:1,
					latitude: 40.040417,
					longitude: 116.273514,
					iconPath: '/../../../../static/images/location.png'
				}]
			}
		},
		methods: {
			locationClick() {
				console.log(123);

				uni.getLocation({
					type: 'gcj02', //返回可以用于uni.openLocation的经纬度
					success: (res) => {
						console.log(res);
						this.latitude = res.latitude;
						this.longitude = res.longitude;
						uni.openLocation({
							latitude: this.latitude,
							longitude: this.longitude,
							success: function() {
								console.log('success');
							}
						});
					},
					fail: (res) => {
						console.log(res);
					}
				});
			},

			getInformation() {
				console.log(this.qqmap);
				this.qqmap = new QQMap({
					key: 'PMGBZ-SDYKQ-6VS5O-4P3HN-ZVT75-VCFOB'
				})

				this.qqmap.reverseGeocoder({
					location: {
						latitude: this.latitude,
						longitude: this.longitude
					},
					success: (res) => {
						console.log('成功');
						console.log(res);
						console.log(res.result.address);
					},
					fail: (res) => {
						console.log(res);
					}
				})
			}

			// locationClick(){
			// 	uni.getLocation({
			// 		type: 'gcj02', //返回可以用于uni.openLocation的经纬度
			// 		success: function (res) {
			// 			console.log(this);
			// 			const latitude = res.latitude;
			// 			const longitude = res.longitude;
			// 			uni.openLocation({
			// 				latitude: latitude,
			// 				longitude: longitude,
			// 				success: function () {
			// 					console.log('success');
			// 				}
			// 			});
			// 		}
			// 	});
			// }
		}
	}
</script>