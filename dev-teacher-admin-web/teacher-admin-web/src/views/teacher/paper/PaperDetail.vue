<template>
  <div class="box" v-loading="loading">
    <div class="detail-header">
      <el-button @click="$router.back()" text>← 返回</el-button>
      <h3 style="margin:10px 0">试卷预览</h3>
    </div>

    <el-card v-if="paper.id" class="info-card">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="试卷名称">{{ paper.name || paper.title }}</el-descriptions-item>
        <el-descriptions-item label="总分">{{ paper.totalScore }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ paper.duration || paper.durationMinutes }} 分钟</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <div v-for="(q, idx) in questions" :key="q.id" class="q-card">
      <div class="q-header">
        <span class="q-num">{{ idx + 1 }}.</span>
        <span class="q-tag" :class="'tag-' + (typeMap[q.type] || 'other').toLowerCase()">{{ typeMap[q.type] || '其他' }}</span>
        <span class="q-score">{{ q.score }}分</span>
      </div>
      <div class="q-content">{{ q.content || q.title }}</div>
      <div class="q-options" v-if="getOptions(q).length">
        <div v-for="(opt, oi) in getOptions(q)" :key="oi" class="q-opt">{{ opt.label }}. {{ opt.content }}</div>
      </div>
      <div class="q-answer">答案：{{ q.answer }}</div>
    </div>

    <div v-if="!questions.length" style="text-align:center;padding:60px;color:#909399">暂无题目数据</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRoute } from 'vue-router'

const route = useRoute()
const paper = ref({})
const questions = ref([])
const loading = ref(true)

const typeMap = { 1: '单选', 2: '多选', 3: '判断', 4: '填空', 'SINGLE': '单选', 'MULTIPLE': '多选', 'JUDGE': '判断', 'FILL': '填空', 'SHORT': '简答' }

const getOptions = (q) => {
  let opts = q.options
  if (!opts) return []
  if (typeof opts === 'string') { try { opts = JSON.parse(opts) } catch { return [] } }
  if (Array.isArray(opts)) {
    return opts.map((o, i) => {
      if (typeof o === 'string') return { label: String.fromCharCode(65 + i), value: String.fromCharCode(65 + i), content: o }
      return o
    })
  }
  return []
}

const getDetail = async () => {
  try {
    const res = await request.get(`/papers/detail/${route.params.id}`)
    if (res.success !== false) {
      const data = res.data || res
      paper.value = data.paper || {}
      questions.value = data.questions || []
    }
  } catch { }
  finally { loading.value = false }
}

onMounted(() => getDetail())
</script>

<style scoped>
.box { padding: 20px; }
.detail-header { margin-bottom: 20px; }
.info-card { margin-bottom: 20px; }
.q-card {
  background: #fff; border: 1px solid #e8e8e8; border-radius: 8px;
  padding: 16px; margin-bottom: 12px;
}
.q-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.q-num { font-weight: 600; color: #409eff; }
.q-tag { font-size: 12px; padding: 1px 8px; border-radius: 4px; color: #fff; }
.tag-单选 { background: #409eff; }
.tag-多选 { background: #67c23a; }
.tag-判断 { background: #e6a23c; }
.tag-填空 { background: #909399; }
.tag-其他 { background: #909399; }
.q-score { margin-left: auto; font-size: 13px; color: #f56c6c; font-weight: 500; }
.q-content { font-size: 15px; margin-bottom: 10px; }
.q-options { margin-bottom: 8px; }
.q-opt { font-size: 14px; color: #555; padding: 2px 0; }
.q-answer { font-size: 13px; color: #67c23a; }
</style>
