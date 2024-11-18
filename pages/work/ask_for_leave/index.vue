<template>
	<view>
		<!-- <uni-card :is-shadow="false" is-full>
			<text class="leave-title">{{title}}假条</text>
		</uni-card> -->
		<uni-forms ref="forms" :modelValue="leaveForm" :rules="rules">
			<uni-forms-item label="请假类型">
				<uni-data-select v-model="leaveForm.permitType" :localdata="permitTypeList"
					:clear="false"></uni-data-select>
			</uni-forms-item>
			<uni-forms-item label="请假理由">
				<uni-easyinput v-model="leaveForm.reason" :disabled="type != '请假' && role == 'student'?true:false" />
			</uni-forms-item>
			<uni-forms-item label="起始日期" name="startTime" required>
				<uni-datetime-picker type="datetime" v-model="leaveForm.startTime"
					:disabled="type != '请假' && role == 'student'?true:false" />
			</uni-forms-item>
			<uni-forms-item label="结束日期" name="endTime" required>
				<uni-datetime-picker type="datetime" v-model="leaveForm.endTime"
					:disabled="type != '请假' && role == 'student'?true:false" />
			</uni-forms-item>
			<uni-forms-item label="请假天数">
				<view style="display: flex;align-items: center;width: 100%;height: 100%;">
					{{comDays()}}天
				</view>
			</uni-forms-item>
			<uni-forms-item label="是否销假" v-if="type != '请假'?true:false">
				<uni-data-checkbox v-model="leaveForm.isBack" :localdata="isBackList"></uni-data-checkbox>
			</uni-forms-item>
			<uni-forms-item label="请假状态" v-if="type == '请假'?false:true">
				<uni-data-checkbox v-model="leaveForm.leaveStatus" :localdata="statusList"></uni-data-checkbox>
			</uni-forms-item>
			<uni-forms-item label="请假日期" v-if="type == '请假'?false:true">
				<uni-datetime-picker type="datetime" v-model="leaveForm.permitTime"
					:disabled="type != '请假' && role == 'student'?true:false" />
			</uni-forms-item>
			<uni-forms-item label="销假日期" v-if="type == '假条管理'?true:false">
				<uni-datetime-picker type="datetime" v-model="leaveForm.backTime"
					:disabled="role == 'student'?true:false" />
			</uni-forms-item>
			<uni-forms-item label="请假位置">
				<view v-if="type == '请假'?true:false">
					<button v-if="!(leaveForm.permitLocationList[0].locationName)"
						@click="getLocation(0)">获取当前位置</button>
					<view v-else>
						{{leaveForm.permitLocationList[0].locationName}}
					</view>
				</view>
				<view v-else>
					{{leaveForm.permitLocationList[0].locationName}}
				</view>
			</uni-forms-item>
			<uni-forms-item label="销假位置" v-if="type != '请假'?true:false">
				<view v-if="type == '销假'?true:false">
					<button v-if="back"
						@click="getLocation(1)">获取当前位置</button>
					<view v-else>
						{{leaveForm.permitLocationList[1].locationName}}
					</view>
				</view>
				<view v-else>
					{{leaveForm.permitLocationList[1].locationName}}
				</view>
			</uni-forms-item>
		</uni-forms>

		<view>
			<button type="primary" style="margin-bottom: 15px;"
				@click="sureButton">{{type == '销假'?'销假请求':'确认'}}</button>
			<button type="warn" @click="cancelButton">取消</button>
		</view>

		<uni-popup ref="message" type="message">
			<uni-popup-message type="success" :message="messageText" :duration="2000"></uni-popup-message>
		</uni-popup>

		<uni-popup ref="msgLocation" type="message">
			<uni-popup-message type="warn" :message="msgLocationText" :duration="2000"></uni-popup-message>
		</uni-popup>
	</view>
</template>

<script>
	import {
		getPermit,
		updatePermit,
		addPermit
	} from '@/api/leave/ask_for_leave.js'
	import {
		getDicts
	} from '@/api/system/dict/data'

	import {
		getInfo
	} from '@/api/system/user.js'

	import QQMap from "@/qqmap-wx-jssdk1.2/qqmap-wx-jssdk.js"
	import storage from '@/utils/storage'
	import constant from '@/utils/constant'

	export default {
		data() {
			return {
				// 参数类型
				type: '',

				// 参数权限
				role: '',

				//QQMap实例
				qqmap: null,

				//假条表单
				leaveForm: {},

				//请假类型数组
				permitTypeList: [],

				//是否销假数组
				isBackList: [],

				//请假状态数组
				statusList: [],

				//假条id
				id: 0,

				//弹出框text
				messageText: '',

				//位置信息弹出框
				msgLocationText: '',

				//用户信息
				userInfo: {},
				
				// 销假位置的显示状态
				back: true,

				//校验规则
				rules: {
					startTime: {
						rules: [{
							required: true,
							errorMessage: '请选择起始日期'
						}]
					},
					endTime: {
						rules: [{
							required: true,
							errorMessage: '请选择结束日期'
						}]
					}
				}
			}
		},
		methods: {
			// 获取数据
			async getData() {
				//请假类型数组
				const permitTypeResult = await getDicts('permit_leave_type')
				//是否销假数组
				const isBackResult = await getDicts('permit_is_back')
				//请假状态数组
				const statusResult = await getDicts('permit_leave_status')

				//请假类型数组
				permitTypeResult.data.forEach((item, index) => {
					this.permitTypeList[index] = {
						value: item.dictValue,
						text: item.dictLabel,
						disable: this.type != '请假' && this.role == 'student' ? true : false
					}

					// this.items[index+1] = item.dictLabel
				})

				//是否销假数组
				isBackResult.data.forEach((item, index) => {
					this.isBackList[index] = {
						value: item.dictValue,
						text: item.dictLabel,
						disabled: this.role == 'student' ? true : false
					}
					// this.items[index+1] = item.dictLabel
				})

				//请假状态数组
				statusResult.data.forEach((item, index) => {
					this.statusList[index] = {
						value: item.dictValue,
						text: item.dictLabel,
						disabled: this.role == 'student' ? true : false
					}
					// this.items[index+1] = item.dictLabel
				})

				if (this.type == '请假') {
					this.leaveForm = {
						userId: this.userInfo.user.userId,
						permitType: '0',
						reason: '',
						startTime: '',
						endTime: '',
						isBack: '0',
						leaveStatus: '0',
						permitTime: null,
						backTime: null,
						permitDays: null,
						permitLocationList: [{
							latitude: null,
							longitude: null,
							locationName: '',
							locationType: '0'
						}]
					}
				} else {
					const res = await getPermit(this.id)
					this.leaveForm = res.data
					// this.leaveForm.permitLocationList[1] = {
					// 	latitude: null,
					// 	longitude: null,
					// 	locationName: '',
					// 	locationType: '1'
					// }
					
					console.log('test');
					console.log(this.leaveForm);
				}

			},

			changeLog(e) {
				console.log(e);
			},

			// 计算请假天数
			comDays() {
				if(this.type == '请假'){
					// 判断非空
					if (!this.leaveForm.startTime || !this.leaveForm.endTime) {
						return 0;
					}
					
					// 将日期字符串转换为Date对象  
					const date1 = new Date(this.leaveForm.startTime);
					const date2 = new Date(this.leaveForm.endTime);
					
					// 计算时间差（毫秒）  
					const timeDifference = Math.abs(date2 - date1);
					
					// 将时间差转换为天数(向上取整)
					const daysDifference = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));
					
					this.leaveForm.permitDays = daysDifference
					
					return daysDifference;
				}else{
					return this.leaveForm.permitDays;
				}
				
			},

			//取消
			cancelButton() {
				// 因为请假界面不是使用组件而是使用的是页面跳转，使用uni.navigateBack()的话会跳转到一个空界面
				if(this.type == '请假'){
					uni.switchTab({
						url: '/pages/work/index'
					})
				}else{
					uni.navigateBack()
				}
			},

			//确认
			async sureButton() {
				
				console.log('确认');
				
				console.log(this.leaveForm);

				if (this.type == '请假'?!this.leaveForm.permitLocationList[0].locationName:(this.leaveForm.permitLocationList[1]== undefined?true:false)) {
					this.msgLocationText = '请获取你的位置信息'
					this.$refs.msgLocation.open()
				} else {
					try {
						if(this.type == '请假'){
							this.leaveForm.permitTime = this.getCurrentTime()
						}else{
							this.leaveForm.backTime = this.getCurrentTime()
						}
						
						await this.$refs.forms.validate()
						
						if (this.type == '请假') {
							const res = await addPermit(this.leaveForm);
							console.log('添加');
							console.log(res);
							this.messageText = '添加成功'
						} else{
							const res = await updatePermit(this.leaveForm);
							console.log('更新');
							console.log(res);
							this.messageText = '编辑成功'
						}

						this.$refs.message.open()
						uni.navigateBack()
					} catch (e) {
						//TODO handle the exception
						console.log(e);
					}
				}
			},

			//获取当前时间
			getCurrentTime() {
				const now = new Date();
				const year = now.getFullYear();
				const month = String(now.getMonth() + 1).padStart(2, '0'); // 月份从0开始，需要加1  
				const day = String(now.getDate()).padStart(2, '0');
				const hours = String(now.getHours()).padStart(2, '0');
				const minutes = String(now.getMinutes()).padStart(2, '0');
				const seconds = String(now.getSeconds()).padStart(2, '0');
				const currentTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;


				console.log(currentTime);
				return currentTime;
			},

			//获取当前位置
			getLocation(num) {

				console.log('初始');
				console.log(this.leaveForm.permitLocationList);

				console.log(storage.get(constant.roles));

				// num为0时就是请假位置，为1时就是销假位置
				uni.getLocation({
					type: 'gcj02', //返回可以用于uni.openLocation的经纬度
					success: (res) => {
						if(!this.leaveForm.permitLocationList[num]){
							this.leaveForm.permitLocationList[num] = {}
						}
						this.leaveForm.permitLocationList[num].latitude = res.latitude
						this.leaveForm.permitLocationList[num].longitude = res.longitude

						console.log(this.leaveForm.permitLocationList);

						// 实例化API核心类
						this.qqmap = new QQMap({
							key: 'PMGBZ-SDYKQ-6VS5O-4P3HN-ZVT75-VCFOB'
						})

						// 逆地址解析
						this.qqmap.reverseGeocoder({
							location: {
								latitude: this.leaveForm.permitLocationList[num].latitude,
								longitude: this.leaveForm.permitLocationList[num].longitude
							},
							success: (res) => {
								console.log(this.leaveForm);

								this.leaveForm.permitLocationList[num].locationName = res.result
									.address
								this.leaveForm.permitLocationList[num].locationType = num

								if(num!=0){
									this.back = false
								}
								console.log('成功');
								console.log(!(this.leaveForm.permitLocationList[num]
								.locationName));
							},
							fail: (res) => {
								console.log(res);
							}
						})
					},
					fail: (res) => {
						console.log(res);
					}
				});
			}
		},
		async created() {
			const res = await getInfo()
			this.userInfo = res
			console.log('leaveForm');
			console.log(this.leaveForm);
			console.log(res);
		},
		onLoad(params) {
			this.id = params.id
			this.type = params.type
			this.role = params.role
			this.getData()
		},
		mounted() {
			this.getData()
		}
	}
</script>

<style>
	.uni-forms {
		padding-top: 10px;
		background-color: #fff;
	}

	.uni-forms-item {
		margin-bottom: 15px !important;
	}

	.uni-forms-item__label {
		width: 90px !important;
		margin: 5px 5px;
	}

	.uni-forms-item__content {
		margin: 0 10px;
	}

	.uni-forms-item__content {
		display: flex;
		align-items: center;
		width: 100%;
		height: 36px;
	}

	uni-data-select {
		width: 100%;
	}

	.uni-card__content {
		height: 60px;
	}

	.leave-title {
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 18px;
		font-weight: 900;
	}

	.uni-date-editor--x__disabled {
		opacity: 0.8 !important;
	}
</style>