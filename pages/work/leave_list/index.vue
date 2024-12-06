<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);position: relative;" scroll-y="true"
		scroll-top="0" enable-flex="true" lower-threshold="50" @scrolltolower="scrolltolower">
		<view class="header-section">
		</view>
		<view class="card-box">
			<view class="card" v-for="(item,index) in permitList" :key="index" @click="fabClick(item.leaveId)">
				<uni-section :title="item.permitType == '0'?'病假':'事假'" type="circle"></uni-section>
				<view class="time">
					<view class="start-end">
						{{formatTime(item.startTime)}} - {{formatTime(item.endTime)}}
					</view>
					<view class="days">
						{{item.permitDays}}天
					</view>
				</view>
				<view class="request-time">
					申请时间：{{type == '请假申请'?item.permitTime:item.backTime}}
				</view>

				<view class="reason" v-if="item.leaveStatus == '0'">
					请假理由：{{item.reason}}
				</view>

				<view class="type">
					{{auditReminder(item)}}
				</view>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	import user from '@/store/modules/user.js'
	import {
		listPermitByDept,
		listPermit
	} from '@/api/leave/ask_for_leave.js'
	import {
		getInfo
	} from '@/api/system/user.js'
	export default {
		data() {
			return {
				// 假条列表
				permitList: [],
				// 类型
				type: '',
				// 请求参数(教师)
				params: {
					pageNum: 1,
					pageSize: 5,
					permitType: null,
					isBack: null,
					leaveStatus: 0,
				},
				// 请求参数（学生）
				backParams: {
					pageNum: 1,
					pageSize: 5,
					isBack: 2,
					userId: 0
				},
				// 总数
				total: 0
			}
		},
		created() {
			console.log('created');
		},
		async onLoad(params) {
			console.log('onload');
			console.log(params);
			console.log(user);
			this.type = params.type
			const res = await getInfo()
			console.log(res);
			this.backParams.userId = res.user.userId
			this.getData()
		},
		mounted() {
			console.log('mounted');
		},
		methods: {
			//获取数据
			getData() {
				if (this.type == '请假申请') {
					this.askRequest()
				} else if (this.type == '销假申请') {
					this.backRequest()
				} else if (this.type == '销假') {
					this.backLeave()
				} else if (this.type == '假条列表') {
					this.getLeaveList()
				}
			},

			/**
			 * type = '请假申请' -----教师
			 */
			async askRequest() {
				console.log('请假申请');
				if (user.state.roles[0] == 'admin') {
					// 超级管理员
					const res = await listPermitByDept(this.params, '', '')
					console.log(res);
					res.rows.forEach(item => {
						this.permitList.push(item)
					})
					this.total = res.total
				} else if (user.state.roles[0] == 'director') {
					// 主任
					const res = await listPermitByDept(this.params, user.state.dept.deptId, true)
					console.log(res);
					res.rows.forEach(item => {
						this.permitList.push(item)
					})
					this.total = res.total
				} else if (user.state.roles[0] == 'counsellor') {
					// 辅导员
					const res = await listPermitByDept(this.params, user.state.dept.parentId, false)
					console.log(res);
					res.rows.forEach(item => {
						this.permitList.push(item)
					})
					this.total = res.total
				}
			},
			/**
			 * type = '销假申请' ------教师
			 */
			async backRequest() {
				console.log('销假申请');
				this.params.leaveStatus = 1
				this.params.isBack = 2
				if (user.state.roles[0] == 'admin') {
					// 超级管理员
					let res = await listPermitByDept(this.params, '', '')
					res.rows.forEach(item => {
						this.permitList.push(item)
					})
					this.total = res.total
					console.log(res)
				} else if (user.state.roles[0] == 'director') {
					// 系主任
					let res = await listPermitByDept(this.params, user.state.dept.deptId, true)
					res.rows.forEach(item => {
						this.permitList.push(item)
					})
					this.total = res.total
					console.log(res)
				} else if (user.state.roles[0] == 'counsellor') {
					// 辅导员
					let res = await listPermitByDept(this.params, user.state.dept.parentId, false)
					res.rows.forEach(item => {
						this.permitList.push(item)
					})
					this.total = res.total
					console.log(res)
				}
			},
			/**
			 * type = '销假' ---- 学生
			 */
			async backLeave() {
				console.log('销假');
				const res = await listPermit(this.backParams)
				console.log(res);
				res.rows.forEach(item => {
					this.permitList.push(item)
				})
				this.total = res.total
			},
			/**
			 * type = '假条列表' -----学生
			 */
			async getLeaveList() {
				console.log('假条列表');
				const res = await listPermit({
					pageNum: this.backParams.pageNum,
					pageSize: this.backParams.pageSize,
					userId: this.backParams.userId
				})
				console.log(res);
				res.rows.forEach(item => {
					this.permitList.push(item)
				})
				this.total = res.total
			},
			// card点击跳转详情页
			fabClick(id){
				console.log('fabClick');
				console.log(this.type);
				console.log(id);
				if(this.type == '假条列表'){
					uni.navigateTo({
						url: `/pages/work/detail/index?id=${id}&type=假条详细`
					})
				}else {
					uni.navigateTo({
						url: `/pages/work/detail/index?id=${id}&type=${this.type}`
					})
				}
				
			},
			// 格式化时间
			formatTime(time) {
				// console.log(time);
				// console.log('格式化');
				const date = time.split(" ")
				// console.log(date);
				return date[0].replace(/-/g, "/")
			},
			// 审核提示
			auditReminder(item) {
				if (item.leaveStatus == '0') {
					return '审批中'
				} else if (item.leaveStatus == '2') {
					return '审批拒绝'
				} else if (item.leaveStatus == '1' && item.isBack == '0') {
					return '审批通过'
				} else if (item.leaveStatus == '1' && item.isBack == '1') {
					return '审批通过'
				} else if (item.leaveStatus == '1' && item.isBack == '2') {
					return '审批中'
				}
			},
			// 滚动到底部后继续渲染直到没有数据为止
			scrolltolower() {
				console.log('底部');
				if (this.type == '请假申请' || this.type == '销假申请') {
					this.params.pageNum += 1

					if ((this.params.pageNum - 1) * this.params.pageSize > this.total) {
						uni.showToast({
							title: "已经没有咯~~",
							icon: "error"
						})
					} else {
						uni.showLoading({
							title: "加载中",
							success: () => {
								setTimeout(() => {
									uni.hideLoading()
									this.getData()
								}, 2000)
							}
						})
					}

				} else if (this.type == '销假' || this.type == '假条列表') {
					this.backParams.pageNum += 1
					if ((this.backParams.pageNum - 1) * this.backParams.pageSize > this.total) {
						uni.showToast({
							title: "已经没有咯~~",
							icon: "error"
						})
					} else {
						uni.showLoading({
							title: "加载中",
							success: () => {
								setTimeout(() => {
									uni.hideLoading()
									this.getData()
								}, 2000)
							}
						})
					}
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

	.card-box {
		width: 90%;
		position: absolute;
		top: 50px;
		left: 0;
		right: 0;
		margin: 0 auto;

		.card {
			width: 100%;
			border: 1px solid #fff;
			border-radius: 15px;
			background-color: #fff;
			padding: 25px 10px;
			margin-bottom: 20px;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: space-between;
			position: relative;

			uni-section {
				width: 100%;

				.uni-section-header__decoration {
					background-color: #f4a042 !important;
				}

				.uni-section__content-title {
					font-size: 18px !important;
					font-weight: 600;
				}
			}

			.time,
			.request-time,
			.reason {
				width: 100%;
				padding-left: 18px;
				margin: 5px 0;
			}

			.time {
				display: flex;
				align-items: center;
				font-size: 18px;

				.days {
					margin-left: 80px;
				}
			}

			.request-time {
				color: #b0b0b0;
			}

			.type {
				position: absolute;
				right: 20px;
				bottom: 10%;
				font-size: 25px;
				font-weight: 600;
				color: #f4a042
			}
		}

	}
</style>