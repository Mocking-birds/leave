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
			<view class="card card_title" v-if="userInfo.roles[0] !== 'student'">
				<uni-grid :column="3" :showBorder="false" @change="changeGrid">
					<uni-grid-item v-for="(item,index) in gridList" :key="index" :index='index'>
						<view class="grid-item-box">
							<uni-icons :type="item.meta.icon" size="40"></uni-icons>
							<text class="text">{{item.meta.title}}</text>
						</view>
					</uni-grid-item>
				</uni-grid>
			</view>
			<view class="card card_student card_title" v-else>
				<view class="card_student_buttons">
					<button class="card_student_button" size="default" @click="request('请假')">请假申请</button>
					<button class="card_student_button" size="default" @click="request('销假')">销假申请</button>
				</view>
			</view>
		</view>

		<view class="title">
			<uni-section class="mb-10" :title="title" sub-title="BackLog"></uni-section>
		</view>

		<view class="content">
			<view class="card leave_card" v-for="(item,index) in permitList" :key="index" v-if="item.leaveStatus == '0' || item.isBack=='2'">
				<view class="leave_pass">
					审批中
				</view>
				<view class="user_name_create_time">
					<uni-section class="mb-10" :title="setChartTitle(item.user.nickName)" type="circle"></uni-section>
					<view class="create_time">
						{{item.permitTime}}
					</view>
				</view>
				<view class="leave_type">
					请假类别：{{item.permitType == '0'?'病假':'事假'}}
				</view>
				<view class="permit_leave_type">
					假条类别：{{item.leaveStatus == '0'?'请假':'销假'}}
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
		listPermitBacklog
	} from '@/api/leave/ask_for_leave.js'

	export default {
		data() {
			return {
				current: 0,
				swiperDotIndex: 0,
				total:0,
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
				permitBacklogQuery:{
					pageNum:1,
					pageSize:5,
					deptId: null,
					userId: null
				},
				userInfo: ''
			}
		},
		methods: {
			changeGrid(e) {
				console.log(e);
				console.log(this.gridList);
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
			},
			// 请销假
			request(type){
				uni.navigateTo({
					url: `/pages/work/request/index?type=${type}`
				})
			},
			// 获取权限路由
			async getGrid() {
				const res = await getRouters()
				res.data.forEach(item => {
					if (item.name == 'Work') {
						item.children.forEach((item1,index) => {
							if (item1.name != "Ask_for_leave" && item1.name != "Back_for_leave") {
								this.gridList.push(item1)
							}
						})
					}
				})
			},
			// 获取基础数据
			async getData() {
				if(this.userInfo.roles[0] == 'admin'){
					// 超级管理员
					this.permitBacklogQuery.deptId = null
					const permitRes = await listPermitBacklog(this.permitBacklogQuery)
					console.log(permitRes);
					this.permitList = permitRes.rows
					this.total = permitRes.total
					this.title = "我的待办（" + this.total + '）'
				}else if(this.userInfo.roles[0] == 'director'){
					// 系主任
					
					this.permitBacklogQuery.deptId = this.userInfo.user.deptId
					const permitRes = await listPermitBacklog(this.permitBacklogQuery)
					console.log('系主任');
					console.log(permitRes);
					this.permitList = permitRes.rows
					this.total = permitRes.total
					this.title = "我的待办（" + this.total + '）'
				}else if(this.userInfo.roles[0] == 'counsellor'){
					// 辅导员
					
					this.permitBacklogQuery.deptId = this.userInfo.user.dept.parentId
					const permitRes = await listPermitBacklog(this.permitBacklogQuery)
					console.log(permitRes);
					this.permitList = permitRes.rows
					this.total = permitRes.total
					this.title = "我的待办（" + this.total + '）'
				}else if(this.userInfo.roles[0] == 'student'){
					// 学生
					
					this.permitBacklogQuery.userId = this.userInfo.user.userId
					const permitRes = await listPermitBacklog(this.permitBacklogQuery)
					console.log(permitRes);
					this.permitList = permitRes.rows
					this.total = permitRes.total
					this.title = "我的待办（" + this.total + '）'
				}
				
			},
			// 滚动到底部后继续渲染直到没有数据为止
			async scrolltolower() {
				console.log('底部');
				
				let permitRes = null
				
				this.permitBacklogQuery.pageNum += 1
				permitRes = await listPermitBacklog(this.permitBacklogQuery)
				
				if((this.permitBacklogQuery.pageNum-1) * this.permitBacklogQuery.pageSize > this.total){
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
				// console.log('格式化');
				const date = time.split(" ")
				// console.log(date);
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
		background-color: #261c3f;
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
	
	.card_title{
		height: 256px;
	}
	
	.card_student{
		position: relative;
	}
	
	.card_student_buttons{
		width: 50%;
		height: 100%;
		display: flex;
		flex-direction: column;
		justify-content: space-evenly;
		align-items: center;
		position: absolute;
		right: 0;
	}
	
	.card_student_button{
		width: 70%;
		padding:0 5px !important;
		border-radius: 25px !important;
		background-color: #5f4b8d !important;
		color:white;
		letter-spacing: 1px;
		font-weight: 600;
		font-size: 20px;
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
	
	.uni-section .uni-section-header__decoration{
		background-color: #f4a042 !important;
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
		position: relative;
	}
	
	.leave_pass{
		position: absolute;
		right:60px;
		top:42%;
		font-size: 25px;
		font-weight: 600;
		color:#ebe525
	}

	.user_name_create_time .uni-section-header {
		padding: 0 !important;
	}

	.leave_type,
	.leave_dept,
	.ask_for_time,
	.permit_leave_type{
		margin: 12px 0;
	}

	.user_name_create_time {
		margin: 0 !important;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.leave_dept,
	.leave_type,
	.permit_leave_type{
		padding-left: 11px;
	}
	
	.permit_leave_type{
		display: flex;
		justify-content: space-between;
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