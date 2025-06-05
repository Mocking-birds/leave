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
<!--      <el-form-item label="请假理由" prop="reason">-->
<!--        <el-input-->
<!--          v-model="queryParams.reason"-->
<!--          placeholder="请输入请假理由"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="起始日期" prop="startTime">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.startTime"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择起始日期">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="结束日期" prop="endTime">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.endTime"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择结束日期">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
      <el-form-item label="是否销假" prop="isBack">
        <el-select v-model="queryParams.isBack" placeholder="请选择请假状态" clearable>
          <el-option
            v-for="dict in dict.type.permit_is_back"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="请假状态" prop="leaveStatus">
        <el-select v-model="queryParams.leaveStatus" placeholder="请选择请假状态" clearable>
          <el-option
            v-for="dict in dict.type.permit_leave_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
<!--      <el-form-item label="请假时间" prop="permitTime">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.permitTime"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择请假时间">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="销假时间" prop="backTime">-->
<!--        <el-date-picker clearable-->
<!--          v-model="queryParams.backTime"-->
<!--          type="date"-->
<!--          value-format="yyyy-MM-dd"-->
<!--          placeholder="请选择销假时间">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['permit:permit:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['permit:permit:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['permit:permit:remove']"
        >删除</el-button>
      </el-col>
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
      <el-table-column label="假条id" align="center" prop="leaveId" />
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
      <el-table-column label="是否销假" align="center" prop="isBack">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.permit_is_back" :value="scope.row.isBack"/>
        </template>
      </el-table-column>
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
      <el-table-column label="销假时间" align="center" prop="backTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.backTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['permit:permit:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['permit:permit:remove']"
          >删除</el-button>
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

    <!-- 添加或修改假条信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="740px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
<!--        <el-form-item label="用户id" prop="userId">-->
<!--          <el-input v-model="form.userId" placeholder="请输入用户id" />-->
<!--        </el-form-item>-->
        <el-form-item label="请假类型" prop="permitType">
          <el-select v-model="form.permitType" placeholder="请选择请假类型">
            <el-option
              v-for="dict in dict.type.permit_leave_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="请假理由" prop="reason">
          <el-input v-model="form.reason" placeholder="请输入请假理由" />
        </el-form-item>
        <el-form-item label="起始日期" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择起始日期"
            @change="changeTime"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束日期" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束日期"
            @change="changeTime"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否销假" prop="isBack">
          <el-radio-group v-model="form.isBack">
            <el-radio
              v-for="dict in dict.type.permit_is_back"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="请假状态" prop="leaveStatus">
          <el-radio-group v-model="form.leaveStatus">
            <el-radio
              v-for="dict in dict.type.permit_leave_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-divider content-position="center">位置信息信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddPermitLocation">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeletePermitLocation">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="permitLocationList" :row-class-name="rowPermitLocationIndex" @selection-change="handlePermitLocationSelectionChange" ref="permitLocation">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="纬度" prop="latitude" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.latitude" placeholder="请输入纬度" />
            </template>
          </el-table-column>
          <el-table-column label="经度" prop="longitude" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.longitude" placeholder="请输入经度" />
            </template>
          </el-table-column>
          <el-table-column label="位置名称" prop="locationName" width="150">
            <template slot-scope="scope">
              <el-input v-model="scope.row.locationName" placeholder="请输入位置名称" />
            </template>
          </el-table-column>
          <el-table-column label="位置类型" prop="locationType" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.locationType" placeholder="请选择位置类型">
                <el-option
                  v-for="dict in dict.type.permit_location_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
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
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        permitType: null,
        reason: null,
        startTime: null,
        endTime: null,
        isBack: null,
        leaveStatus: null,
        permitTime: null,
        backTime: null
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
    console.log(user)
    this.getList();
  },
  methods: {
    /** 查询假条信息列表 */
    async getList() {
      this.loading = true;
      // 根据role权限限定列表

      if(user.state.roles[0] == 'counsellor'){
        // 辅导员
        const res = await listPermitByDept({pageNum: this.queryParams.pageNum, pageSize: this.queryParams.pageSize},user.state.dept.parentId,'')
        console.log('辅导员')
        console.log(res)
        this.permitList = res.rows
        this.total = res.total
      }else if(user.state.roles[0] == 'director'){
        // 系主任
        const res = await listPermitByDept({pageNum: this.queryParams.pageNum, pageSize: this.queryParams.pageSize},user.state.dept.deptId,'')
        console.log('系主任')
        console.log(res)
        this.permitList = res.rows
        this.total = res.total
      }else{
        const res = await listPermit(this.queryParams)
        this.permitList = res.rows;
        this.total = res.total;
      }

      this.loading = false;

      console.log(this.permitList)
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加假条信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const leaveId = row.leaveId || this.ids
      getPermit(leaveId).then(response => {
        this.form = response.data;
        this.permitLocationList = response.data.permitLocationList;
        this.open = true;
        this.title = "修改假条信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.permitLocationList = this.permitLocationList;
          if (this.form.leaveId != null) {
            updatePermit(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPermit(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const leaveIds = row.leaveId || this.ids;
      this.$modal.confirm('是否确认删除假条信息编号为"' + leaveIds + '"的数据项？').then(function() {
        return delPermit(leaveIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
	/** 位置信息序号 */
    rowPermitLocationIndex({ row, rowIndex }) {
      row.index = rowIndex + 1;
    },
    /** 位置信息添加按钮操作 */
    handleAddPermitLocation() {
      let obj = {};
      obj.latitude = "";
      obj.longitude = "";
      obj.locationName = "";
      obj.locationType = "";
      this.permitLocationList.push(obj);
    },
    /** 位置信息删除按钮操作 */
    handleDeletePermitLocation() {
      if (this.checkedPermitLocation.length == 0) {
        this.$modal.msgError("请先选择要删除的位置信息数据");
      } else {
        const permitLocationList = this.permitLocationList;
        const checkedPermitLocation = this.checkedPermitLocation;
        this.permitLocationList = permitLocationList.filter(function(item) {
          return checkedPermitLocation.indexOf(item.index) == -1
        });
      }
    },
    /** 复选框选中数据 */
    handlePermitLocationSelectionChange(selection) {
      this.checkedPermitLocation = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('permit/permit/export', {
        ...this.queryParams
      }, `permit_${new Date().getTime()}.xlsx`)
    },
    // 当时间改变时修改请假天数
    changeTime(){
      console.log('时间改变')
      console.log(this.form)

      // 将日期字符串转换为Date对象
      const date1Obj = new Date(this.form.startTime);
      const date2Obj = new Date(this.form.endTime);

      // 计算时间差（毫秒）
      const timeDifference = Math.abs(date2Obj - date1Obj);

      // 将时间差转换为天数
      this.form.permitDays = Math.ceil(timeDifference / (1000 * 60 * 60 * 24));
    }
  }
};
</script>
