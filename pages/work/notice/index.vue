<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);position: relative;" scroll-y="true"
		scroll-top="0" enable-flex="true" lower-threshold="50">
		<view class="header-section">
		</view>
		<view class="notice-box">
			<view class="notice-list" v-for="(item,index) in allNoticeList" :key="index">
				<uni-section :title="getNoticeType(item)" type="circle"></uni-section>
				<view class="content">
					{{item.noticeContent}}
				</view>
				<view class="right">
					<uni-icons size="30" type="compose" class="edit" @click="fabClick(item)"></uni-icons>
					<uni-icons size="30" type="trash" class="delete" @click="deletePost(item.postId)"></uni-icons>
				</view>
			</view>
		</view>
		
		<uni-popup ref='noticePopup' type='center' background-color="#fff">
			<view style="max-height: 550px; overflow: scroll;">
				<view class='title'>
					{{submitSign}}公告
				</view>
				<uni-forms :modelValue='noticeForm'>
					<uni-forms-item label="公告标题">
						<uni-easyinput v-model="noticeForm.noticeTitle"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="公告类型">
						<uni-data-select v-model="noticeForm.noticeType" :localdata="noticeTypeList"
							:clear="false"></uni-data-select>
					</uni-forms-item>
					<uni-forms-item label="状态">
						<uni-data-checkbox v-model="noticeForm.status" :localdata="statusList"></uni-data-checkbox>
					</uni-forms-item>
					<uni-forms-item label="内容">
						<uni-easyinput v-model="noticeForm.noticeContent"></uni-easyinput>
					</uni-forms-item>
				</uni-forms>
				<view class="buttons">
					<button type="primary" @click="submitNotice">确认</button>
					<button @click="closePopup">取消</button>
				</view>
			</view>
		</uni-popup>
		
		<uni-popup ref="message" type="message">
			<uni-popup-message :type="msgType" :message="messageText" :duration="2000"></uni-popup-message>
		</uni-popup>
		
		<uni-fab ref="fab" :pattern="pattern" :horizontal="left" :vertical="bottom" :direction="horizontal"
			@fabClick="fabClick()" />
	</scroll-view>
	
	<!-- <view style="height: 100vh;">
		<uni-segmented-control :current="0" :values="items" style-type="button"
							 active-color="#007aff" @clickItem="onClickItem" />
							
		<uni-list :border="true">
			<uni-list-item v-for="(item,index) in noticeTab()" :key="index" :avatar="false" clickable
				:title="item.noticeContent" :note="item.noticeTitle">
				<template v-slot:footer>
					<view class="item-button" style="display:flex; justify-content: center;align-items: center;">
						<button size="mini" type="primary" @click="fabClick(item)">修改</button>
						<button size="mini" type="warn" @click="deleteNotice(item.noticeId)">删除</button>
					</view>
				</template>
			</uni-list-item>
		</uni-list>

		
	</view> -->
</template>

<script>
	import {
		getNoticeList,
		delNotice,
		addNotice,
		updateNotice
	} from '@/api/system/notice'

	import {
		getDicts
	} from '@/api/system/dict/data'

	export default {
		data() {
			return {
				pageNum: 1,
				pageSize: 10,

				//全部公告列表
				allNoticeList: [],
				
				//通知列表
				notifyList:[],
				
				//公告列表
				noticeList:[],

				//悬浮按钮样式
				pattern: {
					color: '#7A7E83',
					backgroundColor: '#fff',
					selectedColor: '#007AFF',
					buttonColor: '#007AFF',
					iconColor: '#fff'
				},

				// 公告表单
				noticeForm: {
					noticeTitle: '',
					noticeContent: '',
					status: "0",
					noticeType: '1'
				},

				// 公告类型列表
				noticeTypeList: [{
					value: '1',
					text: "通知"
				},{
					value: '2',
					text: "公告"
				}],

				// 状态列表
				statusList: [{
						value: '0',
						text: "正常"
					},
					{
						value: '1',
						text: "关闭"
					}
				],
				
				//弹出框类型
				msgType: 'success',
				
				//弹出框信息
				messageText:'',
				
				//修改添加标识
				submitSign:''
			}
		},
		methods: {
			// 获取数据
			async getNotice() {
				const res = await getNoticeList({
					pageNum: this.pageNum,
					pageSize: this.pageSize
				})
				this.allNoticeList = res.rows
				console.log(this.allNoticeList);
			},

			// 悬浮按钮点击事件
			fabClick(scope) {
				console.log(scope);
				if (scope) {
					this.submitSign = '修改'
					this.noticeForm = scope
				} else {
					this.submitSign = '添加'
					this.noticeForm = {
						noticeTitle: '',
						noticeContent: '',
						status: "0",
						noticeType: '0'
					}
				}
				this.$refs.noticePopup.open()
			},

			// 关闭popup
			closePopup() {
				this.$refs.noticePopup.close()
			},

			//删除公告
			async deleteNotice(id) {
				console.log(id);
				const res = await delNotice(id)
				this.messageText = '删除成功'
				this.$refs.message.open()
				console.log(res);
				this.getNotice()
			},

			//添加修改公告提交
			async submitNotice() {
				if(this.submitSign === '添加'){
					const res = await addNotice(this.noticeForm)
					this.messageText = '添加成功'
					this.$refs.message.open()
					
				}else if (this.submitSign == '修改'){
					const res = await updateNotice(this.noticeForm)
					this.messageText = '修改成功'
					this.$refs.message.open()
				}
				
				this.getNotice()
				this.$refs.noticePopup.close()
			},
			
			// 返回公告类型
			getNoticeType(item){
				if(item.noticeType == 1){
					return '通知：'+item.noticeTitle
				}else if(item.noticeType == 2){
					return '公告：'+item.noticeTitle
				}
			}
		},
		created() {
			this.getNotice()
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
	
	.notice-box{
		width: 90%;
		position: absolute;
		top: 50px;
		left: 0;
		right: 0;
		margin: 0 auto;
		
		.notice-list{
			width: 100%;
			border: 1px solid #fff;
			border-radius: 15px;
			background-color: #fff;
			padding: 25px 5px;
			margin-bottom: 20px;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: space-between;
			position: relative;
			
			uni-section{
				width: 90%;
				
				.uni-section-header__decoration {
					background-color: #f4a042 !important;
				}
				
				.uni-section__content-title {
					font-size: 18px !important;
					font-weight: 600;
				}
			}
			
			.content{
				width: 80%;
				color: #9e9e9e;
			}
			
		}
		
		.right{
			position: absolute;
			right: 25px;
			top: 70px;
			display: flex;
			justify-content: center;
			align-items: center;
			
			.edit{
				padding-right: 10px;
				
				.uniui-compose{
					color:#86CDEA !important;
				}
			}
			
			.delete{
				.uniui-trash{
					color: red !important;
				}
			}
		}
	}
	
	.uni-popup__wrapper{
		border-radius: 15px;
		width: 350px;
	}

	.item-button button {
		margin: 0 5px;
	}

	.buttons button {
		width: 200px;
		margin: 15px auto;
	}

	.uni-fab__circle {
		bottom: 10px;
		left: 50%;
		transform: translate(-50%, -50%);
	}

	.title {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 50px;
	}

	.uni-forms-item__label {
		width: 75px !important;
		padding-left: 5px !important;
	}

	.uni-forms-item {
		padding: 0 15px;
	}
	
</style>