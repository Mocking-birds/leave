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

		<uni-notice-bar :scrollable="true" :single="true" :showIcon="true" :speed="100"
			:text="text"></uni-notice-bar>

		<uni-card :is-shadow="false">
			<ec-canvas id="mychart-bar" canvas-id="mychart-bar" :ec="ec"></ec-canvas>
		</uni-card>

	</view>
</template>

<script>
	import * as echarts from '@/wxcomponents/ec-canvas/echarts.js'
	import {getNoticeList} from '@/api/system/notice.js'

	export default {
		data() {
			return {
				ec: {
					onInit: this.initChart
				},
				text: ''

			}
		},
		methods: {
			// 获取数据
			async getData(){
				const res = await getNoticeList({pageNum:1,pageSize:10})
				console.log(res);
				this.text = res.rows[0].noticeTitle
			},
			initChart(canvas, width, height, dpr) {
				console.log(canvas, width, height, dpr);
				const chart = echarts.init(canvas, null, {
					width: width,
					height: height,
					devicePixelRatio: dpr // 新加入
				});
				canvas.setChart(chart);

				const option = {
					tooltip: {
						trigger: 'item'
					},
					legend: {
						top: '5%',
						left: 'center'
					},
					series: [{
						name: 'Access From',
						type: 'pie',
						radius: ['40%', '70%'],
						avoidLabelOverlap: false,
						itemStyle: {
							borderRadius: 10,
							borderColor: '#fff',
							borderWidth: 2
						},
						label: {
							show: false,
							position: 'center'
						},
						emphasis: {
							label: {
								show: true,
								fontSize: 40,
								fontWeight: 'bold'
							}
						},
						labelLine: {
							show: false
						},
						data: [{
								value: 1048,
								name: 'Search Engine'
							},
							{
								value: 735,
								name: 'Direct'
							},
							{
								value: 580,
								name: 'Email'
							},
							{
								value: 484,
								name: 'Union Ads'
							},
							{
								value: 300,
								name: 'Video Ads'
							}
						]
					}]
				};

				chart.setOption(option);
				return chart;
			}
		},
		mounted() {
			const ecComponent = this.selectComponent('#mychart-bar');
			console.log(ecComponent);
			// this.initChart(ecComponent,100,100,1)
			console.log(this.ec);
		},
		created(){
			this.getData()
		},
		onShareAppMessage() {
		    return {
		      title: 'ECharts 可以在微信小程序中使用啦！',
		      path: '/pages/index' // 确保这是你的页面路径
		    };
		  }
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
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
</style>