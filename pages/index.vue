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
			<ec-canvas id="mychart-dom-bar" canvas-id="mychart-bar"
				:ec="{
					onInit: initChart,
					deptNameList:deptNameList,
					deptAskForLeaveList:deptAskForLeaveList,
					deptBackForLeaveList:deptBackForLeaveList
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
		listPermitByDeptIds
	} from '../api/leave/ask_for_leave.js'
	import user from '@/store/modules/user.js'

	export default {
		data() {
			return {
				text: '',
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
					permitType: null
				},
				
				// 岗位ids
				deptIds: [],

				// 数据名称数组
				deptNameList: [],

				// 请假数组数据
				deptAskForLeaveList: [],

				// 销假数组数据
				deptBackForLeaveList: []
			}
		},
		async created() {
			console.log('created');
			this.getData()
		},
		mounted() {
			console.log('mounted');
		},
		methods: {
			async getAdmin(item,index){
				// console.log(item+ '——' +index);
				const res = await listPermitByDept(this.permitQuery,item.deptId,null)
				this.deptAskForLeaveList[index] = res.total
				// this.$forceUpdate()
				// console.log(this.deptAskForLeaveList);
			},
			// 获取基本数据
			async getData(){
				
				const noticeRes = await getNoticeList()
				// notice文本
				this.text = noticeRes.rows[0].noticeContent
				
				const res = await getDeptList(this.deptQuery)
				
				// 数据名称数组
				this.deptNameList = res.data.map(item => item.deptName)
				
				this.deptIds = res.data.map(item => item.deptId)
				const res1 = await listPermitByDeptIds({isBack: null, leaveStatus: null},this.deptIds)
				res1.rows.forEach((item) => {
					// 请假数组数据
					this.deptAskForLeaveList.push(item.length)
				})
				console.log(this.deptAskForLeaveList);
				
				const res2 = await listPermitByDeptIds({leaveStatus: 1, isBack: 0},this.deptIds)
				console.log(res2);
				res2.rows.forEach(item => {
					// 销假数组数据
					this.deptBackForLeaveList.push(item.length)
				})
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


				const seriesLabel = {
					show: true
				};
				let option = {
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
						left: 85
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