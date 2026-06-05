import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { ElButton } from 'element-plus/es/components/button/index'
import { ElCard } from 'element-plus/es/components/card/index'
import { ElCol } from 'element-plus/es/components/col/index'
import { ElContainer, ElHeader, ElMain } from 'element-plus/es/components/container/index'
import { ElDivider } from 'element-plus/es/components/divider/index'
import { ElForm, ElFormItem } from 'element-plus/es/components/form/index'
import { ElInput } from 'element-plus/es/components/input/index'
import { ElInputNumber } from 'element-plus/es/components/input-number/index'
import { ElMenu, ElMenuItem } from 'element-plus/es/components/menu/index'
import { ElRow } from 'element-plus/es/components/row/index'
import { ElSwitch } from 'element-plus/es/components/switch/index'
import { ElTable, ElTableColumn } from 'element-plus/es/components/table/index'
import { ElTabPane, ElTabs } from 'element-plus/es/components/tabs/index'
import 'element-plus/dist/index.css'
import './styles.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)

;[
  ElButton,
  ElCard,
  ElCol,
  ElContainer,
  ElDivider,
  ElForm,
  ElFormItem,
  ElHeader,
  ElInput,
  ElInputNumber,
  ElMain,
  ElMenu,
  ElMenuItem,
  ElRow,
  ElSwitch,
  ElTable,
  ElTableColumn,
  ElTabPane,
  ElTabs
].forEach((component) => app.use(component))

app.use(createPinia())
app.use(router)
app.mount('#app')
