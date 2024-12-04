<template>
	<scroll-view style="height:100vh;background-color: rgb(241,245,249);position: relative;" 
		scroll-y="true" 
		scroll-top="0"
		enable-flex="true" 
		lower-threshold="50" 
		@scrolltolower="scrolltolower"
		>
		<view class="header-section">
		</view>
		
		<view class="card-box">
			<view class="card permit_leave_type">
				<uni-section title="假条类别" type="circle"></uni-section>
				<uni-data-select :clear="false" v-model="permitData.permitType" :localdata="permitTypeRange"></uni-data-select>
			</view>
			<view class="card permit_leave_start-time">
				<uni-section title="起始时间" type="circle"></uni-section>
				<uni-datetime-picker type="datetime" :border="false" :clearIcon="false" v-model="permitData.startTime" />
			</view>
			<view class="card permit_leave_end-time">
				<uni-section title="结束时间" type="circle"></uni-section>
				<uni-datetime-picker type="datetime" :border="false" :clearIcon="false" v-model="permitData.endTime" />
			</view>
			<view class="card permit_leave_days">
				<uni-section title="请假天数" type="circle"></uni-section>
				<view class="days">
					5天
				</view>
			</view>
			<view class="card permit_leave_reason">
				<uni-section title="请假理由" type="circle"></uni-section>
				<input class="uni-input"/>
			</view>
			<view class="card permit_leave_ask_location">
				<uni-section title="请假位置" type="circle"></uni-section>
				<view class="location">
					123
				</view>
			</view>
			<view class="card permit_leave_back_location">
				<uni-section title="请假位置" type="circle"></uni-section>
				<view class="location">
					123
				</view>
			</view>
			
			<view class="req-button">
				<button class="req-button-sub">提交</button>
				<button class="req-button-cancel">取消</button>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	export default {
		data() {
			return {
				// form
				permitData: {
					permitType: -1,
					startTime: null,
					endTime: null,
					permitDays: 0,
					permitReason: null,
					askForLeaveLocation: null
				},
				// 请假类型范围
				permitTypeRange: [
					{ value: 0, text: "篮球" },
					{ value: 1, text: "足球" },
					{ value: 2, text: "游泳" }
				],
				// 请销假类型
				type: ''
			}
		},
		methods:{
			created(){
				console.log('created');
			},
			onLoad(params){
				console.log('onLoad');
				this.type = params.type
			}
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
	
	.card-box{
		width: 90%;
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		margin: 0 auto;
	}
	
	.card {
		width: 100%;
		border: 1px solid #fff;
		border-radius: 15px;
		background-color: #fff;
		padding: 5px 2px;
		margin-bottom: 50px;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	
	.permit_leave_type{
		
		.uni-stat__select{
			width: 100px;
			
			.uni-select{
				border: none;
				
				.uni-select__input-text{
					width: 50%;
				}
			}
		}
	}
	
	.permit_leave_start-time{
		.uni-date{
			display: flex;
			justify-content: flex-end;
			align-items: center;
			padding-right: 20px;
			
			.uni-date__x-input{
				margin-left: 5px;
			}
		}
	}
	
	.permit_leave_end-time{
		.uni-date{
			display: flex;
			justify-content: flex-end;
			align-items: center;
			padding-right: 20px;
			
			.uni-date__x-input{
				margin-left: 5px;
			}
		}
	}
	
	.permit_leave_days{
		.days{
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
		}
	}
	
	.permit_leave_reason{
		.uni-input{
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
		}
	}
	
	.permit_leave_ask_location{
		.location{
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
			text-align: right;
		}
	}
	
	.permit_leave_back_location{
		.location{
			display: flex;
			justify-content: flex-end;
			align-items: center;
			flex: 1;
			padding-right: 20px;
			text-align: right;
		}
	}
	
	.req-button{
		display: flex;
		align-items: center;
		justify-content: space-between;
		
		.req-button-sub,.req-button-cancel{
			width: 35%;
			background-color: #261c3f;
			border-radius: 25px;
			font-weight: 600;
			color: #fff;
		}
	}
</style>