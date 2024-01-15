<template>
  <top-layer-settings-view class="settings-view" title="Lavender bilibili数据源" :show-left-arrow="false">
    <van-cell-group title="设置">
      <van-cell title="当前登录账号" :value="loginStatus.username" size="large" />
      <van-cell v-if="loginStatus.loaded && !loginStatus.logined" title="登录"
                to="/settings/login" size="large" is-link />
      <van-cell v-if="loginStatus.loaded && loginStatus.logined" title="退出登录"
                @click="logout()" size="large" is-link />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/TopLayerSettingsView.vue'
import { onMounted, reactive } from 'vue'
import loginApi from '@/api/login'
import { showConfirmDialog } from 'vant'

const loginStatus = reactive({
  loaded: false,
  logined: false,
  username: ''
})

onMounted(() => {
  getLoginStatus()
})

function getLoginStatus() {
  loginStatus.loaded = false
  loginStatus.username = '加载中……'
  loginApi.loginStatus().then(res => {
    loginStatus.loaded = true
    loginStatus.logined = res.data.logined
    loginStatus.username = res.data.logined === true ? res.data.username : '未登录'
  }).catch(() => {
    loginStatus.username = '加载失败'
  })
}

function logout() {
  showConfirmDialog({
    title: '退出登录',
    message: '确认退出登录吗？',
    beforeClose: async action => {
      if(action === 'confirm') {
        await loginApi.logout()
      }
      //返回true表示应该关闭弹窗，而不是退出登录成功
      return true
    }
  }).then(() => {
    //仅当action为confirm时，才执行then中的内容
    getLoginStatus()
  }).catch(() => {})
}
</script>

<style scoped lang="scss">

</style>