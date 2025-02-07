<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="请假类型" prop="permitType">
        <el-select v-model="queryParams.permitType" placeholder="请选择请假类型" clearable>
          <el-option
            v-for="dict in dict.type.permit_leave_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8">
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="primary"-->
<!--          plain-->
<!--          icon="el-icon-plus"-->
<!--          size="mini"-->
<!--          @click="handleAdd"-->
<!--          v-hasPermi="['permit:permit:add']"-->
<!--        >新增</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['permit:permit:edit']"-->
<!--        >修改</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="danger"-->
<!--          plain-->
<!--          icon="el-icon-delete"-->
<!--          size="mini"-->
<!--          :disabled="multiple"-->
<!--          @click="handleDelete"-->
<!--          v-hasPermi="['permit:permit:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['permit:permit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="Array.isArray(permitList)" v-loading="loading" :data="permitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column label="假条id" align="center" prop="leaveId" />-->
      <el-table-column label="用户姓名" align="center" prop="user.userName" />
      <el-table-column label="请假类型" align="center" prop="permitType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.permit_leave_type" :value="scope.row.permitType"/>
        </template>
      </el-table-column>
      <el-table-column label="请假理由" align="center" prop="reason" />
      <el-table-column label="起始日期" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束日期" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
<!--      <el-table-column label="是否销假" align="center" prop="isBack">-->
<!--        <template slot-scope="scope">-->
<!--          <dict-tag :options="dict.type.permit_is_back" :value="scope.row.isBack"/>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="请假状态" align="center" prop="leaveStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.permit_leave_status" :value="scope.row.leaveStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="请假天数" align="center">
        <template slot-scope="scope">
          {{scope.row.permitDays}}天
        </template>
      </el-table-column>
      <el-table-column label="请假时间" align="center" prop="permitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.permitTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
<!--      <el-table-column label="销假时间" align="center" prop="backTime" width="180">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ parseTime(scope.row.backTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            class = 'pass'
            size="default"
            type="text"
            icon="el-icon-success"
            @click="handleUpdate(scope.row,'1')"
            v-hasPermi="['permit:permit:edit']"
          >
            通过
          </el-button>
          <el-button
            class="defeat"
            size="default"
            type="text"
            icon="el-icon-error"
            @click="handleUpdate(scope.row,'2')"
            v-hasPermi="['permit:permit:edit']"
          > 驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listPermit, getPermit, delPermit, addPermit, updatePermit, listPermitByDept } from "@/api/permit/permit";
import user from "@/store/modules/user"

export default {
  name: "Permit",
  dicts: ['permit_leave_type', 'permit_leave_status', 'permit_location_type', 'permit_is_back'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedPermitLocation: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 假条信息表格数据
      permitList: [],
      // 位置信息表格数据
      permitLocationList: [],
      // 弹出层标题
      title: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        permitType: null,
        isBack: null,
        leaveStatus: 0,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        permitType: [
          { required: true, message: "请假类型不能为空", trigger: "change" }
        ],
        startTime: [
          { required: true, message: "起始日期不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束日期不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询请假假条信息列表 */
    async getList() {
      this.loading = true;

      if(user.state.roles[0] == 'admin'){
        // 超级管理员
        let res = await listPermitByDept(this.queryParams,'','')
        this.permitList = res.rows
        this.total = res.total
        console.log(res)
      }else if(user.state.roles[0] == 'director'){
        // 系主任
        let res = await listPermitByDept(this.queryParams,user.state.dept.deptId,true)
        this.permitList = res.rows
        this.total = res.total
        console.log(res)
      }else if(user.state.roles[0] == 'counsellor'){
        // 辅导员
        let res = await listPermitByDept(this.queryParams,user.state.dept.parentId,false)
        this.permitList = res.rows
        this.total = res.total
        console.log(res)
      }

      this.loading = false;

      // listPermit(this.queryParams).then(response => {
      //   this.permitList = response.rows;
      //   this.total = response.total;
      //   this.loading = false;
      //
      //   console.log(this.permitList)
      // });
    },
    // 表单重置
    reset() {
      this.form = {
        leaveId: null,
        userId: null,
        permitType: null,
        reason: null,
        startTime: null,
        endTime: null,
        isBack: null,
        leaveStatus: null,
        permitTime: null,
        backTime: null
      };
      this.permitLocationList = [];
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      console.log(this.queryParams)
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.leaveId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    async handleUpdate(row,status) {
      console.log(row)
      this.reset();
      // const leaveId = row.leaveId || this.ids
      this.form = row
      this.form.leaveStatus = status
      console.log(this.form)
      const res = await updatePermit(this.form)
      console.log(res)
      this.$modal.msgSuccess("修改成功");
      // this.open = false;
      await this.getList();
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('permit/permit/export', {
        ...this.queryParams
      }, `permit_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style>
.pass,.defeat{
  font-weight: 800;
  font-size: 15px;
}

.pass{
  color: #67C23A;
}

.pass:hover{
  color: #3d9b09;
}

.defeat{
  color: #F56C6C;
}

.defeat:hover{
  color: #a86b76;
}
</style>
