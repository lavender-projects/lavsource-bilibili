<template>
  <top-layer-settings-view class="settings-view" title="Lavender bilibili数据源" :show-left-arrow="false">
    <van-cell-group title="设置">
      <van-cell title="当前登录账号" value="未登录" size="large" />
      <van-cell title="登录" to="/settings/login" size="large" is-link />
      <van-cell title="退出登录" size="large" is-link />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/TopLayerSettingsView.vue'
</script>

<style scoped lang="scss">

</style>