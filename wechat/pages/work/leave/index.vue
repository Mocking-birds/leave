<template>
	<view>

		<uni-list :border="true">
			<uni-list-item v-for="(item,index) in askForLeaveList" :key="index" :showArrow="true" :avatar="false"
				clickable :title="setTitle(item)" :note="setNote(item)" @click="fabClick(item.leaveId)" />
		</uni-list>

		<!-- 分页器 -->
		<uni-pagination :total="total" :pageSize="params.pageSize" :show-icon="true" @change="changePage" />
	</view>
</template>

<script>
	import {
		listPermit,
		listPermitByDept,
		listPermitByUserId
	} from '@/api/leave/ask_for_leave.js'
	import user from '@/store/modules/user.js'

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

				// 请假列表
				askForLeaveList: [],

				//假条总数
				total: 5
			}
		},
		methods: {
			//获取数据
			async getData() {
				if (user.state.roles[0] == 'admin') {
					// 超级管理员
					const res = await listPermit({
						pageNum: this.params.pageNum,
						pageSize: this.params.pageSize
					})
					console.log(res);
					this.askForLeaveList = res.rows
					this.total = res.total
				} else if (user.state.roles[0] == 'director') {
					// 系主任
					// console.log(this);
					const res = await listPermitByDept({pageNum: this.params.pageNum, pageSize: this.params.pageSize},user.state.dept.deptId,'')
					console.log(res);
					this.askForLeaveList = res.rows
					this.total = res.total
				}else if (user.state.roles[0] == 'counsellor'){
					// 辅导员
					const res = await listPermitByDept({pageNum: this.params.pageNum, pageSize: this.params.pageSize},user.state.dept.parentId,'')
					console.log(res);
					this.askForLeaveList = res.rows
					this.total = res.total
				}
			},

			//定义note
			setNote(item) {
				if (item.permitType == '0') {
					return '病假' + item.startTime + '  至  ' + item.endTime
				} else {
					return '事假' + item.startTime + '  至  ' + item.endTime
				}
			},

			// 悬浮按钮点击事件
			fabClick(index) {
				console.log(index);
				if (index) {
					uni.navigateTo({
						url: `/pages/work/ask_for_leave/index?id=${index}&type=假条管理&role=${user.state.roles[0]}`
					})
				}
			},

			//切换页码
			changePage(e) {
				this.params.pageNum = e.current
				this.getData()
			},

			// 设置title
			setTitle(item) {

				console.log('title');
				console.log(item);

				let first = ''
				let second = ''

				if (item.isBack == '0') {
					first = '未销假'
				} else if (item.isBack == '1') {
					first = '已销假'
				} else if (item.isBack == '2') {
					first = '申请销假'
				}

				if (item.leaveStatus == '0') {
					second = '未审批'
				} else if (item.leaveStatus == '1') {
					second = '已同意'
				} else if (item.leaveStatus == '2') {
					second = '已驳回'
				}

				return first + ' —— 请假' + second
			}
		},
		async created() {
			this.getData()
		}
	}
</script>

<style>
	uni-list-item {
		padding: 10px 0;
	}

	.uni-pagination {
		background-color: #fff !important;
		height: 65px;
	}

	.uni-pagination__btn {
		margin: 0 20px
	}
</style>