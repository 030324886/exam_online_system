<template>
  <!-- 根容器：统一全局样式，避免组件样式溢出 -->
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// 无需额外逻辑，仅作为路由出口
</script>

<style>
/* 全局样式重置：解决默认边距/盒模型问题 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 全局body样式：避免页面滚动异常 */
body {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", Arial, sans-serif;
  background-color: #f5f7fa;
  overflow-x: hidden; /* 防止横向滚动 */
}

/* 根容器高度：确保页面占满视口 */
#app {
  width: 100vw;
  min-height: 100vh;
}

/* 全局滚动条优化（可选） */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-thumb {
  border-radius: 4px;
  background-color: rgba(144, 147, 153, 0.5);
}
::-webkit-scrollbar-track {
  background-color: transparent;
}
</style>