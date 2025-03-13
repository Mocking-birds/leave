<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['swiper:swiper:add']"
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
          v-hasPermi="['swiper:swiper:edit']"
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
          v-hasPermi="['swiper:swiper:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['swiper:swiper:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="swiperList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="轮播图描述信息" align="center" prop="description" />
      <el-table-column label="轮播图路径" align="center" prop="imageUrl">
        <template slot-scope="scope">
          <img :src="scope.row.imageUrl" style="width: 80px"></img>
        </template>
      </el-table-column>
      <el-table-column label="轮播图状态" align="center" prop="isActive">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.isActive"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['swiper:swiper:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['swiper:swiper:remove']"
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

    <!-- 添加或修改轮播图管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="轮播图描述信息" prop="description">
          <el-input v-model="form.description" placeholder="请输入轮播图描述信息" />
        </el-form-item>
        <el-form-item label="轮播图：" prop="imageUrl">
<!--          <el-input v-model="form.imageUrl" placeholder="请输入轮播图路径" />-->
            <el-upload
              class="avatar-uploader"
              action=""
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload">
              <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" style="width: 275px; height: 275px">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
        </el-form-item>
        <el-form-item label="轮播图状态" prop="isActive">
          <el-select v-model="form.isActive" placeholder="轮播图状态" clearable>
            <el-option
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSwiper, getSwiper, delSwiper, addSwiper, updateSwiper, editSwiperImg } from "@/api/system/swiper";

export default {
  name: "Swiper",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 轮播图管理表格数据
      swiperList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        description: null,
        imageUrl: null,
        isActive: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        imageUrl: [
          { required: true, message: "轮播图路径不能为空", trigger: "blur" }
        ],
      },
      // 图片file
      file: ''
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询轮播图管理列表 */
    getList() {
      this.loading = true;
      listSwiper(this.queryParams).then(response => {
        this.swiperList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        swiperId: null,
        description: null,
        imageUrl: null,
        isActive: null
      };
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
      this.ids = selection.map(item => item.swiperId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加轮播图管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const swiperId = row.swiperId || this.ids
      getSwiper(swiperId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改轮播图管理";
      });
    },
    /** 提交按钮 */
     submitForm() {
      this.$refs["form"].validate(async valid => {
        if (valid) {
          if (this.form.swiperId != null) {
            console.log(this.file)
            if(this.file != ''){
              const res = await editSwiperImg(this.file)
              console.log(res)
              this.form.imageUrl = res
            }
            updateSwiper(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSwiper(this.form).then(response => {
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
      const swiperIds = row.swiperId || this.ids;
      this.$modal.confirm('是否确认删除轮播图管理编号为"' + swiperIds + '"的数据项？').then(function() {
        return delSwiper(swiperIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('swiper/swiper/export', {
        ...this.queryParams
      }, `swiper_${new Date().getTime()}.xlsx`)
    },
    handleAvatarSuccess(res,file){
      console.log('成功')
      console.log(res)
      console.log(file)
      this.form.imageUrl = URL.createObjectURL(res.raw);
      this.file = file[0].raw
    },
    beforeAvatarUpload(file){
      console.log('beforeUpload')
      console.log(file)
    }
  }
};
</script>
