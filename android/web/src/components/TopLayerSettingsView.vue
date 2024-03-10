<template>
  <div class="top-layer-settings-view">
    <van-nav-bar :title="title" :left-arrow="showLeftArrow" @click-left="back" />
    <div class="settings-view-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  showLeftArrow: {
    type: Boolean,
    default: true
  }
})

function back() {
  history.back()
}
</script>

<style scoped lang="scss">

</style>