<template>
	<view>

		<uni-list :border="true">
			<uni-list-item v-for="(item,index) in backForLeaveList" :key="index" :showArrow="true" :avatar="false" clickable
				:title="item.isBack == '0'?'未销假':'已销假'" :note="setNote(item)" @click="fabClick(item.leaveId)"/>
		</uni-list>

		<!-- 分页器 -->
		<uni-pagination :total="total" :pageSize="pageSize" :show-icon="true" @change="changePage"/>
	</view>
</template>

<script>
	import {listBackPermit} from '@/api/leave/back_for_leave.js'
	import {getInfo} from '@/api/system/user.js'
	import storage from '@/utils/storage'
	import constant from '@/utils/constant'
	
	export default{
		data(){
			return{
				pageNum: 1,
				pageSize: 5,
				
				// 销假列表
				backForLeaveList:[],
				
				//用户详细信息
				userInfo: {},
				
				//假条总数
				total: 5
			}
		},
		methods:{
			//获取基本数据
			async getData(id){
				const res = await listBackPermit({
					pageNum: this.pageNum,
					pageSize: this.pageSize
				},id,'0');
				console.log(res);
				
				this.backForLeaveList = res.rows
				this.total = res.total
			},
			
			//定义note
			setNote(item){
				if(item.permitType == '0'){
					return '病假' + item.startTime + '  至  ' + item.endTime
				}else{
					return '事假' + item.startTime + '  至  ' + item.endTime
				}
			},
			
			//切换页码
			changePage(e){
				console.log(e);
				this.pageNum = e.current
				this.getData(this.userInfo.user.userId)
			},
			
			// 悬浮按钮点击事件
			fabClick(index){
				console.log(index);
				if(index){
					uni.navigateTo({
						url: `/pages/work/ask_for_leave/index?id=${index}&type=销假&role=${this.userInfo.roles[0]}`
					})
				}
			}
		},
		async created() {
			const res = await getInfo()
			this.userInfo = res
			console.log(this.userInfo);
			
			this.getData(this.userInfo.user.userId)
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