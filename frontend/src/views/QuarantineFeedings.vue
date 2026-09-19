<template>
  <div class="pane">
    <header class="hd"><h2>隔离加餐台</h2><span class="sub">只收档案状态为「隔离」的动物；饲料名得在最近一张巡查备注里找得到原文</span>
      <button class="prime" @click="openNew">记一笔夜加餐</button></header>

    <div class="rules">
      <p><b>兽医的规矩：</b></p>
      <p>1. 只能挑档案状态此刻是「隔离」的动物；提交当下若已经不是隔离，整张单作废，也不会溜进普通投喂流水。</p>
      <p>2. 饲料名必须能在该动物<b>最近一张</b>巡查的备注里找到原文，找不到就提交失败。</p>
      <p>3. 同一只隔离动物一个日历日只能落下一张，两个人同时记，只认先成功的那张，后到的这笔记失败。</p>
    </div>

    <div class="table">
      <div class="row head">
        <span>加餐单号</span><span>动物</span><span>日期</span>
        <span>饲料（须同巡查备注原文）</span><span>克数</span><span>当班饲养员</span><span>落下时间</span>
      </div>
      <div v-for="o in orders" :key="o.id" class="row">
        <span class="mono">{{ o.feedingCode }}</span>
        <span>{{ animalName(o.animalId) }}</span>
        <span class="dim">{{ o.feedDate }}</span>
        <span>{{ o.foodName }}</span>
        <span class="r">{{ o.amount }} 克</span>
        <span>{{ o.keeperName }}</span>
        <span class="dim">{{ o.createdAt }}</span>
      </div>
      <div v-if="!orders.length" class="empty">还没有落下的隔离加餐单</div>
    </div>

    <el-dialog v-model="dialog" title="记一笔隔离加餐" width="460px">
      <div class="fr"><label>加餐单号</label><el-input model-value="提交时按动物与日期生成" disabled /></div>
      <div class="fr"><label>给哪只</label>
        <el-select v-model="form.animalId" style="flex:1" placeholder="只能挑隔离中的动物" @change="onAnimalChange">
          <el-option v-for="a in quarantined" :key="a.id"
                     :label="a.animalCode + ' ' + a.animalName + '（' + a.species + '）'" :value="a.id" />
        </el-select></div>
      <p v-if="!quarantined.length" class="warn">眼下没有档案状态是「隔离」的动物，先在动物档案里把状态改成隔离。</p>
      <div class="fr"><label>日期</label><el-input v-model="form.feedDate" placeholder="2026-09-19" /></div>
      <div class="fr"><label>饲料名</label><el-input v-model="form.foodName" placeholder="得和最近巡查备注原文一字不差" /></div>
      <div class="fr"><label>克数</label><el-input v-model="form.amount" placeholder="克，例：150" /></div>
      <div class="fr"><label>当班饲养员</label><el-input v-model="form.keeperName" /></div>
      <div v-if="form.animalId" class="remark-box">
        <template v-if="latest">
          <p>最近一张巡查（{{ latest.checkCode }} · {{ latest.checkDate || '未填日期' }} · {{ latest.vetName }}）备注：</p>
          <p class="quote">「{{ latest.remark || '（没写备注）' }}」</p>
        </template>
        <p v-else class="warn">这只动物还没有巡查单，没有可对的备注原文，提交会失败。</p>
      </div>
      <p class="note">提交时后端会再认一次最新巡查和档案状态；记失败的那笔不会留下，刷新也不会冒出来。</p>
      <template #footer><el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">落单</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { animalApi, quarantineApi } from '../api'

const orders = ref([])
const quarantined = ref([])
const dialog = ref(false)
const submitting = ref(false)
const form = ref({})
const latest = ref(null)

function today() {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}
function animalName(id) {
  const a = quarantined.value.find((x) => x.id === id)
  return a ? a.animalCode + ' ' + a.animalName : '#' + id
}
async function load() {
  orders.value = await quarantineApi.list()
  quarantined.value = await animalApi.list({ state: '隔离' })
}
async function onAnimalChange(animalId) {
  latest.value = null
  if (!animalId) return
  try {
    latest.value = await quarantineApi.latestCheck(animalId)
  } catch (e) {
    latest.value = null
  }
}
function openNew() {
  form.value = { feedDate: today() }
  latest.value = null
  dialog.value = true
  // 每次开单都重新拉一遍隔离名单，防止拿着旧状态记单。
  animalApi.list({ state: '隔离' }).then((list) => { quarantined.value = list })
}
async function submit() {
  if (!form.value.animalId) {
    ElMessage.error('得挑一只隔离中的动物')
    return
  }
  submitting.value = true
  try {
    const saved = await quarantineApi.add({
      animalId: form.value.animalId,
      feedDate: form.value.feedDate || today(),
      foodName: form.value.foodName,
      amount: Number(form.value.amount),
      keeperName: form.value.keeperName
    })
    dialog.value = false
    await load()
    ElMessage.success('加餐单 ' + saved.feedingCode + ' 落下了')
  } catch (e) {
    // 失败原因带给值班员：不合规 / 同日已有先成功的单。失败的这一笔绝不进列表。
    ElMessage.error(e.message)
    await load()
    if (form.value.animalId) {
      try { latest.value = await quarantineApi.latestCheck(form.value.animalId) } catch (_) { /* 保持提示 */ }
    }
  } finally {
    submitting.value = false
  }
}
onMounted(load)
</script>

<style scoped>
.hd { display: flex; align-items: center; gap: 14px; margin-bottom: 16px; }
.hd h2 { margin: 0; font-size: 20px; }
.sub { flex: 1; color: #a39694; font-size: 12px; }
.prime { background: var(--el-color-primary); color: #fff; border: none; border-radius: 8px;
  padding: 8px 18px; font-size: 13px; cursor: pointer; }
.rules { background: #fff; border: 1px solid #f0e9e7; border-left: 3px solid var(--el-color-primary);
  border-radius: 8px; padding: 10px 16px; margin-bottom: 16px; }
.rules p { margin: 3px 0; font-size: 12px; color: #7a6d6b; }
.table { background: #fff; border: 1px solid #f0e9e7; border-radius: 12px; overflow: hidden; }
.row { display: grid; grid-template-columns: 170px 1fr 100px 1.4fr 70px 0.9fr 1.2fr; gap: 8px;
  align-items: center; padding: 11px 14px; border-bottom: 1px solid #f8f3f2; font-size: 13px; }
.row.head { background: #fdfbfa; color: #a39694; font-size: 12px; }
.mono { font-family: ui-monospace, Menlo, monospace; color: #a39694; font-size: 12px; }
.dim { color: #a39694; font-size: 12px; }
.r { text-align: right; color: var(--el-color-primary-dark-2); }
.empty { padding: 26px; text-align: center; color: #c9bcba; font-size: 13px; }
.fr { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.fr label { width: 76px; text-align: right; font-size: 13px; color: #7a6d6b; }
.note { font-size: 12px; color: #c9bcba; margin: 4px 0 0 86px; }
.warn { font-size: 12px; color: #c0392b; margin: 4px 0 0 86px; }
.remark-box { margin: 0 0 8px 86px; background: #fdfbfa; border: 1px dashed #e5d8d5;
  border-radius: 8px; padding: 8px 12px; }
.remark-box p { margin: 2px 0; font-size: 12px; color: #7a6d6b; }
.remark-box .quote { color: var(--el-color-primary-dark-2); }
</style>
