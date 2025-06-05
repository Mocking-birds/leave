<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);position: relative;" scroll-y="true"
		scroll-top="0" enable-flex="true" lower-threshold="50">
		<view class="header-section">
		</view>
		<!-- <uni-list :border="true">
			<uni-list-item v-for="(item,index) in roleList" :key="index" :avatar="false" clickable
				:title="item.roleName" :note="item.roleKey">
				<template v-slot:footer>
					<view class="item-button" style="display:flex; justify-content: center;align-items: center;">
						<button size="mini" type="primary" @click="fabClick(item)">修改</button>
						<button size="mini" type="warn" @click="deleteRole(item.roleId)">删除</button>
					</view>
				</template>
			</uni-list-item>
		</uni-list> -->
		
		<view class="role-box">
			<view class="role-list" v-for="(item,index) in roleList" :key="index">
				<uni-section :title="`角色名称：${item.roleName}`" type="circle"></uni-section>
				<view class="roleKey">
					{{item.roleKey}}
				</view>
				<view class="right">
					<uni-icons size="30" type="compose" class="edit" @click="fabClick(item)"></uni-icons>
					<uni-icons size="30" type="trash" class="delete" @click="deleteRole(item.roleId)"></uni-icons>
				</view>
			</view>
		</view>
		
		<uni-popup ref='rolePopup' type='center' background-color="#fff">
			<view style="max-height: 550px; overflow: scroll;">
				<view class='title'>
					{{submitSign}}角色
				</view>
				<uni-forms :modelValue='roleForm'>
					<uni-forms-item label="角色名称">
						<uni-easyinput v-model="roleForm.roleName"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="权限字符">
						<uni-easyinput v-model="roleForm.roleKey"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="角色顺序">
						<uni-number-box v-model="roleForm.roleSort" />
					</uni-forms-item>
					<uni-forms-item label="角色状态">
						<uni-data-checkbox v-model="roleForm.status" :localdata="statusList"></uni-data-checkbox>
					</uni-forms-item>
					<uni-forms-item label="菜单权限">
						<view class="permission">
							请前往PC端进行修改
						</view>
					</uni-forms-item>
					<uni-forms-item label="备注">
						<uni-easyinput v-model="roleForm.remark"></uni-easyinput>
					</uni-forms-item>
				</uni-forms>
				<view class="buttons">
					<button type="primary" @click="submitRole">确认</button>
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
</template>

<script>
	import {
		getRoleList,
		addRole,
		updateRole,
		delRole
	} from '@/api/system/role'

	export default {
		data() {
			return {
				// 角色列表
				roleList: [],

				// 角色表单
				roleForm: [],

				//修改添加标识
				submitSign: '',

				//弹出框类型
				msgType: 'success',

				//弹出框信息
				messageText: '',

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
			// 获取基本数据
			async getData() {
				const res = await getRoleList()
				console.log(res);

				this.roleList = res.rows
			},
			// 悬浮按钮点击事件
			fabClick(scope) {
				if (scope) {
					this.submitSign = '修改'
					this.roleForm = scope
				} else {
					this.submitSign = '添加'
					this.roleForm = {
						roleName: '',
						roleKey: '',
						roleSort: 0,
						status: '0',
						remark: '',
						menuIds: []
					}
				}
				this.$refs.rolePopup.open()
			},
			//关闭popup
			closePopup() {
				this.$refs.rolePopup.close()
			},
			// 增/改
			async submitRole() {
				console.log(this.submitSign);
				if (this.submitSign === '添加') {
					const res = await addRole(this.roleForm)
					console.log(res);
					this.messageText = '添加成功'
					this.$refs.message.open()

				} else if (this.submitSign == '修改') {
					this.roleForm.menuIds = []
					const res = await updateRole(this.roleForm)
					this.messageText = '修改成功'
					this.$refs.message.open()
				}

				this.getData()
				this.$refs.rolePopup.close()
			},
			
			//删
			async deleteRole(id){
				const res = await delRole(id)
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

<style lang="scss">
	
	.header-section {
		width: 100%;
		height: 200px;
		background-color: #261c3f;
		border-bottom-left-radius: 50px 10px;
		border-bottom-right-radius: 50px 10px;
	}
	
	.role-box{
		width: 90%;
		position: absolute;
		top: 50px;
		left: 0;
		right: 0;
		margin: 0 auto;
		
		.role-list{
			width: 100%;
			border: 1px solid #fff;
			border-radius: 15px;
			background-color: #fff;
			padding: 25px 10px;
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
			
			.roleKey{
				width: 80%;
				color: #9e9e9e;
			}
			
			.right{
				position: absolute;
				right: 25px;
				top: 50px;
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

	.permission {
		width: 100%;
		height: 100%;
		display: flex;
		align-items: center;
	}
	
	.uni-popup__wrapper{
		border-radius: 15px;
		width: 350px;
	}
</style>