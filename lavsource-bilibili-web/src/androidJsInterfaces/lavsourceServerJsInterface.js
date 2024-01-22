import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const lavsourceServerJsInterface = jsInterfaceUtils.getJsInterfaceStub('LavsourceServerJsInterface', {
  getUrlPrefix: jsInterfaceUtils.emptyImplementation()
})

export default lavsourceServerJsInterface