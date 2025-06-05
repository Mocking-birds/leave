<template>
	<view class="smscode-container">
		<view class="container">
			<view class="title sms-code-item-padding">
				请输入验证码
			</view>
			<view class="content sms-code-item-padding">
				验证码已发送到 {{smsCodeParams.phonenumber}}
			</view>
			<view class="sms-code sms-code-item-padding">
				<uni-easyinput 
				v-for="(item,index) in smsCodeList"
				:key="index"
					class="uni-mt-5" 
					v-model="smsCodeList[index]"
					@input="change(item,index)"
					:maxlength="1"
					:clearable="false"
					type="number"
					ref="inputs"
				></uni-easyinput>
			</view>
			<view class="down sms-code-item-padding">
				<uni-countdown 
					:second="60"
					:show-day="false"
					:show-hour="false"
					:show-minute="false"
					:showColon="false"
					@timeup="timeUp"
				></uni-countdown>
				<view class="text">
					后重新发送
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 验证码登录参数
				smsCodeParams: {
					phonenumber: '',
					smsCode: ''
				},
				smsCodeList:[null,null,null,null,null,null]
			}
		},
		created() {
			console.log('created');
		},
		mounted() {
			console.log('mounted');
			console.log(this);
			this.$refs.inputs[0].onFocus()
		},
		onLoad(params) {
			console.log(params);
			this.smsCodeParams.phonenumber = params.phonenumber
		},
		methods:{
			// input输入
			async change(item,index){
				this.smsCodeParams.smsCode += item
				if(index == 5){
					console.log('提交了');
					console.log(typeof this.smsCodeParams.phonenumber);
					console.log(typeof this.smsCodeParams.smsCode);
					this.$store.dispatch('PhoneLogin', {
						phonenumber: this.smsCodeParams.phonenumber,
						smsCode: this.smsCodeParams.smsCode
					}).then(() => {
						// 设置用户信息
						this.$store.dispatch('GetInfo').then(res => {
							this.$tab.reLaunch('/pages/index')
						})
					})
				}else{
					this.$refs.inputs[index+1].onFocus()
					console.log(this.$refs.inputs[index+1]);
				}
			},
			// 倒计时时间到
			timeUp(){
				console.log("时间到");
				uni.navigateBack()
			}
		}
	}
</script>

<style lang="scss">
	.smscode-container {
		display: flex;
		justify-content: center;
		align-items: center;

		.container {
			width: 80%;
			
			.sms-code-item-padding{
				padding:20px 0;
			}
			
			.title{
				font-size: 20px;
				font-weight: 800;
			}
			
			.content{
				padding: 0 !important;
			}
			
			.sms-code{
				width: 100%;
				display: flex;
				align-items: center;
				justify-content: space-around;
				
				.uni-easyinput{
					margin: 0 7px;
					text-align: center;
					font-size: 20px;
					font-weight: 800;
					
					.uni-easyinput__content{
						background-color: #f1f1f1 !important;
					}
					
					.is-input-border{
						border: none;
						border-bottom: 1px solid #b1b1b1 !important;
						border-radius: 0;
					}
					
					.is-focused{
						border-color: #000;
					}
				}
			}
			
			.down{
				display: flex;
				justify-content: center;
				align-items: center;
				
				uni-countdown{
					height:20px;
					
					.uni-countdown{
						height: 100%;
						line-height: 20px;
						
						.uni-countdown__number{
							font-size: 12px !important;
							color:#2979ff !important;
						}
						
						.uni-countdown__splitor{
							font-size: 12px;
							color:#2979ff !important;
							margin: 0;
						}
					}
				}
				
				.text{
					height: 20px;
					line-height: 20px;
					font-size: 12px;
				}
			}
		}
	}
</style>