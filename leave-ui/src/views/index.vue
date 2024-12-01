<template>
  <div class="app-container home">
    <div class="echarts-title">
      <div class="echarts-title-text">请销假可视化分析</div>
    </div>
    <div class="echarts-content">
      <div class="echarts-data-graphic">
        <div class="echarts-data-graphic-top">
          <div class="echarts-data-graphic-top-annular-chart">
            <div class="echarts-data-graphic-top-annular-chart-title">请销假统计饼状图</div>
            <div class="echarts-data-graphic-top-annular-chart-image" ref="pieChart"></div>
          </div>
          <div class="echarts-data-graphic-top-calendar">
            <div class="echarts-data-graphic-top-calendar-title">请销假日历图</div>
            <div class="echarts-data-graphic-top-calendar-image" ref="calendarPieChart"></div>
          </div>
        </div>
        <div class="echarts-data-graphic-bottom"></div>
      </div>
      <div class="echarts-data-carts">
        <el-card style="width: 100%;background: #1ab394" shadow="always">
          <div class="data-number">{{askForLeaveTotal}}</div>
          <div class="carts-text">请假总数</div>
        </el-card>
        <el-card style="width: 100%;background: #CDACFF" shadow="always">
          <div class="data-number">{{backForLeaveTotal}}</div>
          <div class="carts-text">销假总数</div>
        </el-card>
        <el-card style="width: 100%;background: #FF9DB7" shadow="always">
          <div class="data-number">{{dayAskForLeaveTotal}}</div>
          <div class="carts-text">当日请假总数</div>
        </el-card>
        <el-card style="width: 100%;background: #F2FF69" shadow="always">
          <div class="data-number">{{ dayBackForLeaveTotal }}</div>
          <div class="carts-text">当日销假总数</div>
        </el-card>
        <el-card style="width: 100%;background: #8EFF77" shadow="always">
          <div class="data-number">{{ monthAskForLeaveTotal }}</div>
          <div class="carts-text">当月请假总数</div>
        </el-card>
        <el-card style="width: 100%;background: #68FFFF" shadow="always">
          <div class="data-number">{{monthBackForLeaveTotal}}</div>
          <div class="carts-text">当月销假总数</div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import {listPermit,listBackPermit} from "@/api/permit/permit";

export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.8.8",

      pageNum: 1,
      pageSize: 5,

      // 请假数据数组
      askForLeaveList:[],

      // 销假数据数组
      backForLeaveList: [],

      // 请假总数
      askForLeaveTotal: 0,

      // 销假总数
      backForLeaveTotal: 0,

      // 当日请假总数
      dayAskForLeaveTotal: 0,

      // 当日销假总数
      dayBackForLeaveTotal: 0,

      //当月请假总数
      monthAskForLeaveTotal: 0,

      //当月销假总数
      monthBackForLeaveTotal: 0
    };
  },
  methods: {
    // 获取初始数据
    async getData(){
      // ----------------------------------------------------------
      //               获取请假总数
      // ----------------------------------------------------------
      // 获取请假总数(默认pageNum:1,pageSize:10)
      const res = await listPermit()
      console.log(res)

      // 再次获取请假列表（将pageSize改为total）
      const askForLeaveRes = await listPermit({pageNum: this.pageNum,pageSize: res.total})

      this.askForLeaveList = askForLeaveRes.rows
      this.askForLeaveTotal = askForLeaveRes.total

      // ----------------------------------------------------------
      //               获取销假总数
      // ----------------------------------------------------------

      // 获取销假总数(默认pageNum:1,pageSize:10)
      const res1 = await listBackPermit({pageNum:this.pageNum,pageSize: this.pageSize},null,'1')

      // 再次获取销假列表（将pageSize改为total）
      const BackForLeaveRes = await listBackPermit({pageNum: this.pageNum,pageSize: res1.total},null,'1')

      this.backForLeaveList = BackForLeaveRes.rows
      this.backForLeaveTotal = BackForLeaveRes.total

      // --------------------------------------------------------------
      //            获取当日请假总数
      // --------------------------------------------------------------

      // 获取当日请假总数 --- 获取当前时间
      const currentDate = new Date();
      const currentYear = currentDate.getFullYear();
      const currentMonth = currentDate.getMonth()+1; // 注意：getMonth()返回的是0-11的索引
      const currentDay = currentDate.getDate();

      const filteredData = this.askForLeaveList.filter(item => {
        const itemDate = new Date(item.permitTime);
        return (
          itemDate.getFullYear() === currentYear &&
          itemDate.getMonth()+1 === currentMonth &&
          itemDate.getDate() === currentDay
        );
      });

      this.dayAskForLeaveTotal = filteredData.length

      // ---------------------------------------------------
      //          获取当日销假总数
      // ---------------------------------------------------
      const filteredData1 = this.backForLeaveList.filter(item => {
        const itemDate = new Date(item.permitTime);
        return (
          itemDate.getFullYear() === currentYear &&
          itemDate.getMonth()+1 === currentMonth &&
          itemDate.getDate() === currentDay
        );
      });


      this.dayBackForLeaveTotal = filteredData1.length

      // ---------------------------------------------------
      //          获取当月请假总数
      // ---------------------------------------------------
      // 初始化一个空数组来存储本月的数据
      let thisMonthAskData = [];

      // 遍历接口返回的数据
      this.askForLeaveList.forEach(item => {
        let itemDate = new Date(item.permitTime);
        let itemYear = itemDate.getFullYear();
        let itemMonth = itemDate.getMonth() + 1; // 月份从0开始，需要加1

        // 检查日期是否在当前月份内
        if (itemYear === currentYear && itemMonth === currentMonth) {
          // 如果是本月的数据，则添加到新数组中
          thisMonthAskData.push(item);
        }
      });

      // 输出本月的数据
      console.log(thisMonthAskData);

      this.monthAskForLeaveTotal = thisMonthAskData.length

      // ---------------------------------------------------
      //          获取当月销假总数
      // ---------------------------------------------------

      // 初始化一个空数组来存储本月的数据
      let thisMonthBackData = [];

      // 遍历接口返回的数据
      this.backForLeaveList.forEach(item => {
        let itemDate = new Date(item.backTime);
        let itemYear = itemDate.getFullYear();
        let itemMonth = itemDate.getMonth() + 1; // 月份从0开始，需要加1

        // 检查日期是否在当前月份内
        if (itemYear === currentYear && itemMonth === currentMonth) {
          // 如果是本月的数据，则添加到新数组中
          thisMonthBackData.push(item);
        }
      });

      // 输出本月的数据
      console.log('本月数据')
      console.log(thisMonthBackData);

      this.monthBackForLeaveTotal = thisMonthBackData.length
    },

    getCalendarOne(year,month,day,type){
      if(type == 'askForLeave'){
        let data = this.askForLeaveList.filter(item => {
          const itemDate = new Date(item.permitTime);
          return (
            itemDate.getFullYear() === year &&
            itemDate.getMonth()+1 === month &&
            itemDate.getDate() === day
          );
        });
        return data.length
      }else{
        let data = this.backForLeaveList.filter(item => {
          const itemDate = new Date(item.permitTime);
          return (
            itemDate.getFullYear() === year &&
            itemDate.getMonth()+1 === month &&
            itemDate.getDate() === day
          );
        });
        return data.length
      }
    },

    // 初始化饼状图
    initPieChart(){
      console.log('饼状图')
      console.log(this.$refs.pieChart)
      let pieChart = echarts.init(this.$refs.pieChart)
      // 指定图表的配置项和数据
      let option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 40,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: this.askForLeaveTotal, name: '请假总数' },
              { value: this.backForLeaveTotal, name: '销假总数' },
            ]
          }
        ]
      }
      // 使用刚指定的配置项和数据显示图表。
      pieChart.setOption(option);
    },

    // 初始化日历饼状图
    initCalendarPieChart(){
      let calendarPieChart = echarts.init(this.$refs.calendarPieChart)

      // 获取当日请假总数 --- 获取当前时间
      const currentDate = new Date();
      const currentYear = currentDate.getFullYear();
      const currentMonth = currentDate.getMonth()+1; // 注意：getMonth()返回的是0-11的索引
      const currentDay = currentDate.getDate();

      const cellSize = [80, 80];
      const pieRadius = 30;
      function getVirtualData() {
        const date = +echarts.time.parse(currentYear+'-'+currentMonth+'-01');
        const end = +echarts.time.parse(currentYear+'-'+(currentMonth+1)+'-01');
        const dayTime = 3600 * 24 * 1000;
        const data = [];
        for (let time = date; time < end; time += dayTime) {
          data.push([
            echarts.time.format(time, '{yyyy}-{MM}-{dd}', false),
            Math.floor(Math.random() * 10000)
          ]);
        }
        return data;
      }
      const scatterData = getVirtualData();
      const pieSeries = scatterData.map((item, index) => {
        console.log(index)
        console.log(item)
        return {
          type: 'pie',
          id: 'pie-' + index,
          center: item[0],
          radius: pieRadius,
          coordinateSystem: 'calendar',
          label: {
            formatter: '{c}',
            position: 'inside'
          },
          data: [
            { name: '请假天数', value: this.getCalendarOne(currentYear,currentMonth,index+1,'askForLeave') },
            { name: '销假天数', value: this.getCalendarOne(currentYear,currentMonth,index+1,'backForLeave') }
          ]
        };
      });
      let option = {
        tooltip: {},
        legend: {
          data: ['Work', 'Entertainment', 'Sleep'],
          bottom: 15
        },
        calendar: {
          top: 'middle',
          left: 'center',
          orient: 'vertical',
          cellSize: cellSize,
          yearLabel: {
            show: false,
            fontSize: 25
          },
          dayLabel: {
            margin: 5,
            firstDay: 1,
            nameMap: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
          },
          monthLabel: {
            show: false
          },
          range: [currentYear+'-'+currentMonth]
        },
        series: [
          {
            id: 'label',
            type: 'scatter',
            coordinateSystem: 'calendar',
            symbolSize: 0,
            label: {
              show: true,
              formatter: function (params) {
                return echarts.time.format(params.value[0], '{dd}', false);
              },
              offset: [-cellSize[0] / 2 + 10, -cellSize[1] / 2 + 10],
              fontSize: 12
            },
            data: scatterData
          },
          ...pieSeries
        ]
      };

      calendarPieChart.setOption(option);
    }
  },
  async created() {
    await this.getData()
    console.log("this")
    console.log(this)
    this.initPieChart()

    this.initCalendarPieChart()
  }
};
</script>

<style scoped>
  .echarts-title{
    width: 100%;
    height: 100px;
    border: 2px solid #F2F6FC;
    background: #F2F6FC;
    border-radius: 10px;
  }

  .echarts-title-text{
    font-size: 20px;
    font-weight: bolder;
    padding:35px 15px;
  }

  .echarts-content{
    width: 100%;
    height: 700px;
    margin-top:20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .echarts-data-graphic{
    width: 75%;
    height: 100%;
  }

  .echarts-data-carts{
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    width: 20%;
  }

  .echarts-data-graphic-top,.echarts-data-graphic-bottom{
    height: 600px;
    width: 100%;
    display: flex;
    justify-content: space-between;
  }

  .echarts-data-graphic-top-annular-chart{
    width: 40%;
    height: 100%;
    border: 1px solid rgba(230, 237, 238, 0.8);
  }

  .echarts-data-graphic-top-calendar{
    width: 55%;
    height: 100%;
    border: 1px solid rgba(230, 237, 238, 0.8);
  }

  .echarts-data-graphic-top-annular-chart-title,.echarts-data-graphic-top-calendar-title{
    background: #f5f5ff;
    border-bottom: 1px solid #F2F6FC;
    height: 12%;
    line-height: 35px;
    padding: 0 10px;
  }

  .echarts-data-graphic-top-annular-chart-image,.echarts-data-graphic-top-calendar-image{
    height: 88%;
    width: 100%;
  }

  .data-number{
    font-size: 25px;
    color: white;
    font-weight: bolder;
  }

  .carts-text{
    color: white;
  }

  .el-card{
    border-radius: 12px;
  }

  .el-card.is-always-shadow{
    box-shadow: 0 2px 12px 2px rgba(0,0,0,0.2);
  }
</style>

