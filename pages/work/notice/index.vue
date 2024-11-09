<template>
	<view style="height: 100vh;">
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

		<uni-popup ref='noticePopup' type='center' background-color="#fff">
			<view style="max-height: 350px; overflow: scroll;">
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
	</view>
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
				noticeTypeList: [],

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
				submitSign:'',
				
				// 分段器
				items:["全部"],
				
				//公告index
				noticeIndex: 0
			}
		},
		methods: {
			// 获取数据
			async getNotice() {
				const res = await getNoticeList({
					pageNum: this.pageNum,
					pageSize: this.pageSize
				})
				
				//全部公告列表
				this.allNoticeList = res.rows
				
				//通知列表
				this.notifyList = this.allNoticeList.filter(item => item.noticeType == 2)
				
				//公告列表
				this.noticeList = this.allNoticeList.filter(item => item.noticeType == 1)
				
				// console.log(res);
				// console.log(a);
				console.log(this.allNoticeList);
				console.log(this.notifyList);
				console.log(this.noticeList);
			},

			//获取公告类型列表
			async getNoticeType() {
				const res = await getDicts("sys_notice_type")

				// noticeTypeList
				res.data.forEach((item, index) => {
					this.noticeTypeList[index] = {
						value: item.dictValue,
						text: item.dictLabel
					}
					this.items[index+1] = item.dictLabel
				})

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
			
			//根据分段器替换数组
			noticeTab(){
				if(this.noticeIndex == 0){
					return this.allNoticeList
				}else if(this.noticeIndex == 1){
					return this.notifyList
				}else if(this.noticeIndex == 2){
					return this.noticeList
				}
			},
			
			//点击分段器
			onClickItem(e){
				this.noticeIndex = e.currentIndex
				this.noticeTab()
				
			}
		},
		created() {
			this.getNotice()
			this.getNoticeType()
		}
	}
</script>

<style>
	uni-list-item {
		padding: 10px 0;
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
	
	/* .segmented-control__item segmented-control__item--button{
		background: #fff !important;
	} */
</style>