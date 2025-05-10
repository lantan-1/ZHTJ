<template>
  <div class="page-header">
    <div class="header-main">
      <div class="title-section">
        <h2 class="page-title">{{ title }}</h2>
        <el-breadcrumb v-if="breadcrumbs && breadcrumbs.length" separator="/">
          <el-breadcrumb-item 
            v-for="(item, index) in breadcrumbs" 
            :key="index"
            :to="item.path ? { path: item.path } : undefined"
          >
            {{ item.title }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="action-section">
        <slot name="actions"></slot>
      </div>
    </div>
    <div v-if="$slots.default" class="header-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
// 定义props
const props = defineProps({
  // 页面标题
  title: {
    type: String,
    required: true
  },
  // 面包屑导航项
  breadcrumbs: {
    type: Array as () => Array<{
      title: string;
      path?: string;
    }>,
    default: () => []
  }
});
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.title-section {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.action-section {
  display: flex;
  gap: 12px;
  align-items: center;
}

.header-content {
  margin-top: 16px;
}

:deep(.el-breadcrumb) {
  font-size: 14px;
}
</style> 