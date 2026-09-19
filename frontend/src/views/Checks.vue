<template>
  <div class="pane">
    <header class="hd"><h2>兽医巡查</h2><span class="sub">点表头能排序（再点一次反向），下面是后端算好的体检概况</span>
      <button class="prime" @click="openNew">登记巡查</button></header>

    <div class="overview">
      <div class="o-card"><b>{{ overview.totalAnimals ?? 0 }}</b><span>在册动物</span></div>
      <div class="o-card"><b>{{ overview.normalCount ?? 0 }}</b><span>正常记录</span></div>
      <div class="o-card"><b class="red">{{ overview.abnormalCount ?? 0 }}</b><span>异常记录</span></div>
      <div class="o-card"><b>{{ overview.watchingCount ?? 0 }}</b><span>观察中</span></div>
      <div class="o-card"><b>{{ overview.todayChecks ?? 0 }}</b><span>今日巡查</span></div>
    </div>

    <div class="table">
      <div class="row head">
        <span v-for="c in columns" :key="c.key" class="th" @click="sortBy(c.key)">
          {{ c.label }}<i v-if="sortKey === c.key">{{ asc ? '▲' : '▼' }}</i>
        </span>
      </div>
      <div v-for="r in sorted" :key="r.id" class="row" :class="{ bad: r.checkResult === '异常' }">
        <span class="mono">{{ r.checkCode }}</span>
        <span>{{ animalName(r.animalId) }}</span>
        <span class="dim">{{ r.checkDate }}</span>
        <span>{{ r.vetName }}</span>
        <span class="res">{{ r.checkResult }}</span>
        <span class="dim">{{ r.remark || '—' }}</span>
        <span><button class="ghost" @click="openEdit(r)">查看</button></span>
      </div>
      <div v-if="!sorted.length" class="empty">还没有巡查记录</div>
    </div>

    <el-dialog v-model="dialog" :title="form.id ? '巡查记录' : '登记巡查'" width="450px">
      <div class="fr"><label>巡查单号</label><el-input v-model="form.checkCode" /></div>
      <div class="fr"><label>巡哪只</label>
        <el-select v-model="form.animalId" style="flex:1">
          <el-option v-for="a in animals" :key="a.id" :label="a.animalCode + ' ' + a.animalName" :value="a.id" />
        </el-select></div>
      <div class="fr"><label>日期</label><el-input v-model="form.checkDate" placeholder="2026-09-19" /></div>
      <div class="fr"><label>兽医</label><el-input v-model="form.vetName" /></div>
      <div class="fr"><label>结论</label><el-input v-model="form.checkResult" placeholder="正常 / 异常" /></div>
      <div class="fr"><label>备注</label><el-input v-model="form.remark" /></div>
      <p class="note">判成异常必须写备注，日期也不能填到未来。</p>
      <template #footer><el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { animalApi, checkApi } from '../api'

const columns = [
  { key: 'checkCode', label: '单号' },
  { key: 'animalId', label: '动物' },
  { key: 'checkDate', label: '日期' },
  { key: 'vetName', label: '兽医' },
  { key: 'checkResult', label: '结论' },
  { key: 'remark', label: '备注' }
]

const items = ref([])
const animals = ref([])
const overview = ref({})
const sortKey = ref('checkDate')
const asc = ref(false)
const dialog = ref(false)
const form = ref({})

// 纯前端排序：点表头切换字段，再点一次换方向
const sorted = computed(() => {
  const list = [...items.value]
  list.sort((a, b) => {
    const x = a[sortKey.value] ?? ''
    const y = b[sortKey.value] ?? ''
    const r = String(x).localeCompare(String(y))
    return asc.value ? r : -r
  })
  return list
})
function sortBy(key) {
  if (sortKey.value === key) {
    asc.value = !asc.value
  } else {
    sortKey.value = key
    asc.value = true
  }
}
function animalName(id) {
  const a = animals.value.find((x) => x.id === id)
  return a ? a.animalCode + ' ' + a.animalName : '—'
}
async function load() {
  items.value = await checkApi.list()
  animals.value = await animalApi.list()
  overview.value = await checkApi.overview()
}
function openNew() {
  form.value = { checkResult: '正常' }
  dialog.value = true
}
function openEdit(row) {
  form.value = { ...row }
  dialog.value = true
}
async function submit() {
  try {
    if (form.value.id) {
      ElMessage.info('巡查记录只能新增，不能改')
      dialog.value = false
      return
    }
    await checkApi.add(form.value)
    dialog.value = false
    await load()
    ElMessage.success('记好了')
  } catch (e) { ElMessage.error(e.message) }
}
onMounted(load)
</script>

<style scoped>
.hd { display: flex; align-items: center; gap: 14px; margin-bottom: 16px; }
.hd h2 { margin: 0; font-size: 20px; }
.sub { flex: 1; color: #a39694; font-size: 12px; }
.prime { background: var(--el-color-primary); color: #fff; border: none; border-radius: 8px;
  padding: 8px 18px; font-size: 13px; cursor: pointer; }
.overview { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 12px; margin-bottom: 16px; }
.o-card { background: #fff; border: 1px solid #f0e9e7; border-radius: 10px; padding: 14px 16px; text-align: center; }
.o-card b { display: block; font-size: 22px; color: var(--el-color-primary-dark-2); }
.o-card b.red { color: #c0392b; }
.o-card span { font-size: 11px; color: #a39694; }
.table { background: #fff; border: 1px solid #f0e9e7; border-radius: 12px; overflow: hidden; }
.row { display: grid; grid-template-columns: 100px 1.2fr 106px 96px 80px 1fr 68px; gap: 8px;
  align-items: center; padding: 11px 14px; border-bottom: 1px solid #f8f3f2; font-size: 13px; }
.row.head { background: #fdfbfa; color: #a39694; font-size: 12px; }
.th { cursor: pointer; user-select: none; }
.th:hover { color: var(--el-color-primary-dark-2); }
.th i { font-style: normal; margin-left: 4px; font-size: 10px; }
.row.bad { background: #fffafa; }
.mono { font-family: ui-monospace, Menlo, monospace; color: #a39694; font-size: 12px; }
.dim { color: #a39694; font-size: 12px; }
.res { color: var(--el-color-primary-dark-2); }
.row.bad .res { color: #c0392b; font-weight: 600; }
.empty { padding: 26px; text-align: center; color: #c9bcba; font-size: 13px; }
.ghost { background: #fff; border: 1px solid var(--el-color-primary-light-7); color: var(--el-color-primary-dark-2);
  border-radius: 6px; padding: 3px 10px; font-size: 12px; cursor: pointer; }
.fr { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.fr label { width: 76px; text-align: right; font-size: 13px; color: #7a6d6b; }
.note { font-size: 12px; color: #c9bcba; margin: 4px 0 0 86px; }
</style>
