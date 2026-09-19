<template>
  <div class="pane">
    <header class="hd"><h2>隔离加餐台</h2><span class="sub">只收隔离中的动物；饲料名得对得上该动物最新一张巡查备注；同一只一天只落一张</span>
      <button class="prime" @click="openNew">记一笔加餐</button></header>

    <div class="table">
      <div class="row head">
        <span>单号</span><span>动物</span><span>日期</span><span>饲料</span><span>克数</span><span>当班饲养员</span>
      </div>
      <div v-for="m in items" :key="m.id" class="row">
        <span class="mono">{{ m.mealCode }}</span>
        <span>{{ animalName(m.animalId) }}</span>
        <span class="dim">{{ m.mealDate }}</span>
        <span>{{ m.foodName }}</span>
        <span class="r">{{ m.grams }} 克</span>
        <span>{{ m.keeperName }}</span>
      </div>
      <div v-if="!items.length" class="empty">还没有落下的加餐单</div>
    </div>

    <el-dialog v-model="dialog" title="记一笔隔离加餐" width="440px">
      <div class="fr"><label>给哪只</label>
        <el-select v-model="form.animalId" style="flex:1" @change="loadRemark">
          <el-option v-for="a in isolated" :key="a.id" :label="a.animalCode + ' ' + a.animalName" :value="a.id" />
        </el-select></div>
      <p v-if="!isolated.length" class="note">眼下没有隔离中的动物，这单子开不了。</p>
      <p v-if="latestRemark" class="note remark">最近一张巡查备注：{{ latestRemark }}</p>
      <div class="fr"><label>饲料</label><el-input v-model="form.foodName" placeholder="得能在上面备注里找到" /></div>
      <div class="fr"><label>克数</label><el-input v-model="form.grams" placeholder="例如 200" /></div>
      <div class="fr"><label>当班饲养员</label><el-input v-model="form.keeperName" /></div>
      <p class="note">饲料名对不上最新巡查备注会提交失败；同一只动物同一天只能落下一张。</p>
      <template #footer><el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { animalApi, checkApi, isolationMealApi } from '../api'

const items = ref([])
const animals = ref([])
const dialog = ref(false)
const form = ref({})
const latestRemark = ref('')

// 只能挑档案状态已是隔离的动物
const isolated = computed(() => animals.value.filter((a) => a.animalState === '隔离'))

function animalName(id) {
  const a = animals.value.find((x) => x.id === id)
  return a ? a.animalCode + ' ' + a.animalName : '—'
}
async function load() {
  items.value = await isolationMealApi.list()
  animals.value = await animalApi.list()
}
function openNew() {
  form.value = {}
  latestRemark.value = ''
  dialog.value = true
}
// 选中动物后把它最新一张巡查备注摆出来当参考；提交时后端仍按最新那张重新核
async function loadRemark() {
  latestRemark.value = ''
  if (!form.value.animalId) return
  const checks = await checkApi.list({ animalId: form.value.animalId })
  latestRemark.value = checks.length ? (checks[0].remark || '（这张没写备注）') : '（还没有巡查记录）'
}
async function submit() {
  try {
    await isolationMealApi.add(form.value)
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
.table { background: #fff; border: 1px solid #f0e9e7; border-radius: 12px; overflow: hidden; }
.row { display: grid; grid-template-columns: 110px 1.2fr 110px 1fr 90px 110px; gap: 8px;
  align-items: center; padding: 11px 14px; border-bottom: 1px solid #f8f3f2; font-size: 13px; }
.row.head { background: #fdfbfa; color: #a39694; font-size: 12px; }
.mono { font-family: ui-monospace, Menlo, monospace; color: #a39694; font-size: 12px; }
.dim { color: #a39694; font-size: 12px; }
.r { color: var(--el-color-primary-dark-2); }
.empty { padding: 26px; text-align: center; color: #c9bcba; font-size: 13px; }
.fr { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.fr label { width: 76px; text-align: right; font-size: 13px; color: #7a6d6b; }
.note { font-size: 12px; color: #c9bcba; margin: 4px 0 0 86px; }
.remark { color: #b88230; }
</style>
