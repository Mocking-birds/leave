<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);" scroll-y="true" scroll-top="0"
		enable-flex="true" lower-threshold="50" @scrolltolower="scrolltolower">
		<view class="header-section">
		</view>
		<view class="all-content">
			<view class="content-section" v-for="(item,index) in userList" :key="index">
				<view class="username card">
					姓名：{{item.userName}}
				</view>
				<view class="dept card">
					系部：{{item.dept.deptName}}
				</view>
				<view class="role card">
					职位：{{item.roles[0].roleName}}
				</view>
				<view class="phonenumber card">
					电话号码：{{item.phonenumber}}
				</view>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	import {
		getListUser
	} from '@/api/system/user.js'

	export default {
		data() {
			return {
				// 用户信息列表
				userList: [],
				userParams: {
					pageNum: 1,
					pageSize: 5
				},
				// 总数
				total: 0
			}
		},
		async created() {
			const res = await getListUser()
			console.log(res);
			this.userList = res.rows
			this.total = res.total
		},
		methods: {
			async scrolltolower() {
				console.log('123');

				let permitRes = null

				this.userParams.pageNum += 1
				permitRes = await getListUser(this.userParams)

				if ((this.userParams.pageNum - 1) * this.userParams.pageSize > this.total) {
					uni.showToast({
						title: "已经没有咯~~",
						icon: "error"
					})
				} else {
					uni.showLoading({
						title: "加载中",
						success: () => {
							setTimeout(() => {
								uni.hideLoading()
								permitRes.rows.forEach(item => {
									this.userList.push(item)
								})
							}, 2000)
						}
					})
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f5f6f7;
	}

	.header-section {
		width: 100%;
		height: 200px;
		background-color: #261c3f;
		border-bottom-left-radius: 50px 10px;
		border-bottom-right-radius: 50px 10px;
	}

	.all-content {
		margin-top: -170px;

		.content-section {
			margin: 15px 15px;
			padding: 20px 0px;
			border-radius: 8px;
			background-color: white;
			display: flex;
			flex-direction: column;
			align-items: center;

			.card {
				width: 90%;
				border: 1px solid #fff;
				border-radius: 15px;
				background-color: #fff;
				margin: 5px;
			}
			
			.username{
				font-size: 18px;
				font-weight: 900;
			}
		}
	}
</style>