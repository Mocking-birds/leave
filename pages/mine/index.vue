<template>
  <view 
	class="mine-container" 
	:style="{height: `${windowHeight}px`}"
>
    <!--顶部个人信息栏-->
    <view class="header-section">
      <view class="flex padding justify-between">
        <view class="flex align-center">
          <view v-if="!avatar" class="cu-avatar xl round bg-white">
            <view class="iconfont icon-people text-gray icon"></view>
          </view>
          <image v-if="avatar" @click="handleToAvatar" :src="avatar" class="cu-avatar xl round" mode="widthFix">
          </image>
          <view v-if="!name" @click="handleToLogin" class="login-tip">
            点击登录
          </view>
          <view v-if="name" class="user-info">
            <view class="u_title">
              {{ name }}
            </view>
			<view class="wechat-promission" v-if="!wechatPermission" @click="wechatBinding">
				点击绑定微信
			</view>
			<view class="wechat-promission-true" v-else>
				<uni-icons type="weixin" size="25"></uni-icons>
				<view style="padding-left: 5px;">
					已绑定
				</view>
			</view>
          </view>
        </view>
        <view @click="handleToInfo" class="flex align-center">
          <text>个人信息</text>
          <view class="iconfont icon-right"></view>
        </view>
      </view>
    </view>

    <view class="content-section">
      <view class="mine-actions">
		<view class="current-location" @click="openLocation">
			<uni-icons type="location" size="22"></uni-icons>
			<view class="current-location-content">
				当前所处位置：{{locationName}}
			</view>
		</view>
      </view>
	  
	  <view class="data-section">
			<view class="data-section-type">
				<view class="data-section-item">
					<view class="data-section-item-number">
						{{permitTotal}}
					</view>
					<view class="data-section-item-text">
						请假
					</view>
				</view>
				<view class="data-section-item">
					<view class="data-section-item-number">
						{{permitBackTotal}}
					</view>
					<view class="data-section-item-text">
						销假
					</view>
				</view>
				<view class="data-section-item">
					<view class="data-section-item-number">
						{{processTotal}}
					</view>
					<view class="data-section-item-text">
						待批阅
					</view>
				</view>
			</view>
	  </view>

      <view class="menu-list">
        <view class="list-cell list-cell-arrow" @click="handleToEditInfo">
          <view class="menu-item-box">
            <view class="iconfont icon-user menu-icon"></view>
            <view>编辑资料</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleHelp">
          <view class="menu-item-box">
            <view class="iconfont icon-help menu-icon"></view>
            <view>常见问题</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleTeam">
          <view class="menu-item-box">
            <view class="iconfont icon-friendfill menu-icon"></view>
            <view>审核团队</view>
          </view>
        </view>
        <view class="list-cell list-cell-arrow" @click="handleToSetting">
          <view class="menu-item-box">
            <view class="iconfont icon-setting menu-icon"></view>
            <view>应用设置</view>
          </view>
        </view>
		<view class="list-cell list-cell-arrow" @click="handleToNotice">
		  <view class="menu-item-box notice">
		    <uni-icons type="notification" size="16"></uni-icons>
		    <view>消息中心</view>
		  </view>
		</view>
      </view>

    </view>
  </view>
</template>

<script>
  import QQMap from "@/qqmap-wx-jssdk1.2/qqmap-wx-jssdk.js"
  import {getInfo,wechatBinding} from '@/api/system/user.js'
  import { listPermit, listPermitBacklog, listPermitByDept } from '@/api/leave/ask_for_leave.js'
  import {listBackPermit} from '@/api/leave/back_for_leave.js'
  
  export default {
    data() {
      return {
        name: this.$store.state.user.name,
        version: getApp().globalData.config.appInfo.version,
		// 微信是否授权
		wechatPermission: false,
		// 用户信息
		userInfo: null,
		// 位置经纬度
		latitude: '',
		longitude: '',
		// 位置名称
		locationName: '',
		// 请假条数
		permitTotal: 0,
		// 销假条数
		permitBackTotal: 0,
		// 待批阅
		processTotal: 0,
		// 待办请求query
		permitBacklogQuery:{
			pageNum:1,
			pageSize:5,
			deptId: null,
			userId: null
		},
		// 请假请求query
		permitQuery:{
			pageNum: 1,
			pageSize: 10,
			leaveStatus: null,
			isBack: null,
			permitType: null
		},
		//销假请求query
		permitBackQuery:{
			pageNum: 1,
			pageSize: 10,
			leaveStatus: 1,
			isBack: 2,
			permitType: null
		}
      }
    },
	async created() {
		console.log('123');
		console.log(this.$store);
		const res = await getInfo()
		this.userInfo = res
		console.log(this.userInfo);
		if(res.user.openId){
			this.wechatPermission = true
		}
		this.getData()
		this.getCurrentLocation()
		
		
		// -----------------
		// let params = parseFloat(this.latitude).toFixed(2)+','+parseFloat(this.longitude).toFixed(2)
		// console.log('111');
		// console.log(params);
		// console.log(this.latitude);
		
	},
    computed: {
      avatar() {
        return this.$store.state.user.avatar
      },
      windowHeight() {
        return uni.getSystemInfoSync().windowHeight - 50
      }
    },
	// 下拉刷新
	onPullDownRefresh() {
		console.log("刷新");
		setTimeout(() => {
			uni.stopPullDownRefresh()
			this.getCurrentLocation()
			this.getData()
		},500)
	},
    methods: {
      handleToInfo() {
        this.$tab.navigateTo('/pages/mine/info/index')
      },
      handleToEditInfo() {
        this.$tab.navigateTo('/pages/mine/info/edit')
      },
      handleToSetting() {
        this.$tab.navigateTo('/pages/mine/setting/index')
      },
      handleToLogin() {
        this.$tab.reLaunch('/pages/login')
      },
      handleToAvatar() {
        this.$tab.navigateTo('/pages/mine/avatar/index')
      },
	  handleToNotice() {
	    this.$tab.navigateTo('/pages/mine/notice/index')
	  },
      handleLogout() {
        this.$modal.confirm('确定注销并退出系统吗？').then(() => {
          this.$store.dispatch('LogOut').then(() => {
            this.$tab.reLaunch('/pages/index')
          })
        })
      },
      handleHelp() {
        this.$modal.showToast('有问题去找辅导员哈~')
      },
      handleTeam() {
        this.$tab.navigateTo('/pages/mine/team/index')
      },
	  // 获取基本数据
	  async getData(){
		  if(this.userInfo.roles[0] == 'admin'){
			  // 超级管理员
			  const premitRes = await listPermit()
			  this.permitTotal = premitRes.total
			  
			  const backRes = await listBackPermit({pageNum: 1,pageSize: 10},null,2)
			  this.permitBackTotal = backRes.total
			  
			  const processRes = await listPermitBacklog(this.permitBacklogQuery)
			  this.processTotal = processRes.total
		  }else if(this.userInfo.roles[0] == 'director'){
			  // 主任
			  const premitRes = await listPermitByDept(
					this.permitQuery,
					this.userInfo.user.deptId,
					null)
			  this.permitTotal = premitRes.total
			  
			  const backRes = await listPermitByDept(
				  this.permitBackQuery,
				  this.userInfo.user.deptId,
				  null)
			  this.permitBackTotal = backRes.total
			  
			  this.permitBacklogQuery.deptId = this.userInfo.user.deptId
			  const processRes = await listPermitBacklog(this.permitBacklogQuery)
			  this.processTotal = processRes.total
		  }else if(this.userInfo.roles[0] == 'counsellor'){
			  // 辅导员
			  const premitRes = await listPermitByDept(
			  					this.permitQuery,
			  					this.userInfo.user.dept.parentId,
			  					null)
			  this.permitTotal = premitRes.total
			  
			  const backRes = await listPermitByDept(
			  				  this.permitBackQuery,
			  				  this.userInfo.user.dept.parentId,
			  				  null)
			  this.permitBackTotal = backRes.total
			  
			  this.permitBacklogQuery.deptId = this.userInfo.user.dept.parentId
			  const processRes = await listPermitBacklog(this.permitBacklogQuery)
			  this.processTotal = processRes.total
		  }else if(this.userInfo.roles[0] == 'student'){
			  // 学生
			  const premitRes = await listPermit({
				  pageNum: 1,
				  pageSize: 10,
				  userId: this.userInfo.user.userId
			  })
			  this.permitTotal = premitRes.total
			  
			  const backRes = await listPermit({
				  pageNum: 1,
				  pageSize: 10,
				  userId: this.userInfo.userId,
				  leaveStatus: 1,
				  isBack: 2
			  })
			  this.permitBackTotal = backRes.total
			  
			  this.permitBacklogQuery.userId = this.userInfo.user.userId
			  const processRes = await listPermitBacklog(this.permitBacklogQuery)
			  this.processTotal = processRes.total
		  }
	  },
	  // 绑定微信
	  wechatBinding(){
		  uni.login({
		  	provider: 'weixin',
			"onlyAuthorize": true,
			success: async (e) => {
				console.log(e.code);
				const res = await wechatBinding({
					jsCode: e.code,
					username: this.name
				})
				console.log(res);
				if(res.code == 200){
					this.$modal.showToast('微信绑定成功')
					this.wechatPermission = true
				}
			},
			fail: (err) => {
				console.log(err);
			}
		  })
	  },
	  // 获取当前位置
	  getCurrentLocation(){
		  console.log("获取位置");
		  uni.getLocation({
		  	type: 'gcj02',
		  	success: async (res) => {
		  		let latitude = res.latitude
		  		let longitude = res.longitude
		  		
		  		this.latitude = latitude
		  		this.longitude = longitude
		  		
		  		// 实例化API核心类
		  		this.qqmap = new QQMap({
		  			key: 'PMGBZ-SDYKQ-6VS5O-4P3HN-ZVT75-VCFOB'
		  		})
		  		
		  		// 逆地址解析
		  		this.qqmap.reverseGeocoder({
		  			location: {
		  				latitude: latitude,
		  				longitude: longitude
		  			},
		  			success: (res) => {
		  				this.locationName = res.result.address
		  			},
		  			fail: (res) => {
		  				console.log(res);
		  			}
		  		})
		  	},
		  	fail: (res) => {
		  		console.log(res);
		  	}
		  })
	  },
	  // 打开地图
	  openLocation(index) {
	  	uni.openLocation({
	  		latitude: Number(this.latitude),
	  		longitude: Number(this.longitude),
	  		success: () => {
	  			console.log('success');
	  		},
	  		fail: (res) => {
	  			console.log(res);
	  		}
	  	})
	  },
    }
  }
</script>

<style lang="scss">
  page {
    background-color: #f5f6f7;
  }

  .mine-container {
    width: 100%;
    height: 100%;


    .header-section {
      padding: 15px 15px 45px 15px;
      background-color: #261c3f;
      color: white;

      .login-tip {
        font-size: 18px;
        margin-left: 10px;
      }

      .cu-avatar {
        border: 2px solid #eaeaea;

        .icon {
          font-size: 40px;
        }
      }

      .user-info {
        margin-left: 15px;

        .u_title {
          font-size: 18px;
          line-height: 30px;
        }
		
		.wechat-promission-true{
			margin-top: 5px;
			display: flex;
			align-items: center;
			border-radius: 20px;
			padding:2px 7px;
			background-image: linear-gradient(to right, #a57b31, #b18a3d, #bc994a, #c8a857, #d3b864);
			
			.uni-icons{
				color:green !important;
			}
		}
      }
    }

    .content-section {
      position: relative;
      top: -50px;

      .mine-actions {
        margin: 15px 15px;
        padding: 20px 0px;
        border-radius: 8px;
        background-color: white;

        .current-location{
			display: flex;
			align-items: center;
			padding: 0 10px;
			
			.uni-icons{
				color: #007AFF !important;
				margin-right: 5px;
			}
			
			.current-location-content{
				height: 21px;
			}
		}
      }
    }
	
	.data-section{
		.data-section-type{
			margin: 15px 15px;
			padding: 15px 0px;
			border-radius: 8px;
			background-color: white;
			display: flex;
			align-items: center;
			justify-content: space-around;
			
			.data-section-item{
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;
				
				.data-section-item-text{
					margin-top: 5px;
				}
			}
		}
	}
	
	.notice{
		.uni-icons{
			color: #007AFF !important;
			margin-right: 5px;
		}
	}
  }
</style>
