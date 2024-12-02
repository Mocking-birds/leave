<template>
	<scroll-view
		style="height:100vh;background-color: rgb(241,245,249);"
		scroll-y="true" 
		scroll-top= "0"
		enable-flex = "true"
		lower-threshold="50"
		@scrolltolower="scrolltolower" 
		>
		<view class="header-section">
		</view>
		<view class="work">
			<view class="card">
				<uni-grid :column="4" :showBorder="false" @change="changeGrid">
					<uni-grid-item v-for="(item,index) in gridList" :key="index" :index='index'>
						<view class="grid-item-box">
							<uni-icons :type="item.meta.icon" size="30"></uni-icons>
							<text class="text">{{item.meta.title}}</text>
						</view>
					</uni-grid-item>
				</uni-grid>
			</view>
		</view>

		<view class="title">
			<uni-section class="mb-10" :title="title" sub-title="BackLog"></uni-section>
		</view>

		<view class="content">
			<view class="card leave_card" v-for="(item,index) in permitList" :key="index">
				<view class="user_name_create_time">
					<uni-section class="mb-10" :title="setChartTitle(item.user.nickName)" type="circle"></uni-section>
					<view class="create_time">
						{{item.permitTime}}
					</view>
				</view>
				<view class="leave_type">
					请假类别：{{item.permitType == '0'?'病假':'事假'}}
				</view>
				<view class="leave_dept">
					专业：{{item.user.dept.deptName}}
				</view>
				<view class="ask_for_time">
					<uni-icons type="calendar-filled" size="20"></uni-icons>
					<view class="time_hour">
						<view class="time">
							请假时间：{{formatTime(item.startTime)}} - {{formatTime(item.endTime)}}
						</view>
						<view class="hour">
							{{item.permitDays}}天
						</view>
					</view>
				</view>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	import {
		getRouters
	} from '@/api/system/router'
	import {
		getInfo
	} from '@/api/system/user.js'
	import {
		listPermit
	} from '@/api/leave/ask_for_leave.js'

	export default {
		data() {
			return {
				current: 0,
				swiperDotIndex: 0,
				data: [{
						image: '/static/images/banner/banner01.jpg'
					},
					{
						image: '/static/images/banner/banner02.jpg'
					},
					{
						image: '/static/images/banner/banner03.jpg'
					}
				],
				// 路由展示列表
				gridList: [],
				permitList: [],
				// 我的待办title
				title:'',
				permitQuery:{
					pageNum:1,
					pageSize:5
				}
			}
		},
		methods: {
			clickBannerItem(item) {
				// console.info(item)
			},
			changeSwiper(e) {
				this.current = e.detail.current
			},
			changeGrid(e) {
				if (this.gridList[e.detail.index].name == "Ask_for_leave") {
					uni.navigateTo({
						url: '/' + this.gridList[e.detail.index].component +
							`?type=请假&role=${this.userInfo.roles[0]}`
					})
				} else {
					uni.navigateTo({
						url: '/' + this.gridList[e.detail.index].component
					})
				}

				// if(e.detail.index == '0'){
				// 	uni.navigateTo({url:'/pages/work/user/index'})
				// }else if(e.detail.index == '1'){
				// 	uni.navigateTo({url:'/pages/work/role/index'})
				// }else if(e.detail.index == '2'){
				// 	uni.navigateTo({url:'/pages/work/dept/index'})
				// }else if(e.detail.index == '3'){
				// 	uni.navigateTo({url:'/pages/work/post/index'})
				// }else if(e.detail.index == '4'){
				// 	uni.navigateTo({url:'/pages/work/notice/index'})
				// }
				// this.$modal.showToast('模块建设中~')
			},
			// 获取权限路由
			async getGrid() {
				const res = await getRouters()
				res.data.forEach(item => {
					if (item.name == 'Work') {
						this.gridList = item.children
						if (item.name == "Ask_for_leave") {
							this.gridList.splice(index, 1)
						}
						if (item.name == "Back_for_leave") {
							this.gridList.splice(index, 1)
						}
					}
				})
				//  	console.log(res);
				console.log(this.gridList);
			},
			// 获取基础数据
			async getData() {
				const permitRes = await listPermit(this.permitQuery)
				console.log(permitRes);
				this.permitList = permitRes.rows
				this.title = "我的待办（" + permitRes.total + '）'
			},
			// 滚动到底部后继续渲染直到没有数据为止
			async scrolltolower() {
				console.log('底部');
				this.permitQuery.pageNum += 1
				const permitRes = await listPermit(this.permitQuery)
				if((this.permitQuery.pageNum-1) * this.permitQuery.pageSize > permitRes.total){
					uni.showToast({
						title:"已经没有咯~~",
						icon: "error"
					})
				}else{
					uni.showLoading({
						title:"加载中",
						success:()=> {
							setTimeout(()=>{
								uni.hideLoading()
								permitRes.rows.forEach(item => {
									this.permitList.push(item)
								})
							},2000)
						}
					})
				}
				
			},
			// 格式化时间
			formatTime(time){
				console.log('格式化');
				const date = time.split(" ")
				console.log(date);
				return date[0].replace(/-/g, "/")
			},
			// 格式化cartTitle
			setChartTitle(name){
				return "申请人：" + name
			}
		},
		async created() {
			const res = await getInfo()
			this.userInfo = res
			console.log('userinfo');
			console.log(this.userInfo);

			this.getGrid()
			this.getData()
		}
	}
</script>

<style lang="scss">
	.header-section {
		width: 100%;
		height: 200px;
		background-color: rebeccapurple;
		border-bottom-left-radius: 50px 10px;
		border-bottom-right-radius: 50px 10px;
	}

	.work {
		width: 100%;
		height: 200px;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-top: -150px;
	}

	.card {
		width: 90%;
		border: 1px solid #fff;
		border-radius: 15px;
		background-color: #fff;
	}

	.uni-grid-item__box {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.grid-item-box {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	.title {
		width: 100%;
		margin-top: 25px;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	
	.uni-section-header__content-sub{
		font-size: 15px !important;
	}
	
	.uni-section__content-title{
		font-size: 18px !important;
		font-weight: 900;
	}

	.title uni-section {
		width: 90%;
	}

	.content {
		width: 100%;
		// height: 800px;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	.leave_card {
		padding: 30px 25px;
		margin-bottom: 20px;
	}

	.user_name_create_time .uni-section-header {
		padding: 0 !important;
	}

	.leave_type,
	.leave_dept,
	.ask_for_time {
		margin: 12px 0;
	}

	.user_name_create_time {
		margin: 0 !important;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.leave_dept,
	.leave_type {
		padding-left: 11px;
	}

	.ask_for_time {
		padding: 5px;
		border: 1px solid #e6e6e6;
		border-radius: 18px;
		display: flex;
		align-items: center;
	}

	.time_hour {
		display: flex;
		align-items: center;
		justify-content: space-between;
		flex: 1;
		padding: 0 7px;
	}
</style>