<template>
	<view class="content">
		<swiper class="swiper" circular :autoplay="true" :interval="2000" :indicator-dots='true' indicator-color='white'
			:duration="500">
			<swiper-item>
				<view class="swiper-item uni-bg-red" style="background-color: palevioletred;">A</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item uni-bg-green" style="background-color: aqua;">B</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item uni-bg-blue" style="background-color: aquamarine;">C</view>
			</swiper-item>
		</swiper>

		<uni-notice-bar :scrollable="true" :single="true" :showIcon="true" :speed="100" :text="text"></uni-notice-bar>

		<uni-card :is-shadow="false">
			<ec-canvas id="mychart-dom-bar" canvas-id="mychart-bar" :ec="{
					onInit: initChart,
					deptNameList:deptNameList,
					deptAskForLeaveList:deptAskForLeaveList,
					deptBackForLeaveList:deptBackForLeaveList,
					roleName: roleName,
					leaveData: leaveData
					}"></ec-canvas>
		</uni-card>
	</view>
</template>

<script>
	import * as echarts from '../wxcomponents/ec-canvas/echarts.js'
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
				
				leaveData: []
			}
		},
		async created() {
			this.roleName = user.state.roles[0]
			console.log('created');
			this.getData()
		},
		mounted() {
			console.log('mounted');
		},
		methods: {
			// 获取基本数据
			async getData() {

				const noticeRes = await getNoticeList()
				// notice文本
				this.text = noticeRes.rows[0].noticeContent

				console.log(user);
				this.deptQuery.parentId = user.state.dept.deptId

				if (user.state.roles[0] == 'admin' || user.state.roles[0] == 'director') {
					// 超级管理员 (六大系部) || 系主任（系部对应各大专业）
					const res = await getDeptList(this.deptQuery)

					// 数据名称数组
					this.deptNameList = res.data.map(item => item.deptName)

					this.deptIds = res.data.map(item => item.deptId)
					console.log(this.deptIds);
					const res1 = await listPermitByDeptIds({
						isBack: null,
						leaveStatus: null
					}, this.deptIds, user.state.roles[0])
					console.log(res1);
					res1.rows.forEach((item) => {
						// 请假数组数据
						this.deptAskForLeaveList.push(item.length)
					})
					console.log(this.deptAskForLeaveList);

					const res2 = await listPermitByDeptIds({
						leaveStatus: 1,
						isBack: 0
					}, this.deptIds, user.state.roles[0])
					console.log(res2);
					res2.rows.forEach(item => {
						// 销假数组数据
						this.deptBackForLeaveList.push(item.length)
					})
				} else if (user.state.roles[0] == 'counsellor') {
					// 辅导员
					
					// 请假
					const res = await listPermitByDept(this.permitQuery,user.state.dept.parentId,null)
					
					this.permitQuery.leaveStatus = 1
					this.permitQuery.isBack = 0
					
					// 销假
					const res1 = await listPermitByDept(this.permitQuery,user.state.dept.parentId,null)
					
					this.leaveData = [res.total,res1.total]
					console.log(this.leaveData);
				}else if (user.state.roles[0] == 'student') {
					// 学生
					
					// 请假
					// const res = await listPermitByDept(this.permitQuery,user.state.dept.parentId,null)
					// console.log(res);
					
					const userInfo = await getInfo()
					
					this.permitQuery.userId = userInfo.user.userId
					const res = await listPermit(this.permitQuery)
					console.log(res);
					
					this.permitQuery.leaveStatus = 1
					this.permitQuery.isBack = 0
					
					// 销假
					const res1 = await listPermit(this.permitQuery)
					
					this.leaveData = [res.total,res1.total]
				}
			},
			// 初始化Echarts
			initChart(canvas, width, height, dpr) {
				console.log('initChart')
				console.log(this);
				let chart = null
				chart = echarts.init(canvas, null, {
					width: width,
					height: height,
					devicePixelRatio: dpr // new
				});
				canvas.setChart(chart);
				
				let option = null

				if (this.roleName == 'admin' || this.roleName == 'director') {
					// 超级管理员 || 系主任
					const seriesLabel = {
						show: true
					};
					option = {
						textStyle: {
							fontSize: 12,
							fontWeight: 700
						},
						title: {
							text: '数据'
						},
						tooltip: {
							trigger: 'axis',
							axisPointer: {
								type: 'shadow'
							}
						},
						legend: {
							data: ['请假', '销假']
						},
						grid: {
							left: 95
						},
						toolbox: {
							show: true,
							feature: {
								saveAsImage: {}
							}
						},
						xAxis: {
							type: 'value',
							name: 'SUM',
							lineHeight: 40,
							axisLabel: {
								formatter: '{value}'
							}
						},
						yAxis: {
							type: 'category',
							inverse: true,
							data: this.deptNameList,
							axisLabel: {
								formatter: function(value) {
									return value;
								},
								margin: 20,
								rich: {
									value: {
										lineHeight: 30,
										align: 'center'
									},
									Sunny: {
										height: 40,
										align: 'center'
									},
									Cloudy: {
										height: 40,
										align: 'center'
									},
									Showers: {
										height: 40,
										align: 'center'
									}
								}
							}
						},
						series: [{
								name: '请假',
								type: 'bar',
								data: this.deptAskForLeaveList,
								label: seriesLabel
							},
							{
								name: '销假',
								type: 'bar',
								label: seriesLabel,
								data: this.deptBackForLeaveList
							}
						]
					};
				} else if (this.roleName == 'counsellor' || this.roleName == 'student') {
					// 辅导员 || 学生
					option = {
						title:{
							text: this.roleName == "counsellor"?"本专业请销假人数":"个人请销假总数"
						},
						tooltip: {
							trigger: 'axis',
							axisPointer: {
								type: 'shadow'
							}
						},
						grid: {
							left: '3%',
							right: '4%',
							bottom: '3%',
							containLabel: true
						},
						xAxis: [{
							type: 'category',
							data: ['请假','销假'],
							axisTick: {
								alignWithLabel: true
							}
						}],
						yAxis: [{
							type: 'value'
						}],
						series: [{
							name: 'Sum',
							type: 'bar',
							barWidth: '60%',
							data: this.leaveData
						}]
					};
				}

				chart.setOption(option);
				return chart;
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
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
</style>