<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);" scroll-y="true" scroll-top="0"
		enable-flex="true" lower-threshold="50">
		
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage" :duration="2000"></uni-popup-message>
		</uni-popup>
		
		<view class="header-section">
		</view>

		<view class="card-box">
			<view class="card permit-steps">
				<uni-steps :options="stepsList" :active="active" :active-icon="activeIcon" :active-color="activeColor"
					style="width: 100%;" />
			</view>
			<view class="card permit-name">
				<uni-section title="申请人" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.user.nickName}}
				</view>
			</view>
			<view class="card permit-major">
				<uni-section title="专业" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.user.dept.deptName}}
				</view>
			</view>
			<view class="card leave_type">
				<uni-section title="请假类别" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.permitType == '0'?'病假':'事假'}}
				</view>
			</view>
			<view class="card permit_leave_type">
				<uni-section title="假条类别" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.leaveStatus == '0'?'请假':'销假'}}
				</view>
			</view>
			<view class="card permit_leave_start-time">
				<uni-section title="起始时间" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.startTime}}
				</view>
			</view>
			<view class="card permit_leave_end-time">
				<uni-section title="结束时间" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.endTime}}
				</view>
			</view>
			<view class="card permit_leave_days">
				<uni-section title="请假天数" type="circle"></uni-section>
				<view class="days permit-value">
					{{permitDetailList.permitDays}}天
				</view>
			</view>
			<view class="card permit_leave_reason">
				<uni-section title="请假理由" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.reason}}
				</view>
			</view>
			<view class="card permit_leave_ask_time">
				<uni-section title="请假时间" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.permitTime}}
				</view>
			</view>
			<view class="card permit_leave_ask_location">
				<uni-section title="请假位置" type="circle"></uni-section>
				<view class="location permit-value" @click="getLocation(0)">
					<uni-icons type="location-filled" size="23" color="#2979FF"></uni-icons>
					{{permitDetailList.permitLocationList[0].locationName}}
				</view>
			</view>
			<view class="card permit_leave_back_time" v-if="permitDetailList.backTime">
				<uni-section title="销假时间" type="circle"></uni-section>
				<view class="permit-value">
					{{permitDetailList.backTime}}
				</view>
			</view>
			<view class="card permit_leave_back_location" v-if="permitDetailList.permitLocationList[1]">
				<uni-section title="销假位置" type="circle"></uni-section>
				<view class="location permit-value" @click="getLocation(1)">
					<uni-icons type="location-filled" size="23" color="#2979FF"></uni-icons>
					{{permitDetailList.permitLocationList[1].locationName}}
				</view>
			</view>
			<view class="permit_detail_btn" v-if="type != '假条详细'">
				<button size="default" hover-class="is-hover" @click="subBtn">{{type == '销假'?'销假请求':'通过'}}</button>
				<button size="default" hover-class="is-hover" @click="rejBtn">
					{{type == '请假申请'?'驳回':'取消'}}
				</button>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	import {
		getPermit,
		updatePermit
	} from '@/api/leave/ask_for_leave.js'
	export default {
		data() {
			return {
				// 假条详细类型
				type: '',
				// 假条id
				leaveId: 0,
				//提示窗类型
				popupType: 'success',
				// 提示窗文本
				popupMessage: '',
				// 当前步骤进度
				active: 2,
				// 步骤条图标
				activeIcon: 'checkbox',
				// 步骤条颜色
				activeColor: '#2979FF',
				// 步骤条列表
				stepsList: [{
					title: '提交申请'
				}, {
					title: '审批中'
				}, {
					title: '审批通过'
				}],
				// 假条详细列表
				permitDetailList: []
			}
		},
		created() {

		},
		onLoad(params) {
			this.leaveId = params.id
			this.type = params.type
			console.log(this.type);
			this.getData()
		},
		methods: {
			// 初始化数据
			async getData() {
				const res = await getPermit(this.leaveId)
				console.log(res);
				this.permitDetailList = res.data

				// 判断当前所处阶段
				if (res.data.leaveStatus == '0') {
					this.active = 1
				} else if (res.data.leaveStatus == '2') {
					this.stepsList[2].title = '申请驳回'
					this.activeIcon = 'close'
					this.activeColor = '#e75353'
					this.active = 2
				} else if (res.data.leaveStatus == '1') {
					if (res.data.isBack == '0') {
						this.active = 2
					} else if (res.data.isBack == '1') {
						this.active = 2
					} else if (res.data.isBack == '2') {
						this.active = 1
					}
				}
			},
			// 打开地图获取位置
			getLocation(index) {
				uni.openLocation({
					latitude: Number(this.permitDetailList.permitLocationList[index].latitude),
					longitude: Number(this.permitDetailList.permitLocationList[index].longitude),
					success: () => {
						console.log('success');
					},
					fail: (res) => {
						console.log(res);
					}
				})
			},
			// 驳回
			async rejBtn(){
				console.log('取消');
				if(this.type == '请假申请'){
					this.permitDetailList.leaveStatus = '2'
					const res = await updatePermit(this.permitDetailList)
					console.log(res);
					if(res.code == 200){
						this.popupMessage = '操作成功'
						this.$refs.popup.open()
						setTimeout(() => {
							uni.switchTab({
								url: '/pages/work/index'
							})
						},2000)
					}else{
						this.popupType = 'error'
						this.popupMessage = '操作失败'
						this.$refs.popup.open()
					}
				}else{
					uni.navigateBack()
				}
			},
			// 通过
			async subBtn(){
				if(this.type == '请假申请'){
					this.permitDetailList.leaveStatus = '1'
				}else if (this.type == '销假申请'){
					this.permitDetailList.isBack = '1'
				}else if (this.type == '销假'){
					this.permitDetailList.isBack = '2'
				}
				const res = await updatePermit(this.permitDetailList)
				console.log(res);
				console.log(res.code);
				if(res.code == 200){
					console.log('成功');
					this.popupMessage = '操作成功'
					this.$refs.popup.open()
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/work/index'
						})
					},2000)
				}else{
					this.popupType = 'error'
					this.popupMessage = '操作失败'
					this.$refs.popup.open()
				}
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

	.card:nth-child(3n) {
		.uni-section {
			.uni-section-header {
				.circle {
					background-color: #37ac55 !important;
				}
			}
		}
	}

	.card:nth-child(3n+1) {
		.uni-section {
			.uni-section-header {
				.circle {
					background-color: #e75150 !important;
				}
			}
		}
	}

	.card:nth-child(3n+2) {
		.uni-section {
			.uni-section-header {
				.circle {
					background-color: #f3a142 !important;
				}
			}
		}
	}

	.card-box {
		width: 90%;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		margin: 0 auto;

		.card {
			width: 100%;
			border: 1px solid #fff;
			border-radius: 15px;
			background-color: #fff;
			padding: 15px 10px;
			margin-bottom: 25px;
			display: flex;
			align-items: center;
			justify-content: space-between;


			uni-section {
				width: 100px;

				.uni-section {
					.uni-section-header {
						padding: 12px 0;

						.uni-section-header__decoration {
							width: 12px;
							height: 12px;
						}

						.uni-section-header__content {
							margin-left: 5px;

							.uni-section__content-title {
								font-size: 17px !important;
								color: #000 !important;
								font-weight: 600;
							}
						}
					}
				}
			}

			.permit-value {
				font-size: 17px;
				color: #000;
				flex: 1;
				padding-left: 30px;
			}
		}

		.permit-steps {
			padding: 35px !important;

			.uni-steps__row-text-container {
				.uni-steps__row-title {
					font-size: 18px;
				}
			}

			.uni-steps__row-container {
				.uni-steps__row-line-item {
					.uni-steps__row-check {
						.uni-icons {
							font-size: 22px !important;
						}
					}

					.uni-steps__row-circle {
						width: 15px !important;
						height: 15px !important;
					}
				}
			}
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
				align-items: center;
				flex: 1;
				padding-right: 20px;
			}
		}

		.permit_leave_ask_location {
			.location {
				display: flex;
				justify-content: flex-start;
				align-items: center;
				flex: 1;
				text-align: left;
				padding-left: 10px;
			}
		}

		.permit_leave_back_location {
			.location {
				display: flex;
				justify-content: flex-start;
				align-items: center;
				flex: 1;
				text-align: left;
				padding-left: 10px;
			}
		}
		
		.permit_detail_btn{
			display: flex;
			align-items: center;
			justify-content: space-around;
			margin-bottom: 80px;
			
			button{
				margin:0;
				width: 30%;
				background-color: #261c3f;
				color:#fff;
				letter-spacing: 1px;
				font-weight: 600;
				font-size: 20px;
				border-radius: 25px !important;
			}
			
		}
	}
	
	.is-hover{
		background-color: #bebbc6 !important;
	}
</style>