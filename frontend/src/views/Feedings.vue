<template>
  <div class="pane">
    <header class="hd"><h2>投喂</h2><span class="sub">按饲养员分堆；上面的汇总是后端 group by 出来的</span>
      <button class="prime" @click="openNew">记一笔投喂</button></header>

    <div class="summary">
      <div v-for="k in summary" :key="k.keeperName" class="s-card">
        <div class="s-name">{{ k.keeperName }}</div>
        <div class="s-nums"><b>{{ k.times }}</b><span>次</span><b>{{ k.totalAmount }}</b><span>份</span></div>
      </div>
    </div>

    <div class="groups">
      <section v-for="g in groups" :key="g.keeper" class="grp">
        <header class="g-head" @click="open = open === g.keeper ? '' : g.keeper">
          <i>{{ open === g.keeper ? '▾' : '▸' }}</i>
          <b>{{ g.keeper }}</b>
          <span class="g-count">{{ g.list.length }} 条</span>
        </header>
        <div v-show="open === g.keeper" class="g-body">
          <div v-for="f in g.list" :key="f.id" class="f-row">
            <span class="mono">{{ f.feedingCode }}</span>
            <span>{{ animalName(f.animalId) }}</span>
            <span class="dim">{{ f.feedTime || '—' }}</span>
            <span>{{ f.foodName }}</span>
            <span class="r">{{ f.amount }} 份</span>
          </div>
        </div>
      </section>
    </div>

    <el-dialog v-model="dialog" title="记一笔投喂" width="440px">
      <div class="fr"><label>投喂单号</label><el-input v-model="form.feedingCode" /></div>
      <div class="fr"><label>喂哪只</label>
        <el-select v-model="form.animalId" style="flex:1">
          <el-option v-for="a in animals" :key="a.id" :label="a.animalCode + ' ' + a.animalName" :value="a.id" />
        </el-select></div>
      <div class="fr"><label>时间</label><el-input v-model="form.feedTime" placeholder="08:30" /></div>
      <div class="fr"><label>饲料</label><el-input v-model="form.foodName" /></div>
      <div class="fr"><label>数量</label><el-input v-model="form.amount" /></div>
      <div class="fr"><label>饲养员</label><el-input v-model="form.keeperName" /></div>
      <p class="note">隔离中的动物不能直接投喂。</p>
      <template #footer><el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { animalApi, feedingApi } from '../api'

const items = ref([])
const summary = ref([])
const animals = ref([])
const open = ref('')
const dialog = ref(false)
const form = ref({})

const groups = computed(() => {
  const m = {}
  items.value.forEach((f) => {
    ;(m[f.keeperName] = m[f.keeperName] || []).push(f)
  })
  return Object.keys(m).map((keeper) => ({ keeper, list: m[keeper] }))
})
function animalName(id) {
  const a = animals.value.find((x) => x.id === id)
  return a ? a.animalCode + ' ' + a.animalName : '—'
}
async function load() {
  items.value = await feedingApi.list()
  summary.value = await feedingApi.byKeeper()
  animals.value = await animalApi.list()
}
function openNew() {
  form.value = {}
  dialog.value = true
}
async function submit() {
  try {
    await feedingApi.add(form.value)
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
.summary { display: grid; grid-template-columns: repeat(auto-fill, minmax(158px, 1fr)); gap: 12px; margin-bottom: 18px; }
.s-card { background: #fff; border: 1px solid #f0e9e7; border-radius: 10px; padding: 12px 14px; }
.s-name { font-size: 13px; color: #7a6d6b; }
.s-nums { display: flex; align-items: baseline; gap: 4px; margin-top: 6px; }
.s-nums b { font-size: 20px; color: var(--el-color-primary-dark-2); }
.s-nums span { font-size: 11px; color: #a39694; margin-right: 10px; }
.groups { background: #fff; border: 1px solid #f0e9e7; border-radius: 12px; overflow: hidden; }
.grp { border-bottom: 1px solid #f8f3f2; }
.g-head { display: flex; align-items: center; gap: 10px; padding: 13px 16px; cursor: pointer; }
.g-head:hover { background: #fdfbfa; }
.g-head i { color: var(--el-color-primary); font-style: normal; }
.g-head b { font-size: 14px; }
.g-count { margin-left: auto; font-size: 12px; color: #a39694; }
.g-body { padding: 0 16px 12px 40px; }
.f-row { display: grid; grid-template-columns: 110px 1.4fr 90px 1fr 80px; gap: 8px; align-items: center;
  padding: 9px 0; border-top: 1px solid #f8f3f2; font-size: 13px; }
.mono { font-family: ui-monospace, Menlo, monospace; color: #a39694; font-size: 12px; }
.dim { color: #a39694; font-size: 12px; }
.r { text-align: right; color: var(--el-color-primary-dark-2); }
.fr { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.fr label { width: 76px; text-align: right; font-size: 13px; color: #7a6d6b; }
.note { font-size: 12px; color: #c9bcba; margin: 4px 0 0 86px; }
</style>
