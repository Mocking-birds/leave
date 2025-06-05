<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);position: relative;" scroll-y="true"
		scroll-top="0" enable-flex="true" lower-threshold="50">
		<view class="header-section">
			
		</view>
		<view class="user-box">
			<view class="user-list" v-for="(item,index) in userList" :key="index" @click="getOne(item.userId)">
				<view class="left">
					<image :src="baseUrl+item.avatar" mode="aspectFit"></image>
				</view>
				<view class="right">
					<view class="user-name">
						{{item.userName}}
					</view>
					<view class="role-name">
						{{item.roles[0].roleName}}
					</view>
				</view>
			</view>
		</view>
		
		<uni-popup ref='popup' type='center' background-color="#fff">
			<view style="max-height: 550px; overflow: scroll;">
				<view class='title'>
					修改用户信息
				</view>
				<uni-forms :modelValue='userform'>
					<uni-forms-item label="用户昵称">
						<uni-easyinput v-model="userForm.userName"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="归属部门">
						<uni-data-select v-model="userForm.deptId" :localdata="deptList" :clear="false"></uni-data-select>
					</uni-forms-item>
					<uni-forms-item label="手机号码">
						<uni-easyinput v-model="userForm.phonenumber"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="邮箱">
						<uni-easyinput v-model="userForm.email"></uni-easyinput>
					</uni-forms-item>
					<uni-forms-item label="用户性别">
						<uni-data-select v-model="userForm.sex" :localdata="sexList" :clear="false"></uni-data-select>
					</uni-forms-item>
					<uni-forms-item label="状态">
						<uni-data-checkbox v-model="userForm.status" :localdata="statusList"></uni-data-checkbox>
					</uni-forms-item>
					<uni-forms-item label="岗位">
						<uni-data-select v-model="postId" :localdata="postsList" :clear="false"></uni-data-select>
					</uni-forms-item>
					<uni-forms-item label="角色">
						<uni-data-select v-model="roleId" :localdata="rolesList" :clear="false"></uni-data-select>
					</uni-forms-item>
					<uni-forms-item label="备注">
						<uni-easyinput v-model="userForm.remark"></uni-easyinput>
					</uni-forms-item>
				</uni-forms>
				<view class="buttons">
					<button type="primary" @click="submitUser">确认</button>
					<button @click="closePopup">取消</button>
				</view>
			</view>
		</uni-popup>
		
		<uni-popup ref="error" type='center'>
			<uni-popup-message type="error" message="无法修改超级管理员的信息" :duration="2000"></uni-popup-message>
		</uni-popup>
	</scroll-view>
	
<!-- 	<view>
		<uni-list :border="true">
			<uni-list-chat v-for="(item,index) in userList" :key="index" clickable @click="getOne(item.userId)"
				:title="item.userName" :avatar="baseUrl+item.avatar" :note="item.roles[0].roleName"></uni-list-chat>
		</uni-list>
		
	</view> -->
</template>

<script>
	import {
		getListUser,
		getUser,
		updateUser
	} from '@/api/system/user'
	
	import {getDeptList} from '@/api/system/dept'

	export default {
		data() {
			return {
				pageNum: 1,
				pageSize: 10,
				userList: {},
				userForm: {},
				baseUrl: 'http://localhost:8080',
				//部门id
				postId: 0,
				
				//角色id
				roleId: 0,
				
				//部门列表
				deptList:[],
				
				//岗位列表
				postsList:[],
				
				//角色列表
				rolesList: [],
				
				// 性别值列表
				sexList:[
					{value: '0' , text: '男'},
					{value: '1' , text: '女'},
					{value: '2' , text: '未知'}
				],
				
				//状态列表
				statusList:[
					{value: '0', text: '正常'},
					{value: '1', text: '停用'}
				]
			}
		},
		methods: {
			async getData() {
				// console.log(this.pageNum);
				//用户信息
				const userResult = await getListUser({
					pageNum: this.pageNum,
					pageSize: this.pageSize
				})
				this.userList = userResult.rows
				
				//部门信息
				const deptResult = await getDeptList()	
				
				deptResult.data.forEach((item,index)=>{
					this.deptList[index] = {value: item.deptId, text:item.deptName}
				})
				
				console.log("deptList");
				console.log(this.deptList);
				console.log(this.userList);
			},
			//获取单个用户信息
			async getOne(id) {
				console.log(id);
				const result = await getUser(id)
				this.userForm = result.data
				this.postId = result.postIds[0]
				this.roleId = result.roleIds[0]
				
				//postsList
				result.posts.forEach((item,index)=>{
					this.postsList[index] = {value: item.postId, text:item.postName}
				})
				
				//rolesList
				result.roles.forEach((item,index)=>{
					this.rolesList[index] = {value: item.roleId, text:item.roleName}
				})
				
				console.log("postList");
				console.log(this.postsList);
				console.log(this.postId);
				
				
				console.log(this.userForm);
				console.log(result);
				// this.value = this.userForm.deptId
				// this.sexValue = this.userForm.sex
				
				// 不能对超级管理员进行操作
				if(result.roleIds[0] == 1){
					this.$refs.error.open()
				}else{
					this.$refs.popup.open()
				}
			},
			
			//修改用户信息
			async submitUser(){
				console.log(this.userForm);
				this.userForm.postIds = [this.postId]
				this.userForm.roleIds = [this.roleId]
				console.log(this.userForm);
				
				const result = await updateUser(this.userForm)
				console.log(result);
				
				this.closePopup()
				this.getData()
			},
			
			//关闭popup弹出层
			closePopup(){
				this.$refs.popup.close()
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
	
	.user-box{
		width: 90%;
		position: absolute;
		top: 50px;
		left: 0;
		right: 0;
		margin: 0 auto;
		
		.user-list{
			width: 100%;
			border: 1px solid #fff;
			border-radius: 15px;
			background-color: #fff;
			padding: 25px 20px;
			margin-bottom: 20px;
			display: flex;
			// flex-direction: column;
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
			
			.left{
				image{
					width: 50px;
					height: 50px;
				}
			}
			
			.right{
				flex: 1;
				display: flex;
				flex-direction: column;
				justify-content: center;
				padding-left: 50px;
			}
		}
	}
	
	.uni-popup__wrapper{
		border-radius: 15px;
		width: 350px;
	}
	
	.uni-list-chat__container {
		padding: 20px 15px !important;
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
	button{
		width: 200px;
		margin:15px auto;
	}
</style>