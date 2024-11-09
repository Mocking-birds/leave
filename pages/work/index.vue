<template>
  <view class="work-container">
    <!-- 轮播图 -->
    <uni-swiper-dot class="uni-swiper-dot-box" :info="data" :current="current" field="content">
      <swiper class="swiper-box" :current="swiperDotIndex" @change="changeSwiper">
        <swiper-item v-for="(item, index) in data" :key="index">
          <view class="swiper-item" @click="clickBannerItem(item)">
            <image :src="item.image" mode="aspectFill" :draggable="false" />
          </view>
        </swiper-item>
      </swiper>
    </uni-swiper-dot>

    <!-- 宫格组件 -->
    <uni-section title="系统管理" type="line"></uni-section>
    <view class="grid-body">
      <uni-grid :column="4" :showBorder="false" @change="changeGrid">
        <uni-grid-item v-for="(item,index) in gridList" :key="index" :index = 'index'>
          <view class="grid-item-box">
            <uni-icons :type="item.meta.icon" size="30"></uni-icons>
            <text class="text">{{item.meta.title}}</text>
          </view>
        </uni-grid-item>
      </uni-grid>
    </view>
  </view>
</template>

<script>
	import {getRouters} from '@/api/system/router'
	import {getInfo} from '@/api/system/user.js'
	import storage from '@/utils/storage'
	import constant from '@/utils/constant'
	
  export default {
    data() {
      return {
        current: 0,
        swiperDotIndex: 0,
        data: [{
            image: '/static/images/banner/banner01.jpg'
          },
          {
            image: '/static/images/banner/banner02.jpg'
          },
          {
            image: '/static/images/banner/banner03.jpg'
          }
        ],
		// 路由展示列表
		gridList:[]
      }
    },
    methods: {
      clickBannerItem(item) {
        // console.info(item)
      },
      changeSwiper(e) {
        this.current = e.detail.current
      },
      changeGrid(e) {
		// console.log(e);
		// console.log(this.gridList[e.detail.index].component);
		
		// if(e.detail.index == '2'){
		// 	this.$modal.showToast('模块建设中~')
		// }else{
		// 	uni.navigateTo({url: '/' + this.gridList[e.detail.index].component})
		// }
		// console.log(this.gridList[e.detail.index]);
		if(this.gridList[e.detail.index].name == "Ask_for_leave"){
			uni.navigateTo({url: '/' + this.gridList[e.detail.index].component + `?type=请假&role=${this.userInfo.roles[0]}`})
		}else{
			uni.navigateTo({url: '/' + this.gridList[e.detail.index].component})
		}
		
		// if(e.detail.index == '0'){
		// 	uni.navigateTo({url:'/pages/work/user/index'})
		// }else if(e.detail.index == '1'){
		// 	uni.navigateTo({url:'/pages/work/role/index'})
		// }else if(e.detail.index == '2'){
		// 	uni.navigateTo({url:'/pages/work/dept/index'})
		// }else if(e.detail.index == '3'){
		// 	uni.navigateTo({url:'/pages/work/post/index'})
		// }else if(e.detail.index == '4'){
		// 	uni.navigateTo({url:'/pages/work/notice/index'})
		// }
        // this.$modal.showToast('模块建设中~')
      },
	  // 获取权限路由
	  async getGrid(){
	  	const res = await getRouters()
		res.data.forEach(item => {
			if(item.name == 'Work'){
				this.gridList = item.children
			}
		})
	 //  	console.log(res);
		// console.log(this.gridList);
	  }
    },
	async created(){
		const res = await getInfo()
		this.userInfo = res
		console.log('userinfo');
		console.log(this.userInfo);
		
		this.getGrid()
	}
  }
</script>

<style lang="scss">
  /* #ifndef APP-NVUE */
  page {
    display: flex;
    flex-direction: column;
    box-sizing: border-box;
    background-color: #fff;
    min-height: 100%;
    height: auto;
  }

  view {
    font-size: 14px;
    line-height: inherit;
  }

  /* #endif */

  .text {
    text-align: center;
    font-size: 26rpx;
    margin-top: 10rpx;
  }

  .grid-item-box {
    flex: 1;
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 15px 0;
  }

  .uni-margin-wrap {
    width: 690rpx;
    width: 100%;
    ;
  }

  .swiper {
    height: 300rpx;
  }

  .swiper-box {
    height: 150px;
  }

  .swiper-item {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #fff;
    height: 300rpx;
    line-height: 300rpx;
  }

  @media screen and (min-width: 500px) {
    .uni-swiper-dot-box {
      width: 400px;
      /* #ifndef APP-NVUE */
      margin: 0 auto;
      /* #endif */
      margin-top: 8px;
    }

    .image {
      width: 100%;
    }
  }
</style>
