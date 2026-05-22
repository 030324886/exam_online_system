<template>
  <div class="box">
    <h3>新增试题</h3>
    <el-form :model="form" label-width="100px" style="max-width:700px;margin-top:20px">
      <el-form-item label="题目内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
      <el-form-item label="题型">
        <el-select v-model="form.type">
          <el-option :value="1" label="单选题" />
          <el-option :value="2" label="多选题" />
          <el-option :value="3" label="判断题" />
          <el-option :value="4" label="填空题" />
          <el-option :value="4" label="简答题" />
        </el-select>
      </el-form-item>
      <template v-if="form.type && form.type !== 4">
        <el-form-item label="选项">
          <div class="option-list">
            <div v-for="(opt, idx) in form.options" :key="idx" class="option-row">
              <span class="opt-label">{{ String.fromCharCode(65 + idx) }}.</span>
              <el-input v-model="form.options[idx]" placeholder="选项内容" style="flex:1" />
              <el-button v-if="form.options.length > 2" type="danger" size="small" @click="form.options.splice(idx,1)">x</el-button>
            </div>
            <el-button type="text" @click="form.options.push('')" size="small">+ 添加选项</el-button>
          </div>
        </el-form-item>
      </template>
      <el-form-item label="正确答案">
        <el-input v-model="form.answer" placeholder="单选/判断填A/B/C/D；多选用逗号分隔如 A,C" />
      </el-form-item>
      <el-form-item label="答案解析"><el-input v-model="form.analysis" type="textarea" :rows="2" /></el-form-item>
      <el-form-item label="知识点"><el-input v-model="form.knowledgePoint" placeholder="如：Java基础" /></el-form-item>
      <el-form-item label="难度">
        <el-select v-model="form.difficulty">
          <el-option :value="1" label="★ 简单" />
          <el-option :value="2" label="★★ 较易" />
          <el-option :value="3" label="★★★ 中等" />
          <el-option :value="4" label="★★★★ 较难" />
          <el-option :value="5" label="★★★★★ 困难" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="submit" :loading="submitting">提交</el-button></el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import request2 from '@/utils/request2'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const submitting = ref(false)

const form = ref({
  content: '',
  type: 1,
  options: ['', '', '', ''],
  answer: '',
  analysis: '',
  knowledgePoint: '',
  difficulty: 3,
  creatorId: parseInt(localStorage.getItem('userId') || '0')
})

watch(() => form.value.type, (val) => {
  if (val === 3) form.value.options = ['正确', '错误']
  else if (val === 4) form.value.options = []
  else if (form.value.options.length < 2) form.value.options = ['', '', '', '']
})

const submit = async () => {
  if (!form.value.content) { ElMessage.warning('请输入题目内容'); return }
  if (!form.value.answer) { ElMessage.warning('请输入正确答案'); return }
  if (form.value.type !== 4 && form.value.options.some(o => !o)) { ElMessage.warning('选项不能为空'); return }

  submitting.value = true
  try {
    const payload = { ...form.value }
    if (payload.type === 3) payload.options = ['正确', '错误']
    const res = await request2.post('/question', payload)
    if (res.code === 200) { ElMessage.success('新增成功'); router.push('/teacher/question') }
    else ElMessage.error(res.msg || '新增失败')
  } catch { ElMessage.error('新增失败') }
  finally { submitting.value = false }
}
</script>

<style scoped>
.box { padding: 20px; }
.option-list { width: 100%; }
.option-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.opt-label { font-weight: 600; color: #409eff; width: 20px; }
</style>
