<template>
	<view>
		<uni-list :border="true">
			<uni-list-item v-for="(item,index) in askForLeaveRequestList" :key="index" :showArrow="true" :avatar="false" clickable
				:title="setTitle(item)" :note="setNote(item)" @click="fabClick(item.leaveId)"/>
		</uni-list>

		<!-- 分页器 -->
		<uni-pagination :total="total" :pageSize="params.pageSize" :show-icon="true" @change="changePage"/>
	</view>
</template>

<script>
	import user from '@/store/modules/user.js'
	import {
		listPermitByDept
	} from '@/api/leave/ask_for_leave.js'

	export default {
		data() {
			return {
				// 请求参数
				params: {
					pageNum: 1,
					pageSize: 5,
					permitType: null,
					isBack: null,
					leaveStatus: 0,
				},
				// 请假申请数据
				askForLeaveRequestList:[],
				// 假条总数
				total: 5
			}
		},
		created() {
			console.log(user);
			this.getData()
		},
		methods: {
			// 获取基本数据
			async getData() {
				if (user.state.roles[0] == 'admin') {
					// 超级管理员
					const res = await listPermitByDept(this.params, '', '')
					console.log(res);
					this.askForLeaveRequestList = res.rows
					this.total = res.total
				}else if(user.state.roles[0] == 'director'){
					// 主任
					const res = await listPermitByDept(this.params,user.state.dept.deptId,true)
					console.log(res);
					this.askForLeaveRequestList = res.rows
					this.total = res.total
				}else if(user.state.roles[0] == 'counsellor'){
					// 辅导员
					const res = await listPermitByDept(this.params,user.state.dept.parentId,false)
					console.log(res);
					this.askForLeaveRequestList = res.rows
					this.total = res.total
				}
			},
			//定义note
			setNote(item){
				if(item.permitType == '0'){
					return '病假' + item.startTime + '  至  ' + item.endTime
				}else{
					return '事假' + item.startTime + '  至  ' + item.endTime
				}
			},
			// 设置title
			setTitle(item){
				
				console.log('title');
				
				let first = ''
				let second = ''
				
				first = item.user.dept.parentName + '--' + item.user.dept.deptName + '--' + item.user.nickName
				
				if(item.leaveStatus == '0'){
					second = '未审批'
				}else if(item.leaveStatus == '1'){
					second = '已同意'
				}else if(item.leaveStatus == '2'){
					second = '已驳回'
				}
				
				return first + ' —— 请假' + second
			},
			// 切换页码
			changePage(e){
				this.params.pageNum = e.current
				this.getData()
			},
			// 悬浮按钮点击事件
			fabClick(index){
				console.log(index);
				console.log('userinfo');
				console.log(this.userInfo);
				if(index){
					uni.navigateTo({
						url: `/pages/work/ask_for_leave/index?id=${index}&type=请假申请&role=${user.state.roles[0]}`
					})
				}
			}
		}
	}
</script>

<style>
	uni-list-item {
		padding: 10px 0;
	}
	.uni-pagination{
		background-color: #fff !important;
		height: 65px;
	}
	.uni-pagination__btn{
		margin:0 20px
	}
</style>