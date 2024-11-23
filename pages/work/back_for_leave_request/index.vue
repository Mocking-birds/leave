<template>
	<view>
		<uni-list :border="true">
			<uni-list-item v-for="(item,index) in backForLeaveRequestList" :key="index" :showArrow="true" :avatar="false" clickable
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
	
	export default{
		data(){
			return {
				// 请求参数
				params: {
					pageNum: 1,
					pageSize: 5,
					permitType: null,
					isBack: 2,
					leaveStatus: 1,
				},
				// 销假申请数据
				backForLeaveRequestList: [],
				// 假条总数
				total: 5
			}
		},
		methods: {
			// 获取数据
			async getData(){
				if(user.state.roles[0] == 'admin'){
				        // 超级管理员
				        let res = await listPermitByDept(this.params,'','')
				        this.backForLeaveRequestList = res.rows
				        this.total = res.total
				        console.log(res)
				      }else if(user.state.roles[0] == 'director'){
				        // 系主任
				        let res = await listPermitByDept(this.params,user.state.dept.deptId,true)
				        this.backForLeaveRequestList = res.rows
				        this.total = res.total
				        console.log(res)
				      }else if(user.state.roles[0] == 'counsellor'){
				        // 辅导员
				        let res = await listPermitByDept(this.params,user.state.dept.parentId,false)
				        this.backForLeaveRequestList = res.rows
				        this.total = res.total
				        console.log(res)
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
				
				if(item.isBack == '0'){
					second = '未销假'
				}else if(item.isBack == '1'){
					second = '已销假'
				}else if(item.isBack == '2'){
					second = '申请销假'
				}
				
				return first + ' —— ' + second
			},
			// 切换页码
			changePage(e){
				this.params.pageNum = e.current
				this.getData()
			},
			// 悬浮按钮点击事件
			fabClick(index){
				console.log(index);
				if(index){
					uni.navigateTo({
						url: `/pages/work/ask_for_leave/index?id=${index}&type=销假申请&role=${user.state.roles[0]}`
					})
				}
			}
		},
		created(){
			this.getData()
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