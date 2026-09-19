<template>
  <div class="pane">
    <header class="hd"><h2>笼舍</h2><span class="sub">下面那条是「住了几只 / 可容纳」，数据是后端按笼舍汇总出来的</span>
      <button class="prime" @click="openNew">新增笼舍</button></header>
    <div class="cards">
      <article v-for="e in items" :key="e.id" class="card" :class="{ fixing: e.enclosureState === '维修' }">
        <div class="c-top"><span class="code">{{ e.enclosureCode }}</span><span class="chip">{{ e.enclosureState }}</span></div>
        <div class="c-name">{{ e.enclosureName }}</div>
        <div class="c-facts"><span>面积 {{ e.zoneArea ?? '-' }} ㎡</span><span>可容纳 {{ e.capacity ?? '-' }}</span></div>
        <div class="c-bar"><div class="c-fill" :style="{ width: pct(e) + '%' }"></div></div>
        <div class="c-foot"><span>已住 {{ living(e.id) }} 只</span>
          <span v-if="avgAge(e.id)">均龄 {{ avgAge(e.id) }} 岁</span></div>
        <button class="ghost" @click="openEdit(e)">调整</button>
      </article>
    </div>
    <el-dialog v-model="dialog" :title="form.id ? '修改笼舍' : '新增笼舍'" width="420px">
      <div class="fr"><label>编号</label><el-input v-model="form.enclosureCode" /></div>
      <div class="fr"><label>名称</label><el-input v-model="form.enclosureName" /></div>
      <div class="fr"><label>面积</label><el-input v-model="form.zoneArea" /></div>
      <div class="fr"><label>可容纳</label><el-input v-model="form.capacity" /></div>
      <div class="fr"><label>状态</label><el-input v-model="form.enclosureState" placeholder="开放 / 维修" /></div>
      <template #footer><el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { enclosureApi } from '../api'

const items = ref([])
const stats = ref([])
const dialog = ref(false)
const form = ref({})

function statOf(id) {
  return stats.value.find((s) => Number(s.enclosureId) === id) || {}
}
function living(id) {
  return statOf(id).animalCount ?? 0
}
function avgAge(id) {
  const v = statOf(id).avgAge
  return v == null ? null : Number(v).toFixed(1)
}
function pct(e) {
  if (!e.capacity) return 0
  return Math.min(100, Math.round((living(e.id) * 100) / e.capacity))
}
async function load() {
  items.value = await enclosureApi.list()
  stats.value = await enclosureApi.occupancy()
}
function openNew() {
  form.value = { enclosureState: '开放' }
  dialog.value = true
}
function openEdit(row) {
  form.value = { ...row }
  dialog.value = true
}
async function submit() {
  try {
    if (form.value.id) await enclosureApi.save(form.value.id, form.value)
    else await enclosureApi.add(form.value)
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
.cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(252px, 1fr)); gap: 14px; }
.card { background: #fff; border: 1px solid #f0e9e7; border-radius: 12px; padding: 16px 18px; }
.card.fixing { background: #fffdf8; border-color: #f0dcc0; }
.c-top { display: flex; justify-content: space-between; }
.code { font-size: 12px; color: #a39694; }
.chip { font-size: 11px; background: #f6f1ef; border-radius: 10px; padding: 2px 10px; color: #8d7f7c; }
.c-name { font-size: 16px; font-weight: 600; margin: 10px 0 8px; }
.c-facts { display: flex; gap: 14px; font-size: 12px; color: #a39694; margin-bottom: 10px; }
.c-bar { height: 9px; background: #f6f1ef; border-radius: 5px; overflow: hidden; }
.c-fill { height: 100%; background: var(--el-color-primary); border-radius: 5px; }
.c-foot { display: flex; justify-content: space-between; font-size: 12px; color: #a39694; margin: 8px 0 12px; }
.ghost { background: #fff; border: 1px solid var(--el-color-primary-light-7); color: var(--el-color-primary-dark-2);
  border-radius: 7px; padding: 5px 14px; font-size: 12px; cursor: pointer; }
.fr { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.fr label { width: 66px; text-align: right; font-size: 13px; color: #7a6d6b; }
</style>
