<template>
	<view>
		<uni-list :border="true">
			<uni-list-item v-for="(item,index) in postList" :key="index" :avatar="false" clickable
				:title="item.postName" :note="item.postCode">
				<template v-slot:footer>
					<view class="item-button" style="display:flex; justify-content: center;align-items: center;">
						<button size="mini" type="primary" @click="fabClick(item)">修改</button>
						<button size="mini" type="warn" @click="deletePost(item.postId)">删除</button>
					</view>
				</template>
			</uni-list-item>
		</uni-list>

		<uni-popup ref='postPopup' type='center' background-color="#fff">
			<view style="max-height: 350px; overflow: scroll;">
				<view class='title'>
					{{submitSign}}岗位
				</view>
				<uni-forms :modelValue='postForm'>
					<uni-forms-item label="岗位名称">
						<uni-easyinput v-model="postForm.postName"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="岗位编码">
						<uni-easyinput v-model="postForm.postCode"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="岗位顺序">
						<uni-number-box v-model="postForm.postSort" />
					</uni-forms-item>
					<uni-forms-item label="岗位状态">
						<uni-data-checkbox v-model="postForm.status" :localdata="statusList"></uni-data-checkbox>
					</uni-forms-item>
					<uni-forms-item label="备注">
						<uni-easyinput v-model="postForm.remark"></uni-easyinput>
					</uni-forms-item>
				</uni-forms>
				<view class="buttons">
					<button type="primary" @click="submitPost">确认</button>
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
		getPostList,
		addPost,
		updatePost,
		delPost
	} from '@/api/system/post'

	export default {
		data() {
			return {
				pageNum: 1,
				pageSize: 10,

				//悬浮按钮样式
				pattern: {
					color: '#7A7E83',
					backgroundColor: '#fff',
					selectedColor: '#007AFF',
					buttonColor: '#007AFF',
					iconColor: '#fff'
				},

				//修改添加标识
				submitSign: '',
				
				//弹出框类型
				msgType: 'success',
				
				//弹出框信息
				messageText:'',

				//岗位表单
				postForm: {},

				//岗位列表
				postList: [],

				//状态列表
				statusList: [{
						value: "0",
						text: "正常"
					},
					{
						value: "1",
						text: "停用"
					}
				]
			}
		},
		methods: {
			//获取基本数据
			async getData() {
				const res = await getPostList({
					pageNum: this.pageNum,
					pageSize: this.pageSize
				})
				console.log(res);

				this.postList = res.rows
			},

			// 悬浮按钮点击事件
			fabClick(scope) {
				if (scope) {
					this.submitSign = '修改'
					this.postForm = scope
				} else {
					this.submitSign = '添加'
					this.postForm = {
						postName: '',
						postCode: '',
						postSort: 0,
						status: '0',
						remark: ''
					}
				}

				this.$refs.postPopup.open()
			},

			//关闭popup
			closePopup() {
				this.$refs.postPopup.close()
			},

			// 增/改
			async submitPost() {
				console.log("add");
				console.log(this.submitSign);
				if (this.submitSign === '添加') {
					const res = await addPost(this.postForm)
					console.log(res);
					this.messageText = '添加成功'
					this.$refs.message.open()

				} else if (this.submitSign == '修改') {
					const res = await updateNotice(this.postForm)
					this.messageText = '修改成功'
					this.$refs.message.open()
				}

				this.getData()
				this.$refs.postPopup.close()
			},
			
			//删
			async deletePost(id){
				const res = await delPost(id)
				this.messageText = '删除成功'
				this.$refs.message.open()
				console.log(res);
				this.getData()
			}
		},
		created() {
			this.getData()
		}
	}
</script>

<style>
	uni-list-item {
		padding: 5px 0;
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