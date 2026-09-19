<template>
  <div class="pg">
    <div class="hd">
      <h2>兽医巡查</h2>
      <span class="hint">每条一个结论徽章，点开看备注。</span>
      <input class="search" v-model="kw" placeholder="搜索编号或名称" />
      <button class="btn solid" @click="openNew">新增</button>
    </div>
    <div class="rb">
      <div class="ri" v-for="it in filtered" :key="it.id" :class="{ open: openId === it.id }">
        <div class="rhead" @click="openId = openId === it.id ? null : it.id">
          <span class="badge" :class="cls(it)">{{ it[RK] ?? '-' }}</span>
          <span class="rc">{{ it.code }}</span>
          <span class="rn">{{ it.name }}</span>
          <span class="rd">{{ dateOf(it) }}</span>
          <span class="arrow">{{ openId === it.id ? '▾' : '▸' }}</span>
        </div>
        <div class="rbody" v-show="openId === it.id">
          <div class="rr" v-for="fd in BODY_FIELDS" :key="fd.k"><i>{{ fd.l }}</i>{{ it[fd.k] }}</div>
          <button class="btn sm" @click="openEdit(it)">修改</button>
        </div>
      </div>
    </div>
  </div>

    <el-dialog v-model="show" :title="form.id ? '修改' : '新增'" width="440px">
      <div class="frm">
        <div class="fr" v-for="fd in FORM_FIELDS" :key="fd.k">
          <label>{{ fd.l }}</label>
          <el-input v-model="form[fd.k]" :placeholder="'请填写' + fd.l" />
        </div>
      </div>
      <template #footer>
        <el-button @click="show = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { vetApi } from '../api'

const rows = ref([])
const show = ref(false)
const form = ref({})
const kw = ref('')
const FORM_FIELDS = [{"k":"code","l":"编号"},{"k":"animalId","l":"巡查动物"},{"k":"checkDate","l":"巡查日期"},{"k":"veterinarian","l":"兽医"},{"k":"result","l":"结论"},{"k":"remark","l":"备注"}]
const BODY_FIELDS = [{"k":"animalId","l":"巡查动物"},{"k":"checkDate","l":"巡查日期"},{"k":"veterinarian","l":"兽医"},{"k":"remark","l":"备注"}]
const ST = 'result'
const OPTS = ["正常","异常"]

const picked = ref([])
const filtered = computed(() => {
  if (!kw.value) return rows.value
  const k = kw.value.toLowerCase()
  return rows.value.filter(r => (r.code || '').toLowerCase().includes(k) || (r.name || '').toLowerCase().includes(k))
})

async function load() { rows.value = await vetApi.list() }
function openNew() { form.value = {}; show.value = true }
function openEdit(it) { form.value = { ...it }; show.value = true }
async function save() {
  try {
    if (form.value.id) await vetApi.update(form.value.id, form.value)
    else await vetApi.create(form.value)
    show.value = false
    await load()
    ElMessage.success('已保存')
  } catch (e) { ElMessage.error(e.message) }
}
async function patch(it, key, value) {
  try {
    await vetApi.update(it.id, { [key]: value })
    await load()
    ElMessage.success('已更新')
  } catch (e) { ElMessage.error(e.message); await load() }
}
function togglePick(id) {
  const i = picked.value.indexOf(id)
  if (i >= 0) picked.value.splice(i, 1)
  else picked.value.push(id)
}
onMounted(load)
const openId = ref(null)
const RK = (BODY_FIELDS.find(f => /结论|结果|判定/.test(f.l)) || { k: ST }).k
const DK = (BODY_FIELDS.find(f => /日期/.test(f.l)) || {}).k
function dateOf(it) { return DK ? it[DK] : '' }
function cls(it) {
  const v = String(it[RK] ?? '')
  if (/正常|达标|合格/.test(v)) return 'b0'
  if (/异常|超标|不合格/.test(v)) return 'b2'
  return 'b1'
}
</script>
<style scoped>
.pg { padding: 4px 2px 40px; color: #303133; }
.pg h2 { margin: 0; font-size: 19px; }
.hd { display: flex; align-items: center; gap: 14px; margin-bottom: 16px; flex-wrap: wrap; }
.hd .hint { color: #888; font-size: 13px; flex: 1; }
.btn { border: 1px solid var(--el-color-primary); background: #fff; color: var(--el-color-primary);
  border-radius: 6px; padding: 6px 14px; cursor: pointer; font-size: 13px; }
.btn:hover { background: var(--el-color-primary-light-9); }
.btn.solid { background: var(--el-color-primary); color: #fff; }
.btn.sm { padding: 3px 10px; font-size: 12px; }
.frm .fr { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.frm .fr label { width: 88px; text-align: right; color: #666; font-size: 13px; }
.blank { color: #bbb; padding: 30px; text-align: center; }
.search { border: 1px solid #e3e3e3; border-radius: 6px; padding: 6px 12px; font-size: 13px; width: 160px; }
.rb { background: #fff; border: 1px solid #eee; border-radius: 12px; overflow: hidden; }
.ri { border-bottom: 1px solid #f5f5f5; }
.ri.open { background: #fcfcfc; }
.rhead { display: grid; grid-template-columns: 92px 110px 1fr 110px 24px; gap: 12px; align-items: center;
  padding: 13px 16px; cursor: pointer; font-size: 13px; }
.badge { text-align: center; border-radius: 12px; padding: 3px 0; font-size: 12px; background: #f2f2f2; color: #888; }
.badge.b0 { background: #f0f9eb; color: #529b2e; }
.badge.b1 { background: #fdf6ec; color: #b88230; }
.badge.b2 { background: #fef0f0; color: #c45656; }
.rc { font-family: ui-monospace, monospace; color: #aaa; }
.rd { color: #999; font-size: 12px; }
.arrow { color: #ccc; }
.rbody { padding: 0 16px 16px 118px; font-size: 13px; color: #666; }
.rr { padding: 5px 0; }
.rr i { font-style: normal; color: #aaa; margin-right: 8px; }
.rbody .btn { margin-top: 8px; }

</style>
