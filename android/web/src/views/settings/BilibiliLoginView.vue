<template>
  <top-layer-settings-view class="bilibili-login-view" title="登录 - bilibili">
    <van-form class="top-margin" @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.username" name="用户名" label="用户名" placeholder="用户名" size="large"
                   :rules="[{ required: true, message: '请填写用户名' }]" />
        <van-field v-model="form.password" type="password" name="密码" label="密码" placeholder="密码" size="large"
                   :rules="[{ required: true, message: '请填写密码' }]" />
        <van-field class="validate-code-field" label="验证码" size="large">
          <template #input>
            <div v-if="!status.validateCodeLoaded && !status.validateCodeLoadFailed">
              {{ componentData.validateCodeLoadingStatusText }}
            </div>
            <div class="bilibili-validate-code" ref="validateCodeDom" v-if="status.validateCodeLoaded"></div>
            <div v-if="status.validateCodeLoadFailed" @click="initValidateCode()">加载失败，点击重新加载</div>
          </template>
        </van-field>
      </van-cell-group>
      <div class="around-margin">
        <van-button round block type="primary" native-type="submit" :loading="status.formSubmitting">
          提交
        </van-button>
      </div>
    </van-form>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/TopLayerSettingsView.vue'
import { onMounted, reactive, ref } from 'vue'
import messageUtils from '@/utils/message'
import loginApi from '@/api/login'
import { useRouter } from 'vue-router'

const status = reactive({
  formSubmitting: false,
  validateCodeLoaded: false,
  validateCodeLoadFailed: false
})

const form = reactive({
  username: null,
  password: null
})

const componentData = reactive({
  validateCodeLoadingStatusText: ''
})

let validationData = {
  token: null,
  challenge: null,
  gt: null,
  validate: null,
  seccode: null,
  passed: false
}

const validateCodeDom = ref()

const router = useRouter()

onMounted(() => {
  initValidateCode()
})

function initValidateCode() {
  status.validateCodeLoaded = false
  status.validateCodeLoadFailed = false
  componentData.validateCodeLoadingStatusText = '加载中……'
  loginApi.validateCode().then(res => {
    let resData = res.data ?? {
      geetest: {
        challenge: null,
        gt: null
      }
    }
    validationData.token = resData.token
    validationData.challenge = resData.geetest.challenge
    validationData.gt = resData.geetest.gt
    //更多前端配置参数说明请参见：http://docs.geetest.com/install/client/web-front/
    initGeetest({
      //以下4个配置参数为必须，不能缺少
      gt: validationData.gt,
      challenge: validationData.challenge,
      //表示用户后台检测极验服务器是否宕机
      offline: false,
      //用于宕机时表示是新验证码的宕机
      new_captcha: true,
      //产品形式，包括：float，popup
      product: 'popup',
      width: '210px',
      https: true
    }, geeTestCallback)
  }).catch(() => {
    status.validateCodeLoadFailed = true
  }).finally(() => {
    componentData.validateCodeLoadingStatusText = ''
  })
}

function geeTestCallback(captchaObj) {
  captchaObj = captchaObj ?? {
    getValidate: () => ({
      geetest_validate: null,
      geetest_seccode: null
    })
  }
  status.validateCodeLoaded = true
  captchaObj.appendTo('.bilibili-login-view .bilibili-validate-code')
  captchaObj.onSuccess(() => {
    let result = captchaObj.getValidate()
    validationData.validate = result.geetest_validate
    validationData.seccode = result.geetest_seccode
    validationData.passed = true
  }).onError(error => {
    console.log('captcha error: ', error)
    messageUtils.error('验证失败，请查看控制台')
  })
}

function onSubmit() {
  if(!validationData.passed) {
    messageUtils.message('请先完成验证码验证')
    return
  }
  status.formSubmitting = true
  loginApi.login({
    ...form,
    ...validationData
  }).then(res => {
    if(res.status !== true) {
      //noinspection JSUnresolvedReference
      messageUtils.error(res.msg)
      return
    }
    router.push('/settings')
  }).catch(() => {
    validateCodeDom.value.innerHTML = ''
    initValidateCode()
  }).finally(() => {
    status.formSubmitting = false
  })
}
</script>

<style scoped lang="scss">
.bilibili-login-view {
  .validate-code-field {
    align-items: center;
  }

  .bilibili-validate-code {
    width: 210px;
  }

  ::v-deep(.geetest_holder) {
    min-width: unset;
    width: 210px;
  }
}

.top-margin {
  margin-top: 16px;
}

.around-margin {
  margin: 16px;
}
</style>