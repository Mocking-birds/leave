<template>
	<view>

		<uni-list :border="true">
			<uni-list-item v-for="(item,index) in askForLeaveList" :key="index" :showArrow="true" :avatar="false" clickable
				:title="setTitle(item)" :note="setNote(item)" @click="fabClick(item.leaveId)"/>
		</uni-list>

		<!-- 分页器 -->
		<uni-pagination :total="total" :pageSize="pageSize" :show-icon="true" @change="changePage"/>
	</view>
</template>

<script>
	
	import {listPermit,listPermitByUserId} from '@/api/leave/ask_for_leave.js'
	import {getInfo} from '@/api/system/user.js'
	import storage from '@/utils/storage'
	import constant from '@/utils/constant'
	
	export default{
		data(){
			return{
				pageNum: 1,
				pageSize: 5,
				
				// 请假列表
				askForLeaveList:[],
				
				//用户详细信息
				userInfo: {},
				
				//假条总数
				total: 5
			}
		},
		methods:{
			//获取数据("管理员查询全部假条信息，学生只能查询自己的假条信息")
			async getData(id){
				if(id == 'ok'){
					const res = await listPermit({
						pageNum: this.pageNum,
						pageSize: this.pageSize
					})
					console.log('管理员');
					console.log(res);
					this.total = res.total
					this.askForLeaveList = res.rows

				}else{
					console.log('id:' + id);
					const res = await listPermitByUserId({
						pageNum: this.pageNum,
						pageSize: this.pageSize
					},id)
					console.log('普通用户');
					console.log(res);
					this.total = res.total
					this.askForLeaveList = res.rows
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
			
			// 悬浮按钮点击事件
			fabClick(index){
				console.log(index);
				console.log('userinfo');
				console.log(this.userInfo);
				if(index){
					uni.navigateTo({
						url: `/pages/work/ask_for_leave/index?id=${index}&type=假条管理&role=${this.userInfo.roles[0]}`
					})
				}
			},
			
			//切换页码
			changePage(e){
				console.log(e);
				this.pageNum = e.current
				if(storage.get(constant.roles)[0] != 'student'){
					this.getData('ok')
				}else{
					this.getData(this.userInfo.user.userId)
				}
			},
			
			// 设置title
			setTitle(item){
				
				console.log('title');
				console.log(this.userInfo);
				console.log(item);
				
				let first = ''
				let second = ''
				
				if(item.isBack == '0'){
					first = '未销假'
				}else if(item.isBack == '1'){
					first = '已销假'
				}else if(item.isBack == '2'){
					first = '申请销假'
				}
				
				if(item.leaveStatus == '0'){
					second = '未审批'
				}else if(item.leaveStatus == '1'){
					second = '已同意'
				}else if(item.leaveStatus == '2'){
					second = '已驳回'
				}
				
				return first + ' —— 请假' + second
			}
		},
		async created() {
			console.log(storage.get(constant.roles));
			console.log(storage.get(constant.roles)[0]);
			const res = await getInfo()
			this.userInfo = res
			console.log(res);
			if(storage.get(constant.roles)[0] != 'student'){
				this.getData('ok')
			}else{
				console.log(typeof this.userInfo.user.userId);
				console.log(this.userInfo.user.userId);
				this.getData(this.userInfo.user.userId)
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