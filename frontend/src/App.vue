<template>
  <div class="shell">
    <div class="row1">动物园 · 饲养与巡查</div>
    <div class="row2">
      <router-link v-for="m in mods" :key="m.path" :to="m.path"
                   class="tb" :class="{ on: $route.path === m.path }">{{ m.label }}</router-link>
    </div>
    <main class="body"><router-view /></main>
    <footer class="status">
      <span>当前：{{ currentLabel }}</span>
      <span class="right">{{ mods.length }} 个模块</span>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { mods } from './router'

const route = useRoute()
const currentLabel = computed(() => (mods.find((m) => m.path === route.path) || {}).label || '')
</script>

<style>
html, body { margin: 0; }
body { background: #faf7f6; font-family: -apple-system, 'PingFang SC', sans-serif; }
.shell { min-height: 100vh; padding-bottom: 38px; }
.row1 { background: var(--el-color-primary); color: #fff; font-size: 15px; font-weight: 700;
  padding: 13px 26px; }
.row2 { display: flex; gap: 2px; background: #fff; padding: 0 22px; border-bottom: 1px solid #eee; }
.tb { padding: 13px 15px; text-decoration: none; color: #7a6d6b; font-size: 13px;
  border-bottom: 3px solid transparent; }
.tb.on { color: var(--el-color-primary-dark-2); font-weight: 600; border-bottom-color: var(--el-color-primary); }
.body { padding: 22px 26px; }
.status { position: fixed; left: 0; right: 0; bottom: 0; height: 34px; background: #fff;
  border-top: 1px solid #eee; display: flex; align-items: center; justify-content: space-between;
  padding: 0 26px; font-size: 12px; color: #a39694; }
.status .right { color: #c9bcba; }
</style>
