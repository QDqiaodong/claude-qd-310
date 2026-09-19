<template>
  <div class="pane">
    <header class="hd"><h2>动物</h2><span class="sub">一只一格头像，角上挂状态色点；点开改档案</span>
      <button class="prime" @click="openNew">登记动物</button></header>
    <div class="wall">
      <div v-for="a in items" :key="a.id" class="cell" :class="{ isolate: a.animalState === '隔离' }" @click="openEdit(a)">
        <div class="face">{{ (a.animalName || '?').slice(0, 1) }}</div>
        <div class="c-name">{{ a.animalName }}</div>
        <div class="c-species">{{ a.species }}</div>
        <div class="c-home">{{ enclosureName(a.enclosureId) }}</div>
        <span class="dot" :class="dotOf(a.animalState)"></span>
      </div>
    </div>
    <el-dialog v-model="dialog" :title="form.id ? '修改档案' : '登记动物'" width="440px">
      <div class="fr"><label>编号</label><el-input v-model="form.animalCode" /></div>
      <div class="fr"><label>名字</label><el-input v-model="form.animalName" /></div>
      <div class="fr"><label>物种</label><el-input v-model="form.species" /></div>
      <div class="fr"><label>笼舍</label>
        <el-select v-model="form.enclosureId" style="flex:1">
          <el-option v-for="e in enclosures" :key="e.id" :label="e.enclosureName" :value="e.id" />
        </el-select></div>
      <div class="fr"><label>出生日期</label><el-input v-model="form.birthDate" placeholder="2020-07-21" /></div>
      <div class="fr"><label>状态</label><el-input v-model="form.animalState" placeholder="健康 / 观察 / 隔离" /></div>
      <template #footer><el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { animalApi, enclosureApi } from '../api'

const items = ref([])
const enclosures = ref([])
const dialog = ref(false)
const form = ref({})

function enclosureName(id) {
  const e = enclosures.value.find((x) => x.id === id)
  return e ? e.enclosureName : '未安排'
}
function dotOf(state) {
  return { 健康: 'd0', 观察: 'd1', 隔离: 'd2' }[state] || 'd0'
}
async function load() {
  items.value = await animalApi.list()
  enclosures.value = await enclosureApi.list()
}
function openNew() {
  form.value = { animalState: '健康' }
  dialog.value = true
}
function openEdit(row) {
  form.value = { ...row }
  dialog.value = true
}
async function submit() {
  try {
    if (form.value.id) await animalApi.save(form.value.id, form.value)
    else await animalApi.add(form.value)
    dialog.value = false
    await load()
    ElMessage.success('保存好了')
  } catch (e) { ElMessage.error(e.message) }
}
onMounted(load)
</script>

<style scoped>
.hd { display: flex; align-items: center; gap: 14px; margin-bottom: 18px; }
.hd h2 { margin: 0; font-size: 20px; }
.sub { flex: 1; color: #a39694; font-size: 12px; }
.prime { background: var(--el-color-primary); color: #fff; border: none; border-radius: 8px;
  padding: 8px 18px; font-size: 13px; cursor: pointer; }
.wall { display: grid; grid-template-columns: repeat(auto-fill, minmax(118px, 1fr)); gap: 12px; }
.cell { position: relative; background: #fff; border: 1px solid #f0e9e7; border-radius: 12px;
  padding: 14px 8px; text-align: center; cursor: pointer; }
.cell:hover { border-color: var(--el-color-primary); }
.cell.isolate { background: #fffafa; border-color: #f0cfcf; }
.face { width: 48px; height: 48px; border-radius: 50%; margin: 0 auto 8px;
  background: var(--el-color-primary-light-8); color: var(--el-color-primary-dark-2);
  display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: 700; }
.c-name { font-size: 13px; font-weight: 600; }
.c-species { font-size: 11px; color: #a39694; margin: 2px 0; }
.c-home { font-size: 11px; color: #c9bcba; }
.dot { position: absolute; right: 10px; top: 10px; width: 9px; height: 9px; border-radius: 50%; }
.dot.d0 { background: #67c23a; }
.dot.d1 { background: #e6a23c; }
.dot.d2 { background: #f56c6c; }
.fr { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.fr label { width: 70px; text-align: right; font-size: 13px; color: #7a6d6b; }
</style>
