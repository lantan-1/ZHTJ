<template>
  <div class="league-wiki-container">
    <div class="wiki-layout">
      <!-- 左侧分类树 -->
      <el-card class="category-card">
        <template #header>
          <div class="category-header">
            <span>分类导航</span>
          </div>
        </template>
        <el-tree
          ref="categoryTreeRef"
          :data="categoryTree"
          node-key="id"
          :props="{ label: 'name' }"
          highlight-current
          @node-click="handleNodeClick"
        />
      </el-card>

      <!-- 右侧内容区 -->
      <div class="wiki-content">
        <!-- 搜索栏 -->
        <el-card class="search-card">
          <el-form :inline="true" :model="queryParams" class="search-form">
            <el-form-item label="关键词">
              <el-input v-model="queryParams.keyword" placeholder="请输入关键词" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery">
                <el-icon><Search /></el-icon>查询
              </el-button>
              <el-button @click="resetQuery">
                <el-icon><Refresh /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 百科列表 -->
        <template v-if="viewMode === 'list'">
          <el-card class="list-card" v-loading="loading">
            <template #header>
              <div class="list-header">
                <span>{{ currentCategory?.name || '全部百科' }}</span>
                <span class="article-count">共{{ totalRecords }}篇</span>
              </div>
            </template>
            <div v-if="wikiList.length === 0" class="empty-data">
              <el-empty description="暂无数据" />
            </div>
            <div v-else class="wiki-list">
              <div 
                v-for="item in wikiList" 
                :key="item.id" 
                class="wiki-item"
                :class="{ 'wiki-item-highlighted': selectedSubCategory && item.categoryId === selectedSubCategory }"
                @click="handleViewDetail(item)"
              >
                <div class="wiki-item-title">{{ item.title }}</div>
                <div class="wiki-item-desc">{{ item.summary }}</div>
                <div class="wiki-item-footer">
                  <span class="wiki-item-category">{{ item.categoryName }}</span>
                  <span class="wiki-item-date">更新于 {{ formatDate(item.updateTime) }}</span>
                  <span class="wiki-item-views">
                    <el-icon><View /></el-icon> {{ item.views }}
                  </span>
                </div>
              </div>
            </div>
            <el-pagination
              v-if="totalRecords > 0"
              class="pagination"
              :total="totalRecords"
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
            />
          </el-card>
        </template>

        <!-- 百科详情 -->
        <template v-else-if="viewMode === 'detail'">
          <el-card class="detail-card" v-loading="detailLoading">
            <template #header>
              <div class="detail-header">
                <el-button type="default" @click="backToList">
                  <el-icon><Back /></el-icon>返回列表
                </el-button>
              </div>
            </template>
            <div class="wiki-detail">
              <h1 class="wiki-title">{{ currentWiki.title }}</h1>
              <div class="wiki-meta">
                <span class="wiki-category">{{ currentWiki.categoryName }}</span>
                <span class="wiki-author">作者: {{ currentWiki.creatorName }}</span>
                <span class="wiki-time">更新时间: {{ formatDate(currentWiki.updateTime) }}</span>
                <span class="wiki-views">
                  <el-icon><View /></el-icon> {{ currentWiki.views }}
                </span>
              </div>
              <div class="wiki-content-html" v-html="currentWiki.content"></div>
            </div>
          </el-card>
        </template>
      </div>
    </div>

    <!-- 添加/编辑百科对话框 -->
    <el-dialog 
      :title="dialogType === 'add' ? '添加团务百科' : '编辑团务百科'" 
      v-model="dialogVisible"
      width="800px"
      append-to-body
    >
      <el-form 
        ref="wikiFormRef"
        :model="wikiForm"
        :rules="wikiRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="wikiForm.title" placeholder="请输入标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-cascader
            v-model="wikiForm.categoryId"
            :options="categoryOptions"
            :props="{ 
              checkStrictly: true,
              value: 'id',
              label: 'name',
              children: 'children'
            }"
            placeholder="请选择分类"
            clearable
          />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input 
            v-model="wikiForm.summary" 
            type="textarea"
            placeholder="请输入摘要" 
            maxlength="200"
            :rows="3"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <div class="editor-container">
            <!-- 这里应该集成富文本编辑器，如wangEditor、tinymce等 -->
            <!-- 此处仅用textarea模拟 -->
            <el-input
              v-model="wikiForm.content"
              type="textarea"
              :rows="15"
              placeholder="请输入百科内容"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确认</el-button>
      </template>
    </el-dialog>

    <!-- 添加分类对话框 -->
    <el-dialog
      title="添加分类"
      v-model="categoryDialogVisible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="父级分类">
          <el-cascader
            v-model="categoryForm.parentId"
            :options="categoryOptions"
            :props="{ 
              checkStrictly: true,
              value: 'id',
              label: 'name',
              children: 'children'
            }"
            placeholder="请选择父级分类，不选择则为一级分类"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategoryForm" :loading="submitLoading">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { Plus, Search, Refresh, View, Back, CirclePlus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { useUserStore } from '@/stores/user';
import { formatDate } from '@/utils/format';

// 用户信息
const userStore = useUserStore();

// 查询参数
const queryParams = reactive({
  keyword: '',
  categoryId: 1, // 默认显示"团的基本知识"分类
  pageNum: 1,
  pageSize: 10
});

// 分页参数
const pageNum = ref(1);
const pageSize = ref(10);
const totalRecords = ref(0);

// 视图模式：列表/详情
const viewMode = ref('list');

// 百科列表
const wikiList = ref([]);
const loading = ref(false);
const detailLoading = ref(false);

// 当前分类与百科
const currentCategory = ref(null);
const selectedSubCategory = ref(null);
const currentWiki = ref({
  id: 0,
  title: '',
  summary: '',
  content: '',
  categoryId: 0,
  categoryName: '',
  creatorId: 0,
  creatorName: '',
  views: 0,
  createTime: '',
  updateTime: ''
});

// 分类树
const categoryTreeRef = ref();
const categoryTree = ref([
  {
    id: 1,
    name: '团的基本知识',
    children: [
      { id: 11, name: '团的性质和宗旨' },
      { id: 12, name: '团的组织制度' },
      { id: 13, name: '团员的权利和义务' }
    ]
  },
  {
    id: 2,
    name: '团的工作制度',
    children: [
      { id: 21, name: '团的代表大会' },
      { id: 22, name: '团的会议制度' },
      { id: 23, name: '团的工作规范' }
    ]
  },
  {
    id: 3,
    name: '团的历史与传统',
    children: [
      { id: 31, name: '团的发展历程' },
      { id: 32, name: '团的优良传统' }
    ]
  },
  {
    id: 4,
    name: '中国共青团简介',
    children: [
      { id: 41, name: '基本概况' },
      { id: 42, name: '基本任务' },
      { id: 43, name: '组织象征和标志' },
      { id: 44, name: '创立历史' },
      { id: 45, name: '历次全国代表大会' }
    ]
  }
]);

// 分类选项（用于表单选择）
const categoryOptions = computed(() => {
  return categoryTree.value.map(item => ({
    id: item.id,
    name: item.name,
    children: item.children ? item.children.map(child => ({
      id: child.id,
      name: child.name
    })) : []
  }));
});

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref('add');
const wikiFormRef = ref<FormInstance>();
const submitLoading = ref(false);

// 百科表单
const wikiForm = reactive({
  id: undefined,
  title: '',
  categoryId: undefined,
  summary: '',
  content: ''
});

// 表单验证规则
const wikiRules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  summary: [
    { required: true, message: '请输入摘要', trigger: 'blur' },
    { max: 200, message: '长度不超过 200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入百科内容', trigger: 'blur' }
  ]
});

// 分类对话框相关
const categoryDialogVisible = ref(false);
const categoryFormRef = ref<FormInstance>();

// 分类表单
const categoryForm = reactive({
  parentId: undefined,
  name: '',
  sort: 0
});

// 分类表单验证规则
const categoryRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
});

// 生命周期钩子
onMounted(() => {
  // 初始化时获取团的基本知识分类的数据
  getWikiList();
  
  // 设置树组件默认选中的节点
  setTimeout(() => {
    if (categoryTreeRef.value) {
      // 默认选中"团的基本知识"分类
      categoryTreeRef.value.setCurrentKey(1);
      // 设置当前分类
      currentCategory.value = findCategoryById(categoryTree.value, 1);
    }
  }, 100);
});

// 获取百科列表
const getWikiList = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    setTimeout(() => {
      // 完整数据集
      const allWikiList = [
        // 已删除ID为1的"团的基本知识概述"文章
        {
          id: 1,
          title: '团的宗旨与性质',
          summary: '深入解析共青团的宗旨和性质。',
          content: '<h2>团的宗旨</h2><p>中国共产主义青年团的宗旨是：坚决拥护中国共产党的纲领，以马克思列宁主义、毛泽东思想、邓小平理论、"三个代表"重要思想、科学发展观、习近平新时代中国特色社会主义思想为行动指南，解放思想，实事求是，与时俱进，开拓创新，团结全国各族青年，为把我国建设成为富强民主文明和谐美丽的社会主义现代化强国，为最终实现共产主义而奋斗。</p><h2>团的性质</h2><p>中国共产主义青年团是中国共产党领导的先进青年的群众组织，是广大青年在实践中学习中国特色社会主义和共产主义的学校，是中国共产党的助手和后备军。</p>',
          categoryId: 11,
          categoryName: '团的性质和宗旨',
          creatorId: 1,
          creatorName: '蓝探',
          views: 320,
          createTime: '2023-04-15 08:45:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 3,
          title: '团的组织体系',
          summary: '介绍共青团的组织体系和工作机制。',
          content: '<h2>组织体系</h2><p>中国共产主义青年团的组织体系自上而下分为：全国、省（自治区、直辖市）、市（地区）、县（市区）四级组织。团的基层组织是团的工作的基础，在企业、农村、机关、学校、社区等都可以建立团的基层组织。</p><h2>工作机制</h2><p>共青团采用民主集中制的组织原则，各级团组织按照团章规定开展工作，贯彻执行上级团组织的决议，定期向上级团组织报告工作，参与同级党组织有关青年工作的决策。</p>',
          categoryId: 12,
          categoryName: '团的组织制度',
          creatorId: 3,
          creatorName: '蓝探',
          views: 205,
          createTime: '2023-04-12 11:20:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 2,
          title: '团员的权利与义务',
          summary: '介绍共青团团员应具有的权利和需要履行的义务。',
          content: '<h2>团员的权利</h2><ul><li>参加团的会议，阅读团的文件，接受团的教育和培训。</li><li>在团的会议上和团报团刊上，参加关于团的工作的讨论，对团的工作提出建议和倡议。</li><li>参加团组织讨论对自己处分的会议，并且有权为自己辩护。</li></ul><h2>团员的义务</h2><ul><li>认真学习马克思列宁主义、毛泽东思想、邓小平理论。</li><li>努力学习科学、文化、业务知识，不断提高为人民服务的本领。</li><li>带头遵纪守法，维护社会公德，引导青年创造美好的社会风尚。</li></ul>',
          categoryId: 13,
          categoryName: '团员的权利和义务',
          creatorId: 2,
          creatorName: '蓝探',
          views: 178,
          createTime: '2023-04-10 09:15:00',
          updateTime: '2025-05-04 05:04:54'
        },
        

        {
          id: 5,
          title: '团的代表大会制度',
          summary: '介绍共青团代表大会的选举和召开程序。',
          content: '<h2>代表大会的地位</h2><p>团的代表大会是各级团组织的最高权力机关，团的全国代表大会每五年召开一次，地方各级团的代表大会每五年召开一次。</p><h2>代表大会职权</h2><p>团的代表大会的主要职权包括：听取和审查同级团的委员会的工作报告；讨论和决定团的工作方针和任务；修改团的章程；选举同级团的委员会。</p>',
          categoryId: 21,
          categoryName: '团的代表大会',
          creatorId: 4,
          creatorName: '蓝探',
          views: 187,
          createTime: '2023-04-16 14:25:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 6,
          title: '团的会议制度与规范',
          summary: '详细说明共青团各级组织的会议制度。',
          content: '<h2>团组织的会议制度</h2><p>团的各级组织定期召开会议，包括全体会议、常委会会议和书记处会议。团的基层组织按照不同类型，定期召开团员大会或团员代表大会，讨论决定本单位的重要事项。</p><h2>会议规范</h2><p>团的会议必须有三分之二以上的应到会人员到会方能召开。团的各种会议都必须认真贯彻执行民主集中制的原则，充分发扬民主，集体作出决定。</p>',
          categoryId: 22,
          categoryName: '团的会议制度',
          creatorId: 5,
          creatorName: '蓝探',
          views: 163,
          createTime: '2023-04-18 10:05:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 7,
          title: '团的工作规范与要求',
          summary: '解释共青团工作的基本规范与要求。',
          content: '<h2>工作规范</h2><p>团的工作必须遵循以下规范：坚持党的领导；注重思想政治引领；围绕中心，服务大局；坚持全心全意为青年服务；坚持从严治团。</p><h2>工作要求</h2><p>团的工作要求包括：紧密联系青年，倾听青年呼声，反映青年诉求；创新工作方法，增强吸引力和凝聚力；注重工作实效，避免形式主义；加强自身建设，保持先进性和纯洁性。</p>',
          categoryId: 23,
          categoryName: '团的工作规范',
          creatorId: 6,
          creatorName: '蓝探',
          views: 142,
          createTime: '2023-04-20 09:15:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 8,
          title: '中国共青团的发展历程',
          summary: '回顾中国共青团从建立到发展的历史进程。',
          content: '<h2>创建与发展</h2><p>中国共青团创建于1922年5月，历经社会主义革命和建设时期、改革开放和社会主义现代化建设新时期、中国特色社会主义新时代三个历史阶段，走过近百年光辉历程。</p><h2>重要历史节点</h2><p>1922年：中国社会主义青年团成立<br>1925年：改名为中国共产主义青年团<br>1949年：改名为中国新民主主义青年团<br>1957年：恢复中国共产主义青年团名称<br>1966-1978年：文革期间工作基本停止<br>1978年：恢复团的工作<br>2018年：召开团的十八大，开启新时代共青团事业新征程</p>',
          categoryId: 31,
          categoryName: '团的发展历程',
          creatorId: 7,
          creatorName: '蓝探',
          views: 289,
          createTime: '2023-04-22 15:40:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 9,
          title: '共青团的优良传统',
          summary: '介绍中国共青团的优良传统与精神。',
          content: '<h2>理想信念</h2><p>坚定不移跟党走，为共产主义事业而奋斗的理想信念。</p><h2>爱国主义</h2><p>热爱祖国、热爱人民、热爱社会主义的爱国主义情怀。</p><h2>实事求是</h2><p>解放思想、求真务实、与时俱进的思想路线。</p><h2>艰苦奋斗</h2><p>不怕困难、不畏艰险、艰苦奋斗的革命精神。</p><h2>密切联系青年</h2><p>深入青年、了解青年、服务青年的工作作风。</p>',
          categoryId: 32,
          categoryName: '团的优良传统',
          creatorId: 8,
          creatorName: '蓝探',
          views: 231,
          createTime: '2023-04-25 13:50:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 10,
          title: '中国共青团基本概况',
          summary: '详细介绍中国共青团的组织性质、组织成员、领导体制和基层组织。',
          content: '<h2>组织性质</h2><p>中国共产主义青年团是中国共产党领导的先进青年的群团组织，是广大青年在实践中学习中国特色社会主义和共产主义的学校，是中国共产党的助手和后备军。</p><p>中国共产党领导是中国特色社会主义最本质的特征，是中国特色社会主义制度的最大优势。中国共产主义青年团坚决拥护中国共产党的纲领，以马克思列宁主义、毛泽东思想、邓小平理论、"三个代表"重要思想、科学发展观、习近平新时代中国特色社会主义思想为行动指南。</p><p>中国共产主义青年团在中国共产党领导下发展壮大，始终站在革命斗争的前列，有着光荣的历史。在建立新中国，确立和巩固社会主义制度，发展社会主义的经济、政治、文化的进程中发挥了生力军和突击队作用，为党培养、输送了大批新生力量和工作骨干。党的十一届三中全会以来，共青团根据党的工作重心的转移，紧密围绕改革开放和经济建设开展工作，为推进社会主义现代化建设事业作出了重要贡献，促进了青年一代的健康成长。中国特色社会主义进入新时代，共青团紧扣时代主题，锐意改革创新，坚持从严治团，团结带领广大青年在党的领导下奋力投身伟大斗争、伟大工程、伟大事业、伟大梦想的生动实践。</p><h2>组织成员</h2><p>年龄在十四周岁以上，二十八周岁以下的中国青年，承认团的章程，愿意参加团的一个组织并在其中积极工作、执行团的决议和按期交纳团费的，可以申请加入中国共产主义青年团。</p><p>团员年满二十八周岁，没有担任团内职务，应该办理离团手续。</p><p>团员加入共产党以后仍保留团籍，年满二十八周岁，没有在团内担任职务，不再保留团籍。</p><h2>领导体制</h2><p>中国共产主义青年团是按照民主集中制组织起来的统一整体。团的全国领导机关，是团的全国代表大会和它产生的中央委员会。地方各级团的领导机关，是同级团的代表大会和它产生的团的委员会，团的各级委员会向同级代表大会负责并报告工作。有关全团性的工作，由团的中央委员会作出决定，统一部署。</p><p>团的全国代表大会每五年举行一次，由中央委员会召集，在特殊情况下，可以提前或延期举行。在全国代表大会闭会期间，中央委员会执行全国代表大会的决议，领导团的全部工作。</p><h2>基层组织</h2><p>企业、农村、机关、学校、科研院所、街道社区、社会组织、人民解放军连队、人民武装警察部队中队和其他基层单位，凡是有团员三人以上的，都应当建立团的基层组织。</p>',
          categoryId: 41,
          categoryName: '基本概况',
          creatorId: 1,
          creatorName: '蓝探',
          views: 520,
          createTime: '2023-05-04 10:00:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 11,
          title: '中国共青团的基本任务',
          summary: '介绍中国共青团在新时代的基本任务和使命。',
          content: '<h2>新时代的基本任务</h2><p>中国共产主义青年团在新时代的基本任务是：高举中国特色社会主义伟大旗帜，以习近平新时代中国特色社会主义思想为指导，坚定不移地贯彻党在社会主义初级阶段的基本路线，以经济建设为中心，坚持四项基本原则，坚持改革开放，切实保持和增强政治性、先进性、群众性，把培养社会主义建设者和接班人作为根本任务，把巩固和扩大党执政的青年群众基础作为政治责任，把围绕中心、服务大局作为工作主线，用社会主义核心价值体系教育青年，在建设中国特色社会主义的伟大实践中，造就有理想、有道德、有文化、有纪律的青年，努力为党输送新鲜血液，为国家培养青年建设人才，团结带领广大青年，自力更生，艰苦创业，积极推动社会主义经济建设、政治建设、文化建设、社会建设、生态文明建设，踊跃投身全面建设社会主义现代化国家、全面深化改革、全面依法治国、全面从严治党实践，为实现"两个一百年"奋斗目标、实现中华民族伟大复兴的中国梦贡献智慧和力量。</p>',
          categoryId: 42,
          categoryName: '基本任务',
          creatorId: 1,
          creatorName: '蓝探',
          views: 345,
          createTime: '2023-05-04 11:15:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 12,
          title: '团的象征与标志',
          summary: '介绍中国共青团的团旗、团徽、团歌等象征和标志。',
          content: '<h2>团旗</h2><p>中国共产主义青年团团旗旗面为红色，象征革命胜利；左上角缀黄色五角星，周围环绕黄色圆圈，象征中国青年一代紧密团结在中国共产党周围。</p><h2>团徽</h2><p>中国共产主义青年团团徽的内容为团旗、齿轮、麦穗、初升的太阳及其光芒，写有"中国共青团"五字的绶带。它象征着共青团在马克思列宁主义、毛泽东思想的光辉照耀下，团结各族青年，朝着党所指引的方向奋勇前进。</p><h2>团歌</h2><p>中国共产主义青年团团歌为《光荣啊，中国共青团》。</p><p>中国共产主义青年团的团旗、团徽、团歌是中国共产主义青年团的象征和标志。要按照规定制作和使用团旗、团徽、团歌。</p>',
          categoryId: 43,
          categoryName: '组织象征和标志',
          creatorId: 1,
          creatorName: '蓝探',
          views: 389,
          createTime: '2023-05-04 14:20:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 13,
          title: '中国共青团的创立',
          summary: '回顾中国共青团的创立历史，从五四运动到第一次全国代表大会的召开。',
          content: '<h2>创立历程</h2><p>1919年爆发的五四运动，是一场彻底反帝反封建的伟大爱国革命运动。由于这场运动是以一批先进青年知识分子为先锋，因此，五四运动也是一场伟大的青年运动，为中国共产党和中国共青团的建立作了思想上和组织上的准备，开辟了中国青年运动的新纪元。五四爱国运动标志着中国青年群体首次登上社会历史舞台。</p><p>1920年5月，在共产国际代表团的帮助下，陈独秀组建了秘密团体"马克思主义研究会"，并于8月成立了上海共产党早期组织。陈独秀吸收国际共产主义运动的经验，在酝酿组建共产党时，主张组织一个社会主义青年团，成为中国共产党的后备军和共产主义预备学校。于是，陈独秀在上海党的早期组织建立后，指派最年轻的成员俞秀松筹建社会主义青年团。</p><p>1921年7月，中国共产党召开了第一次全国代表大会，宣告正式成立。党的"一大"研究了在各地建立和发展社会主义青年团作为党的预备学校问题，决定了吸收优秀团员入党的办法。"一大"后，中央和各地党组织派了大批党员去开展团的工作。</p><p>1922年5月5日至5月10日，中国社会主义青年团第一次全国代表大会在广州东园隆重举行。出席大会的有来自上海、长沙、武昌、南京、唐山、天津、保定、杭州等15个地方团的25名代表。中共中央局书记陈独秀、青年国际代表达林出席并指导了会议。参加会议的还有全国劳动大会代表和来宾，共1500余人。会议选择5月5日马克思诞辰104周年纪念日召开，表明中国社会主义青年团是信仰马克思主义的革命组织。</p><p>中国社会主义青年团第一次全国代表大会的召开，使中国社会主义青年团实现了思想上、组织上的完全统一，成为在政治纲领和奋斗目标上与中国共产党保持一致的全国性的先进青年组织。由此，中国青年团组织正式诞生了，这是中国青年运动发展史上的一个里程碑。</p>',
          categoryId: 44,
          categoryName: '创立历史',
          creatorId: 1,
          creatorName: '蓝探',
          views: 467,
          createTime: '2023-05-05 09:30:00',
          updateTime: '2025-05-04 05:04:54'
        },
        {
          id: 14,
          title: '中国共青团历次全国代表大会',
          summary: '概述中国共青团十八次全国代表大会的基本情况。',
          content: '<h2>历次全国代表大会</h2><p>中国共青团自1922年成立以来，已经召开了十八次全国代表大会。每次大会都是在重要的历史时期召开，对共青团的发展具有重要意义。</p><h3>第一次全国代表大会</h3><p>1922年5月5日至10日在广州举行。出席代表25人。共有团员5000余人。大会通过了团的纲领，确定中国社会主义青年团的性质是中国青年无产阶级的组织，选举产生了由5名委员和3名候补委员组成的团第一届中央执行委员会。施存统当选为团中央局书记。</p><h3>第十八次全国代表大会</h3><p>2018年6月26日到29日在北京召开。出席代表1529人。共有团员8124.6万余人。王沪宁代表中共中央作了题为《乘新时代东风 放飞青春梦想》的致词。贺军科代表共青团第十七届中央委员会作了题为《高举习近平新时代中国特色社会主义思想伟大旗帜 奋力谱写决胜全面建成小康社会 全面建设社会主义现代化国家的壮丽青春篇章》的报告。大会通过了关于《中国共产主义青年团章程（修正案）》的决议。大会选举产生了由170名委员和129名候补委员组成的共青团第十八届中央委员会。贺军科当选为团中央书记处第一书记。</p>',
          categoryId: 45,
          categoryName: '历次全国代表大会',
          creatorId: 1,
          creatorName: '蓝探',
          views: 532,
          createTime: '2023-05-05 11:45:00',
          updateTime: '2025-05-04 05:04:54'
        }
      ];
      
      // 根据查询参数筛选数据
      if (queryParams.categoryId) {
        // 如果选中的是父分类，则查找该父分类下所有子分类的文章
        const selectedCategory = findCategoryById(categoryTree.value, queryParams.categoryId);
        if (selectedCategory && selectedCategory.children && selectedCategory.children.length > 0) {
          // 获取所有子分类ID
          const childCategoryIds = selectedCategory.children.map(child => child.id);
          // 筛选属于这些子分类的文章
          wikiList.value = allWikiList.filter(item => 
            childCategoryIds.includes(item.categoryId) || item.categoryId === queryParams.categoryId
          );
        } else {
          // 如果是子分类，直接筛选
          wikiList.value = allWikiList.filter(item => item.categoryId === queryParams.categoryId);
        }
      } else {
        // 没有选择分类，显示全部
        wikiList.value = allWikiList;
      }
      
      // 关键词搜索
      if (queryParams.keyword) {
        const keyword = queryParams.keyword.toLowerCase();
        wikiList.value = wikiList.value.filter(item => 
          item.title.toLowerCase().includes(keyword) || 
          item.summary.toLowerCase().includes(keyword) ||
          item.content.toLowerCase().includes(keyword)
        );
      }
      
      totalRecords.value = wikiList.value.length;
      loading.value = false;
    }, 500);
  } catch (error) {
    loading.value = false;
    ElMessage.error('获取百科列表失败');
    console.error('获取百科列表失败:', error);
  }
};

// 查询操作
const handleQuery = () => {
  pageNum.value = 1;
  getWikiList();
};

// 重置查询
const resetQuery = () => {
  queryParams.keyword = '';
  queryParams.categoryId = 1; // 重置为"团的基本知识"分类
  selectedSubCategory.value = null; // 清除子分类选中状态
  pageNum.value = 1;
  
  // 重新设置树组件选中的节点
  if (categoryTreeRef.value) {
    categoryTreeRef.value.setCurrentKey(1);
    currentCategory.value = findCategoryById(categoryTree.value, 1);
  }
  
  getWikiList();
};

// 分页操作
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  getWikiList();
};

const handleCurrentChange = (val: number) => {
  pageNum.value = val;
  getWikiList();
};

// 分类树节点点击
const handleNodeClick = (data: any) => {
  currentCategory.value = data;
  
  // 判断是否为父分类
  const isParentCategory = data.children && data.children.length > 0;
  
  if (isParentCategory) {
    // 点击父分类时，清除子分类选中状态
    selectedSubCategory.value = null;
  queryParams.categoryId = data.id;
  } else {
    // 点击子分类时，记录选中的子分类ID
    selectedSubCategory.value = data.id;
    
    // 查找父分类
    const parentCategory = findParentCategory(categoryTree.value, data.id);
    if (parentCategory) {
      // 设置查询参数为父分类ID
      queryParams.categoryId = parentCategory.id;
    } else {
      // 如果找不到父分类，使用当前分类ID
      queryParams.categoryId = data.id;
    }
  }
  
  pageNum.value = 1;
  getWikiList();
};

// 递归查找父分类
const findParentCategory = (categories: any[], childId: number): any => {
  for (const category of categories) {
    if (category.children && category.children.length > 0) {
      for (const child of category.children) {
        if (child.id === childId) {
          return category;
        }
      }
      // 在更深层次查找
      const found = findParentCategory(category.children, childId);
      if (found) return found;
    }
  }
  return null;
};

// 查看详情
const handleViewDetail = (item: any) => {
  detailLoading.value = true;
  // 模拟获取详情API调用
  setTimeout(() => {
    currentWiki.value = { ...item };
    viewMode.value = 'detail';
    detailLoading.value = false;
  }, 500);
};

// 返回列表
const backToList = () => {
  viewMode.value = 'list';
};

// 添加百科
const handleAdd = () => {
  resetWikiForm();
  dialogType.value = 'add';
  dialogVisible.value = true;
};

// 编辑百科
const handleEdit = () => {
  resetWikiForm();
  dialogType.value = 'edit';
  // 填充表单数据
  Object.assign(wikiForm, {
    id: currentWiki.value.id,
    title: currentWiki.value.title,
    categoryId: currentWiki.value.categoryId,
    summary: currentWiki.value.summary,
    content: currentWiki.value.content
  });
  dialogVisible.value = true;
};

// 删除百科
const handleDelete = () => {
  ElMessageBox.confirm(
    `确认删除百科 "${currentWiki.value.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟API调用
    setTimeout(() => {
      ElMessage.success('删除成功');
      backToList();
      getWikiList();
    }, 500);
  }).catch(() => {
    // 取消删除
  });
};

// 重置百科表单
const resetWikiForm = () => {
  if (wikiFormRef.value) {
    wikiFormRef.value.resetFields();
  }
  Object.assign(wikiForm, {
    id: undefined,
    title: '',
    categoryId: undefined,
    summary: '',
    content: ''
  });
};

// 提交百科表单
const submitForm = async () => {
  if (!wikiFormRef.value) return;
  
  await wikiFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        // 模拟API调用
        setTimeout(() => {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功');
          dialogVisible.value = false;
          if (dialogType.value === 'edit' && viewMode.value === 'detail') {
            // 如果是编辑模式且当前在详情页，更新当前显示的数据
            currentWiki.value = {
              ...currentWiki.value,
              title: wikiForm.title,
              categoryId: wikiForm.categoryId,
              summary: wikiForm.summary,
              content: wikiForm.content,
              updateTime: formatDate(new Date())
            };
          } else {
            // 否则刷新列表
            getWikiList();
          }
          submitLoading.value = false;
        }, 1000);
      } catch (error) {
        submitLoading.value = false;
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '修改失败');
        console.error(dialogType.value === 'add' ? '添加失败' : '修改失败', error);
      }
    }
  });
};

// 添加分类
const handleAddCategory = () => {
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields();
  }
  Object.assign(categoryForm, {
    parentId: undefined,
    name: '',
    sort: 0
  });
  categoryDialogVisible.value = true;
};

// 提交分类表单
const submitCategoryForm = async () => {
  if (!categoryFormRef.value) return;
  
  await categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        // 模拟API调用
        setTimeout(() => {
          ElMessage.success('分类添加成功');
          categoryDialogVisible.value = false;
          // 更新分类树
          if (categoryForm.parentId) {
            // 添加子分类
            const parentCategory = findCategoryById(categoryTree.value, categoryForm.parentId);
            if (parentCategory) {
              if (!parentCategory.children) {
                parentCategory.children = [];
              }
              parentCategory.children.push({
                id: Date.now(), // 模拟ID生成
                name: categoryForm.name
              });
            }
          } else {
            // 添加一级分类
            categoryTree.value.push({
              id: Date.now(), // 模拟ID生成
              name: categoryForm.name,
              children: []
            });
          }
          submitLoading.value = false;
        }, 1000);
      } catch (error) {
        submitLoading.value = false;
        ElMessage.error('分类添加失败');
        console.error('分类添加失败:', error);
      }
    }
  });
};

// 递归查找分类
const findCategoryById = (categories: any[], id: number): any => {
  for (const category of categories) {
    if (category.id === id) {
      return category;
    }
    if (category.children && category.children.length > 0) {
      const found = findCategoryById(category.children, id);
      if (found) return found;
    }
  }
  return null;
};
</script>

<style scoped>
.league-wiki-container {
  padding: 0;
}

.wiki-layout {
  display: flex;
  gap: 16px;
  margin-top: 16px;
  padding: 16px;
}

.category-card {
  width: 260px;
  flex-shrink: 0;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wiki-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card {
  margin-bottom: 0;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.article-count {
  font-size: 14px;
  color: #909399;
}

.wiki-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.wiki-item {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.wiki-item:hover {
  background-color: #f5f7fa;
}

.wiki-item-highlighted {
  background-color: #ecf5ff;
  border-left: 4px solid #409eff;
}

.wiki-item:last-child {
  border-bottom: none;
}

.wiki-item-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.wiki-item-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.6;
}

.wiki-item-footer {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.wiki-item-category {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 16px;
}

.wiki-item-date {
  margin-right: 16px;
}

.wiki-item-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wiki-detail {
  padding: 16px;
}

.wiki-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 16px;
  text-align: center;
}

.wiki-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
  margin-bottom: 24px;
  font-size: 14px;
  color: #909399;
}

.wiki-category {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
}

.wiki-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

.wiki-content-html {
  line-height: 1.8;
  font-size: 16px;
}

.wiki-content-html h2 {
  font-size: 20px;
  font-weight: bold;
  margin: 24px 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.wiki-content-html p {
  margin-bottom: 16px;
}

.wiki-content-html ul, .wiki-content-html ol {
  padding-left: 20px;
  margin-bottom: 16px;
}

.wiki-content-html li {
  margin-bottom: 8px;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.empty-data {
  padding: 40px 0;
}
</style> 