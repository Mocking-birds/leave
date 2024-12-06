<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);position: relative;" scroll-y="true"
		scroll-top="0" enable-flex="true" lower-threshold="50">

		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage" :duration="2000"></uni-popup-message>
		</uni-popup>

		<view class="header-section">
		</view>

		<view class="card-box">
			<view class="card permit_leave_type">
				<uni-section title="假条类别" type="circle"></uni-section>
				<uni-data-select :clear="false" v-model="permitData.permitType"
					:localdata="permitTypeRange"></uni-data-select>
			</view>
			<view class="card permit_leave_start-time">
				<uni-section title="起始时间" type="circle"></uni-section>
				<uni-datetime-picker type="datetime" :border="false" :clearIcon="false" v-model="permitData.startTime"
					@change="changeTime" />
			</view>
			<view class="card permit_leave_end-time">
				<uni-section title="结束时间" type="circle"></uni-section>
				<uni-datetime-picker type="datetime" :border="false" :clearIcon="false" v-model="permitData.endTime"
					@change="changeTime" />
			</view>
			<view class="card permit_leave_days">
				<uni-section title="请假天数" type="circle"></uni-section>
				<view class="days">
					{{permitData.permitDays}}天
				</view>
			</view>
			<view class="card permit_leave_reason">
				<uni-section title="请假理由" type="circle"></uni-section>
				<input class="uni-input" v-model="permitData.reason" />
			</view>
			<view class="card permit_leave_ask_location">
				<uni-section title="请假位置" type="circle"></uni-section>
				<view class="location" @click="getCurrentLocation(0)"
					v-if="!(permitData.permitLocationList[0].locationName)">
					点击获取位置
				</view>
				<view class="show-location" @click="getLocation(0)" v-else>
					<uni-icons type="location-filled" size="23" color="#2979FF"></uni-icons>
					{{permitData.permitLocationList[0].locationName}}
				</view>
			</view>

			<view class="req-button">
				<button class="req-button-sub" @click="subBtn">提交</button>
				<button class="req-button-cancel" @click="cancelBtn">取消</button>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	import QQMap from "@/qqmap-wx-jssdk1.2/qqmap-wx-jssdk.js"
	import {getInfo} from '@/api/system/user.js'
	import {
		addPermit
	} from '@/api/leave/ask_for_leave.js'
	export default {
		data() {
			return {
				// form
				permitData: {
					permitType: -1,
					startTime: null,
					endTime: null,
					permitDays: 0,
					reason: null,
					permitLocationList: [],
					leaveStatus: 0,
					permitTime: null,
					userId: 0
				},
				// 请假类型范围
				permitTypeRange: [{
						value: 0,
						text: "事假"
					},
					{
						value: 1,
						text: "病假"
					},
				],
				// 请销假类型
				type: '',
				//提示窗类型
				popupType: 'success',
				// 提示窗文本
				popupMessage: '',
				// 用户信息
				userInfo: ''
			}
		},
		async created() {
			console.log('created');
			const res = await getInfo()
			console.log(res);
			this.userInfo = res
			this.permitData.userId = res.user.userId
		},
		onLoad(params) {
			console.log('onLoad');
			this.type = params.type
		},
		methods: {
			// 时间发生改变
			changeTime() {
				console.log('时间改变')
				// 非空判断
				if (this.permitData.startTime != null && this.permitData.endTime != null) {
					// 将日期字符串转换为Date对象
					const date1Obj = new Date(this.permitData.startTime);
					const date2Obj = new Date(this.permitData.endTime);

					// 计算时间差（毫秒）
					const timeDifference = Math.abs(date2Obj - date1Obj);

					// 将时间差转换为天数
					this.permitData.permitDays = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));
				}

			},
			// 获取当前时间
			getCurrentTime() {
				// 创建一个新的 Date 对象
				const now = new Date();

				// 获取各个时间组件
				const year = now.getFullYear();
				const month = String(now.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以要加1，并填充0
				const day = String(now.getDate()).padStart(2, '0');
				const hours = String(now.getHours()).padStart(2, '0');
				const minutes = String(now.getMinutes()).padStart(2, '0');
				const seconds = String(now.getSeconds()).padStart(2, '0');

				// 格式化时间字符串
				const formattedTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
				console.log(formattedTime);
				return formattedTime;
			},
			// 获取当前位置
			getCurrentLocation(num) {
				uni.showLoading({
					title: '正在获取',
					success: () => {
						uni.getLocation({
							type: 'gcj02', //返回可以用于uni.openLocation的经纬度
							success: (res) => {
								let latitude = res.latitude
								let longitude = res.longitude

								// 实例化API核心类
								this.qqmap = new QQMap({
									key: 'PMGBZ-SDYKQ-6VS5O-4P3HN-ZVT75-VCFOB'
								})

								// 逆地址解析
								this.qqmap.reverseGeocoder({
									location: {
										latitude: latitude,
										longitude: longitude
									},
									success: (res) => {
										let locationName = res.result.address
										let locationType = num
										this.permitData.permitLocationList.push({
											latitude: latitude,
											longitude: longitude,
											locationName: locationName,
											locationType: locationType
										})

										console.log('成功');
										setTimeout(() => {
											uni.hideLoading()
										}, 1000)
									},
									fail: (res) => {
										console.log(res);
									}
								})
							},
							fail: (res) => {
								console.log(res);
							}
						});
					}
				})
			},
			// 打开地图获取位置
			getLocation(index) {
				uni.openLocation({
					latitude: Number(this.permitData.permitLocationList[index].latitude),
					longitude: Number(this.permitData.permitLocationList[index].longitude),
					success: () => {
						console.log('success');
					},
					fail: (res) => {
						console.log(res);
					}
				})
			},
			// 提交
			async subBtn() {
				console.log(this.permitData);
				this.getCurrentTime()
				// if((this.permitData.permitType == 0 || this.permitData == 1) && this.permitData.startTime != null && this.permitData.endTime != null && this.permitData.reason != null && this.permitData.permitLocationList[0].locationName != null){
				// 	this.permitData.permitTime = this.getCurrentTime()
				// 	console.log('提交');
				// 	console.log(this.permitData);
				// 	// const res = await addPermit(this.permitData)
				// }else{

				// }
				if ((this.permitData.permitType == 0 || this.permitData.permitType == 1) && this.permitData.startTime != null && this.permitData.endTime != null && this.permitData.reason != null && this.permitData.permitLocationList[0].locationName != null) {
					console.log('提交');
					this.permitData.permitTime = this.getCurrentTime()
					console.log(this.permitData);
					const res = await addPermit(this.permitData)
					console.log(res);
					if (res.code == 200) {
						console.log('成功');
						this.popupMessage = '操作成功'
						this.popupType = 'success'
						this.$refs.popup.open()
						setTimeout(() => {
							uni.switchTab({
								url: '/pages/work/index'
							})
						}, 2000)
					}
				} else {
					
					console.log(this.permitData.permitType == 0 || this.permitData == 1);
					console.log(this.permitData.startTime != null);
					console.log(this.permitData.endTime != null);
					console.log(this.permitData.permitLocationList[0].locationName != null);
					console.log(this.permitData.permitLocationList[0].locationName);
					
					
					this.popupType = 'error'
					this.popupMessage = '请将数据填写完整'
					this.$refs.popup.open()
				}
			},
			// 点击取消按钮
			cancelBtn() {
				uni.navigateBack()
			}
		}
	}
</script>

<style lang="scss">
	.header-section {
		width: 100%;
		height: 200px;
		background-color: #261c3f;
		border-bottom-left-radius: 50px 10px;
		border-bottom-right-radius: 50px 10px;
	}

	.card-box {
		width: 90%;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		margin: 0 auto;
	}

	.card {
		width: 100%;
		border: 1px solid #fff;
		border-radius: 15px;
		background-color: #fff;
		padding: 5px 2px;
		margin-bottom: 50px;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.permit_leave_type {

		.uni-stat__select {
			width: 100px;

			.uni-select {
				border: none;

				.uni-select__input-text {
					width: 50%;
				}
			}
		}
	}

	.permit_leave_start-time {
		.uni-date {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			padding-right: 20px;

			.uni-date__x-input {
				margin-left: 5px;
			}
		}
	}

	.permit_leave_end-time {
		.uni-date {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			padding-right: 20px;

			.uni-date__x-input {
				margin-left: 5px;
			}
		}
	}

	.permit_leave_days {
		.days {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
		}
	}

	.permit_leave_reason {
		.uni-input {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
			text-align: end;
		}
	}

	.permit_leave_ask_location {
		.location {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
			text-align: right;
		}
	}

	.permit_leave_back_location {
		.location {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
			text-align: right;
		}
	}

	.req-button {
		display: flex;
		align-items: center;
		justify-content: space-between;

		.req-button-sub,
		.req-button-cancel {
			width: 35%;
			background-color: #261c3f;
			border-radius: 25px;
			font-weight: 600;
			color: #fff;
		}
	}
</style>