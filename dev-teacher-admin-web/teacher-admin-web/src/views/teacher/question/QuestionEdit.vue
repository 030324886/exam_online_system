<template>
  <div class="box">
    <h3>编辑试题</h3>
    <el-form :model="form" label-width="100px" style="max-width:700px;margin-top:20px" v-loading="loading">
      <el-form-item label="题目内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
      <el-form-item label="题型">
        <el-select v-model="form.type" disabled>
          <el-option :value="1" label="单选题" />
          <el-option :value="2" label="多选题" />
          <el-option :value="3" label="判断题" />
          <el-option :value="4" label="填空题" />
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
      <el-form-item label="正确答案"><el-input v-model="form.answer" /></el-form-item>
      <el-form-item label="答案解析"><el-input v-model="form.analysis" type="textarea" :rows="2" /></el-form-item>
      <el-form-item label="知识点"><el-input v-model="form.knowledgePoint" /></el-form-item>
      <el-form-item label="难度">
        <el-select v-model="form.difficulty">
          <el-option :value="1" label="★" /><el-option :value="2" label="★★" />
          <el-option :value="3" label="★★★" /><el-option :value="4" label="★★★★" />
          <el-option :value="5" label="★★★★★" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="submit" :loading="submitting">保存</el-button></el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request2 from '@/utils/request2'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const id = route.params.id
const loading = ref(true)
const submitting = ref(false)

const form = ref({ content: '', type: 1, options: [], answer: '', analysis: '', knowledgePoint: '', difficulty: 3 })

const getInfo = async () => {
  try {
    const res = await request2.get('/question/list')
    if (res.code === 200) {
      const found = (res.data || []).find(item => String(item.id) === String(id))
      if (found) {
        form.value = {
          content: found.content || '',
          type: found.type || 1,
          options: Array.isArray(found.options) ? [...found.options] : [],
          answer: found.answer || '',
          analysis: found.analysis || '',
          knowledgePoint: found.knowledgePoint || '',
          difficulty: found.difficulty || 3,
          id: found.id,
          creatorId: found.creatorId
        }
      } else { ElMessage.error('未找到该题目') }
    }
  } catch { ElMessage.error('获取题目失败') }
  finally { loading.value = false }
}

const submit = async () => {
  if (!form.value.content) { ElMessage.warning('请输入题目内容'); return }
  submitting.value = true
  try {
    const res = await request2.put('/question', form.value)
    if (res.code === 200) { ElMessage.success('修改成功'); router.push('/teacher/question') }
    else ElMessage.error(res.msg || '修改失败')
  } catch { ElMessage.error('修改失败') }
  finally { submitting.value = false }
}

onMounted(() => getInfo())
</script>

<style scoped>
.box { padding: 20px; }
.option-list { width: 100%; }
.option-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.opt-label { font-weight: 600; color: #409eff; width: 20px; }
</style>
