import { jsInterfaceUtils } from '@/utils/androidJsInterfaces'

const methodDefinitions = {
  getUrlPrefix: () => {}
}

const lavsourceServerJsInterface = jsInterfaceUtils.getJsInterfaceStub(
  'LavsourceServerJsInterface', methodDefinitions
) ?? methodDefinitions

export default lavsourceServerJsInterface