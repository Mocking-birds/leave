<template>
	<view class="content" @touchstart="calendarStart" @touchmove="calendarMove" @touchend="calendarEnd">
			<uni-calendar :insert="true" :lunar="true" ref="calendar"
				:selected="selected" @change="calendarChange"
				style="width: 100%;height: 440px; transition: all .28s ease;" />
			<view class="calendar-other" :style="{ transform: `translateY(${calendarStyle}px)` }">
				<view class="calendar-icon">
					<uni-icons :type="calendarIcon" size="25" style="transition: all .28s ease;"></uni-icons>
				</view>
				<scroll-view class="other" :scroll-y="true" scroll-top="0" enable-flex="true"
					style="min-height: 200px; max-height: 600px;" @scroll="otherScroll">
					<swiper class="swiper" circular :autoplay="true" :interval="2000" :indicator-dots='true'
						indicator-color='white' :duration="500">
						<swiper-item v-for="(item,index) in swiperList" :key="index">
							<image :src="getAvatar(item.imageUrl)" mode="" style="width: 100%;height:100%"></image>
						</swiper-item>
					</swiper>
				
					<uni-notice-bar :scrollable="true" :single="true" :showIcon="true" :speed="100" :text="text"
						width="100%"></uni-notice-bar>
				
					<view class="weather" v-if="weatherInfo">
						<view class="weather-content">
							<view class="top">
								<image :src="getWeatherIcon()" mode=""></image>
								<view class="wea-text-tem">
									<view class="wea-text">
										{{ weatherInfo.text }}
									</view>
									<view class="wea-tem">
										{{ weatherInfo.temp }}℃
									</view>
								</view>
							</view>
							<view class="bottom">
								<view class="wind-dir">
									<uni-icons type="paperplane" size="20"></uni-icons>
									<view class="">
										{{ weatherInfo.windDir }}
									</view>
								</view>
								<view class="province">
									<uni-icons type="location" size="20"></uni-icons>
									<view class="">
										{{ locationName }}
									</view>
								</view>
							</view>
						</view>
					</view>
					<view class="test">
						
					</view>
				</scroll-view>
		</view>
	</view>
</template>

<script>
	import {
		getNoticeList
	} from '../api/system/notice.js'
	import {
		getDeptList
	} from '../api/system/dept.js'
	import {
		listPermitByDept,
		listPermitByDeptIds,
		listPermit
	} from '../api/leave/ask_for_leave.js'
	import {
		getInfo
	} from '../api/system/user.js'
	import user from '@/store/modules/user.js'
	import {
		getWeather
	} from '@/api/weather.js'
	import QQMap from "@/qqmap-wx-jssdk1.2/qqmap-wx-jssdk.js"
	import {
		listSwiper
	} from '../api/system/swiper.js'


	export default {
		data() {
			return {
				text: '',
				roleName: '',
				// 部门查询参数
				deptQuery: {
					status: 0,
					parentId: 100
				},
				// 假条查询参数
				permitQuery: {
					pageNum: 1,
					pageSize: 10,
					leaveStatus: null,
					isBack: null,
					permitType: null,
					userId: null
				},

				// 岗位ids
				deptIds: [],

				// 数据名称数组
				deptNameList: [],

				// 请假数组数据
				deptAskForLeaveList: [],

				// 销假数组数据
				deptBackForLeaveList: [],

				leaveData: [],

				// 经纬度
				latitude: '',
				longitude: '',

				// 天气信息
				weatherInfo: '',
				// 位置名称
				locationName: '',
				// 日历滑动icon
				calendarIcon: 'up',
				// 滑动情况
				scrollType: true,
				// 日历height伸缩
				calendarStyle: 0,
				// 手势触碰strat
				calendarStartY: 0,
				// 手势移动距离(0: 上滑，1: 下滑)
				calendarMoveY: 0,
				// 日历打点
				selected: [],
				// 轮播图列表
				swiperList: []
			}
		},
		onLoad() {
			// uni.authorize({
			// 	scope: 'scope.userLocation',
			// 	success: (res) => {
			// 		console.log(res);
			// 		this.getWeatherInfo()
			// 	}
			// })
			this.roleName = user.state.roles[0]


			this.getData()
			this.getWeatherInfo()
		},
		created() {
			console.log(this);
			
		},
		mounted() {
			
		},
		methods: {
			// 获取基本数据
			async getData() {
				
				// 获取轮播图列表
				const swiperRes = await listSwiper({pageNum:1,pageSize:3,isActive:'0'})
				console.log(swiperRes);
				this.swiperList = swiperRes.rows

				const userInfoData = await getInfo()
				console.log(userInfoData);

				this.permitQuery.userId = userInfoData.user.userId
				console.log(this.permitQuery);


				const test = await listPermit(this.permitQuery)
				console.log(test);
				this.getSelected(test.rows)

				const noticeRes = await getNoticeList()
				// notice文本
				this.text = noticeRes.rows[0].noticeContent

				// console.log(user);
				this.deptQuery.parentId = user.state.dept.deptId

				if (user.state.roles[0] == 'admin' || user.state.roles[0] == 'director') {
					// 超级管理员 (六大系部) || 系主任（系部对应各大专业）
					const res = await getDeptList(this.deptQuery)

					// 数据名称数组
					this.deptNameList = res.data.map(item => item.deptName)

					this.deptIds = res.data.map(item => item.deptId)
					// console.log(this.deptIds);
					const res1 = await listPermitByDeptIds({
						isBack: null,
						leaveStatus: null
					}, this.deptIds, user.state.roles[0])
					// console.log(res1);
					res1.rows.forEach((item) => {
						// 请假数组数据
						this.deptAskForLeaveList.push(item.length)
					})
					// console.log(this.deptAskForLeaveList);

					const res2 = await listPermitByDeptIds({
						leaveStatus: 1,
						isBack: 0
					}, this.deptIds, user.state.roles[0])
					// console.log(res2);
					res2.rows.forEach(item => {
						// 销假数组数据
						this.deptBackForLeaveList.push(item.length)
					})
				} else if (user.state.roles[0] == 'counsellor') {
					// 辅导员

					// 请假
					const res = await listPermitByDept(this.permitQuery, user.state.dept.parentId, null)

					this.permitQuery.leaveStatus = 1
					this.permitQuery.isBack = 0

					// 销假
					const res1 = await listPermitByDept(this.permitQuery, user.state.dept.parentId, null)

					this.leaveData = [res.total, res1.total]
					// console.log(this.leaveData);
				} else if (user.state.roles[0] == 'student') {
					// 学生

					// 请假
					// const res = await listPermitByDept(this.permitQuery,user.state.dept.parentId,null)
					// console.log(res);

					const userInfo = await getInfo()

					this.permitQuery.userId = userInfo.user.userId
					const res = await listPermit(this.permitQuery)
					// console.log(res);

					this.permitQuery.leaveStatus = 1
					this.permitQuery.isBack = 0

					// 销假
					const res1 = await listPermit(this.permitQuery)

					this.leaveData = [res.total, res1.total]
				}
			},
			// 日历下其他部分发生滚动
			otherScroll(e) {
				// e.stopPropagation()	
				console.log(e);
			},
			// 手势事件触碰开始
			calendarStart(e) {
				console.log(e);
				this.calendarStartY = e.touches[0].clientY
			},
			// 手势事件移动
			calendarMove(e) {
				let num = e.touches[0].clientY - this.calendarStartY
				// console.log(this.calendarStyle += num);
				
				if(this.calendarIcon == 'up'){
					// 可以上拉
					if( num <= -285){
						this.calendarStyle = -285
						this.calendarIcon = 'down'
					}else if(num > 0){
						this.calendarStyle = 0
					}else{
						this.calendarStyle = num
					}
				}else if(this.calendarIcon == 'down'){
					// 可以下滑
					if(num >= 285){
						this.calendarStyle = 0
						this.calendarIcon = 'up'
					}else if(num < 0){
						this.calendarStyle = -285
					}else{
						this.calendarStyle = num - 285
					}
				}
				
			},
			// 手势事件结束
			calendarEnd(e) {
				console.log('结束');
				if(this.calendarIcon == 'up' && this.calendarStyle != 0){
					this.calendarStyle = -285
					this.calendarIcon = 'down'
				}else if(this.calendarIcon == 'down' && this.calendarStyle != -285){
					this.calendarStyle = 0
					this.calendarIcon = 'up'
				}
			},
			calendarChange(e) {
				console.log(e);
			},
			// 获取日历所有标记
			getSelected(list) {
				let arr = []
				list.forEach((item,index) => {
					arr[index] = this.transDateRange(item.startTime,item.endTime)
				})
				console.log(arr);
				this.selected = arr.flat()
				console.log(this.selected);
			},
			// 日期范围转换
			transDateRange(startTime, endTime) {
				const startDate = new Date(startTime); // 转换为 Date 对象
				const endDate = new Date(endTime); // 转换为 Date 对象
				const dateArray = []; // 存储结果的数组

				// 循环从 startDate 到 endDate
				while (startDate <= endDate) {
					// 格式化日期为 YYYY-MM-DD
					const year = startDate.getFullYear();
					const month = String(startDate.getMonth() + 1).padStart(2, "0"); // 月份从 0 开始，需要 +1
					const day = String(startDate.getDate()).padStart(2, "0");
					const formattedDate = `${year}-${month}-${day}`;

					// 将格式化后的日期存入数组
					dateArray.push({
						date: formattedDate,
						info: '请假'
					});

					// 增加一天
					startDate.setDate(startDate.getDate() + 1);
				}
				console.log(dateArray);
				return dateArray;
			},
			// 修改图片路径
			getAvatar(imgUrl){
				return "http://localhost:8080" + imgUrl.replace(/\/dev-api/g,"")
			},
			// 获取天气图标
			getWeatherIcon() {
				return '/static/icons/' + this.weatherInfo.icon + '.png'
			},
			// 获取实时天气以及当前位置
			getWeatherInfo() {
				uni.getLocation({
					type: 'gcj02',
					success: async (res) => {
						this.latitude = res.latitude
						this.longitude = res.longitude

						// 实例化API核心类
						this.qqmap = new QQMap({
							key: 'PMGBZ-SDYKQ-6VS5O-4P3HN-ZVT75-VCFOB'
						})

						// 逆地址解析（获取地址）
						this.qqmap.reverseGeocoder({
							location: {
								latitude: this.latitude,
								longitude: this.longitude
							},
							success: (res) => {
								// console.log(res);
								this.locationName = res.result.address_component.province + res
									.result.address_component.city + res.result.address_component
									.district
							},
							fail: (res) => {
								console.log(res);
							}
						})

						let weatherParam = parseFloat(this.longitude).toFixed(2) + ',' + parseFloat(this
							.latitude).toFixed(2)
						// console.log(weatherParam);
						// 获取天气信息
						const weatherData = await getWeather()
						// console.log(weatherData);
						this.weatherInfo = weatherData[1].data.now
					},
					fail: (res) => {
						console.log(res);
					}
				})
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		width: 100vw;
		height: 100vh;
	}

	.swiper {
		width: 100%;
		height: 300px;
	}

	.swiper-item {
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 20px;
	}

	uni-notice-bar {
		width: 100%;
		margin-top: 20px;
	}

	uni-card {
		width: 100%;
		height: 100%;
	}

	.uni-card,
	.uni-card__content {
		width: 100%;
		height: 100%;
		margin: 0 !important;
		padding: 0 !important;
	}

	ec-canvas {
		width: 100%;
		height: 100%;
	}

	.content {
		width: 100%;
		height: 100vh;
		overflow: hidden;
	}

	.weather {
		margin: 10px 0;
		width: 90%;
		border: 1px solid #fff;
		border-radius: 15px;
		padding: 20px;
		position: relative;
		box-shadow: 0 15px 20px rgba(141, 222, 243, 0.4);
		background-image: linear-gradient(to right top, #a6eef6, #99e6f4, #8ddef3, #81d6f2, #78cdf0);
	}

	.top {
		display: flex;
	}

	image {
		width: 50px;
		height: 50px;
	}

	.wea-text-tem {
		margin-left: 20px;
	}

	.wea-tem {
		font-size: 20px;
		font-weight: 700;
	}

	.wea-text {
		font-size: 15px;
	}

	.bottom {
		margin-top: 10px;
		display: flex;
		justify-content: space-between;
	}

	.wind-dir {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.province {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.calendar-box {
		width: 100%;
		background-color: white;
		overflow: hidden;
	}

	.calendar-other {
		width: 100%;
		background-color: white;
		z-index: 99;
		transition: all .08s linear; 
	}
	
	.calendar-icon{
		width: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.other {
		width: 100%;
		display: flex;
		align-items: center;
		flex-direction: column;
	}
	
	.test{
		height: 400px;
		width: 100%;
		background-color: white;
	}
</style>