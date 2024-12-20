<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);" scroll-y="true" scroll-top="0"
		enable-flex="true" lower-threshold="50" @scrolltolower="scrolltolower">
		<view class="header-section">
		</view>
		<view class="content">
			<view class="notice-content" v-for="(item,index) in noticeList" :key="index">
				<uni-section class="mb-10" :title="setTitle(item.noticeContent)" type="line"></uni-section>
				<view class="create_time">
					{{item.createTime}}
				</view>
			</view>
		</view>
	</scroll-view>
</template>

<script>
import { getNoticeList } from '@/api/system/notice'
	export default{
		data(){
			return{
				// 通知请求参数
				noticeParams:{
					pageNum: 1,
					pageSize: 10
				},
				// 通知列表
				noticeList:[],
				// 总数
				total: 0
			}
		},
		created() {
			this.getData()
		},
		methods:{
			// 获取数据
			async getData(){
				const res = await getNoticeList(this.noticeParams)
				console.log(res);
				this.noticeList = res.rows
				this.total = res.total
			},
			// 设置title
			setTitle(title){
				return "通知：" + title
			},
			// 滚动
			async scrolltolower(){
				let permitRes = null
				
				this.noticeParams.pageNum += 1
				permitRes = await getNoticeList(this.noticeParams)
				
				if ((this.noticeParams.pageNum - 1) * this.noticeParams.pageSize > this.total) {
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
									this.noticeList.push(item)
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
	
	.content{
		margin-top: -150px;
		
		.notice-content{
			margin: 15px 15px;
			padding: 20px 0px;
			border-radius: 8px;
			background-color: white;
			display: flex;
			align-items: center;
			justify-content: space-between;
			
			.create_time{
				margin-right: 15px;
			}
		}
	}
</style>