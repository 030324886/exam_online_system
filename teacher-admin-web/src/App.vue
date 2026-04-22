<template>
  <!-- 路由页面的渲染容器，必须保留 -->
  <router-view />
</template>

<script setup>
// 清空所有复杂逻辑，先保证基础渲染
</script>

<style>
/* 全局样式重置，确保页面占满全屏 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body, #app {
  height: 100%;
  width: 100%;
}
</style>