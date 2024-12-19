<template>
	<view class="normal-login-container">
		<view class="header-section">
			<view class="header-section-center">
				<image style="width: 100px;height: 100px;" :src="globalConfig.appInfo.logo" mode="widthFix">
				</image>
				<text class="title">欢迎来到请销假系统</text>
			</view>
		</view>
		<view class="login-form-content">
			<view class="login-for-password" v-if="!phone">
				<view class="input-item flex align-center">
					<view class="iconfont icon-user icon"></view>
					<input v-model="loginForm.username" class="input" type="text" placeholder="请输入账号" maxlength="30" />
				</view>
				<view class="input-item flex align-center">
					<view class="iconfont icon-password icon"></view>
					<input v-model="loginForm.password" type="password" class="input" placeholder="请输入密码" maxlength="20" />
				</view>
				<view class="input-item flex align-center" style="width: 60%;margin: 0px;" v-if="captchaEnabled">
					<view class="iconfont icon-code icon"></view>
					<input v-model="loginForm.code" type="number" class="input" placeholder="请输入验证码" maxlength="4" />
					<view class="login-code">
						<image :src="codeUrl" @click="getCode" class="login-code-img"></image>
					</view>
				</view>
			</view>
			<view class="login-for-phone" v-else>
				<view class="input-item flex align-center">
					<view class="iconfont icon">
						<uni-icons type="phone"></uni-icons>
					</view>
					<input v-model="phonenumber" class="input" type="text" placeholder="请输入手机号" maxlength="30" />
				</view>
			</view>
			<view class="action-btn">
				<button @click="handleLogin" class="login-btn cu-btn block bg-blue lg round">{{phone == true?"获取验证码":"登录"}}</button>
			</view>
			<view class="reg text-center" v-if="register">
				<text class="text-grey1">没有账号？</text>
				<text @click="handleUserRegister" class="text-blue">立即注册</text>
			</view>
			<view class="xieyi text-center">
				<text class="text-grey1">登录即代表同意</text>
				<text @click="handleUserAgrement" class="text-blue">《用户协议》</text>
				<text @click="handlePrivacy" class="text-blue">《隐私协议》</text>
			</view>
			<view class="other-login">
				<button class="other-login-btn weixin-btn" size="35" @click="wechatLogin">
					<uni-icons class="weixin" type="weixin" size="35"></uni-icons>
				</button>
				
				<button class="other-login-btn phone-btn" size="35" @click="phoneLogin" v-if="!phone">
					<uni-icons class="phone" type="phone-filled" size="35"></uni-icons>
				</button>
				
				<button class="other-login-btn psw-btn" size="35" @click="pswLogin" v-else>
					<uni-icons class="psw" type="person-filled" size="35"></uni-icons>
				</button>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		getCodeImg,
		wechatLogin,
		getSMSCode
	} from '@/api/login'

	export default {
		data() {
			return {
				codeUrl: "",
				captchaEnabled: true,
				// 用户注册开关
				register: false,
				// 短信验证码登录开关
				phone: false,
				globalConfig: getApp().globalData.config,
				// 密码登录参数
				loginForm: {
					username: "admin",
					password: "admin123",
					code: "",
					uuid: ''
				},
				// 电话号码
				phonenumber:''
			}
		},
		created() {
			this.getCode()
			console.log(this);
		},
		methods: {
			// 用户注册
			handleUserRegister() {
				this.$tab.redirectTo(`/pages/register`)
			},
			// 隐私协议
			handlePrivacy() {
				let site = this.globalConfig.appInfo.agreements[0]
				this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
			},
			// 用户协议
			handleUserAgrement() {
				let site = this.globalConfig.appInfo.agreements[1]
				this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`)
			},
			// 获取图形验证码
			getCode() {
				getCodeImg().then(res => {
					this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
					if (this.captchaEnabled) {
						this.codeUrl = 'data:image/gif;base64,' + res.img
						this.loginForm.uuid = res.uuid
					}
				})
			},
			// 登录方法
			async handleLogin() {
				if(this.phone){
					// 获取验证码
					if(this.phonenumber === ""){
						this.$modal.msgError("请输入你的电话")
					}else{
						const res = await getSMSCode(this.phonenumber)
						console.log(res);
						if(res.code == 200){
							uni.navigateTo({
								url: `/pages/smsCode?phonenumber=${this.phonenumber}`
							})
						}
						
						// console.log('111 ');
						// uni.navigateTo({
						// 	url: `/pages/smsCode?phonenumber=${this.phonenumber}`
						// })
					}
				}else{
					// 密码
					if (this.loginForm.username === "") {
						this.$modal.msgError("请输入您的账号")
					} else if (this.loginForm.password === "") {
						this.$modal.msgError("请输入您的密码")
					} else if (this.loginForm.code === "" && this.captchaEnabled) {
						this.$modal.msgError("请输入验证码")
					} else {
						this.$modal.loading("登录中，请耐心等待...")
						this.pwdLogin()
					}
				}
				
			},
			// 密码登录
			async pwdLogin() {
				this.$store.dispatch('Login', this.loginForm).then(() => {
					this.$modal.closeLoading()
					this.loginSuccess()
				}).catch(() => {
					if (this.captchaEnabled) {
						this.getCode()
					}
				})
			},
			// 登录成功后，处理函数
			loginSuccess(result) {
				// 设置用户信息
				this.$store.dispatch('GetInfo').then(res => {
					this.$tab.reLaunch('/pages/index')
				})
			},
			// 微信登录
			wechatLogin(e) {
				this.$modal.loading("登录中，请耐心等待...")
				
				uni.login({
					provider: 'weixin',
					"onlyAuthorize": true,
					success:(e)=>{
						console.log(e);
						this.$store.dispatch('WechatLogin',e.code).then(() => {
							this.$modal.closeLoading()
							this.loginSuccess()
						})
					},
					fail(err){
						console.log(err);
					}
				})
			},
			// 点击手机登录
			phoneLogin(){
				this.phone = true
			},
			// 点击密码登录
			pswLogin(){
				this.phone = false
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #ffffff;
	}

	.normal-login-container {
		width: 100%;
		.header-section{
			width: 100%;
			height: 200px;
			background-color: #261c3f;
			border-bottom-left-radius: 50px 10px;
			border-bottom-right-radius: 50px 10px;
			display: flex;
			justify-content: center;
			align-items: center;
			
			.header-section-center{
				width: 100%;
				height: 100%;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: start;
				color: #ffffff;
				
				.title{
					font-size: 17px;
					font-weight: 700;
				}
			}
		}

		.login-form-content {
			text-align: center;
			margin: 20px auto;
			margin-top: 10%;
			width: 80%;
			
			.login-for-phone{
				.uniui-phone{
					color: #999 !important;
					font-size: 38rpx !important;
				}
			}

			.input-item {
				margin: 20px auto;
				background-color: #f5f6f7;
				height: 45px;
				border-radius: 20px;

				.icon {
					font-size: 38rpx;
					margin-left: 10px;
					color: #999;
				}

				.input {
					width: 100%;
					font-size: 14px;
					line-height: 20px;
					text-align: left;
					padding-left: 15px;
				}

			}

			.login-btn {
				margin-top: 40px;
				height: 45px;
			}

			.reg {
				margin-top: 15px;
			}

			.xieyi {
				color: #333;
				margin-top: 20px;
			}

			.login-code {
				height: 38px;
				float: right;

				.login-code-img {
					height: 38px;
					position: absolute;
					margin-left: 10px;
					width: 200rpx;
				}
			}
			
			.other-login{
				display: flex;
				align-items: center;
				justify-content: center;
				margin: 25px 0;

				.other-login-btn{
					width: 50px;
					height: 50px;
					margin: 0 20px;
					padding: 0;
					display: flex;
					justify-content: center;
					align-items: center;
					border-radius: 50%;
					
					.weixin{
						.uni-icons{
							color: #ffffff !important;
						}
					}
					
					.phone{
						.uni-icons{
							color: #ffffff !important;
						}
					}
					
					.psw{
						.uni-icons{
							color: #ffffff !important;
						}
					}
				}
				
				.weixin-btn{
					background-color: green;
				}
				
				.phone-btn,.psw-btn{
					background-color: #000;
				}
			}
		}
	}
</style>